package org.teamapps.ux.session.navigation;

import org.glassfish.jersey.uri.UriTemplate;
import org.teamapps.ux.session.SessionContext;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.teamapps.ux.session.navigation.NavigationHistoryOperation.PUSH;
import static org.teamapps.ux.session.navigation.NavigationHistoryOperation.REPLACE;
import static org.teamapps.ux.session.navigation.RoutingUtil.concatenatePaths;
import static org.teamapps.ux.session.navigation.RoutingUtil.normalizePath;

public class Router {

	static final String PATH_REMAINDER_VARNAME = "_remainder";
	static final String PATH_REMAINDER_SUFFIX = "{" + PATH_REMAINDER_VARNAME + ":(/.*)?}";

	private final String pathPrefix;
	private final List<UriTemplateAndHandler> handlers = new CopyOnWriteArrayList<>(); // handler execution might add more handlers => prevent ConcurrentModificationException

	private String path;
	private NavigationHistoryOperation pathChangeOperation;
	private final Map<String, WithNavigationHistoryChangeOperation<String>> queryParams = new HashMap<>();

	private Supplier<String> pathSupplier;
	private NavigationHistoryOperation pathSupplierChangeOperation;

	private final Map<String, WithNavigationHistoryChangeOperation<Supplier<String>>> queryParameterSuppliers = new HashMap<>();
	private Supplier<Map<String, String>> queryParametersSupplier;
	private NavigationHistoryOperation queryParametersSupplierChangeOperation;

	private Supplier<Route> routeSupplier;
	private NavigationHistoryOperation routeSupplierChangeOperation;

	public Router(String pathPrefix) {
		this.pathPrefix = normalizePath(pathPrefix);
	}

	public RouteInfo calculateRouteInfo() {
		Route route = Route.create();
		NavigationHistoryOperation pathChangeOperation = REPLACE;
		Set<String> queryParameterNamesTriggeringPush = new HashSet<>();
		if (routeSupplier != null) {
			route = routeSupplier.get();
			pathChangeOperation = routeSupplierChangeOperation;
			if (routeSupplierChangeOperation == PUSH) {
				queryParameterNamesTriggeringPush.addAll(route.getQueryParams().keySet());
			}
		}
		if (queryParametersSupplier != null) {
			Map<String, String> queryParameters = queryParametersSupplier.get();
			route = route.withQueryParams(queryParameters);
			if (queryParametersSupplierChangeOperation == PUSH) {
				queryParameterNamesTriggeringPush.addAll(queryParameters.keySet());
			} else {
				queryParameterNamesTriggeringPush.removeAll(queryParameters.keySet());
			}
		}
		for (var entry : queryParameterSuppliers.entrySet()) {
			String paramName = entry.getKey();
			String paramValue = entry.getValue().getValue().get();
			NavigationHistoryOperation changeOperation = entry.getValue().getNavigationHistoryOperation();
			route = route.withQueryParam(paramName, paramValue);
			if (changeOperation == PUSH) {
				queryParameterNamesTriggeringPush.add(paramName);
			} else {
				queryParameterNamesTriggeringPush.remove(paramName);
			}
		}
		if (pathSupplier != null) {
			String path = pathSupplier.get();
			route = route.withPath(path);
			pathChangeOperation = pathSupplierChangeOperation;
		}
		for (var entry : queryParams.entrySet()) {
			String paramName = entry.getKey();
			String paramValue = entry.getValue().getValue();
			NavigationHistoryOperation changeOperation = entry.getValue().getNavigationHistoryOperation();
			route = route.withQueryParam(paramName, paramValue);
			if (changeOperation == PUSH) {
				queryParameterNamesTriggeringPush.add(paramName);
			} else {
				queryParameterNamesTriggeringPush.remove(paramName);
			}
		}
		if (path != null) {
			route = route.withPath(path);
			pathChangeOperation = this.pathChangeOperation;
		}
		return new RouteInfo(route, pathChangeOperation == PUSH, queryParameterNamesTriggeringPush);
	}

	public Registration registerRoutingHandler(String pathTemplate, RoutingHandler handler) {
		return registerRoutingHandler(pathTemplate, false, handler);
	}

	public Registration registerRoutingHandler(String pathTemplate, boolean exact, RoutingHandler handler) {
		var uriTemplate = new UriTemplate(concatenatePaths(pathPrefix, pathTemplate) + (exact ? "" : PATH_REMAINDER_SUFFIX));
		UriTemplateAndHandler templateAndRouter = new UriTemplateAndHandler(uriTemplate, handler);
		handlers.add(templateAndRouter);
		return () -> handlers.remove(templateAndRouter);
	}

	public Map<String, Registration> registerRoutingHandlers(Object annotatedClassInstance) {
		return new AnnotationBasedRoutingHandlerFactory(SessionContext.current().getRoutingParamConverterProvider())
				.createRouters(annotatedClassInstance).stream()
				.collect(Collectors.toMap(
						handler -> handler.getPathTemplate(),
						handler -> registerRoutingHandler(handler.getPathTemplate(), handler.isExact(), handler))
				);
	}

	public Router getSubRouter(String relativePath) {
		return SessionContext.current().getRouter(concatenatePaths(pathPrefix, relativePath));
	}

	public void route(Route route) {
		for (UriTemplateAndHandler uriTemplateAndHandler : handlers) {
			HashMap<String, String> pathParams = new HashMap<>();
			boolean matches = uriTemplateAndHandler.getUriTemplate().match(route.getPath(), pathParams);
			if (matches) {
				uriTemplateAndHandler.getHandler().handle(route.getPath(), pathParams, route.getQueryParams());
			}
		}
	}

	public void setPath(String path) {
		setPath(path, PUSH);
	}

	public void setPath(String path, NavigationHistoryOperation changeOperation) {
		this.path = path;
		pathChangeOperation = changeOperation;
	}

	public void setQueryParameter(String parameterName, String value) {
		setQueryParameter(parameterName, value, PUSH);
	}

	public void setQueryParameter(String parameterName, String value, NavigationHistoryOperation changeOperation) {
		this.queryParams.put(parameterName, new WithNavigationHistoryChangeOperation<>(value, changeOperation));
	}

	public Registration setPathSupplier(Supplier<String> pathSupplier) {
		return setPathSupplier(pathSupplier, PUSH);
	}

	public Registration setPathSupplier(Supplier<String> pathSupplier, NavigationHistoryOperation changeOperation) {
		this.pathSupplier = pathSupplier;
		pathSupplierChangeOperation = changeOperation;
		return () -> {
			if (this.pathSupplier == pathSupplier) {
				this.pathSupplier = null;
			}
		};
	}

	public Registration setQueryParameterSupplier(String parameterName, Supplier<String> supplier) {
		return setQueryParameterSupplier(parameterName, supplier, PUSH);
	}

	public Registration setQueryParameterSupplier(String parameterName, Supplier<String> supplier, NavigationHistoryOperation changeOperation) {
		this.queryParameterSuppliers.put(parameterName, new WithNavigationHistoryChangeOperation<>(supplier, changeOperation));
		return () -> {
			if (this.queryParameterSuppliers.get(parameterName) != null && this.queryParameterSuppliers.get(parameterName).getValue() == supplier) {
				queryParameterSuppliers.remove(parameterName);
			}
		};
	}

	public Registration setQueryParametersSupplier(Supplier<Map<String, String>> supplier) {
		return setQueryParametersSupplier(supplier, PUSH);
	}

	public Registration setQueryParametersSupplier(Supplier<Map<String, String>> supplier, NavigationHistoryOperation changeOperation) {
		this.queryParametersSupplier = supplier;
		queryParametersSupplierChangeOperation = changeOperation;
		return () -> {
			if (this.queryParametersSupplier == supplier) {
				this.queryParametersSupplier = null;
			}
		};
	}

	public Registration setRouteSupplier(Supplier<Route> supplier) {
		return setRouteSupplier(supplier, PUSH);
	}

	public Registration setRouteSupplier(Supplier<Route> supplier, NavigationHistoryOperation changeOperation) {
		this.routeSupplier = supplier;
		this.routeSupplierChangeOperation = changeOperation;
		return () -> {
			if (this.routeSupplier == supplier) {
				this.routeSupplier = null;
			}
		};
	}

	private static class UriTemplateAndHandler {
		private final UriTemplate uriTemplate;
		private final RoutingHandler handler;

		public UriTemplateAndHandler(UriTemplate uriTemplate, RoutingHandler handler) {
			this.uriTemplate = uriTemplate;
			this.handler = handler;
		}

		public UriTemplate getUriTemplate() {
			return uriTemplate;
		}

		public RoutingHandler getHandler() {
			return handler;
		}
	}
}
