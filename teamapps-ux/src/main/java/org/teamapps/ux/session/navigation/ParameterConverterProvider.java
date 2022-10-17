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