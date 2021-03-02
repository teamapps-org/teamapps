/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
package org.teamapps.data.extract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanPropertyInjector<RECORD> implements PropertyInjector<RECORD> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanPropertyInjector.class);
	private static final Map<ClassAndPropertyName, ValueInjector> settersByClassAndPropertyName = new ConcurrentHashMap<>();

	private final Map<String, ValueInjector> customInjectors = new HashMap<>(0);

	@Override
	public void setValue(RECORD record, String propertyName, Object value) {
		ValueInjector valueInjector = getValueInjector(record.getClass(), propertyName);
		valueInjector.inject(record, value);
	}

	private ValueInjector getValueInjector(Class clazz, String propertyName) {
		ValueInjector ValueInjector = customInjectors.get(propertyName);
		if (ValueInjector != null) {
			return ValueInjector;
		} else {
			return settersByClassAndPropertyName.computeIfAbsent(
					new ClassAndPropertyName(clazz, propertyName),
					classAndPropertyName -> createValueInjector(classAndPropertyName)
			);
		}
	}

	private ValueInjector<RECORD, ?> createValueInjector(ClassAndPropertyName classAndPropertyName) {
		Method setter = ReflectionUtil.findSetter(classAndPropertyName.clazz, classAndPropertyName.propertyName);
		Field field = ReflectionUtil.findField(classAndPropertyName.clazz, classAndPropertyName.propertyName);
		return (record, value) -> {
			if (setter != null) {
				ReflectionUtil.invokeMethod(record, setter, value);
			} else {
				if (field != null) {
					try {
						field.set(record, value);
					} catch (IllegalAccessException ex) {
						LOGGER.debug("Could not access field for property {} on class {}!", classAndPropertyName.propertyName, record.getClass().getCanonicalName());
					}
				} else {
					LOGGER.debug("Could not find setter or field for property {} on class {}!", classAndPropertyName.propertyName, record.getClass().getCanonicalName());
				}
			}
		};
	}

	public <VALUE> BeanPropertyInjector<RECORD> addProperty(String propertyName, ValueInjector<RECORD, VALUE> valueInjector) {
		this.customInjectors.put(propertyName, valueInjector);
		return this;
	}

}
