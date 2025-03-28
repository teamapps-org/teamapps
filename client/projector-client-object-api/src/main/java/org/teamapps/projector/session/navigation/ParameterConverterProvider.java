/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.projector.session.navigation;

import jakarta.inject.Inject;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.internal.inject.ParamConverters;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParameterConverterProvider implements ParamConverterProvider {

	private final List<ParamConverterProvider> providers;

	@Inject
	public ParameterConverterProvider() {
		try {
			this.providers = new ArrayList<>(List.of(
					// ordering is important (e.g. Date provider must be executed before String Constructor
					// as Date has a deprecated String constructor
					createInternalJerseyParameterConverter(ParamConverters.DateProvider.class),
					createInternalJerseyParameterConverter(ParamConverters.TypeFromStringEnum.class),
					createInternalJerseyParameterConverter(ParamConverters.TypeValueOf.class),
					createInternalJerseyParameterConverter(ParamConverters.CharacterProvider.class),
					createInternalJerseyParameterConverter(ParamConverters.TypeFromString.class),
					createInternalJerseyParameterConverter(ParamConverters.StringConstructor.class),
					//OptionalCustomProvider intentionally omitted
					createInternalJerseyParameterConverter(ParamConverters.OptionalProvider.class),
					new ListParamConverterProvider()
			));
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
    }

	/**
	 * Most (but not all) of the ParameterConverters in Jersey 3.0.11+ and 3.1.x now have a private constructor
	 * and a configurable toggle specified by property {@code "jersey.config.paramconverters.throw.iae"}.
	 * This method uses reflection to create an instance of the specified converter with a default
	 * value of "false" for the toggle.
	 *
	 * @param paramConverterClass class to instantiate
	 * @return instantiated ParameterConverterProvider
	 * @throws NoSuchMethodException     when the instantiation fails
	 * @throws InvocationTargetException when the instantiation fails
	 * @throws InstantiationException    when the instantiation fails
	 * @throws IllegalAccessException    when the instantiation fails
	 */
	private static ParamConverterProvider createInternalJerseyParameterConverter(Class<? extends ParamConverterProvider> paramConverterClass)
			throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		//noinspection unchecked
		Constructor<? extends ParamConverterProvider>[] declaredConstructors = (Constructor<? extends ParamConverterProvider>[]) paramConverterClass.getDeclaredConstructors();
		Optional<Constructor<? extends ParamConverterProvider>> noArgConstructor = Arrays.stream(declaredConstructors).filter(constructor -> constructor.getParameterCount() == 0).findFirst();
		if (noArgConstructor.isPresent()) {
			Constructor<? extends ParamConverterProvider> constructor = noArgConstructor.get();
			if (!Modifier.isPublic(constructor.getModifiers())) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance();
		}
		Constructor<? extends ParamConverterProvider> oneArgConstructor = Arrays.stream(declaredConstructors)
				.filter(constructor -> Arrays.equals(constructor.getParameterTypes(), new Class[]{boolean.class}))
				.findFirst()
				.orElseThrow(InstantiationError::new);
		if (!Modifier.isPublic(oneArgConstructor.getModifiers())) {
			oneArgConstructor.setAccessible(true);
		}
		return oneArgConstructor.newInstance(false); //"false" is the default of "jersey.config.paramconverters.throw.iae" in Jersey 3.1.3
	}

	@Override
	public <T> ParamConverter<T> getConverter(final Class<T> rawType,
											  final Type genericType,
											  final Annotation[] annotations) {
		for (final ParamConverterProvider p : providers) {
			final ParamConverter<T> reader = p.getConverter(rawType, genericType, annotations);
			if (reader != null) {
				return reader;
			}
		}
		return null;
	}

	public void addConverterProvider(ParamConverterProvider converterProvider) {
		providers.add(0, converterProvider);
	}

	public <T> void addConverter(Class<T> clazz, ParamConverter<T> converter) {
		providers.add(0, new ParameterConverterProvider() {
			@Override
			public <X> ParamConverter<X> getConverter(Class<X> rawType, Type genericType, Annotation[] annotations) {
				if (rawType == clazz) {
					return (ParamConverter<X>) converter;
				} else {
					return null;
				}
			}
		});
	}

	private class ListParamConverterProvider implements ParamConverterProvider {
		@SuppressWarnings("unchecked")
		@Override
		public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
			if (!List.class.isAssignableFrom(rawType)) {
				return null;
			}
			Type parameterType;
			if (genericType instanceof ParameterizedType) {
				parameterType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
				return (ParamConverter<T>) new ListValuedParamConverter(parameterType);
			} else {
				return null;
			}
		}
	}

	private class ListValuedParamConverter implements ParamConverter<List<?>> {

		private final ParamConverter<?> itemConverter;

		public ListValuedParamConverter(Type parameterType) {
			itemConverter = ParameterConverterProvider.this.getConverter((Class<?>) parameterType, parameterType, null);
		}

		@Override
		public List<?> fromString(String param) {
			if (param == null || param.trim().isEmpty()) {
				return null;
			}
			return Arrays.stream(param.split(","))
					.map(s -> (itemConverter).fromString(s))
					.collect(Collectors.toList());
		}

		@Override
		public String toString(List<?> list) {
			if (list == null || list.isEmpty()) {
				return null;
			}
			return list.stream()
					.map(item -> (((ParamConverter) itemConverter)).toString(item))
					.collect(Collectors.joining(","));
		}
	}
}
