/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.icons.IconLibraryRegistry;
import org.teamapps.icons.IconProvider;
import org.teamapps.json.TeamAppsObjectMapperFactory;
import org.teamapps.uisession.TeamAppsSessionManager;
import org.teamapps.util.threading.SequentialExecutorFactory;
import org.teamapps.ux.servlet.WebSocketCommunicationEndpoint;
import org.teamapps.webcontroller.WebController;

public class TeamAppsCore {

	private final TeamAppsConfiguration config;
	private final WebController webController;
	private final ObjectMapper objectMapper;
	private final TeamAppsSessionManager sessionManager;
	private final IconLibraryRegistry iconLibraryRegistry;
	private final TeamAppsUploadManager uploadManager;
	private final IconProvider iconProvider;
	private final WebSocketCommunicationEndpoint webSocketCommunicationEndpoint;

	public TeamAppsCore(TeamAppsConfiguration config, SequentialExecutorFactory sessionExecutorFactory, WebController webController) {
		this.config = config;
		this.webController = webController;
		this.objectMapper = TeamAppsObjectMapperFactory.create();
		this.iconLibraryRegistry = new IconLibraryRegistry();
		this.uploadManager = new TeamAppsUploadManager();

		this.iconProvider = new IconProvider(iconLibraryRegistry);
		this.sessionManager = new TeamAppsSessionManager(config, objectMapper, sessionExecutorFactory, webController, iconProvider, uploadManager);
		this.webSocketCommunicationEndpoint = new WebSocketCommunicationEndpoint(sessionManager, config);
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
}
