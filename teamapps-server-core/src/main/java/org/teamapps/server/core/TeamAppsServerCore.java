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
package org.teamapps.server.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.teamapps.server.config.TeamAppsConfiguration;
import org.teamapps.icons.IconLibraryRegistry;
import org.teamapps.icons.IconProvider;
import org.teamapps.server.json.TeamAppsObjectMapperFactory;
import org.teamapps.server.uisession.TeamAppsSessionManager;
import org.teamapps.server.threading.SequentialExecutorFactory;
import org.teamapps.projector.clientobject.ComponentLibraryRegistry;
import org.teamapps.server.servlet.WebSocketCommunicationEndpoint;
import org.teamapps.server.webcontroller.WebController;

public class TeamAppsServerCore {

	private final TeamAppsConfiguration config;
	private final WebController webController;
	private final ObjectMapper objectMapper;
	private final TeamAppsSessionManager sessionManager;
	private final IconLibraryRegistry iconLibraryRegistry;
	private final IconProvider iconProvider;
	private final ComponentLibraryRegistry componentLibraryRegistry;
	private final TeamAppsUploadManager uploadManager;
	private final WebSocketCommunicationEndpoint webSocketCommunicationEndpoint;

	public TeamAppsServerCore(TeamAppsConfiguration config, SequentialExecutorFactory sessionExecutorFactory, WebController webController) {
		this.config = config;
		this.webController = webController;
		this.objectMapper = TeamAppsObjectMapperFactory.create();
		this.iconLibraryRegistry = new IconLibraryRegistry();
		this.componentLibraryRegistry = new ComponentLibraryRegistry("/components/");
		this.uploadManager = new TeamAppsUploadManager();

		this.iconProvider = new IconProvider(iconLibraryRegistry);
		this.sessionManager = new TeamAppsSessionManager(config, objectMapper, sessionExecutorFactory, webController, iconProvider, uploadManager, componentLibraryRegistry);
		this.webSocketCommunicationEndpoint = new WebSocketCommunicationEndpoint(sessionManager, config, objectMapper);
	}

	public TeamAppsConfiguration getConfig() {
		return config;
	}

	public WebController getWebController() {
		return webController;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public TeamAppsSessionManager getSessionManager() {
		return sessionManager;
	}

	public IconLibraryRegistry getIconLibraryRegistry() {
		return iconLibraryRegistry;
	}

	public IconProvider getIconProvider() {
		return iconProvider;
	}

	public TeamAppsUploadManager getUploadManager() {
		return uploadManager;
	}

	public WebSocketCommunicationEndpoint getWebSocketCommunicationEndpoint() {
		return webSocketCommunicationEndpoint;
	}

	public ComponentLibraryRegistry getComponentLibraryRegistry() {
		return componentLibraryRegistry;
	}
}
