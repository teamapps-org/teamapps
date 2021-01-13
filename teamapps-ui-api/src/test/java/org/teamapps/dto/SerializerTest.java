/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.teamapps.dto.testpojo.MyEnum;
import org.teamapps.dto.testpojo.ObjectReferencePojo;
import org.teamapps.dto.testpojo.Pojo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SerializerTest {

	@Test
	public void serializePojo() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ObjectReferencePojo(new Pojo()));
		Assert.assertEquals("{\"o\":{\"_type\":\"org.teamapps.dto.testpojo.Pojo\",\"x\":\"pojo\"}}", json);
	}

	@Test
	public void serializeString() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ObjectReferencePojo("myString"));
		Assert.assertEquals("{\"o\":\"myString\"}", json);
	}

	@Test
	public void serializeInteger() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ObjectReferencePojo(123));
		Assert.assertEquals("{\"o\":123}", json);
	}

	@Test
	public void serializeList() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ObjectReferencePojo(Arrays.asList(123, "myString", new Pojo(), null, Collections.singletonList(new Pojo()))));
		Assert.assertEquals("{\"o\":[123,\"myString\",{\"_type\":\"org.teamapps.dto.testpojo.Pojo\",\"x\":\"pojo\"},null,[{\"_type\":\"org.teamapps.dto.testpojo.Pojo\",\"x\":\"pojo\"}]]}", json);
	}

	@Test
	public void serializeMap() throws Exception {
		Map<Object, Object> map = new LinkedHashMap<>();
		map.put("stringKey", "stringValue");
		map.put("pojoKey", new Pojo());
		map.put("nullKey", null);
		HashMap<String, Object> nestedMap = new HashMap<>();
		nestedMap.put("aKey", "aValue");
		nestedMap.put("aPojoKey", new Pojo());
		map.put("nestedMapKey", nestedMap);
		String json = new ObjectMapper().writeValueAsString(new ObjectReferencePojo(map));
		Assert.assertEquals("{\"o\":{\"stringKey\":\"stringValue\",\"pojoKey\":{\"_type\":\"org.teamapps.dto.testpojo.Pojo\",\"x\":\"pojo\"},\"nullKey\":null,\"nestedMapKey\":{\"aKey\":\"aValue\","
				+ "\"aPojoKey\":{\"_type\":\"org.teamapps.dto.testpojo.Pojo\",\"x\":\"pojo\"}}}}", json);
	}

	@Test
	public void serializeUiObject() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ObjectReferencePojo(new UiCurrencyValue(123, "EUR")));
		Assert.assertEquals("{\"o\":{\"_type\":\"UiCurrencyValue\",\"value\":123,\"currencyCode\":\"EUR\"}}", json);
	}

	@Test
	public void serializeEnum() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new ObjectReferencePojo(MyEnum.A));
		Assert.assertEquals("{\"o\":{\"_type\":\"org.teamapps.dto.testpojo.MyEnum\",\"_name\":\"A\",\"i\":111,\"s\":\"a\"}}", json);
	}

	// ===========

	@Test
	public void deserializePojo() throws Exception {
		TeamAppsJacksonTypeIdResolver.registerPojoClass(Pojo.class);
		ObjectReferencePojo p = new ObjectMapper().readValue("{\"o\":{\"_type\":\"org.teamapps.dto.testpojo.Pojo\",\"x\":\"xValue\"}}", ObjectReferencePojo.class);
		Assert.assertTrue(p.getO() instanceof Pojo);
		Assert.assertEquals("xValue", ((Pojo) p.getO()).getX());
	}

	@Test
	public void deserializeString() throws Exception {
		ObjectReferencePojo p = new ObjectMapper().readValue("{\"o\":\"myString\"}", ObjectReferencePojo.class);
		Assert.assertEquals("myString", p.getO());
	}

	@Test
	public void deserializeInteger() throws Exception {
		ObjectReferencePojo p = new ObjectMapper().readValue("{\"o\":123}", ObjectReferencePojo.class);
		Assert.assertEquals(123, p.getO());
	}

	@Test
	public void deserializeList() throws Exception {
		TeamAppsJacksonTypeIdResolver.registerPojoClass(Pojo.class);
		ObjectReferencePojo p = new ObjectMapper().readValue("{\"o\":[123,\"myString\",{\"_type\":\"org.teamapps.dto.testpojo.Pojo\",\"x\":\"xValue\"},null,[{\"_type\":\"org.teamapps.dto.testpojo.Pojo\",\"x\":\"nestedPojo\"}]]}",
				ObjectReferencePojo.class);
		Assert.assertTrue(p.getO() instanceof List);
		List o = (List) p.getO();
		Assert.assertEquals(123, o.get(0));
		Assert.assertEquals("myString", o.get(1));
		Assert.assertEquals("xValue", ((Pojo) o.get(2)).getX());
		Assert.assertNull(o.get(3));
		Assert.assertEquals("nestedPojo", ((Pojo) ((List) o.get(4)).get(0)).getX());
	}

	@Test
	public void deserializeMap() throws Exception {
		TeamAppsJacksonTypeIdResolver.registerPojoClass(Pojo.class);
		ObjectReferencePojo p = new ObjectMapper().readValue("{\"o\":{\"stringKey\":\"stringValue\", \"pojoKey\": {\"_type\":\"org.teamapps.dto.testpojo.Pojo\",\"x\":\"xValue\"}, \"nullKey\": null, "
						+ "\"nestedMapKey\": {\"nestedPojo\":{\"_type\":\"org.teamapps.dto.testpojo.Pojo\",\"x\":\"nestedPojoX\"}}}}",
				ObjectReferencePojo.class);
		Map map = (Map) p.getO();
		Assert.assertEquals("stringValue", map.get("stringKey"));
		Assert.assertEquals("xValue", ((Pojo) map.get("pojoKey")).getX());
		Assert.assertTrue(map.containsKey("nullKey"));
		Assert.assertNull(map.get("nullKey"));
		Assert.assertEquals("nestedPojoX", ((Pojo) ((Map) map.get("nestedMapKey")).get("nestedPojo")).getX());
	}

	@Test
	public void deserializeUiObject() throws Exception {
		ObjectReferencePojo p = new ObjectMapper().readValue("{\"o\":{\"_type\":\"UiCurrencyValue\",\"value\":123,\"currencyCode\":\"EUR\"}}", ObjectReferencePojo.class);
		Assert.assertEquals(123, ((UiCurrencyValue) p.getO()).getValue());
		Assert.assertEquals("EUR", ((UiCurrencyValue) p.getO()).getCurrencyCode());
	}

	@Test
	public void deserializeEnum() throws Exception {
		TeamAppsJacksonTypeIdResolver.registerPojoClass(MyEnum.class);
		ObjectReferencePojo p = new ObjectMapper().readValue("{\"o\":{\"_type\":\"org.teamapps.dto.testpojo.MyEnum\",\"_name\":\"A\",\"i\":111,\"s\":\"a\"}}", ObjectReferencePojo.class);
		Assert.assertEquals(MyEnum.A, p.getO());
	}

	@Test(expected = JsonMappingException.class)
	public void deserializeUnregisteredPojoClass() throws Exception {
		new ObjectMapper().readValue("{\"o\":{\"_type\":\"org.teamapps.dto.testpojo.UnregisteredPojo\"}}", ObjectReferencePojo.class);
	}
}
