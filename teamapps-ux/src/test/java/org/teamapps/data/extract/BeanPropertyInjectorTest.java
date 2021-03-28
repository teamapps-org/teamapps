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
package org.teamapps.data.extract;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BeanPropertyInjectorTest {

	@Test
	public void testSetValueUsingProperty() throws Exception {
		BeanPropertyInjector<A> injector = new BeanPropertyInjector<>();
		A record = new A();
		injector.setValue(record, "q", "newQValue");
		assertEquals("newQValue", record.q);
	}

	@Test
	public void testNotSetPublicFieldValue() throws Exception {
		BeanPropertyInjector<A> injector = new BeanPropertyInjector<>();
		A record = new A();
		injector.setValue(record, "publicField", 9);
		assertEquals(1337, record.publicField);
	}

	@Test
	public void testSetPublicFieldValue() throws Exception {
		BeanPropertyInjector<A> injector = new BeanPropertyInjector<>(true);
		A record = new A();
		injector.setValue(record, "publicField", 9);
		assertEquals(9, record.publicField);
	}

	@Test
	public void testValueInjectorsAreCachedSeparatelyDependingOnFallbackToFields() throws Exception {
		BeanPropertyInjector<A> injector = new BeanPropertyInjector<>();
		A record = new A();
		injector.setValue(record, "x", "foo");
		assertEquals("xValue", record.x);
		BeanPropertyInjector<A> injector2 = new BeanPropertyInjector<>(true);
		injector2.setValue(record, "x", "bar");
		assertEquals("bar", record.x);
	}

	@Test
	public void testDoesNotChangeFinalFields() throws Exception {
		BeanPropertyInjector<A> injector = new BeanPropertyInjector<>(true);
		A record = new A();
		injector.setValue(record, "finalField", "foo");
		Assert.assertEquals("finalFieldValue", record.finalField);
	}

	@Test
	public void testSetPrivateFieldValue() throws Exception {
		BeanPropertyInjector<A> injector = new BeanPropertyInjector<>(true);
		A record = new A();
		injector.setValue(record, "privateField", 7);
		assertEquals(7, record.privateField);
	}

	@Test
	public void testAddCustomValueInjector() throws Exception {
		BeanPropertyInjector<A> injector = new BeanPropertyInjector<>();
		injector.addProperty("blah", (record, value) -> record.custom = (String) value);
		A record = new A();
		injector.setValue(record, "blah", "blub");
		assertEquals("blub", record.custom);
	}

	@Test
	public void testAddCustomValueInjectorOverwritesProperty() throws Exception {
		BeanPropertyInjector<A> injector = new BeanPropertyInjector<>();
		injector.addProperty("q", (record, value) -> record.custom = (String) value);
		A record = new A();
		injector.setValue(record, "q", "blub");
		assertEquals("qValue", record.q);
		assertEquals("blub", record.custom);
	}

	@SuppressWarnings("FieldMayBeFinal")
	public static class A {
		private String q = "qValue";
		public int publicField = 1337;
		private int privateField = 2337;
		private String custom;
		private String x = "xValue";
		private final String finalField = "finalFieldValue";

		public void setQ(String q) {
			this.q = q;
		}

	}

}
