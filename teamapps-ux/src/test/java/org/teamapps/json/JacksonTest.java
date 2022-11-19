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
package org.teamapps.json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.teamapps.dto.DtoCommand;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JacksonTest {

	@Test
	public void testAccessFieldsOnly() throws Exception {
		ObjectMapper mapper  = new ObjectMapper();
		mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

		FieldPojo value = new FieldPojo();
		System.out.println(mapper.writeValueAsString(value));
	}

	@Test
	public void testAccessAccessorsOnly() throws Exception {
		ObjectMapper mapper  = new ObjectMapper();
		AccessorPojo value = new AccessorPojo();
		System.out.println(mapper.writeValueAsString(value));
	}

	@Test
	public void testBeanPropertyDefinitions() throws Exception {
		ObjectMapper mapper  = new ObjectMapper();
		JavaType javaType = mapper.getTypeFactory().constructType(AccessorPojo.class);
		BeanDescription beanDescription = mapper.getSerializationConfig().introspect(javaType);
		List<BeanPropertyDefinition> properties = beanDescription.findProperties();
		BeanPropertyDefinition property = properties.stream()
				.filter(p -> Objects.equals(p.getName(), "a"))
				.findFirst().orElse(null);
		System.out.println(property.getAccessor());
		System.out.println(property.getField());
		System.out.println();
	}

	@Test
	public void testBeanPropertyDefinitionsForFields() throws Exception {
		ObjectMapper mapper  = new ObjectMapper();
		mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		JavaType javaType = mapper.getTypeFactory().constructType(FieldPojo.class);
		BeanDescription beanDescription = mapper.getSerializationConfig().introspect(javaType);
		List<BeanPropertyDefinition> properties = beanDescription.findProperties();
		BeanPropertyDefinition property = properties.stream()
				.filter(p -> Objects.equals(p.getName(), "a"))
				.findFirst().orElse(null);
		System.out.println(property.getAccessor());
		System.out.println(property.getField());
		System.out.println();
	}

	@Test
	@Ignore
	public void testLowLevelJsonParsingPerformance() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		String json = JsonDeserializationBenchmark.readResourceToString("sample.json");

		long startTime;
		JsonNode[] jsonNodes = new JsonNode[1024];
		DtoCommand[] uiCommands = new DtoCommand[1024];

		for (int j = 0; j < 5; j++) {
			startTime = System.currentTimeMillis();
			for (int i = 0; i < 1_000; i++) {
				jsonNodes[i % 1024] = mapper.readTree(json);
			}
			System.out.println("Jackson TreeNode: " + (System.currentTimeMillis() - startTime));

			startTime = System.currentTimeMillis();
			for (int i = 0; i < 1_000; i++) {
				uiCommands[i % 1024] = mapper.readValue(json, DtoCommand.class);
			}
			System.out.println("Jackson data binding: " + (System.currentTimeMillis() - startTime));
		}
		System.out.println(jsonNodes[0]);
	}

	@Test
	public void localDateTime() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		String json = objectMapper.writeValueAsString(LocalDateTime.now());
		Assertions.assertThat(json).matches("\\[(\\d+,){6}\\d+]"); // [2018,7,10,14,43,49,207000000]
	}

	public static class Container {
		Object x;

		public Container() {
		}

		public Container(Object x) {
			this.x = x;
		}

		public Object getX() {
			return x;
		}

		public void setX(Object x) {
			this.x = x;
		}
	}

	public static class A {
		String a;

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}
	}

	public static class FieldPojo {
		private final String a = "propertyA";
		private final String b = "propertyB";
		private final int c = 123;

		@JsonAnyGetter
		public Map<String, Object> any() {
			HashMap<String, Object> map = new HashMap<>();
			map.put("x", "x");
			map.put("a", "aFromAnyGetter");
			map.put("b", "bFromAnyGetter");
			return map;
		}
	}

	public static class AccessorPojo {
		private String a = "propertyA";
		private String b = "propertyB";
		private int c = 123;

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}

		public String getB() {
			return b;
		}

		public void setB(String b) {
			this.b = b;
		}

		public int getC() {
			return c;
		}

		public void setC(int c) {
			this.c = c;
		}
	}


}
