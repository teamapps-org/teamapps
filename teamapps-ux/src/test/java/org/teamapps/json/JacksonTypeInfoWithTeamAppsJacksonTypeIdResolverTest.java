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
package org.teamapps.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import org.junit.Before;
import org.junit.Test;
import org.teamapps.dto.TeamAppsJacksonTypeIdResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonTypeInfoWithTeamAppsJacksonTypeIdResolverTest {

	@Before
	public void setUp() throws Exception {
		TeamAppsJacksonTypeIdResolver.registerPojoClass(Pojo.class);
		TeamAppsJacksonTypeIdResolver.registerPojoClass(ObjectReferencePojo.class);
		TeamAppsJacksonTypeIdResolver.registerPojoClass(ObjectListReferencePojo.class);
		TeamAppsJacksonTypeIdResolver.registerPojoClass(ConcreteReferencePojo.class);
		TeamAppsJacksonTypeIdResolver.registerPojoClass(ClassLevelTypeInfoPojo.class);
	}

	@Test
	public void testTypeInfoOnObjectReference() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ObjectReferencePojo(new Pojo()));
		System.out.println(json);
		assertThat(json).isEqualTo("{\"x\":{\"_attribute_type\":\"org.teamapps.json.JacksonTypeInfoWithTeamAppsJacksonTypeIdResolverTest.Pojo\",\"x\":123}}");

		ObjectReferencePojo deserializedValue = new ObjectMapper().readValue(json, ObjectReferencePojo.class);
		assertThat(deserializedValue.getX()).isInstanceOf(Pojo.class);
	}

	@Test
	public void testClassLevelTypeInfo() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ClassLevelTypeInfoPojo());
		System.out.println(json);
		assertThat(json).contains("\"_class_type\":\"org.teamapps.json.JacksonTypeInfoWithTeamAppsJacksonTypeIdResolverTest.ClassLevelTypeInfoPojo\"");

		ClassLevelTypeInfoPojo deserializedValue = new ObjectMapper().readValue(json, ClassLevelTypeInfoPojo.class);
		assertThat(deserializedValue).isInstanceOf(ClassLevelTypeInfoPojo.class);
	}

	@Test
	public void testObjectListTypeInfo() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ObjectListReferencePojo(Arrays.asList(new Pojo(), new Pojo())));
		System.out.println(json);
		assertThat(json).contains("{\"x\":[{\"_list_attribute_type\":\"org.teamapps.json.JacksonTypeInfoWithTeamAppsJacksonTypeIdResolverTest.Pojo\",\"x\":123},"
				+ "{\"_list_attribute_type\":\"org.teamapps.json.JacksonTypeInfoWithTeamAppsJacksonTypeIdResolverTest.Pojo\",\"x\":123}]}");

		ObjectListReferencePojo deserializedValue = new ObjectMapper().readValue(json, ObjectListReferencePojo.class);
		assertThat(deserializedValue.getX().get(0)).isInstanceOf(Pojo.class);
	}

	@Test
	public void attributeLevelWins() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ObjectListReferencePojo(Arrays.asList(new ClassLevelTypeInfoPojo(), new ClassLevelTypeInfoPojo())));
		System.out.println(json);
		assertThat(json).isEqualTo("{\"x\":[{\"_list_attribute_type\":\"org.teamapps.json.JacksonTypeInfoWithTeamAppsJacksonTypeIdResolverTest.ClassLevelTypeInfoPojo\",\"x\":123},{\"_list_attribute_type\":\"org.teamapps.json"
				+ ".JacksonTypeInfoWithTeamAppsJacksonTypeIdResolverTest.ClassLevelTypeInfoPojo\",\"x\":123}]}");

		ObjectListReferencePojo deserializedValue = new ObjectMapper().readValue(json, ObjectListReferencePojo.class);
		assertThat(deserializedValue.getX().get(0)).isInstanceOf(ClassLevelTypeInfoPojo.class);
	}

	@Test
	public void testConcreteClassReferenceToAnnotatedObject() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ConcreteReferencePojo(new ClassLevelTypeInfoPojo()));
		System.out.println(json);
		assertThat(json).isEqualTo("{\"x\":{\"_class_type\":\"org.teamapps.json.JacksonTypeInfoWithTeamAppsJacksonTypeIdResolverTest.ClassLevelTypeInfoPojo\",\"x\":123}}");
	}

	@Test
	public void testConcreteListReferencePojo() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ConcreteListReferencePojo(Collections.singletonList(new Pojo()), Collections.singletonList(new Pojo())));
		System.out.println(json);
		assertThat(json).isEqualTo("{\"annotated\":[{\"_list_attribute_type\":\"org.teamapps.json.JacksonTypeInfoWithTeamAppsJacksonTypeIdResolverTest.Pojo\",\"x\":123}],"
				+ "\"nonAnnotated\":[{\"x\":123}]}");

		ConcreteListReferencePojo deserializedValue = new ObjectMapper().readValue(json, ConcreteListReferencePojo.class);
		assertThat(deserializedValue.getAnnotated().get(0)).isInstanceOf(Pojo.class);
		assertThat(deserializedValue.getNonAnnotated().get(0)).isInstanceOf(Pojo.class);
	}

	@Test
	public void testObjectReferenceContainsList() throws Exception {
		List<Pojo> arrayList = new ArrayList<>();
		arrayList.add(new Pojo());
		arrayList.add(new Pojo());
		arrayList.add(new Pojo());
		String json = new ObjectMapper().writeValueAsString(new ObjectReferencePojo(arrayList));
		System.out.println(json);

		ObjectReferencePojo deserializedValue = new ObjectMapper().readValue(json, ObjectReferencePojo.class);
		assertThat(deserializedValue.getX()).isInstanceOf(ArrayList.class);
	}

	public static class ObjectReferencePojo {

		@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_attribute_type")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonTypeIdResolver(TeamAppsJacksonTypeIdResolver.class)
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

		@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_list_attribute_type")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonTypeIdResolver(TeamAppsJacksonTypeIdResolver.class)
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

	public static class ConcreteListReferencePojo {

		@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_list_attribute_type")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonTypeIdResolver(TeamAppsJacksonTypeIdResolver.class)
		public List<Pojo> annotated;

		public List<Pojo> nonAnnotated;

		public ConcreteListReferencePojo() {
		}

		public ConcreteListReferencePojo(List<Pojo> annotated, List<Pojo> nonAnnotated) {
			this.annotated = annotated;
			this.nonAnnotated = nonAnnotated;
		}

		public List<Pojo> getAnnotated() {
			return annotated;
		}

		public void setAnnotated(List<Pojo> annotated) {
			this.annotated = annotated;
		}

		public List<Pojo> getNonAnnotated() {
			return nonAnnotated;
		}

		public void setNonAnnotated(List<Pojo> nonAnnotated) {
			this.nonAnnotated = nonAnnotated;
		}
	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_class_type")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonTypeIdResolver(TeamAppsJacksonTypeIdResolver.class)
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
