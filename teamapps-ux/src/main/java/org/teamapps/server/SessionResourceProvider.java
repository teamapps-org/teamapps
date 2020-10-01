/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.server;

import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.ux.resource.Resource;
import org.teamapps.ux.resource.ResourceProvider;
import org.teamapps.ux.session.SessionContext;

import java.util.function.Function;

public class SessionResourceProvider implements ResourceProvider {

	private final Function<QualifiedUiSessionId, SessionContext> sessionContexts;

	public SessionResourceProvider(Function<QualifiedUiSessionId, SessionContext> sessionContexts) {
		this.sessionContexts = sessionContexts;
	}

	private SessionContext getSessionContext(String resource, String httpSessionId) {
		if (resource == null) {
			return null;
		}
		String[] parts = resource.split("/");
		QualifiedUiSessionId qualifiedUiSessionId = new QualifiedUiSessionId(httpSessionId, parts[0]);
		return sessionContexts.apply(qualifiedUiSessionId);
	}

	private Resource getResource(String resource, String httpSessionId) {
		try {
			if (resource == null) {
				return null;
			}
			if (resource.startsWith("/")) {
				resource = resource.substring(1);
			}
			String[] parts = resource.split("/");
			QualifiedUiSessionId qualifiedUiSessionId = new QualifiedUiSessionId(httpSessionId, parts[0]);
			SessionContext sessionContext = sessionContexts.apply(qualifiedUiSessionId);
			if (sessionContext == null) {
				return null;
			}
			int id = Integer.parseInt(parts[1].substring(3));
			return sessionContext.getBinaryResource(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Resource getResource(String servletPath, String relativeResourcePath, String httpSessionId) {
		return getResource(relativeResourcePath, httpSessionId);
	}
	
}
