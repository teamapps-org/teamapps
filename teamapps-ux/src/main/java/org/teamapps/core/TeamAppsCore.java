package org.teamapps.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.icons.IconLibraryRegistry;
import org.teamapps.json.TeamAppsObjectMapperFactory;
import org.teamapps.uisession.TeamAppsUiSessionManager;
import org.teamapps.webcontroller.WebController;

public class TeamAppsCore {

	private final TeamAppsConfiguration config;
	private final WebController webController;
	private final ObjectMapper objectMapper;
	private final TeamAppsUiSessionManager uiSessionManager;
	private final IconLibraryRegistry iconLibraryRegistry;
	private final TeamAppsUxSessionManager sessionManager;
	private final TeamAppsUploadManager uploadManager;

	public TeamAppsCore(TeamAppsConfiguration config, WebController webController) {
		this.config = config;
		this.webController = webController;
		this.objectMapper = TeamAppsObjectMapperFactory.create();
		this.iconLibraryRegistry = new IconLibraryRegistry();
		this.uploadManager = new TeamAppsUploadManager();

		this.uiSessionManager = new TeamAppsUiSessionManager(config, objectMapper);
		this.sessionManager = new TeamAppsUxSessionManager(webController, uiSessionManager, objectMapper, iconLibraryRegistry, uploadManager);
		this.uiSessionManager.setUiSessionListener(sessionManager);
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

	public TeamAppsUiSessionManager getUiSessionManager() {
		return uiSessionManager;
	}

	public IconLibraryRegistry getIconLibraryRegistry() {
		return iconLibraryRegistry;
	}

	public TeamAppsUxSessionManager getSessionManager() {
		return sessionManager;
	}

	public TeamAppsUploadManager getUploadManager() {
		return uploadManager;
	}
}
