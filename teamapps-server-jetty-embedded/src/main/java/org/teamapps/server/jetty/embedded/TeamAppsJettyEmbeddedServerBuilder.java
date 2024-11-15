package org.teamapps.server.jetty.embedded;

import jakarta.servlet.ServletContextListener;
import org.teamapps.server.config.TeamAppsConfiguration;
import org.teamapps.server.core.TeamAppsServerCore;
import org.teamapps.projector.resourceprovider.ClassPathResourceProvider;
import org.teamapps.projector.resourceprovider.DirectoryResolutionStrategy;
import org.teamapps.projector.resourceprovider.ResourceProvider;
import org.teamapps.server.threading.CompletableFutureChainSequentialExecutorFactory;
import org.teamapps.server.threading.SequentialExecutorFactory;
import org.teamapps.server.webcontroller.WebController;

import java.util.ArrayList;
import java.util.List;

public class TeamAppsJettyEmbeddedServerBuilder {

	private static final int DEFAULT_NUMBER_OF_SESSION_EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors() * 2;

	private final WebController webController;
	private int port = 8080;
	private TeamAppsConfiguration config = new TeamAppsConfiguration();
	private ResourceProvider baseResourceProvider = new ClassPathResourceProvider(
			"org/teamapps/projector/core/resources",
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

	public TeamAppsJettyEmbeddedServerBuilder(WebController webController) {
		this.webController = webController;
	}

	public TeamAppsJettyEmbeddedServerBuilder withPort(int port) {
		this.port = port;
		return this;
	}

	public TeamAppsJettyEmbeddedServerBuilder withConfig(TeamAppsConfiguration config) {
		this.config = config;
		return this;
	}

	public TeamAppsJettyEmbeddedServerBuilder withBaseResourceProvider(ResourceProvider baseResourceProvider) {
		this.baseResourceProvider = baseResourceProvider;
		return this;
	}

	public TeamAppsJettyEmbeddedServerBuilder withServletContextListener(ServletContextListener servletContextListener) {
		servletContextListeners.add(servletContextListener);
		return this;
	}

	public TeamAppsJettyEmbeddedServerBuilder withSequentialExecutorFactory(SequentialExecutorFactory sequentialExecutorFactory) {
		this.customSequentialExecutorFactory = sequentialExecutorFactory;
		return this;
	}

	public TeamAppsJettyEmbeddedServer build() {
		SequentialExecutorFactory executorFactory = customSequentialExecutorFactory != null ? customSequentialExecutorFactory
				: new CompletableFutureChainSequentialExecutorFactory(DEFAULT_NUMBER_OF_SESSION_EXECUTOR_THREADS);
		TeamAppsServerCore teamAppsServerCore = new TeamAppsServerCore(config, executorFactory, webController);
		return new TeamAppsJettyEmbeddedServer(teamAppsServerCore, port, baseResourceProvider, servletContextListeners);
	}

}