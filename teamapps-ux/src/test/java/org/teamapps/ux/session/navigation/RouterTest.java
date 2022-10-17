package org.teamapps.ux.session.navigation;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.internal.inject.ParamConverters;
import org.glassfish.jersey.uri.UriTemplate;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RouterTest {

	@Test
	public void name() {
		String path = "/foos/foo/bars/12223/23asf/sdf";

		Map<String, String> map = new HashMap<>();
		UriTemplate template = new UriTemplate("/foos/{x}/bars/{y:\\d+}/{_remainder:.*}");
		if (template.match(path, map)) {
			System.out.println("Matched, " + map);
		} else {
			System.out.println("Not matched, " + map);
		}

	}

	@Test
	public void queryParams() {

	}

	@Test
	public void testConverterProvider() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

		Method m = RouterTest.class.getMethod("x", Integer.class);

		Path pathAnnotation = m.getAnnotation(Path.class);
		UriTemplate template = new UriTemplate(pathAnnotation.value());

		String path = "/asdf/123";

		// TODO parse query paramters
		Map<String, String> queryParams = Map.of();

		Map<String, String> parametersMap = new HashMap<>();
		template.match(path, parametersMap);

		Class<?>[] parameterTypes = m.getParameterTypes();
		Type[] genericParameterTypes = m.getGenericParameterTypes();
		AggregatedConverterProvider converterProvider = new AggregatedConverterProvider();

		LocationParameters parameters = new LocationParameters(path, parametersMap, queryParams);

		Parameter[] methodParameters = m.getParameters();
		Object[] methodParameterValues = new Object[methodParameters.length];
		for (int i = 0; i < methodParameters.length; i++) {
			Parameter parameter = methodParameters[i];
			PathParam pathParam = parameter.getAnnotation(PathParam.class);
			QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
			String stringValue = null;
			if (pathParam != null) {
				stringValue = parameters.getPathParameters().get(pathParam.value());
			} else if (queryParam != null) {
				stringValue = parameters.getQueryParameters().get(queryParam.value());
			}
			if (stringValue != null) {
				ParamConverter<?> converter = converterProvider.getConverter(parameterTypes[i], genericParameterTypes[i], parameter.getAnnotations());
				Object paramValue = converter.fromString(stringValue);
				methodParameterValues[i] = paramValue;
			}
		}

		m.invoke(new RouterTest(), methodParameterValues);

	}

	public static class LocationParameters {
		private final String path;
		private final Map<String, String> pathParameters;
		private final Map<String, String> queryParameters;

		LocationParameters(String path, Map<String, String> pathParameters, Map<String, String> queryParameters) {
			this.path = path;
			this.pathParameters = pathParameters;
			this.queryParameters = queryParameters;
		}

		public String getPath() {
			return path;
		}

		public Map<String, String> getPathParameters() {
			return pathParameters;
		}

		public Map<String, String> getQueryParameters() {
			return queryParameters;
		}
	}

	@Path("/asdf/{a}")
	public void x(@PathParam("a") Integer a) {
		System.out.println(a);
	}


	public static class AggregatedConverterProvider implements ParamConverterProvider {

		private final ParamConverterProvider[] providers;

		@Inject
		public AggregatedConverterProvider() {
			this.providers = new ParamConverterProvider[]{
					// ordering is important (e.g. Date provider must be executed before String Constructor
					// as Date has a deprecated String constructor
					new ParamConverters.DateProvider(),
					new ParamConverters.TypeFromStringEnum(),
					new ParamConverters.TypeValueOf(),
					new ParamConverters.CharacterProvider(),
					new ParamConverters.TypeFromString(),
					new ParamConverters.StringConstructor(),
					new ParamConverters.OptionalProvider()
			};
		}

		@Override
		public <T> ParamConverter<T> getConverter(final Class<T> rawType,
												  final Type genericType,
												  final Annotation[] annotations) {
			for (final ParamConverterProvider p : providers) {
				// This iteration trough providers is important. It can't be replaced by just registering all the internal
				// providers of this class. Using iteration trough array the correct ordering of providers is ensured (see
				// javadoc of PathParam, HeaderParam, ... - there is defined a fixed order of constructing objects form Strings).
				final ParamConverter<T> reader = p.getConverter(rawType, genericType, annotations);
				if (reader != null) {
					return reader;
				}
			}
			return null;
		}
	}

}