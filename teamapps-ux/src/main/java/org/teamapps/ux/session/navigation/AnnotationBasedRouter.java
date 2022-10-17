package org.teamapps.ux.session.navigation;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.uri.UriTemplate;
import org.teamapps.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class AnnotationBasedRouter implements HierarchicalRouter {

	private static final String REMAINDER_VARNAME = "_remainder";
	private static final String REMAINDER_SUFFIX = "{" + REMAINDER_VARNAME + ":.*}";

	private final List<Router> subRouters = new ArrayList<>();
	private final List<RoutingMethodInfo> routingMethodInfos = new ArrayList<>();
	private final boolean wildcardSuffix;

	public AnnotationBasedRouter() {
		this(new ParameterConverterProvider(), true);
	}

	public AnnotationBasedRouter(ParamConverterProvider converterProvider) {
		this(converterProvider, true);
	}

	public AnnotationBasedRouter(ParamConverterProvider converterProvider, boolean wildcardSuffix) {
		this.wildcardSuffix = wildcardSuffix;
		List<Method> routingMethods = ReflectionUtil.findMethods(this.getClass(), method -> method.isAnnotationPresent(Path.class));
		for (Method m : routingMethods) {
			createRoutingMethodInfo(converterProvider, m);
		}
	}

	private void createRoutingMethodInfo(ParamConverterProvider converterProvider, Method m) {
		Path pathAnnotation = m.getAnnotation(Path.class);
		String pathTemplateString = pathAnnotation.value();
		if (wildcardSuffix) {
			pathTemplateString = pathTemplateString + REMAINDER_SUFFIX;
		}
		UriTemplate uriTemplate = new UriTemplate(pathTemplateString);

		Class<?>[] parameterTypes = m.getParameterTypes();
		Type[] genericParameterTypes = m.getGenericParameterTypes();
		Parameter[] methodParameters = m.getParameters();
		ParameterValueExtractor[] methodParameterExtractors = new ParameterValueExtractor[methodParameters.length];
		for (int i = 0; i < methodParameters.length; i++) {
			Parameter parameter = methodParameters[i];
			PathParam pathParam = parameter.getAnnotation(PathParam.class);
			QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
			ParamConverter<?> converter = converterProvider.getConverter(parameterTypes[i], genericParameterTypes[i], parameter.getAnnotations());

			if (pathParam != null) {
				methodParameterExtractors[i] = (path, pathParams, queryParams) -> converter.fromString(pathParams.get(pathParam.value()));
			} else if (queryParam != null) {
				methodParameterExtractors[i] = (path, pathParams, queryParams) -> converter.fromString(queryParams.get(queryParam.value()));
			} else {
				methodParameterExtractors[i] = (path, pathParams, queryParams) -> null;
			}
		}
		this.routingMethodInfos.add(new RoutingMethodInfo(m, uriTemplate, methodParameterExtractors));
	}

	@Override
	public boolean route(String path, Map<String, String> queryParams) {
		for (RoutingMethodInfo routingMethodInfo : routingMethodInfos) {
			RoutingUtil.MatchingResult result = RoutingUtil.match(routingMethodInfo.getUriTemplate(), path);
			if (result.isMatch()) {
				String pathRemainder = result.getPathParams().remove(REMAINDER_VARNAME);

				invokeRoutingMethod(routingMethodInfo, path, result.getPathParams(), queryParams);

				if (StringUtils.isNotBlank(pathRemainder) && !pathRemainder.startsWith("/")) {
					pathRemainder = "/" + pathRemainder;
				}
				invokeSubRouters(queryParams, pathRemainder);
				return true;
			}
		}
		return false;
	}

	protected void invokeRoutingMethod(RoutingMethodInfo routingMethodInfo, String path, Map<String, String> pathParams, Map<String, String> queryParams) {
		Object[] argumentValues = Arrays.stream(routingMethodInfo.methodParameterExtractors)
				.map(extractor -> extractor.extract(path, pathParams, queryParams))
				.toArray();
		try {
			routingMethodInfo.method.invoke(this, argumentValues);
		} catch (Exception e) {
			throw new RuntimeException(e); // TODO more specific exception class here
		}
	}

	private void invokeSubRouters(Map<String, String> queryParams, String pathRemainder) {
		if (StringUtils.isNotBlank(pathRemainder)) {
			for (Router subRouter : subRouters) {
				boolean matched = subRouter.route(pathRemainder, queryParams);
				if (matched) {
					break;
				}
			}
		}
	}

	@Override
	public void addSubRouter(Router router) {
		subRouters.add(router);
	}

	public List<Router> getSubRouters() {
		return List.copyOf(subRouters);
	}

	interface ParameterValueExtractor {
		Object extract(String path, Map<String, String> pathParams, Map<String, String> queryParams);
	}

	private static class RoutingMethodInfo {
		private final Method method;
		private final UriTemplate uriTemplate;
		private final ParameterValueExtractor[] methodParameterExtractors;

		public RoutingMethodInfo(Method method, UriTemplate uriTemplate, ParameterValueExtractor[] methodParameterExtractors) {
			this.method = method;
			this.uriTemplate = uriTemplate;
			this.methodParameterExtractors = methodParameterExtractors;
		}

		public Method getMethod() {
			return method;
		}

		public UriTemplate getUriTemplate() {
			return uriTemplate;
		}

		public ParameterValueExtractor[] getMethodParameterExtractors() {
			return methodParameterExtractors;
		}
	}
}
