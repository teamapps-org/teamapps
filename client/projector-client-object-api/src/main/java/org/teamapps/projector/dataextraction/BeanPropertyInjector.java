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
package org.teamapps.projector.dataextraction;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.commons.util.ReflectionUtil;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of {@link PropertyInjector} for JavaBean objects.
 * <p>
 * This class uses reflection to set property values on JavaBean objects using setter methods.
 * It can optionally fall back to direct field access if a setter method is not found.
 * <p>
 * The class caches the injectors for each class and property name combination to improve performance.
 * <p>
 * Custom injectors can be added for specific properties using the {@link #addProperty(String, ValueInjector)} method.
 *
 * @param <RECORD> the type of the record object
 */
public class BeanPropertyInjector<RECORD> implements PropertyInjector<RECORD> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final Map<ClassAndPropertyName, ValueInjector> settersByClassAndPropertyName = new ConcurrentHashMap<>();

	private final Map<String, ValueInjector> customInjectors = new HashMap<>(0);
	private final boolean fallbackToFields;

	/**
	 * Constructs a new BeanPropertyInjector with fallbackToFields set to false.
	 * <p>
	 * This constructor creates a BeanPropertyInjector that will only use setter methods to set property values.
	 */
	public BeanPropertyInjector() {
		this(false);
	}

	/**
	 * Constructs a new BeanPropertyInjector with the specified fallbackToFields value.
	 *
	 * @param fallbackToFields whether to fall back to direct field access if a setter method is not found
	 */
	public BeanPropertyInjector(boolean fallbackToFields) {
		this.fallbackToFields = fallbackToFields;
	}

	/**
	 * Sets a single property value on a record object.
	 * <p>
	 * This method uses a {@link ValueInjector} to set the property value on the record object.
	 * The injector is obtained from the {@link #getValueInjector(Class, String)} method.
	 *
	 * @param record the record object to set the property value on
	 * @param propertyName the name of the property to set
	 * @param value the value to set
	 */
	@Override
	public void setValue(RECORD record, String propertyName, Object value) {
		ValueInjector valueInjector = getValueInjector(record.getClass(), propertyName);
		valueInjector.inject(record, value);
	}

	/**
	 * Adds a custom property injector for a specific property.
	 * <p>
	 * This method allows you to override the default property injection behavior for a specific property.
	 * The custom injector will be used instead of the default reflection-based injector.
	 *
	 * @param <VALUE> the type of the value to inject
	 * @param propertyName the name of the property
	 * @param valueInjector the custom injector to use for the property
	 * @return this BeanPropertyInjector instance for method chaining
	 */
	public <VALUE> BeanPropertyInjector<RECORD> addProperty(String propertyName, ValueInjector<RECORD, VALUE> valueInjector) {
		this.customInjectors.put(propertyName, valueInjector);
		return this;
	}

	private ValueInjector getValueInjector(Class clazz, String propertyName) {
		ValueInjector ValueInjector = customInjectors.get(propertyName);
		if (ValueInjector != null) {
			return ValueInjector;
		} else {
			return settersByClassAndPropertyName.computeIfAbsent(
					new ClassAndPropertyName(clazz, propertyName, fallbackToFields),
					classAndPropertyName -> createValueInjector(classAndPropertyName)
			);
		}
	}

	private ValueInjector<RECORD, ?> createValueInjector(ClassAndPropertyName classAndPropertyName) {
		Method setter = findSetter(classAndPropertyName.clazz(), classAndPropertyName.propertyName());
		if (setter != null) {
			return (record, value) -> ReflectionUtil.invokeMethod(record, setter, value);
		} else if (fallbackToFields) {
			Field field = ReflectionUtil.findField(classAndPropertyName.clazz(), classAndPropertyName.propertyName());
			if (field != null && !Modifier.isFinal(field.getModifiers())) {
				return (record, value) -> ReflectionUtil.setField(record, field, value, true);
			}
		}
		LOGGER.debug("Could not find setter or field for property {} on class {}!", classAndPropertyName.propertyName(), classAndPropertyName.getClass().getCanonicalName());
		return (record, value) -> {
		};
	}

	private static Method findSetter(Class<?> clazz, String propertyName) {
		String methodName = "set" + StringUtils.capitalize(propertyName);
		return ReflectionUtil.findMethods(clazz, method -> method.getName().equals(methodName) && method.getParameterCount() == 1)
				.stream()
				.findFirst().orElse(null);
	}

}
