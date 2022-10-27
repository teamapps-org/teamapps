/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.ux.session.navigation;

import jakarta.inject.Inject;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.internal.inject.ParamConverters;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ParameterConverterProvider implements ParamConverterProvider {

	private final List<ParamConverterProvider> providers;

	@Inject
	public ParameterConverterProvider() {
		this.providers = new ArrayList<>(List.of(
				// ordering is important (e.g. Date provider must be executed before String Constructor
				// as Date has a deprecated String constructor
				new ParamConverters.DateProvider(),
				new ParamConverters.TypeFromStringEnum(),
				new ParamConverters.TypeValueOf(),
				new ParamConverters.CharacterProvider(),
				new ParamConverters.TypeFromString(),
				new ParamConverters.StringConstructor(),
				new ParamConverters.OptionalProvider()
		));
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
}
