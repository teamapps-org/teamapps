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
package org.teamapps.ux.session.navigation;

import org.teamapps.ux.session.SessionContext;

import static org.teamapps.ux.session.navigation.RoutingUtil.concatenatePaths;
import static org.teamapps.ux.session.navigation.RoutingUtil.normalizePath;

public class SubRouter implements Router {

	private final String pathPrefix;
	private final SessionContext sessionContext;

	public SubRouter(String pathPrefix) {
		this(pathPrefix, SessionContext.current());
	}

	public SubRouter(String pathPrefix, SessionContext sessionContext) {
		this.pathPrefix = normalizePath(pathPrefix);
		this.sessionContext = sessionContext;
	}

	@Override
	public RoutingHandlerRegistration registerRoutingHandler(String pathTemplate, boolean exact, RoutingHandler handler, boolean applyImmediately) {
		return sessionContext.registerRoutingHandler(concatenatePaths(pathPrefix, pathTemplate), false, handler, applyImmediately);
	}

	@Override
	public Router createSubRouter(String relativePath) {
		return new SubRouter(concatenatePaths(pathPrefix, relativePath));
	}
}
