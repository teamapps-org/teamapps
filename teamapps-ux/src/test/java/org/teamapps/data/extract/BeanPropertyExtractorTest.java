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
package org.teamapps.data.extract;

import org.junit.Test;

import static org.junit.Assert.*;

public class BeanPropertyExtractorTest {

	@Test
	public void testGetValue() throws Exception {
		BeanPropertyExtractor<Object> extractor = new BeanPropertyExtractor<>();
		Object value = extractor.getValue(new A(), "q");
		assertEquals("qValue", value);
	}

	@Test
	public void testWontGetFieldValue() throws Exception {
		BeanPropertyExtractor<Object> extractor = new BeanPropertyExtractor<>();
		Object value = extractor.getValue(new A(), "r");
		assertNull(value);
		// see debug log!
	}

	@Test
	public void testGetsBooleanGetter() throws Exception {
		BeanPropertyExtractor<Object> extractor = new BeanPropertyExtractor<>();
		Object value = extractor.getValue(new A(), "s");
		assertEquals(true, value);
	}

	@Test
	public void testAddCustomPropertyExtractor() throws Exception {
		BeanPropertyExtractor<Object> extractor = new BeanPropertyExtractor<>();
		extractor.addProperty("blah", (object) -> "blahValue");
		Object value = extractor.getValue(new A(), "blah");
		assertEquals("blahValue", value);
	}

	@Test
	public void testAddCustomPropertyExtractorOverwritesProperty() throws Exception {
		BeanPropertyExtractor<Object> extractor = new BeanPropertyExtractor<>();
		extractor.addProperty("q", (object) -> "overwrittenQValue");
		Object value = extractor.getValue(new A(), "q");
		assertEquals("overwrittenQValue", value);
	}

	public static class A {
		private final String q = "qValue";
		private final String r = "rValue";

		public String getQ() {
			return q;
		}

		public boolean isS() {
			return true;
		}
	}
	
}
