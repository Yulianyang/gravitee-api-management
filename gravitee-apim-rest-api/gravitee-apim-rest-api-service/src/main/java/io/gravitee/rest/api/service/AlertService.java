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
package io.gravitee.rest.api.service;

import io.gravitee.common.data.domain.Page;
import io.gravitee.rest.api.model.AlertEventQuery;
import io.gravitee.rest.api.model.alert.*;
import io.gravitee.rest.api.service.common.ExecutionContext;
import java.util.List;

/**
 * @author Azize ELAMRANI (azize at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface AlertService {
    AlertTriggerEntity create(ExecutionContext executionContext, NewAlertTriggerEntity alert);
    AlertTriggerEntity update(ExecutionContext executionContext, UpdateAlertTriggerEntity alert);
    AlertTriggerEntity findById(String alertId);
    List<AlertTriggerEntity> findAll();
    List<AlertTriggerEntity> findByReference(AlertReferenceType referenceType, String referenceId);
    List<AlertTriggerEntity> findByReferences(AlertReferenceType referenceType, List<String> referenceIds);
    List<AlertTriggerEntity> findByReferenceWithEventCounts(AlertReferenceType referenceType, String referenceId);
    void delete(String alertId, String referenceId);
    AlertStatusEntity getStatus(ExecutionContext executionContext);
    Page<AlertEventEntity> findEvents(String alertId, AlertEventQuery eventQuery);
    void applyDefaults(ExecutionContext executionContext, String alertId, AlertReferenceType referenceType);
}
