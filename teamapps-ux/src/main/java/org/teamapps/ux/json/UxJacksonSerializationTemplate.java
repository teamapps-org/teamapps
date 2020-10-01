/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.teamapps.icons.api.Icon;
import org.teamapps.icons.api.StaticIcon;
import org.teamapps.ux.session.SessionContext;

import java.io.IOException;

public class UxJacksonSerializationTemplate {

	private static final SimpleModule UX_SERIALIZERS_JACKSON_MODULE = new UxSerializersJacksonModule();
	private static final ThreadLocal<SessionContext> SESSION_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

	private final SessionContext sessionContext;

	public UxJacksonSerializationTemplate(ObjectMapper objectMapper, SessionContext sessionContext) {
		this.sessionContext = sessionContext;
		if (!objectMapper.getRegisteredModuleIds().contains(UxSerializersJacksonModule.class)) {
			objectMapper.registerModule(UX_SERIALIZERS_JACKSON_MODULE);
		}
	}

	public void doWithUxJacksonSerializers(Runnable runnable) {
		SESSION_CONTEXT_THREAD_LOCAL.set(sessionContext);
		try {
			runnable.run();
		} finally {
			SESSION_CONTEXT_THREAD_LOCAL.remove();
		}
	}

	private static SessionContext getCurrentSessionContext() {
		return SESSION_CONTEXT_THREAD_LOCAL.get();
	}

	public static class UxSerializersJacksonModule extends SimpleModule {
		public UxSerializersJacksonModule() {
			super();
			this.addSerializer(Icon.class, new JsonSerializer<Icon>() {
				@Override
				public void serialize(Icon icon, JsonGenerator gen, SerializerProvider serializers) throws IOException {
					SessionContext currentSessionContext = getCurrentSessionContext();
					gen.writeString(currentSessionContext.resolveIcon(icon));
				}

				@Override
				public void serializeWithType(Icon value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
					serialize(value, gen, serializers);
				}
			});
			this.addDeserializer(Icon.class, new JsonDeserializer<Icon>() {
				@Override
				public Icon deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
					String qualifiedIconId = p.getText();
					return new StaticIcon(qualifiedIconId);
				}
			});
		}

		@Override
		public Object getTypeId() {
			return this.getClass();
		}
	}

}
