package org.teamapps.projector.server.jetty.embedded;

import jakarta.servlet.ServletContextListener;
import org.teamapps.server.config.ProjectorConfiguration;
import org.teamapps.server.core.ProjectorServerCore;
import org.teamapps.projector.resourceprovider.ClassPathResourceProvider;
import org.teamapps.projector.resourceprovider.DirectoryResolutionStrategy;
import org.teamapps.projector.resourceprovider.ResourceProvider;
import org.teamapps.server.threading.CompletableFutureChainSequentialExecutorFactory;
import org.teamapps.server.threading.SequentialExecutorFactory;
import org.teamapps.server.webcontroller.WebController;

import java.util.ArrayList;
import java.util.List;

public class ProjectorJettyEmbeddedServerBuilder {

	private static final int DEFAULT_NUMBER_OF_SESSION_EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors() * 2;

	private final WebController webController;
	private int port = 8080;
	private ProjectorConfiguration config = new ProjectorConfiguration();
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

	public ProjectorJettyEmbeddedServerBuilder(WebController webController) {
		this.webController = webController;
	}

	public ProjectorJettyEmbeddedServerBuilder withPort(int port) {
		this.port = port;
		return this;
	}

	public ProjectorJettyEmbeddedServerBuilder withConfig(ProjectorConfiguration config) {
		this.config = config;
		return this;
	}

	public ProjectorJettyEmbeddedServerBuilder withBaseResourceProvider(ResourceProvider baseResourceProvider) {
		this.baseResourceProvider = baseResourceProvider;
		return this;
	}

	public ProjectorJettyEmbeddedServerBuilder withServletContextListener(ServletContextListener servletContextListener) {
		servletContextListeners.add(servletContextListener);
		return this;
	}

	public ProjectorJettyEmbeddedServerBuilder withSequentialExecutorFactory(SequentialExecutorFactory sequentialExecutorFactory) {
		this.customSequentialExecutorFactory = sequentialExecutorFactory;
		return this;
	}

	public ProjectorJettyEmbeddedServer build() {
		SequentialExecutorFactory executorFactory = customSequentialExecutorFactory != null ? customSequentialExecutorFactory
				: new CompletableFutureChainSequentialExecutorFactory(DEFAULT_NUMBER_OF_SESSION_EXECUTOR_THREADS);
		ProjectorServerCore projectorServerCore = new ProjectorServerCore(config, executorFactory, webController);
		return new ProjectorJettyEmbeddedServer(projectorServerCore, port, baseResourceProvider, servletContextListeners);
	}

}
