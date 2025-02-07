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
package org.teamapps.projector.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.clientobject.ClientObject;

import java.io.IOException;

public class UxSerializersJacksonModule extends SimpleModule {

	public record ClientObjectReference(@JsonProperty("_ref") String ref) {
	}

	public UxSerializersJacksonModule() {
		super();
		this.addSerializer(Icon.class, new JsonSerializer<>() {
			@Override
			public void serialize(Icon icon, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				SessionContext currentSessionContext = SessionContext.current();
				gen.writeString(currentSessionContext.resolveIcon(icon));
			}

			@Override
			public void serializeWithType(Icon value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
				serialize(value, gen, serializers);
			}
		});
		this.addSerializer(ClientObject.class, new JsonSerializer<>() {
			@Override
			public void serialize(ClientObject clientObject, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				SessionContext currentSessionContext = SessionContext.current();
				gen.writeObject(new ClientObjectReference(currentSessionContext.ensureRenderedAndGetId(clientObject)));
			}

			@Override
			public void serializeWithType(ClientObject clientObject, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
				serialize(clientObject, gen, serializers);
			}
		});
		this.addDeserializer(ClientObject.class, new JsonDeserializer<>() {
			@Override
			public ClientObject deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
				SessionContext currentSessionContext = SessionContext.current();
				ClientObjectReference ref = p.readValueAs(ClientObjectReference.class);
				return currentSessionContext.getClientObjectById(ref.ref());
			}
		});
	}

	@Override
	public Object getTypeId() {
		return this.getClass();
	}

}
