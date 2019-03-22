/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.util.TokenBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ObjectDeserializer extends JsonDeserializer<Object> {

	private final StdTypeResolverBuilder typeResolverBuilder;
	private TypeIdResolver typeIdResolver;

	public ObjectDeserializer() {
		typeResolverBuilder = new ObjectMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL)
				.inclusion(JsonTypeInfo.As.PROPERTY)
				.init(JsonTypeInfo.Id.CLASS, new TeamAppsJacksonTypeIdResolver())
				.typeProperty("_type");

	}

	public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		if (p.isExpectedStartArrayToken()) {
			ArrayList<Object> list = new ArrayList<>();
			p.setCurrentValue(list); // assign current value, to be accessible by custom serializers
			JsonToken t;
			while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
				Object value;
				if (t == JsonToken.VALUE_NULL) {
					value = null;
				} else {
					value = deserialize(p, ctxt);
				}
				list.add(value);
			}
			return list;
		} else if (p.isExpectedStartObjectToken()) {
			JsonToken t = p.nextToken();
			if (t == JsonToken.FIELD_NAME) {
				String name = p.getCurrentName();
				if (name.equals("_type")) {
					p.nextToken();
					if (this.typeIdResolver == null) {
						typeIdResolver = typeResolverBuilder.buildTypeDeserializer(ctxt.getConfig(), ctxt.constructType(Object.class), null).getTypeIdResolver();
					}
					JavaType javaType = typeIdResolver.typeFromId(ctxt, p.getText());
					Class<?> rawClass = javaType.getRawClass();
					if (UiObject.class.isAssignableFrom(rawClass)) {
						TokenBuffer tb = new TokenBuffer(p, ctxt);
						tb.writeStartObject();
						// teamapps dto classes are annotated in a way they _need_ the "_type" attribute... maybe we can add more generic support for such kind of classes later...
						tb.writeFieldName("_type");
						tb.copyCurrentStructure(p); // typeId
						p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
						return p.readValueAs(rawClass);
					} else if (Enum.class.isAssignableFrom(rawClass)) {
						return deserializeObjectStyleEnum((Class<Enum>) rawClass, p);
					} else {
						TokenBuffer tb = new TokenBuffer(p, ctxt);
						tb.writeStartObject();
						p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
						return p.readValueAs(rawClass);
					}
				} else {
					HashMap<Object, Object> result = new HashMap<>();
					String key = p.getCurrentName();

					for (; key != null; key = p.nextFieldName()) {
						t = p.nextToken();
						Object value;
						if (t == JsonToken.VALUE_NULL) {
							value = null;
						} else {
							value = deserialize(p, ctxt);
						}
						result.put(key, value);
					}
					return result;
				}
			} else {
				return new HashMap<>();
			}
		} else {
			return p.readValueAs(Object.class);
		}
	}

	private Enum deserializeObjectStyleEnum(Class<? extends Enum> enumClass, JsonParser p) throws IOException {
		Enum result = null;
		String key = p.nextFieldName();
		for (; key != null; key = p.nextFieldName()) {
			JsonToken t = p.nextToken();
			if (key.equals("_name")) {
				String text = p.getText();
				result = Arrays.stream(enumClass.getEnumConstants())
						.filter(c -> ((Enum) c).name().equals(text))
						.findFirst()
						.orElseThrow(() -> new IllegalArgumentException("Could not find enum value \"" + text + "\" in enum class " + enumClass));
			}
		}
		if (result == null) {
			throw new IllegalArgumentException("Name (_name) of object style enum value not specified!");
		}
		return result;
	}
}
