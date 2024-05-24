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
package org.teamapps.ux.servlet;

import org.teamapps.projector.resource.Resource;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.projector.resourceprovider.ResourceProvider;

import java.util.function.Function;

import static org.teamapps.projector.session.SessionContextResourceManager.RESOURCE_LINK_ID_PREFIX;

public class TeamAppsSessionResourceProvider implements ResourceProvider {

	private final Function<String, SessionContext> sessionContextLookup;

	public TeamAppsSessionResourceProvider(Function<String, SessionContext> sessionContextLookup) {
		this.sessionContextLookup = sessionContextLookup;
	}

	@Override
	public Resource getResource(String servletPath, String relativeResourcePath, String httpSessionId) {
		try {
			if (relativeResourcePath == null) {
				return null;
			}
			if (relativeResourcePath.startsWith("/")) {
				relativeResourcePath = relativeResourcePath.substring(1);
			}
			String[] parts = relativeResourcePath.split("/");
			String uiSessionId = parts[0];
			SessionContext sessionContext = sessionContextLookup.apply(uiSessionId);
			if (sessionContext == null) {
				return null;
			}
			int id = Integer.parseInt(parts[1].substring(RESOURCE_LINK_ID_PREFIX.length()));
			return sessionContext.getBinaryResource(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
