package org.teamapps.ux.component;

import org.teamapps.ux.component.annotations.ProjectorComponent;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class ComponentLibraryRegistry {

	private final String componentsUrlBasePath;

	private final Map<Class<? extends ComponentLibrary>, ComponentLibraryInfo> registeredComponentLibraries = new HashMap<>();
	private final Map<String, ComponentLibraryInfo> librariesByUuid = new HashMap<>();
	private final Map<Class<? extends ClientObject>, ComponentLibraryInfo> componentLibraryByClientObjectClass = new HashMap<>();

	public ComponentLibraryRegistry(String componentsUrlBasePath) {
		this.componentsUrlBasePath = componentsUrlBasePath;
	}

	public ComponentLibraryInfo getComponentLibraryForClientObject(ClientObject clientObject) {
		return getComponentLibraryForClientObjectClass(clientObject.getClass());
	}

	public ComponentLibraryInfo getComponentLibraryForClientObjectClass(Class<? extends ClientObject> clientObjectClass) {
		return componentLibraryByClientObjectClass.computeIfAbsent(clientObjectClass, c -> {
			ProjectorComponent annotation = c.getAnnotation(ProjectorComponent.class);
			if (annotation == null) {
				throw new IllegalArgumentException("ClientObject class " + clientObjectClass + " is not annotated with @" + ProjectorComponent.class.getSimpleName());
			}
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
			String uuid = componentLibraryClass.getSimpleName() + "-" + UUID.randomUUID();
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
		ComponentLibraryInfo componentLibraryInfo = librariesByUuid.get(uuid);
		return componentLibraryInfo != null ? componentLibraryInfo.componentLibrary : null;
	}

	public String getMainJsUrl(Class<? extends ClientObject> clientObjectClass) {
		String libraryUuid = getComponentLibraryForClientObjectClass(clientObjectClass).uuid;
		return componentsUrlBasePath + libraryUuid + "/";
	}

	public String getMainCssUrl(Class<? extends ClientObject> clientObjectClass) {
		String libraryUuid = getComponentLibraryForClientObjectClass(clientObjectClass).uuid;
		return componentsUrlBasePath + libraryUuid + "/" + libraryUuid + ".css";
	}

	public static class ComponentLibraryInfo {
		private final ComponentLibrary componentLibrary;
		private final String uuid;

		public ComponentLibraryInfo(ComponentLibrary componentLibrary, String uuid) {
			this.componentLibrary = componentLibrary;
			this.uuid = uuid;
		}

		public ComponentLibrary getComponentLibrary() {
			return componentLibrary;
		}

		public String getUuid() {
			return uuid;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ComponentLibraryInfo that = (ComponentLibraryInfo) o;
			return Objects.equals(componentLibrary, that.componentLibrary) && Objects.equals(uuid, that.uuid);
		}

		@Override
		public int hashCode() {
			return Objects.hash(componentLibrary, uuid);
		}
	}
}
