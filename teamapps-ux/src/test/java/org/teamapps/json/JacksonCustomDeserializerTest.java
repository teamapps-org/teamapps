/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class JacksonCustomDeserializerTest {

	@Test
	public void test() throws Exception {
		Object o = new ObjectMapper().readValue("{\"x\":[{\"x\":123}]}", ObjectReferencePojo.class);
		System.out.println(o);
	}

	public static class ObjectReferencePojo {

		@JsonDeserialize(using = CustomDeserializer.class)
		public Object x;

		public ObjectReferencePojo() {
		}

		public ObjectReferencePojo(Object x) {
			this.x = x;
		}

		public Object getX() {
			return x;
		}

		public void setX(Object x) {
			this.x = x;
		}
	}

	public static class CustomDeserializer extends StdDeserializer<Object> {


		protected CustomDeserializer() {
			super(Object.class);
		}

		@Override
		public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			if (p.getCurrentToken() == JsonToken.START_ARRAY) {
				return ctxt.readValue(p, List.class);
			}

			return null;
		}
	}

}
