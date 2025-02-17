/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.std.EnumSerializer;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public class ObjectSerializer extends JsonSerializer<Object> {

	private final StdTypeResolverBuilder typeResolverBuilder;
	private final TeamAppsJacksonTypeIdResolver typeIdResolver;
	private final CopyOnWriteLeakyCache<Class<?>, TypeSerializer> typeSerializersCache = new CopyOnWriteLeakyCache<>();
	private final CopyOnWriteLeakyCache<Class<?>, BeanDescription> beanDescriptionsCache = new CopyOnWriteLeakyCache<>();

	public ObjectSerializer() {
		typeIdResolver = new TeamAppsJacksonTypeIdResolver();
		typeResolverBuilder = new ObjectMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL)
				.inclusion(JsonTypeInfo.As.PROPERTY)
				.init(JsonTypeInfo.Id.CLASS, typeIdResolver)
				.typeProperty("_type");
	}

	public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value == null) {
			gen.writeNull();
		} else if (value instanceof List) {
			gen.writeStartArray();
			for (Object entry : (List) value) {
				serialize(entry, gen, serializers);
			}
			gen.writeEndArray();
		} else if (value instanceof Map) {
			gen.writeStartObject();
			for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
				gen.writeFieldName(entry.getKey());
				serialize(entry.getValue(), gen, serializers);
			}
			gen.writeEndObject();
		} else if (value instanceof Enum) {
			JsonSerializer valueSerializer = serializers.findValueSerializer(value.getClass());
			if (!(valueSerializer instanceof EnumSerializer)) {
				// this is a custom serializer. Prefer custom serializers!
				valueSerializer.serialize(value, gen, serializers);
			} else {
				serializeEnum((Enum) value, gen, serializers);
			}
		} else {
			TypeSerializer typeSerializer = typeSerializersCache.computeIfAbsent(value.getClass(), clazz -> typeResolverBuilder.buildTypeSerializer(serializers.getConfig(), serializers
					.getTypeFactory().constructType(clazz), null));
			if (typeSerializer != null) {
				serializeWithType(value, gen, serializers, typeSerializer);
			} else {
				gen.writeObject(value);
			}
		}
	}

	public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
		if (value instanceof List || value instanceof Map) {
			serialize(value, gen, serializers);
		} else {
			serializers.findValueSerializer(value.getClass()).serializeWithType(value, gen, serializers, typeSer);
		}
	}

	public void serializeEnum(Enum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeFieldName("_type");
		gen.writeString(typeIdResolver.idFromClass(value.getDeclaringClass()));
		gen.writeFieldName("_name");
		gen.writeString(value.name());

		BeanDescription beanDescription = beanDescriptionsCache.computeIfAbsent(value.getClass(), aClass -> {
			JavaType javaType = serializers.getTypeFactory().constructType(aClass);
			return serializers.getConfig().introspect(javaType);
		});

		for (BeanPropertyDefinition property : beanDescription.findProperties()) {
			if (Modifier.isStatic(property.getField().getModifiers())
				|| property.getName().equals("declaringClass")) {
				continue;
			}
			Object propertyValue = property.getAccessor().getValue(value);
			if (propertyValue == null) {
				continue; // skip null values
			}
			JsonSerializer<Object> serializer = serializers.findValueSerializer(propertyValue.getClass());
			if (serializer.isEmpty(serializers, propertyValue)) {
				continue;
			}

			gen.writeFieldName(property.getName());
			serializer.serialize(propertyValue, gen, serializers);
		}

		gen.writeEndObject();
	}
}
