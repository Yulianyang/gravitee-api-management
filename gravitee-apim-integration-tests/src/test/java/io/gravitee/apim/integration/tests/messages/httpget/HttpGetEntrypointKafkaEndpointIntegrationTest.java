/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.apim.integration.tests.messages.httpget;

import static org.assertj.core.api.Assertions.assertThat;

import com.graviteesource.entrypoint.http.get.HttpGetEntrypointConnectorFactory;
import io.gravitee.apim.gateway.tests.sdk.annotations.DeployApi;
import io.gravitee.apim.gateway.tests.sdk.annotations.GatewayTest;
import io.gravitee.apim.gateway.tests.sdk.connector.EntrypointBuilder;
import io.gravitee.apim.integration.tests.messages.AbstractKafkaEndpointIntegrationTest;
import io.gravitee.common.http.MediaType;
import io.gravitee.gateway.api.http.HttpHeaderNames;
import io.gravitee.gateway.reactive.api.qos.Qos;
import io.gravitee.plugin.entrypoint.EntrypointConnectorPlugin;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.http.HttpClient;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@GatewayTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpGetEntrypointKafkaEndpointIntegrationTest extends AbstractKafkaEndpointIntegrationTest {

    @Override
    public void configureEntrypoints(Map<String, EntrypointConnectorPlugin<?, ?>> entrypoints) {
        entrypoints.putIfAbsent("http-get", EntrypointBuilder.build("http-get", HttpGetEntrypointConnectorFactory.class));
    }

    @Test
    @DeployApi({ "/apis/v4/messages/http-get/http-get-entrypoint-kafka-endpoint.json" })
    void should_receive_all_messages(HttpClient client, Vertx vertx) {
        // In order to simplify the test, Kafka endpoint's consumer is configured with "autoOffsetReset": "earliest"
        // It allows us to publish the messages in the topic before opening the api connection.
        Single
            .fromCallable(() -> getKafkaProducer(vertx))
            .flatMapCompletable(producer ->
                publishToKafka(producer, "message1")
                    .andThen(publishToKafka(producer, "message2"))
                    .andThen(publishToKafka(producer, "message3"))
                    .doFinally(producer::close)
            )
            .blockingAwait();

        // First request should receive 2 first messages
        client
            .rxRequest(HttpMethod.GET, "/test")
            .flatMap(request -> {
                request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                return request.send();
            })
            .flatMap(response -> {
                assertThat(response.statusCode()).isEqualTo(200);
                return response.body();
            })
            .test()
            .awaitDone(30, TimeUnit.SECONDS)
            .assertValue(body -> {
                final JsonObject jsonResponse = new JsonObject(body.toString());
                final JsonArray items = jsonResponse.getJsonArray("items");
                assertThat(items).hasSize(2);
                final JsonObject message = items.getJsonObject(0);
                assertThat(message.getString("id")).isNull();
                assertThat(message.getString("content")).isEqualTo("message1");
                final JsonObject message2 = items.getJsonObject(1);
                assertThat(message2.getString("id")).isNull();
                assertThat(message2.getString("content")).isEqualTo("message2");
                return true;
            });

        // Second request should receive last messages
        client
            .rxRequest(HttpMethod.GET, "/test")
            .flatMap(request -> {
                request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                return request.send();
            })
            .flatMap(response -> {
                assertThat(response.statusCode()).isEqualTo(200);
                return response.body();
            })
            .test()
            .awaitDone(30, TimeUnit.SECONDS)
            .assertValue(body -> {
                final JsonObject jsonResponse = new JsonObject(body.toString());
                final JsonArray items = jsonResponse.getJsonArray("items");
                assertThat(items).hasSize(1);
                final JsonObject message = items.getJsonObject(0);
                assertThat(message.getString("id")).isNull();
                assertThat(message.getString("content")).isEqualTo("message3");
                return true;
            });

        // Third request should not receive anything
        client
            .rxRequest(HttpMethod.GET, "/test")
            .flatMap(request -> {
                request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                return request.send();
            })
            .flatMap(response -> {
                assertThat(response.statusCode()).isEqualTo(200);
                return response.body();
            })
            .test()
            .awaitDone(30, TimeUnit.SECONDS)
            .assertValue(body -> {
                final JsonObject jsonResponse = new JsonObject(body.toString());
                final JsonArray items = jsonResponse.getJsonArray("items");
                assertThat(items).isEmpty();
                return true;
            });
    }

    @EnumSource(value = Qos.class, names = { "AT_MOST_ONCE", "AT_LEAST_ONCE" })
    @ParameterizedTest(name = "should receive all messages with {0} qos")
    @DeployApi(
        {
            "/apis/v4/messages/http-get/http-get-entrypoint-kafka-endpoint-at-least-once.json",
            "/apis/v4/messages/http-get/http-get-entrypoint-kafka-endpoint-at-most-once.json",
        }
    )
    void should_receive_all_messages_with_qos(Qos qos, HttpClient client, Vertx vertx) {
        // In order to simplify the test, Kafka endpoint's consumer is configured with "autoOffsetReset": "earliest"
        // It allows us to publish the messages in the topic before opening the api connection through SSE entrypoint.
        Single
            .fromCallable(() -> getKafkaProducer(vertx))
            .flatMapCompletable(producer ->
                publishToKafka(producer, "message1")
                    .andThen(publishToKafka(producer, "message2"))
                    .andThen(publishToKafka(producer, "message3"))
                    .doFinally(producer::close)
            )
            .blockingAwait();

        // First request should receive 2 first messages
        client
            .rxRequest(HttpMethod.GET, "/test-" + qos.getLabel())
            .flatMap(request -> {
                request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                return request.send();
            })
            .flatMap(response -> {
                assertThat(response.statusCode()).isEqualTo(200);
                return response.body();
            })
            .test()
            .awaitDone(30, TimeUnit.SECONDS)
            .assertValue(body -> {
                final JsonObject jsonResponse = new JsonObject(body.toString());
                final JsonArray items = jsonResponse.getJsonArray("items");
                assertThat(items).hasSize(2);
                final JsonObject message = items.getJsonObject(0);
                assertThat(message.getString("content")).isEqualTo("message1");
                assertThat(message.getString("id")).isEqualTo("dGVzdC10b3BpY0AwIzA="); // test-topic@0#0
                final JsonObject message2 = items.getJsonObject(1);
                assertThat(message2.getString("content")).isEqualTo("message2");
                assertThat(message2.getString("id")).isEqualTo("dGVzdC10b3BpY0AwIzE="); // test-topic@0#1

                final JsonObject pagination = jsonResponse.getJsonObject("pagination");
                assertThat(pagination.getString("nextCursor")).isEqualTo("dGVzdC10b3BpY0AwIzE="); // test-topic@0#1

                return true;
            });

        // Second request should receive last messages
        client
            .rxRequest(HttpMethod.GET, "/test-" + qos.getLabel())
            .flatMap(request -> {
                request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                return request.send();
            })
            .flatMap(response -> {
                assertThat(response.statusCode()).isEqualTo(200);
                return response.body();
            })
            .test()
            .awaitDone(30, TimeUnit.SECONDS)
            .assertValue(body -> {
                final JsonObject jsonResponse = new JsonObject(body.toString());
                final JsonArray items = jsonResponse.getJsonArray("items");
                assertThat(items).hasSize(1);
                final JsonObject message3 = items.getJsonObject(0);
                assertThat(message3.getString("content")).isEqualTo("message3");
                assertThat(message3.getString("id")).isEqualTo("dGVzdC10b3BpY0AwIzI="); // test-topic@0#2

                final JsonObject pagination = jsonResponse.getJsonObject("pagination");
                assertThat(pagination.getString("nextCursor")).isEqualTo("dGVzdC10b3BpY0AwIzI="); // test-topic@0#2

                return true;
            });

        // Third request should not receive anything
        client
            .rxRequest(HttpMethod.GET, "/test-" + qos.getLabel())
            .flatMap(request -> {
                request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                return request.send();
            })
            .flatMap(response -> {
                assertThat(response.statusCode()).isEqualTo(200);
                return response.body();
            })
            .test()
            .awaitDone(30, TimeUnit.SECONDS)
            .assertValue(body -> {
                final JsonObject jsonResponse = new JsonObject(body.toString());
                final JsonArray items = jsonResponse.getJsonArray("items");
                assertThat(items).isEmpty();

                final JsonObject pagination = jsonResponse.getJsonObject("pagination");
                assertThat(pagination.getString("nextCursor")).isEqualTo("dGVzdC10b3BpY0AwIzI="); // test-topic@0#1

                return true;
            });
    }

    @EnumSource(value = Qos.class, names = { "AT_MOST_ONCE", "AT_LEAST_ONCE" })
    @ParameterizedTest(name = "should receive empty items while requesting recent cursor with {0} qos")
    @DeployApi(
        {
            "/apis/v4/messages/http-get/http-get-entrypoint-kafka-endpoint-at-least-once.json",
            "/apis/v4/messages/http-get/http-get-entrypoint-kafka-endpoint-at-most-once.json",
        }
    )
    void should_receive_empty_items_while_requesting_recent_cursor_with_specific_qos(Qos qos, HttpClient client, Vertx vertx) {
        // In order to simplify the test, Kafka endpoint's consumer is configured with "autoOffsetReset": "earliest"
        // It allows us to publish the messages in the topic before opening the api connection through SSE entrypoint.
        Single
            .fromCallable(() -> getKafkaProducer(vertx))
            .flatMapCompletable(producer ->
                publishToKafka(producer, "message1")
                    .andThen(publishToKafka(producer, "message2"))
                    .andThen(publishToKafka(producer, "message3"))
                    .doFinally(producer::close)
            )
            .blockingAwait();

        // cursor=test-topic@0#2
        client
            .rxRequest(HttpMethod.GET, "/test-" + qos.getLabel() + "?cursor=dGVzdC10b3BpY0AwIzI=")
            .flatMap(request -> {
                request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                return request.send();
            })
            .flatMap(response -> {
                assertThat(response.statusCode()).isEqualTo(200);
                return response.body();
            })
            .test()
            .awaitDone(30, TimeUnit.SECONDS)
            .assertValue(body -> {
                final JsonObject jsonResponse = new JsonObject(body.toString());
                final JsonArray items = jsonResponse.getJsonArray("items");
                assertThat(items).isEmpty();

                final JsonObject pagination = jsonResponse.getJsonObject("pagination");
                assertThat(pagination.getString("nextCursor")).isEqualTo("dGVzdC10b3BpY0AwIzI="); // test-topic@0#2

                return true;
            });
    }

    @EnumSource(value = Qos.class, names = { "AT_MOST_ONCE", "AT_LEAST_ONCE" })
    @ParameterizedTest(name = "should receive empty items with_initial_cursor with_no_messages and {0} qos")
    @DeployApi(
        {
            "/apis/v4/messages/http-get/http-get-entrypoint-kafka-endpoint-at-least-once.json",
            "/apis/v4/messages/http-get/http-get-entrypoint-kafka-endpoint-at-most-once.json",
        }
    )
    void should_receive_empty_items_with_initial_cursor_with_no_messages_and_specific_qos(Qos qos, HttpClient client) {
        client
            .rxRequest(HttpMethod.GET, "/test-" + qos.getLabel())
            .flatMap(request -> {
                request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                return request.send();
            })
            .flatMap(response -> {
                assertThat(response.statusCode()).isEqualTo(200);
                return response.body();
            })
            .test()
            .awaitDone(30, TimeUnit.SECONDS)
            .assertValue(body -> {
                final JsonObject jsonResponse = new JsonObject(body.toString());
                final JsonArray items = jsonResponse.getJsonArray("items");
                assertThat(items).isEmpty();

                final JsonObject pagination = jsonResponse.getJsonObject("pagination");
                assertThat(pagination.getString("nextCursor")).isEqualTo("dGVzdC10b3BpY0AwIzA="); // test-topic@0#2

                return true;
            });
    }

    @Test
    @DeployApi({ "/apis/v4/messages/http-get/http-get-entrypoint-kafka-endpoint-failure.json" })
    void should_receive_error_messages_when_error_occurred(HttpClient client) {
        // First request should receive 2 first messages
        client
            .rxRequest(HttpMethod.GET, "/test-failure")
            .flatMap(request -> {
                request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                return request.rxSend();
            })
            .flatMap(response -> {
                assertThat(response.statusCode()).isEqualTo(200);
                return response.body();
            })
            .test()
            .awaitDone(30, TimeUnit.SECONDS)
            .assertValue(body -> {
                final JsonObject jsonResponse = new JsonObject(body.toString());
                final JsonArray items = jsonResponse.getJsonArray("items");
                assertThat(items).isEmpty();

                final JsonObject error = jsonResponse.getJsonObject("error");
                assertThat(error.getString("id")).isNotNull();
                final JsonObject metadata = error.getJsonObject("metadata");
                assertThat(metadata.getString("key")).isEqualTo("FAILURE_ENDPOINT_CONFIGURATION_INVALID");

                return true;
            });
    }
}
