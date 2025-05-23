/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.projector.resource.provider;

import org.teamapps.projector.resource.Resource;

/**
 * Interface for providing resources in a web application context.
 * ResourceProvider implementations are responsible for locating and returning resources
 * based on the servlet path, relative resource path, and HTTP session ID.
 * <p>
 * This interface is typically used for serving static resources like CSS, JavaScript, images,
 * or other files that need to be accessible via HTTP.
 */
public interface ResourceProvider {

    /**
     * Retrieves a resource based on the provided paths and session information.
     *
     * @param servletPath The path of the servlet handling the request
     * @param relativeResourcePath The path of the requested resource relative to the servlet path
     * @param httpSessionId The ID of the HTTP session making the request
     * @return The requested resource, or null if the resource cannot be found
     */
    Resource getResource(String servletPath, String relativeResourcePath, String httpSessionId);

}
