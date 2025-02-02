package org.teamapps.projector.clientobject;

import org.teamapps.projector.annotation.ClientObjectLibrary;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class ComponentLibraryRegistry {

	private final String componentsUrlBasePath;

	private final Map<Class<? extends org.teamapps.projector.clientobject.ClientObjectLibrary>, ClientObjectLibraryInfo> registeredComponentLibraries = new HashMap<>();
	private final Map<String, ClientObjectLibraryInfo> librariesByUuid = new HashMap<>();
	private final Map<Class<? extends ClientObject>, ClientObjectLibraryInfo> componentLibraryByClientObjectClass = new HashMap<>();

	public ComponentLibraryRegistry(String componentsUrlBasePath) {
		this.componentsUrlBasePath = componentsUrlBasePath;
	}

	public ClientObjectLibraryInfo getComponentLibraryForClientObject(ClientObject clientObject) {
		return getComponentLibraryForClientObjectClass(clientObject.getClass());
	}

	public ClientObjectLibraryInfo getComponentLibraryForClientObjectClass(Class<? extends ClientObject> clientObjectClass) {
		return componentLibraryByClientObjectClass.computeIfAbsent(clientObjectClass, c -> {
			ClientObjectLibrary annotation = c.getAnnotation(ClientObjectLibrary.class);
			if (annotation == null) {
				throw new IllegalArgumentException("ClientObject class " + clientObjectClass + " is not annotated with @" + ClientObjectLibrary.class.getSimpleName());
			}
			Class<? extends org.teamapps.projector.clientobject.ClientObjectLibrary> componentLibraryClass = annotation.value();
			return getComponentLibraryInternal(componentLibraryClass, () -> {
				org.teamapps.projector.clientobject.ClientObjectLibrary clientObjectLibrary1;
				Constructor<? extends org.teamapps.projector.clientobject.ClientObjectLibrary> constructor;
				try {
					constructor = componentLibraryClass.getConstructor();
				} catch (NoSuchMethodException e) {
					throw new IllegalArgumentException("ComponentLibrary must have a default constructor or be registered manually!", e);
				}
				try {
					clientObjectLibrary1 = constructor.newInstance();
				} catch (Exception e) {
					throw new IllegalArgumentException("Exception while calling component library constructor!", e);
				}
				return clientObjectLibrary1;
			});
		});
	}

	private ClientObjectLibraryInfo getComponentLibraryInternal(Class<? extends org.teamapps.projector.clientobject.ClientObjectLibrary> componentLibraryClass, Supplier<org.teamapps.projector.clientobject.ClientObjectLibrary> componentLibrarySupplier) {
		if (!registeredComponentLibraries.containsKey(componentLibraryClass)) {
			org.teamapps.projector.clientobject.ClientObjectLibrary clientObjectLibrary = componentLibrarySupplier.get();
			String uuid = componentLibraryClass.getSimpleName() + "-" + UUID.randomUUID();
			String mainJsUrl = getMainJsUrl(uuid);
			String mainCss = getMainCssUrl(uuid);
			ClientObjectLibraryInfo clientObjectLibraryInfo = new ClientObjectLibraryInfo(clientObjectLibrary, uuid, mainJsUrl, mainCss);
			registeredComponentLibraries.put(componentLibraryClass, clientObjectLibraryInfo);
			librariesByUuid.put(uuid, clientObjectLibraryInfo);
			registerComponentLibrary(clientObjectLibrary);
		}
		return registeredComponentLibraries.get(componentLibraryClass);
	}

	public String registerComponentLibrary(org.teamapps.projector.clientobject.ClientObjectLibrary clientObjectLibrary) {
		return getComponentLibraryInternal(clientObjectLibrary.getClass(), () -> clientObjectLibrary).uuid;
	}

	public org.teamapps.projector.clientobject.ClientObjectLibrary getComponentLibraryById(String uuid) {
		ClientObjectLibraryInfo clientObjectLibraryInfo = librariesByUuid.get(uuid);
		return clientObjectLibraryInfo != null ? clientObjectLibraryInfo.clientObjectLibrary : null;
	}

	public String getMainJsUrl(String libraryUuid) {
		return componentsUrlBasePath + libraryUuid + "/";
	}

	public String getMainCssUrl(String libraryUuid) {
		return componentsUrlBasePath + libraryUuid + "/" + libraryUuid + ".css";
	}

	public record ClientObjectLibraryInfo(org.teamapps.projector.clientobject.ClientObjectLibrary clientObjectLibrary, String uuid, String mainJsUrl, String mainCssUrl) {
		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			ClientObjectLibraryInfo that = (ClientObjectLibraryInfo) o;
			return Objects.equals(clientObjectLibrary, that.clientObjectLibrary) && Objects.equals(uuid, that.uuid);
		}

		@Override
		public int hashCode() {
			return Objects.hash(clientObjectLibrary, uuid);
		}
	}
}
