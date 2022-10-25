package org.teamapps.ux.session.navigation;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import org.apache.commons.lang3.StringUtils;
import org.teamapps.util.ReflectionUtil;

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
		Path classLevelPathAnnotation = annotatedClassInstance.getClass().getAnnotation(Path.class);
		String pathPrefix = classLevelPathAnnotation != null ? classLevelPathAnnotation.value() : "";
		List<Method> routingMethods = ReflectionUtil.findMethods(annotatedClassInstance.getClass(), method -> method.isAnnotationPresent(Path.class));
		return routingMethods.stream()
				.map(m -> createRoutingMethodInfo(m, pathPrefix))
				.map(routingMethodInfo -> new AnnotationBasedRoutingHandler(routingMethodInfo, annotatedClassInstance))
				.collect(Collectors.toList());
	}

	private RoutingMethodInfo createRoutingMethodInfo(Method m, String pathPrefix) {
		Path pathAnnotation = m.getAnnotation(Path.class);
		String pathTemplate = RoutingUtil.concatenatePaths(pathPrefix, pathAnnotation.value());

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
				methodParameterExtractors[i] = (path, pathParams, queryParams) -> {
					String paramValue = queryParams.get(queryParam.value());
					return StringUtils.isNotBlank(paramValue) ? converter.fromString(paramValue) : null;
				};
			} else {
				methodParameterExtractors[i] = (path, pathParams, queryParams) -> null;
			}
		}
		return new RoutingMethodInfo(pathTemplate, m, methodParameterExtractors);
	}

	interface ParameterValueExtractor {
		Object extract(String path, Map<String, String> pathParams, Map<String, String> queryParams);
	}

	private static class RoutingMethodInfo {
		private final String pathTemplate;
		private final Method method;
		private final ParameterValueExtractor[] methodParameterExtractors;

		public RoutingMethodInfo(String pathTemplate, Method method, ParameterValueExtractor[] methodParameterExtractors) {
			this.pathTemplate = pathTemplate;
			this.method = method;
			this.methodParameterExtractors = methodParameterExtractors;
		}

		public String getPathTemplate() {
			return pathTemplate;
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
	}
}
