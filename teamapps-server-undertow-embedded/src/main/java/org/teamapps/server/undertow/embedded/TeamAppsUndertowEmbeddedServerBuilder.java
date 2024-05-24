package org.teamapps.server.undertow.embedded;

import jakarta.servlet.ServletContextListener;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.core.TeamAppsCore;
import org.teamapps.threading.CompletableFutureChainSequentialExecutorFactory;
import org.teamapps.threading.SequentialExecutorFactory;
import org.teamapps.projector.resourceprovider.ClassPathResourceProvider;
import org.teamapps.projector.resourceprovider.DirectoryResolutionStrategy;
import org.teamapps.projector.resourceprovider.ResourceProvider;
import org.teamapps.webcontroller.WebController;

import java.util.ArrayList;
import java.util.List;

public class TeamAppsUndertowEmbeddedServerBuilder {

	private static final int DEFAULT_NUMBER_OF_SESSION_EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors() * 2;

	private final WebController webController;
	private int port = 8080;
	private TeamAppsConfiguration config = new TeamAppsConfiguration();
	private ResourceProvider baseResourceProvider = new ClassPathResourceProvider(
			"/org/teamapps/projector/core/resources",
			path -> {
				if (path.endsWith(".js") || path.endsWith(".mjs")) {
					return "text/javascript";
				} else {
					return null;
				}
			},
			DirectoryResolutionStrategy.index("index.html")
	);
	private SequentialExecutorFactory customSequentialExecutorFactory;
	private final List<ServletContextListener> servletContextListeners = new ArrayList<>();

	public TeamAppsUndertowEmbeddedServerBuilder(WebController webController) {
		this.webController = webController;
	}

	public TeamAppsUndertowEmbeddedServerBuilder withPort(int port) {
		this.port = port;
		return this;
	}

	public TeamAppsUndertowEmbeddedServerBuilder withConfig(TeamAppsConfiguration config) {
		this.config = config;
		return this;
	}

	public TeamAppsUndertowEmbeddedServerBuilder withBaseResourceProvider(ResourceProvider baseResourceProvider) {
		this.baseResourceProvider = baseResourceProvider;
		return this;
	}

	public TeamAppsUndertowEmbeddedServerBuilder withServletContextListener(ServletContextListener servletContextListener) {
		servletContextListeners.add(servletContextListener);
		return this;
	}

	public TeamAppsUndertowEmbeddedServerBuilder withSequentialExecutorFactory(SequentialExecutorFactory sequentialExecutorFactory) {
		this.customSequentialExecutorFactory = sequentialExecutorFactory;
		return this;
	}

	public TeamAppsUndertowEmbeddedServer build() {
		SequentialExecutorFactory executorFactory = customSequentialExecutorFactory != null ? customSequentialExecutorFactory
				: new CompletableFutureChainSequentialExecutorFactory(DEFAULT_NUMBER_OF_SESSION_EXECUTOR_THREADS);
		TeamAppsCore teamAppsCore = new TeamAppsCore(config, executorFactory, webController);
		return new TeamAppsUndertowEmbeddedServer(teamAppsCore, port, baseResourceProvider, servletContextListeners);
	}

}