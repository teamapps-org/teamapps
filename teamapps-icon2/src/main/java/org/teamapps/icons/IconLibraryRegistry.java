package org.teamapps.icons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.icons.api.Icon;
import org.teamapps.icons.spi.IconEncoder;
import org.teamapps.icons.spi.IconLibrary;
import org.teamapps.icons.spi.IconProvider;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IconLibraryRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Map<Class<? extends Icon>, IconLibrary> libraryAnnotationByIconClass = new HashMap<>();
	private final Map<String, IconEncoder<?>> defaultEncodersByLibraryName = new HashMap<>();
	private final Map<String, IconProvider> providersByLibraryName = new HashMap<>();


	public <I extends Icon> IconEncoder<I> getDefaultIconEncoder(Class<I> iconClass) {
		registerIconLibrary(iconClass);
		String libraryName = libraryAnnotationByIconClass.get(iconClass).name();
		return (IconEncoder<I>) defaultEncodersByLibraryName.get(libraryName);
	}

	public IconProvider getIconProvider(String libraryName) {
		return providersByLibraryName.get(libraryName); // may be null, if no icon of this type has ever been encoded
	}

	public String getLibraryName(Icon icon) {
		registerIconLibrary(icon.getClass());
		return libraryAnnotationByIconClass.get(icon.getClass()).name();
	}

	public <I extends Icon> void registerIconLibrary(Class<I> iconClass) {
		if (!libraryAnnotationByIconClass.containsKey(iconClass)) {
			IconLibrary libraryAnnotation = findAnnotation(iconClass, IconLibrary.class);
			libraryAnnotationByIconClass.put(iconClass, libraryAnnotation);
			if (libraryAnnotation != null) {
				try {
					IconEncoder<I> iconEncoder = (IconEncoder<I>) libraryAnnotation.encoder().getDeclaredConstructor().newInstance();
					defaultEncodersByLibraryName.put(libraryAnnotation.name(), iconEncoder);
				} catch (Exception e) {
					LOGGER.error("Could not create default icon encoder or provider for icon class " + iconClass, e);
					throw new RuntimeException(e);
				}
				try {
					IconProvider iconProvider = libraryAnnotation.provider().getDeclaredConstructor().newInstance();
					providersByLibraryName.put(libraryAnnotation.name(), iconProvider);
				} catch (Exception e) {
					LOGGER.error("Could not create default icon encoder or provider for icon class " + iconClass, e);
					throw new RuntimeException(e);
				}
			}
		}
	}

	public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationClass) {
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
