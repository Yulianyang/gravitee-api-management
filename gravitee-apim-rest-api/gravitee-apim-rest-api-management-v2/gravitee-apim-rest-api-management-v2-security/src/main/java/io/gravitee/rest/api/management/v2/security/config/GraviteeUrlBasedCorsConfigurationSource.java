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
package io.gravitee.rest.api.management.v2.security.config;

import io.gravitee.common.event.EventManager;
import io.gravitee.rest.api.service.ParameterService;
import io.gravitee.rest.api.service.common.GraviteeContext;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
public class GraviteeUrlBasedCorsConfigurationSource extends UrlBasedCorsConfigurationSource {

    private final Map<String, GraviteeCorsConfiguration> corsConfigurationByOrganization = new HashMap<>();

    private final ParameterService parameterService;
    private final EventManager eventManager;

    public GraviteeUrlBasedCorsConfigurationSource(ParameterService parameterService, EventManager eventManager) {
        this.parameterService = parameterService;
        this.eventManager = eventManager;
    }

    private String computeOrganizationId(HttpServletRequest request) {
        // FIXME: should return null if no organization found.
        return GraviteeContext.getDefaultOrganization();
    }

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        String organizationId = computeOrganizationId(request);
        if (organizationId != null) {
            GraviteeCorsConfiguration corsConfiguration = corsConfigurationByOrganization.get(organizationId);
            if (corsConfiguration == null) {
                corsConfiguration = new GraviteeCorsConfiguration(parameterService, eventManager, organizationId);
                this.corsConfigurationByOrganization.put(organizationId, corsConfiguration);
            }
            return corsConfiguration;
        }
        return super.getCorsConfiguration(request);
    }
}
