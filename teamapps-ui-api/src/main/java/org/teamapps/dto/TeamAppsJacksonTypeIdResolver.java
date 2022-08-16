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
package org.teamapps.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This TypeIdResolver provides type ids for all TeamApps UI API classes.
 * Additionally it also provides type ids for all classes that get serialized, so they can be deserialized in the future.
 * <p>
 * Ratio: Components are allowed to send and receive.
 * <p>
 * On the sending side, this enables app developers to send arbitrary data for templates. E.g., a MustacheTemplate might be filled using
 * a list of deep json structures.
 * <p>
 * On the receiving side, this is technically not necessary, since all data that comes from UiComponents might be defined as UiObjects.
 * However, component developers might decide to send back arbitrary object (in original or changed state) that were previously sent to the
 * client. This approach is not recommended but possible. Since only classes that have ever been serialized can be deserialized, there is
 * no security risk in technically allowing {@link Object} values (unless developers send security critical classes, which we regard as
 * very unlikely).
 */
public class TeamAppsJacksonTypeIdResolver implements TypeIdResolver {

	private static final Map<String, JavaType> JAVA_TYPE_BY_ID = new ConcurrentHashMap<>();
	private static final Map<Class, String> ID_BY_CLASS = new ConcurrentHashMap<>();

	public TeamAppsJacksonTypeIdResolver() {
		// register all ui classes for de/serialization
		registerAll(UiObjectJacksonTypeIdMaps.ID_BY_CLASS);
	}

	public static void registerAll(Map<Class, String> idByClass) {
		idByClass.forEach(TeamAppsJacksonTypeIdResolver::registerPojoClass);
	}

	public static String registerPojoClass(Class clazz) {
		return registerPojoClass(clazz, clazz.getCanonicalName());
	}

	public static String registerPojoClass(Class clazz, String typeId) {
		ID_BY_CLASS.put(clazz, typeId);
		JAVA_TYPE_BY_ID.put(typeId, TypeFactory.defaultInstance().constructType(clazz));
		return typeId;
	}

	@Override
	public void init(JavaType baseType) {
	}

	@Override
	public JsonTypeInfo.Id getMechanism() {
		return JsonTypeInfo.Id.CUSTOM;
	}

	public String idFromClass(Class clazz) {
		return idFromValueAndType(null, clazz);
	}

	@Override
	public String idFromValue(Object obj) {
		return idFromValueAndType(obj, obj.getClass());
	}

	@Override
	public String idFromValueAndType(Object obj, Class<?> clazz) {
		String id = ID_BY_CLASS.get(clazz);
		if (id != null) {
			return id;
		} else {
			// classes that are serialized may also be deserialized
			return registerPojoClass(clazz);
		}
	}

	@Override
	public String idFromBaseType() {
		return "java.util.Map";
	}

	@Override
	public JavaType typeFromId(DatabindContext context, String id) {
		JavaType javaType = JAVA_TYPE_BY_ID.get(id);
		if (javaType == null) {
			throw new IllegalArgumentException("Unregistered type id: " + id + ". Pojo types must be registered in order to be deserialized. Use registerPojoClass().");
		}
		return javaType;
	}

	@Override
	public String getDescForKnownTypeIds() {
		return "Known ids/types: \n" + JAVA_TYPE_BY_ID.entrySet().stream()
				.map(e -> e.getKey() + ": " + e.getValue().getTypeName())
				.collect(Collectors.joining(",\n"));
	}

}
