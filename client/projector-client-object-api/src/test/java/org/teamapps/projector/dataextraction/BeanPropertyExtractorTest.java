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
package org.teamapps.projector.dataextraction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BeanPropertyExtractorTest {

	@Test
	public void testGetValue() throws Exception {
		BeanPropertyExtractor<A> extractor = new BeanPropertyExtractor<>();
		Object value = extractor.getValue(new A(), "q");
		assertEquals("qValue", value);
	}

	@Test
	public void testWontGetPrivateFieldValue() throws Exception {
		BeanPropertyExtractor<A> extractor = new BeanPropertyExtractor<>();
		Object value = extractor.getValue(new A(), "publicField");
		assertNull(value);
		// see debug log!
	}

	@Test
	public void testValueExtractorsGetCachedSeparatelyDependingOnFallbackToFields() throws Exception {
		BeanPropertyExtractor<A> extractor = new BeanPropertyExtractor<>();
		assertNull(extractor.getValue(new A(), "x"));
		BeanPropertyExtractor<A> extractor2 = new BeanPropertyExtractor<>(true);
		assertEquals("xValue", extractor2.getValue(new A(), "x"));
	}

	@Test
	public void testGetPublicFieldValue() throws Exception {
		BeanPropertyExtractor<A> extractor = new BeanPropertyExtractor<>(true);
		Object value = extractor.getValue(new A(), "publicField");
		assertEquals(1337, value);
	}

	@Test
	public void testGetPrivateFieldValue() throws Exception {
		BeanPropertyExtractor<A> extractor = new BeanPropertyExtractor<>(true);
		Object value = extractor.getValue(new A(), "privateField");
		assertEquals(2337, value);
	}

	@Test
	public void testGetsBooleanGetter() throws Exception {
		BeanPropertyExtractor<A> extractor = new BeanPropertyExtractor<>();
		Object value = extractor.getValue(new A(), "s");
		assertEquals(true, value);
	}

	@Test
	public void testAddCustomValueExtractor() throws Exception {
		BeanPropertyExtractor<A> extractor = new BeanPropertyExtractor<>();
		extractor.addProperty("blah", (object) -> "blahValue");
		Object value = extractor.getValue(new A(), "blah");
		assertEquals("blahValue", value);
	}

	@Test
	public void testAddCustomValueExtractorOverwritesProperty() throws Exception {
		BeanPropertyExtractor<A> extractor = new BeanPropertyExtractor<>();
		extractor.addProperty("q", (object) -> "overwrittenQValue");
		Object value = extractor.getValue(new A(), "q");
		assertEquals("overwrittenQValue", value);
	}

	// TODO uncomment once we have Java 14
//	@Test
//	public void testWithRecords() throws Exception {
//		BeanPropertyExtractor<R> extractor = new BeanPropertyExtractor<>();
//		Object value = extractor.getValue(new R("asdf"), "s");
//		assertEquals("asdf", value);
//	}

	public static class A {
		private final String q = "qValue";
		private final String r = "rValue";
		public int publicField = 1337;
		private final int privateField = 2337;
		private final String x = "xValue";

		public String getQ() {
			return q;
		}

		public boolean isS() {
			return true;
		}
	}

	// TODO uncomment once we have Java 14
//	public static record R (String s) {}

}
