/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.UiClientInfo;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiSessionClosingReason;
import org.teamapps.icons.provider.IconProvider;
import org.teamapps.server.ServletRegistration;
import org.teamapps.server.SessionRecorder;
import org.teamapps.server.SessionResourceProvider;
import org.teamapps.server.UxServerContext;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.uisession.UiCommandExecutor;
import org.teamapps.uisession.UiSessionListener;
import org.teamapps.ux.component.ClientObject;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.resource.ResourceProviderServlet;
import org.teamapps.ux.resource.SystemIconResourceProvider;
import org.teamapps.ux.session.ClientInfo;
import org.teamapps.ux.session.ClientSessionResourceProvider;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.webcontroller.WebController;

import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TeamAppsUxClientGate implements UiSessionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamAppsUxClientGate.class);

	private final WebController webController;
	private final UiCommandExecutor commandExecutor;

	private final ObjectMapper objectMapper;
	private Map<QualifiedUiSessionId, SessionContext> sessionContextById = new ConcurrentHashMap<>();
	private Map<String, File> uploadedFilesByUuid = new ConcurrentHashMap<>();

	private final UxServerContext uxServerContext = new UxServerContext() {
		@Override
		public SessionContext getSessionContextById(QualifiedUiSessionId sessionId) {
			return sessionContextById.get(sessionId);
		}

		@Override
		public File getUploadedFileByUuid(String uuid) {
			return uploadedFilesByUuid.get(uuid);
		}
	};

	private String userSessionCommandsRecordingPath;

	public TeamAppsUxClientGate(WebController webController, UiCommandExecutor commandExecutor, ObjectMapper objectMapper) {
		this.webController = webController;
		this.commandExecutor = commandExecutor;
		this.objectMapper = objectMapper;


	}

	@Override
	public void onUiSessionStarted(QualifiedUiSessionId sessionId, UiClientInfo uiClientInfo, HttpSession httpSession) {
		SessionRecorder sessionRecorder = null;
		if (userSessionCommandsRecordingPath != null) {
			try {
				String timeStamp = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(new Date());
				File outputFile = new File(userSessionCommandsRecordingPath, "Session-" + timeStamp + ".log");
				sessionRecorder = new SessionRecorder(objectMapper, new BufferedOutputStream(new FileOutputStream(outputFile)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		ClientInfo clientInfo = new ClientInfo(
				uiClientInfo.getIp(),
				uiClientInfo.getScreenWidth(),
				uiClientInfo.getScreenHeight(),
				uiClientInfo.getViewPortWidth(),
				uiClientInfo.getViewPortHeight(),
				uiClientInfo.getPreferredLanguageIso(),
				uiClientInfo.getHighDensityScreen(),
				uiClientInfo.getTimezoneIana(),
				uiClientInfo.getTimezoneOffsetMinutes(),
				uiClientInfo.getClientTokens(),
				uiClientInfo.getUserAgentString(),
				uiClientInfo.getClientUrl(),
				uiClientInfo.getClientParameters());

		SessionContext context = new SessionContext(sessionId, clientInfo, httpSession, commandExecutor, uxServerContext,
				webController.getDefaultIconTheme(clientInfo.isMobileDevice()), objectMapper);
		sessionContextById.put(sessionId, context);

		CompletableFuture<Void> future = context.runWithContext(() -> {
			context.setConfiguration(webController.createSessionConfiguration(context));
			context.registerTemplates(Arrays.stream(BaseTemplate.values())
					.collect(Collectors.toMap(Enum::name, BaseTemplate::getTemplate)));
			webController.onSessionStart(context);
		});

		try {
			// TODO make non-blocking when exception handling (and thereby session invalidation) is changed
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}


	public Collection<ServletRegistration> getServletRegistrations() {
		ArrayList<ServletRegistration> registrations = new ArrayList<>();
		registrations.add(new ServletRegistration(new ResourceProviderServlet(createSystemIconResourceProvider(webController.getIconProvider(), webController.getAdditionalIconProvider())), "/icons"
				+ "/*"));
		registrations.add(new ServletRegistration(new ResourceProviderServlet(new SessionResourceProvider(sessionContextById::get)), ClientSessionResourceProvider.BASE_PATH + "*"));
		registrations.addAll(webController.getServletRegistrations(uxServerContext));
		return registrations;
	}

	private SystemIconResourceProvider createSystemIconResourceProvider(IconProvider iconProvider, List<IconProvider> customIconProvider) {
		try {
			File tempDir = File.createTempFile("temp", "temp").getParentFile();
			SystemIconResourceProvider systemIconProvider = new SystemIconResourceProvider(tempDir);
			systemIconProvider.registerStandardIconProvider(iconProvider);
			if (customIconProvider != null) {
				customIconProvider.forEach(provider -> systemIconProvider.registerCustomIconProvider(provider));
			}
			return systemIconProvider;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void onUiSessionClientRefresh(QualifiedUiSessionId sessionId, UiClientInfo clientInfo, HttpSession httpSession) {
		this.onUiSessionStarted(sessionId, clientInfo, httpSession);
	}

	@Override
	public void onActivityStateChanged(QualifiedUiSessionId sessionId, boolean active) {
		SessionContext context = sessionContextById.get(sessionId);
		if (context != null) {
			context.handleActivityStateChangedInternal(active);
		}
	}

	@Override
	public void onUiSessionClosed(QualifiedUiSessionId sessionId, UiSessionClosingReason reason) {
		SessionContext context = sessionContextById.remove(sessionId);
		if (context != null) {
			context.handleSessionDestroyedInternal();
		}
	}

	@Override
	public void onUiEvent(QualifiedUiSessionId sessionId, UiEvent event) {
		SessionContext sessionContext = sessionContextById.get(sessionId);
		if (sessionContext != null) {
			sessionContext.runWithContext(() -> {
				String uiComponentId = event.getComponentId();
				ClientObject clientObject = sessionContext.getClientObject(uiComponentId);
				if (clientObject != null) {
					clientObject.handleUiEvent(event);
				}
			}).exceptionally(e -> {
				LOGGER.error("Exception while handling ui event", e);
				commandExecutor.closeSession(sessionId, UiSessionClosingReason.SERVER_SIDE_ERROR);
				return null;
			});
		}
	}

	public void handleFileUpload(File file, String uuid) {
		this.uploadedFilesByUuid.put(uuid, file);
	}

	public SessionContext getSessionContextById(QualifiedUiSessionId qualifiedUiSessionId) {
		return sessionContextById.get(qualifiedUiSessionId);
	}

	public String getUserSessionCommandsRecordingPath() {
		return userSessionCommandsRecordingPath;
	}

	public void setUserSessionCommandsRecordingPath(String userSessionCommandsRecordingPath) {
		this.userSessionCommandsRecordingPath = userSessionCommandsRecordingPath;
	}
}
