package org.teamapps.ux.session.navigation;

import org.glassfish.jersey.uri.UriTemplate;
import org.teamapps.ux.session.SessionContext;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.teamapps.ux.session.navigation.NavigationHistoryOperation.PUSH;
import static org.teamapps.ux.session.navigation.NavigationHistoryOperation.REPLACE;
import static org.teamapps.ux.session.navigation.RoutingUtil.*;

public class Router {

	static final String PATH_REMAINDER_VARNAME = "_remainder";
	static final String PATH_REMAINDER_SUFFIX = "{" + PATH_REMAINDER_VARNAME + ":(/.*)?}";

	private final String pathPrefix;
	private final UriTemplate pathPrefixTemplate;
	private final UriTemplate pathPrefixExactTemplate;
	private final List<UriTemplateAndHandler> handlers = new CopyOnWriteArrayList<>(); // handler execution might add more handlers => prevent ConcurrentModificationException

	private String path;
	private NavigationHistoryOperation pathChangeOperation;
	private final Map<String, WithNavigationHistoryChangeOperation<String>> queryParams = new HashMap<>();

	private Supplier<String> pathSupplier;
	private NavigationHistoryOperation pathSupplierChangeOperation;

	private final Map<String, WithNavigationHistoryChangeOperation<Supplier<String>>> queryParameterSuppliers = new HashMap<>();
	private final List<WithNavigationHistoryChangeOperation<Supplier<Map<String, String>>>> queryParametersSuppliers = new ArrayList<>();

	private Supplier<Route> routeSupplier;
	private NavigationHistoryOperation routeSupplierChangeOperation;

	private final List<Runnable> routeHandlingChangeListeners = new ArrayList<>();

	public Router(String pathPrefix) {
		this.pathPrefix = normalizePath(pathPrefix);
		this.pathPrefixTemplate = createUriTemplate(this.pathPrefix, false);
		this.pathPrefixExactTemplate = createUriTemplate(this.pathPrefix, true);
	}

	private UriTemplate createUriTemplate(String path, boolean exact) {
		path = normalizePath(path);
		String templateString;
		if (exact) {
			templateString = path;
		} else {
			templateString = isEmptyPath(path) ? PATH_REMAINDER_SUFFIX : path + PATH_REMAINDER_SUFFIX;
		}
		return new UriTemplate(templateString);
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
		for (WithNavigationHistoryChangeOperation<Supplier<Map<String, String>>> supplierAndOperation : queryParametersSuppliers) {
			Map<String, String> queryParameters = supplierAndOperation.getValue().get();
			route = route.withQueryParams(queryParameters);
			if (supplierAndOperation.getNavigationHistoryOperation() == PUSH) {
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

	public Registration registerRouteHandler(String pathTemplate, RouteHandler handler) {
		return registerRouteHandler(pathTemplate, false, handler);
	}

	public Registration registerRouteHandler(String pathTemplate, boolean exact, RouteHandler handler) {
		var uriTemplate = createUriTemplate(pathTemplate, exact);
		UriTemplateAndHandler templateAndRouter = new UriTemplateAndHandler(uriTemplate, handler);
		handlers.add(templateAndRouter);
		fireRouteHandlingChange();
		return () -> handlers.remove(templateAndRouter);
	}

	public Registration registerRouteHandlers(Object annotatedClassInstance) {
		List<Registration> registrations = new AnnotationBasedRouteHandlerFactory(SessionContext.current().getRoutingParamConverterProvider())
				.createRouteHandlers(annotatedClassInstance).stream()
				.map(handler -> registerRouteHandler(handler.getPathTemplate(), handler.isExact(), handler))
				.collect(Collectors.toList());
		fireRouteHandlingChange();
		return () -> registrations.forEach(Registration::dispose);
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

	public Registration addQueryParametersSupplier(Supplier<Map<String, String>> supplier) {
		return addQueryParametersSupplier(supplier, PUSH);
	}

	public Registration addQueryParametersSupplier(Supplier<Map<String, String>> supplier, NavigationHistoryOperation changeOperation) {
		this.queryParametersSuppliers.add(new WithNavigationHistoryChangeOperation<>(supplier, changeOperation));
		return () -> {
			this.queryParametersSuppliers.removeIf(sao -> sao.getValue() == supplier && sao.getNavigationHistoryOperation() == changeOperation);
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

	public boolean matchesPath(String path) {
		return pathPrefixTemplate.match(path, new HashMap<>());
	}

	public boolean matchesPathPrefix(String pathPrefix) {
		return pathPrefixExactTemplate.match(pathPrefix, new HashMap<>());
	}

	public String getPathPrefix() {
		return pathPrefix;
	}

	public Registration addChangeListener(Runnable listener) {
		routeHandlingChangeListeners.add(listener);
		return () -> routeHandlingChangeListeners.remove(listener);
	}

	private void fireRouteHandlingChange() {
		routeHandlingChangeListeners.forEach(Runnable::run);
	}

	private static class UriTemplateAndHandler {
		private final UriTemplate uriTemplate;
		private final RouteHandler handler;

		public UriTemplateAndHandler(UriTemplate uriTemplate, RouteHandler handler) {
			this.uriTemplate = uriTemplate;
			this.handler = handler;
		}

		public UriTemplate getUriTemplate() {
			return uriTemplate;
		}

		public RouteHandler getHandler() {
			return handler;
		}
	}
}
