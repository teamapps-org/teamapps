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
package org.teamapps.projector.server.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.teamapps.projector.server.config.ProjectorConfiguration;
import org.teamapps.icons.IconLibraryRegistry;
import org.teamapps.icons.IconProvider;
import org.teamapps.projector.server.json.ObjectMapperFactory;
import org.teamapps.projector.server.uisession.SessionManager;
import org.teamapps.projector.server.threading.SequentialExecutorFactory;
import org.teamapps.projector.clientobject.ComponentLibraryRegistry;
import org.teamapps.projector.server.servlet.WebSocketCommunicationEndpoint;
import org.teamapps.projector.server.webcontroller.WebController;

public class ProjectorServerCore {

	private final ProjectorConfiguration config;
	private final WebController webController;
	private final ObjectMapper objectMapper;
	private final SessionManager sessionManager;
	private final IconLibraryRegistry iconLibraryRegistry;
	private final IconProvider iconProvider;
	private final ComponentLibraryRegistry componentLibraryRegistry;
	private final UploadManager uploadManager;
	private final WebSocketCommunicationEndpoint webSocketCommunicationEndpoint;

	public ProjectorServerCore(ProjectorConfiguration config, SequentialExecutorFactory sessionExecutorFactory, WebController webController) {
		this.config = config;
		this.webController = webController;
		this.objectMapper = ObjectMapperFactory.create();
		this.iconLibraryRegistry = new IconLibraryRegistry();
		this.componentLibraryRegistry = new ComponentLibraryRegistry("/components/");
		this.uploadManager = new UploadManager();

		this.iconProvider = new IconProvider(iconLibraryRegistry);
		this.sessionManager = new SessionManager(config, objectMapper, sessionExecutorFactory, webController, iconProvider, uploadManager, componentLibraryRegistry);
		this.webSocketCommunicationEndpoint = new WebSocketCommunicationEndpoint(sessionManager, config, objectMapper);
	}

	public ProjectorConfiguration getConfig() {
		return config;
	}

	public WebController getWebController() {
		return webController;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public IconLibraryRegistry getIconLibraryRegistry() {
		return iconLibraryRegistry;
	}

	public IconProvider getIconProvider() {
		return iconProvider;
	}

	public UploadManager getUploadManager() {
		return uploadManager;
	}

	public WebSocketCommunicationEndpoint getWebSocketCommunicationEndpoint() {
		return webSocketCommunicationEndpoint;
	}

	public ComponentLibraryRegistry getComponentLibraryRegistry() {
		return componentLibraryRegistry;
	}
}
