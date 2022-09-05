package org.teamapps.ux.component;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;

public class ComponentLibraryRegistry {

	private final String componentsUrlBasePath;

	private final Map<Class<? extends ComponentLibrary>, ComponentLibraryInfo> registeredComponentLibraries = new HashMap<>();
	private final Map<String, ComponentLibraryInfo> librariesByUuid = new HashMap<>();
	private final Map<Class<? extends ClientObject>, ComponentLibraryInfo> componentLibraryByClientObjectClass = new HashMap<>();

	public ComponentLibraryRegistry(String componentsUrlBasePath) {
		this.componentsUrlBasePath = componentsUrlBasePath;
	}

	public ComponentLibrary getComponentLibraryForClientObject(ClientObject clientObject) {
		return getComponentLibraryForClientObjectClass(clientObject.getClass()).componentLibrary;
	}

	private ComponentLibraryInfo getComponentLibraryForClientObjectClass(Class<? extends ClientObject> clientObjectClass) {
		return componentLibraryByClientObjectClass.computeIfAbsent(clientObjectClass, c -> {
			TeamAppsComponent annotation = c.getAnnotation(TeamAppsComponent.class);
			Class<? extends ComponentLibrary> componentLibraryClass = annotation.library();
			return getComponentLibraryInternal(componentLibraryClass, () -> {
				ComponentLibrary componentLibrary1;
				Constructor<? extends ComponentLibrary> constructor;
				try {
					constructor = componentLibraryClass.getConstructor();
				} catch (NoSuchMethodException e) {
					throw new IllegalArgumentException("ComponentLibrary must have a default constructor or be registered manually!", e);
				}
				try {
					componentLibrary1 = constructor.newInstance();
				} catch (Exception e) {
					throw new IllegalArgumentException("Exception while calling component library constructor!", e);
				}
				return componentLibrary1;
			});
		});
	}

	private ComponentLibraryInfo getComponentLibraryInternal(Class<? extends ComponentLibrary> componentLibraryClass, Supplier<ComponentLibrary> componentLibrarySupplier) {
		if (!registeredComponentLibraries.containsKey(componentLibraryClass)) {
			ComponentLibrary componentLibrary = componentLibrarySupplier.get();
			String uuid = UUID.randomUUID().toString();
			ComponentLibraryInfo componentLibraryInfo = new ComponentLibraryInfo(componentLibrary, uuid);
			registeredComponentLibraries.put(componentLibraryClass, componentLibraryInfo);
			librariesByUuid.put(uuid, componentLibraryInfo);
			registerComponentLibrary(componentLibrary);
		}
		return registeredComponentLibraries.get(componentLibraryClass);
	}

	public String registerComponentLibrary(ComponentLibrary componentLibrary) {
		return getComponentLibraryInternal(componentLibrary.getClass(), () -> componentLibrary).uuid;
	}

	public ComponentLibrary getComponentLibraryById(String uuid) {
		return librariesByUuid.get(uuid).componentLibrary;
	}

	public String getMainJsUrl(Class<? extends ClientObject> clientObjectClass) {
		return componentsUrlBasePath + getComponentLibraryForClientObjectClass(clientObjectClass).uuid;
	}

	private static class ComponentLibraryInfo {
		final ComponentLibrary componentLibrary;
		final String uuid;

		public ComponentLibraryInfo(ComponentLibrary componentLibrary, String uuid) {
			this.componentLibrary = componentLibrary;
			this.uuid = uuid;
		}
	}
}
