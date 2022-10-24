package org.teamapps.ux.session;

import jakarta.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.uri.UriTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.ux.session.navigation.*;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.teamapps.ux.session.navigation.RoutingUtil.concatenatePaths;
import static org.teamapps.ux.session.navigation.RoutingUtil.normalizePathPrefix;

class BaseRouting implements RouterRegistrationPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final String pathPrefix;
	private final ParamConverterProvider paramConverterProvider;
	private final List<UriTemplateAndRouter> routers = new ArrayList<>();
	private Location currentLocation;

	public BaseRouting(String pathPrefix, ParamConverterProvider paramConverterProvider) {
		this.pathPrefix = normalizePathPrefix(pathPrefix);
		this.paramConverterProvider = paramConverterProvider;
	}

	@Override
	public RouterRegistration registerRouter(String pathTemplate, Router router, boolean applyImmediately) {
		var uriTemplate = new UriTemplate(pathTemplate);
		UriTemplateAndRouter templateAndRouter = new UriTemplateAndRouter(uriTemplate, router);
		routers.add(templateAndRouter);
		if (applyImmediately) {
			applyRoutersIfMatching(currentLocation, List.of(templateAndRouter));
		}
		return new RouterRegistration() {
			@Override
			public String createPath(Map<String, String> params) {
				return uriTemplate.createURI(params);
			}

			@Override
			public void dispose() {
				routers.remove(templateAndRouter);
			}
		};
	}

	@Override
	public RouterRegistrationPoint createSubRouterRegistrationPoint(String relativePath) {
		return new RelativeRouterRegistrationPoint(concatenatePaths(pathPrefix, relativePath));
	}

	public void route(Location location) {
		this.currentLocation = location;
		applyRoutersIfMatching(location, routers);
	}

	private void applyRoutersIfMatching(Location location, List<UriTemplateAndRouter> routers) {
		String path = location.getPathname();
		if (!path.startsWith(pathPrefix)) {
			LOGGER.warn("path does not start with prefix '{}'. Will not route!", pathPrefix);
		}
		String relativePath = pathPrefix.equals("/") ? path : path.substring(pathPrefix.length());

		Map<String, String> queryParams = RoutingUtil.parseQueryParams(location.getSearch());
		for (UriTemplateAndRouter uriTemplateAndRouter : routers) {
			HashMap<String, String> pathParams = new HashMap<>();
			boolean matches = uriTemplateAndRouter.getUriTemplate().match(relativePath, pathParams);
			if (matches) {
				uriTemplateAndRouter.getRouter().route(relativePath, pathParams, queryParams);
			}
		}
	}

	public ParamConverterProvider getParamConverterProvider() {
		return paramConverterProvider;
	}

	private static class UriTemplateAndRouter {
		private final UriTemplate uriTemplate;
		private final Router router;

		public UriTemplateAndRouter(UriTemplate uriTemplate, Router router) {
			this.uriTemplate = uriTemplate;
			this.router = router;
		}

		public UriTemplate getUriTemplate() {
			return uriTemplate;
		}

		public Router getRouter() {
			return router;
		}
	}
}
