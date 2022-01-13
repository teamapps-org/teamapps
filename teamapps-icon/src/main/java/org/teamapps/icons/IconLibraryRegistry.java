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

public class IconLibraryRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Map<Class<? extends Icon<?, ?>>, String> libraryNameByIconClass = new HashMap<>();
	private final Map<String, IconEncoder> encodersByLibraryName = new HashMap<>();
	private final Map<String, IconDecoder> decodersByLibraryName = new HashMap<>();
	private final Map<String, IconLoader> loadersByLibraryName = new HashMap<>();
	private final Map<String, Object> defaultIconStylesByLibraryName = new HashMap<>();


	public <I extends Icon<I, S>, S> IconEncoder<I> getIconEncoder(Class<I> iconClass) {
		registerIconLibrary(iconClass);
		String libraryName = libraryNameByIconClass.get(iconClass);
		return encodersByLibraryName.get(libraryName);
	}

	public <I extends Icon<I, S>, S> IconDecoder<I> getIconDecoder(String libraryName) {
		return decodersByLibraryName.get(libraryName); // may be null, if no icon of this type has ever been encoded
	}

	public <I extends Icon<I, S>, S> IconLoader<I> getIconLoader(String libraryName) {
		return loadersByLibraryName.get(libraryName);
	}

	public <S, I extends Icon<I, S>> S getDefaultStyle(Class<I> iconClass) {
		return (S) defaultIconStylesByLibraryName.get(getLibraryName(iconClass));
	}

	public String getLibraryName(Icon<?, ?> icon) {
		return getLibraryName(icon.getClass());
	}

	public <I extends Icon<I, S>, S> String getLibraryName(Class<I> iconClass) {
		registerIconLibrary(iconClass);
		return libraryNameByIconClass.get(iconClass);
	}

	public <I extends Icon<I, S>, S> void registerIconLibrary(Class<I> iconClass) {
		if (!libraryNameByIconClass.containsKey(iconClass)) {
			IconLibrary libraryAnnotation = findAnnotation(iconClass, IconLibrary.class);
			if (libraryAnnotation != null) {
				IconEncoder<I> iconEncoder;
				try {
					iconEncoder = libraryAnnotation.encoder().getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					LOGGER.error("Could not create icon encoder for icon class " + iconClass, e);
					throw new RuntimeException(e);
				}
				IconDecoder<I> iconDecoder;
				try {
					iconDecoder = libraryAnnotation.decoder().getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					LOGGER.error("Could not create icon decoder for icon class " + iconClass, e);
					throw new RuntimeException(e);
				}
				IconLoader<I> iconLoader;
				try {
					iconLoader = libraryAnnotation.loader().getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					LOGGER.error("Could not create icon loader for icon class " + iconClass, e);
					throw new RuntimeException(e);
				}
				DefaultStyleSupplier<S> defaultStyleSupplier;
				try {
					defaultStyleSupplier = libraryAnnotation.defaultStyleSupplier().getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					LOGGER.error("Could not create defaultStyleSupplier for icon class " + iconClass, e);
					throw new RuntimeException(e);
				}
				registerIconLibrary(iconClass, libraryAnnotation.name(), iconEncoder, iconDecoder, iconLoader, defaultStyleSupplier.getDefaultStyle());
			}
		}
	}

	public <I extends Icon<I, S>, S> void registerIconLibrary(Class<I> iconClass, String libraryName, IconEncoder<I> iconEncoder, IconDecoder<I> iconDecoder, IconLoader<I> iconLoader, S defaultStyle) {
		synchronized (this) {
			if (!libraryNameByIconClass.containsKey(iconClass)) {
				libraryNameByIconClass.put(iconClass, libraryName);
				encodersByLibraryName.put(libraryName, iconEncoder);
				decodersByLibraryName.put(libraryName, iconDecoder);
				loadersByLibraryName.put(libraryName, iconLoader);
				defaultIconStylesByLibraryName.put(libraryName, defaultStyle);
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
