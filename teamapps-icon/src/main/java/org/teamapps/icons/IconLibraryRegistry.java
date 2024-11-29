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
package org.teamapps.icons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.icons.spi.*;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class IconLibraryRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Map<Class<? extends Icon>, IconLibrary<?>> libraryByIconClass = new HashMap<>();
	private final Map<String, IconLibrary<?>> libraryByName = new HashMap<>();

	public <I extends Icon> IconLibrary<I> getIconLibrary(I icon) {
		return (IconLibrary<I>) getIconLibrary(icon.getClass());
	}

	public <I extends Icon> IconLibrary<I> getIconLibrary(Class<I> iconClass) {
		registerIconLibrary(iconClass);
		return (IconLibrary<I>) libraryByIconClass.get(iconClass);
	}

	public <I extends Icon> IconLibrary<I> getIconLibrary(String name) {
		return (IconLibrary<I>) libraryByName.get(name);
	}

	public <I extends Icon> void registerIconLibrary(Class<I> iconClass) {
		if (!libraryByIconClass.containsKey(iconClass)) {
			org.teamapps.icons.spi.annotation.IconLibrary libraryAnnotation = findAnnotation(iconClass, org.teamapps.icons.spi.annotation.IconLibrary.class);
			if (libraryAnnotation != null) {
				IconLibrary<I> iconLibrary;
				try {
					iconLibrary = (IconLibrary<I>) libraryAnnotation.value().getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					LOGGER.error("Could not create icon encoder for icon class " + iconClass, e);
					throw new RuntimeException(e);
				}
				registerIconLibrary(iconLibrary);
			}
		}
	}

	public <I extends Icon> void registerIconLibrary(IconLibrary<I> iconLibrary) {
		synchronized (this) {
			if (!libraryByIconClass.containsKey(iconLibrary.getIconClass())) {
				libraryByIconClass.put(iconLibrary.getIconClass(), iconLibrary);
				libraryByName.put(iconLibrary.getName(), iconLibrary);
			}
		}
	}

	private static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationClass) {
		A annotation = clazz.getAnnotation(annotationClass);
		if (annotation != null) {
			return annotation;
		}
		return Arrays.stream(clazz.getInterfaces())
				.map(i -> findAnnotation(i, annotationClass))
				.filter(Objects::nonNull)
				.findFirst()
				.orElseGet(() -> findAnnotation(clazz.getSuperclass(), annotationClass));
	}
}
