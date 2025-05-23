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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of {@link PropertyExtractor} for JavaBean and {@link Record} objects.
 * <p>
 * This class uses reflection to extract property values from JavaBean objects using getter methods.
 * It can optionally fall back to direct field access if a getter method is not found.
 * <p>
 * The class caches the extractors for each class and property name combination to improve performance.
 * <p>
 * Custom extractors can be added for specific properties using the {@link #addProperty(String, ValueExtractor)} method.
 * <p>
 * Getter methods are identified by the "get" or "is" (for boolean properties) prefix followed by the capitalized property name,
 * or by the property name itself (for Java records).
 *
 * @param <RECORD> the type of the record object
 */
public class BeanPropertyExtractor<RECORD> implements PropertyExtractor<RECORD> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final Map<ClassAndPropertyName, ValueExtractor> valueExtractorsByClassAndPropertyName = new ConcurrentHashMap<>();

	private final Map<String, ValueExtractor<RECORD, ?>> customExtractors = new HashMap<>(0);
	private final boolean fallbackToFields;

	/**
	 * Constructs a new BeanPropertyExtractor with {@link #fallbackToFields} set to false.
	 * <p>
	 * This constructor creates a BeanPropertyExtractor that will only use getter methods to extract property values.
	 */
	public BeanPropertyExtractor() {
		this(false);
	}

	/**
	 * Constructs a new BeanPropertyExtractor with the specified {@link #fallbackToFields} value.
	 *
	 * @param fallbackToFields whether to fall back to direct field access if a getter method is not found
	 */
	public BeanPropertyExtractor(boolean fallbackToFields) {
		this.fallbackToFields = fallbackToFields;
	}

	/**
	 * Gets a single property value from a record object.
	 * <p>
	 * This method uses a {@link ValueExtractor} to extract the property value from the record object.
	 * The extractor is obtained from the {@link #getValueExtractor(Class, String)} method.
	 *
	 * @param record the record object to get the property value from
	 * @param propertyName the name of the property to get
	 * @return the property value, or null if the property does not exist
	 */
	@Override
	public Object getValue(RECORD record, String propertyName) {
		ValueExtractor<RECORD, ?> valueExtractor = getValueExtractor(record.getClass(), propertyName);
		return valueExtractor.extract(record);
	}

	/**
	 * Adds a custom property extractor for a specific property.
	 * <p>
	 * This method allows you to override the default property extraction behavior for a specific property.
	 * The custom extractor will be used instead of the default reflection-based extractor.
	 *
	 * @param propertyName the name of the property
	 * @param valueExtractor the custom extractor to use for the property
	 * @return this BeanPropertyExtractor instance for method chaining
	 */
	public BeanPropertyExtractor<RECORD> addProperty(String propertyName, ValueExtractor<RECORD, ?> valueExtractor) {
		this.customExtractors.put(propertyName, valueExtractor);
		return this;
	}

	protected ValueExtractor<RECORD, ?> getValueExtractor(Class clazz, String propertyName) {
		ValueExtractor<RECORD, ?> valueExtractor = customExtractors.get(propertyName);
		if (valueExtractor != null) {
			return valueExtractor;
		} else {
			return valueExtractorsByClassAndPropertyName.computeIfAbsent(
					new ClassAndPropertyName(clazz, propertyName, fallbackToFields),
					classAndPropertyName -> createValueExtractor(classAndPropertyName)
			);
		}
	}

	private ValueExtractor<RECORD, ?> createValueExtractor(ClassAndPropertyName classAndPropertyName) {
		Method getter = findGetter(classAndPropertyName.clazz(), classAndPropertyName.propertyName());
		if (getter != null) {
			return record -> ReflectionUtil.invokeMethod(record, getter);
		} else if (fallbackToFields) {
			Field field = ReflectionUtil.findField(classAndPropertyName.clazz(), classAndPropertyName.propertyName());
			if (field != null) {
				return record -> ReflectionUtil.readField(record, field, true);
			}
		}
		LOGGER.debug("Could not find getter " + (fallbackToFields ? "or field " : "") + "for property {} on class {}!", classAndPropertyName.propertyName(), classAndPropertyName.getClass().getCanonicalName());
		return record -> null;
	}

	private static Method findGetter(Class<?> clazz, String propertyName) {
		String normalGetterName = "get" + StringUtils.capitalize(propertyName);
		String booleanGetterName = "is" + StringUtils.capitalize(propertyName);
        return ReflectionUtil.findMethods(clazz,
				method -> (method.getName().equals(normalGetterName)
						|| method.getName().equals(booleanGetterName)
						|| method.getName().equals(propertyName) // record getter name
				) && method.getParameterCount() == 0)
				.stream()
				.findFirst().orElse(null);
	}

}
