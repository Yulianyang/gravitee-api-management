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
package io.gravitee.rest.api.service.v4;

import io.gravitee.rest.api.model.PrimaryOwnerEntity;
import io.gravitee.rest.api.service.common.ExecutionContext;
import io.gravitee.rest.api.service.exceptions.TechnicalManagementException;
import java.util.List;
import java.util.Map;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface PrimaryOwnerService {
    PrimaryOwnerEntity getPrimaryOwner(ExecutionContext executionContext, String apiId) throws TechnicalManagementException;

    /**
     * Resolves the primary owner email for the given API.
     * If the primary owner is a user, the email is returned.
     * If the primary owner is a group, the email of the member entitled as an API primary owner is returned.
     * @param executionContext the execution context
     * @param apiId the API id
     * @return the primary owner email
     */
    String getPrimaryOwnerEmail(ExecutionContext executionContext, String apiId);

    PrimaryOwnerEntity getPrimaryOwner(ExecutionContext executionContext, String userId, PrimaryOwnerEntity currentPrimaryOwner);

    Map<String, PrimaryOwnerEntity> getPrimaryOwners(ExecutionContext executionContext, List<String> apiIds);
}
