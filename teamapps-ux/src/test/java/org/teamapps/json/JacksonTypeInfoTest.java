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
package org.teamapps.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonTypeInfoTest {

	@Test
	public void testTypeInfoOnObjectReference() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(new ObjectReferencePojo(new Pojo()));
		System.out.println(json);
		assertThat(json).contains("\"_attribute_type\":\"org.teamapps.json.JacksonTypeInfoTest$Pojo\"");
	}

	@Test
	public void testClassLevelTypeInfo() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(new ClassLevelTypeInfoPojo());
		System.out.println(json);
		assertThat(json).contains("\"_class_type\":\"org.teamapps.json.JacksonTypeInfoTest$ClassLevelTypeInfoPojo\"");
	}

	@Test
	public void testObjectListTypeInfo() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(new ClassLevelTypeInfoPojo());
		System.out.println(json);
		assertThat(json).contains("\"_class_type\":\"org.teamapps.json.JacksonTypeInfoTest$ClassLevelTypeInfoPojo\"");
	}

	@Test
	public void attributeLevelWins() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(new ObjectListReferencePojo(Arrays.asList(new Pojo(), new Pojo())));
		System.out.println(json);
		assertThat(json).isEqualTo("{\"x\":[{\"_list_attribute_type\":\"org.teamapps.json.JacksonTypeInfoTest$Pojo\",\"x\":123},{\"_list_attribute_type\":\"org.teamapps.json"
				+ ".JacksonTypeInfoTest$Pojo\",\"x\":123}]}");
	}

	@Test
	public void testConcreteClassReferenceToAnnotatedObject() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(new ConcreteReferencePojo(new ClassLevelTypeInfoPojo()));
		System.out.println(json);
		assertThat(json).isEqualTo("{\"x\":{\"_class_type\":\"org.teamapps.json.JacksonTypeInfoTest$ClassLevelTypeInfoPojo\",\"x\":123}}");
	}

	public static class ObjectReferencePojo {

		@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "_attribute_type")
		@JsonInclude(JsonInclude.Include.NON_NULL)
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

	public static class ObjectListReferencePojo {

		@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "_list_attribute_type")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		public List x;

		public ObjectListReferencePojo() {
		}

		public ObjectListReferencePojo(List x) {
			this.x = x;
		}

		public List getX() {
			return x;
		}

		public void setX(List x) {
			this.x = x;
		}
	}

	public static class ConcreteReferencePojo {

		public ClassLevelTypeInfoPojo x;

		public ConcreteReferencePojo() {
		}

		public ConcreteReferencePojo(ClassLevelTypeInfoPojo x) {
			this.x = x;
		}

		public ClassLevelTypeInfoPojo getX() {
			return x;
		}

		public void setX(ClassLevelTypeInfoPojo x) {
			this.x = x;
		}
	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "_class_type")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ClassLevelTypeInfoPojo {
		int x = 123;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}
	}

	public static class Pojo {
		int x = 123;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}
	}
}
