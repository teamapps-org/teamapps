package org.teamapps.ux.session.navigation;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import org.apache.commons.lang3.StringUtils;
import org.teamapps.util.ReflectionUtil;
import org.teamapps.ux.session.navigation.annotation.PathParameter;
import org.teamapps.ux.session.navigation.annotation.QueryParameter;
import org.teamapps.ux.session.navigation.annotation.RoutingPath;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnnotationBasedRoutingHandlerFactory {

	private final ParamConverterProvider converterProvider;

	public AnnotationBasedRoutingHandlerFactory() {
		this(new ParameterConverterProvider());
	}

	public AnnotationBasedRoutingHandlerFactory(ParamConverterProvider converterProvider) {
		this.converterProvider = converterProvider;
	}

	public List<AnnotationBasedRoutingHandler> createRouters(Object annotatedClassInstance) {
		RoutingPath classLevelPathAnnotation = annotatedClassInstance.getClass().getAnnotation(RoutingPath.class);
		String pathPrefix = classLevelPathAnnotation != null ? classLevelPathAnnotation.value() : "";
		List<Method> routingMethods = ReflectionUtil.findMethods(annotatedClassInstance.getClass(), method -> method.isAnnotationPresent(RoutingPath.class));
		return routingMethods.stream()
				.map(m -> createRoutingMethodInfo(m, pathPrefix))
				.map(routingMethodInfo -> new AnnotationBasedRoutingHandler(routingMethodInfo, annotatedClassInstance))
				.collect(Collectors.toList());
	}

	private RoutingMethodInfo createRoutingMethodInfo(Method m, String pathPrefix) {
		RoutingPath pathAnnotation = m.getAnnotation(RoutingPath.class);
		String pathTemplate = RoutingUtil.concatenatePaths(pathPrefix, pathAnnotation.value());

		Class<?>[] parameterTypes = m.getParameterTypes();
		Type[] genericParameterTypes = m.getGenericParameterTypes();
		Parameter[] methodParameters = m.getParameters();
		ParameterValueExtractor[] methodParameterExtractors = new ParameterValueExtractor[methodParameters.length];
		for (int i = 0; i < methodParameters.length; i++) {
			Parameter parameter = methodParameters[i];
			PathParameter pathParam = parameter.getAnnotation(PathParameter.class);
			QueryParameter queryParam = parameter.getAnnotation(QueryParameter.class);
			ParamConverter<?> converter = converterProvider.getConverter(parameterTypes[i], genericParameterTypes[i], parameter.getAnnotations());

			if (pathParam != null) {
				methodParameterExtractors[i] = (path, pathParams, queryParams) -> converter.fromString(pathParams.get(pathParam.value()));
			} else if (queryParam != null) {
				methodParameterExtractors[i] = (path, pathParams, queryParams) -> {
					String paramValue = queryParams.get(queryParam.value());
					return StringUtils.isNotBlank(paramValue) ? converter.fromString(paramValue) : null;
				};
			} else {
				methodParameterExtractors[i] = (path, pathParams, queryParams) -> null;
			}
		}
		return new RoutingMethodInfo(pathTemplate, pathAnnotation.exact(), m, methodParameterExtractors);
	}

	interface ParameterValueExtractor {
		Object extract(String path, Map<String, String> pathParams, Map<String, String> queryParams);
	}

	private static class RoutingMethodInfo {
		private final String pathTemplate;
		private final boolean exact;
		private final Method method;
		private final ParameterValueExtractor[] methodParameterExtractors;

		public RoutingMethodInfo(String pathTemplate, boolean exact, Method method, ParameterValueExtractor[] methodParameterExtractors) {
			this.pathTemplate = pathTemplate;
			this.exact = exact;
			this.method = method;
			this.methodParameterExtractors = methodParameterExtractors;
		}

		public String getPathTemplate() {
			return pathTemplate;
		}

		public boolean isExact() {
			return exact;
		}

		public Method getMethod() {
			return method;
		}

		public ParameterValueExtractor[] getMethodParameterExtractors() {
			return methodParameterExtractors;
		}
	}

	public static class AnnotationBasedRoutingHandler implements RoutingHandler {
		private final RoutingMethodInfo routingMethodInfo;
		private final Object annotatedClassInstance;

		public AnnotationBasedRoutingHandler(RoutingMethodInfo routingMethodInfo, Object annotatedClassInstance) {
			this.routingMethodInfo = routingMethodInfo;
			this.annotatedClassInstance = annotatedClassInstance;
		}

		@Override
		public void handle(String path, Map<String, String> pathParams, Map<String, String> queryParams) {
			Object[] argumentValues = Arrays.stream(routingMethodInfo.getMethodParameterExtractors())
					.map(extractor -> extractor.extract(path, pathParams, queryParams))
					.toArray();
			try {
				routingMethodInfo.getMethod().invoke(annotatedClassInstance, argumentValues);
			} catch (Exception e) {
				throw new RuntimeException(e); // TODO more specific exception class here
			}
		}

		public String getPathTemplate() {
			return routingMethodInfo.getPathTemplate();
		}

		public boolean isExact() {
			return routingMethodInfo.isExact();
		}
	}
}
