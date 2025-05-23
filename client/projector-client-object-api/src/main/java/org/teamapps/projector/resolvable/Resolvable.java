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
package org.teamapps.projector.resolvable;

import org.teamapps.projector.session.SessionContext;

/**
 * An interface for objects that can be resolved to a URL.
 * <p>
 * Implementing classes provide a method for generating URLs that are specific to a given session context.
 */
public interface Resolvable {

    /**
     * Resolves this object to a URL string within the provided session context.
     *
     * @param sessionContext The session context in which this object should be resolved
     * @return A URL string representing this object in the given context
     */
	String getUrl(SessionContext sessionContext);

}
