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
package io.gravitee.rest.api.management.rest.resource;

import io.gravitee.common.http.MediaType;
import io.gravitee.rest.api.management.rest.security.Permission;
import io.gravitee.rest.api.management.rest.security.Permissions;
import io.gravitee.rest.api.model.permissions.RolePermission;
import io.gravitee.rest.api.model.permissions.RolePermissionAction;
import io.gravitee.rest.api.model.platform.plugin.PlatformPluginEntity;
import io.gravitee.rest.api.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Context;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
@Tag(name = "Plugins")
public class ResourceResource {

    @Context
    private ResourceContext resourceContext;

    @Inject
    private ResourceService resourceService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a resource", description = "User must have the MANAGEMENT_API[READ] permission to use this service")
    @ApiResponse(
        responseCode = "200",
        description = "Resource plugin",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PlatformPluginEntity.class))
    )
    @ApiResponse(responseCode = "404", description = "Resource not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({ @Permission(value = RolePermission.ENVIRONMENT_API, acls = RolePermissionAction.READ) })
    public PlatformPluginEntity getResource(@PathParam("resource") String resource) {
        return resourceService.findById(resource);
    }

    @GET
    @Path("schema")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a resource's schema", description = "User must have the MANAGEMENT_API[READ] permission to use this service")
    @Permissions({ @Permission(value = RolePermission.ENVIRONMENT_API, acls = RolePermissionAction.READ) })
    public String getResourceSchema(@PathParam("resource") String resource) {
        // Check that the resource exists
        resourceService.findById(resource);

        return resourceService.getSchema(resource);
    }

    @GET
    @Path("documentation")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
        summary = "Get a resource's documentation",
        description = "User must have the MANAGEMENT_API[READ] permission to use this service"
    )
    @Permissions({ @Permission(value = RolePermission.ENVIRONMENT_API, acls = RolePermissionAction.READ) })
    public String getResourceDoc(@PathParam("resource") String resource) {
        // Check that the policy exists
        resourceService.findById(resource);

        return resourceService.getDocumentation(resource);
    }
}
