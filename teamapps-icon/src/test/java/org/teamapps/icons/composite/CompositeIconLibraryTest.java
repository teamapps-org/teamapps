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
package org.teamapps.icons.composite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.teamapps.icon.TestIcon;

public class CompositeIconLibraryTest {

	@Test
	public void testDecode() throws Exception {
		CompositeIcon compositeIcon = new CompositeIconLibrary().decodeIcon("0(x)3(x)", qualifiedEncodedIcon -> TestIcon.A);
		Assertions.assertEquals(TestIcon.A, compositeIcon.getBaseIcon());
		Assertions.assertEquals(TestIcon.A, compositeIcon.getTopLeftIcon());
		Assertions.assertNull(compositeIcon.getBottomRightIcon());
		Assertions.assertNull(compositeIcon.getBottomLeftIcon());
		Assertions.assertNull(compositeIcon.getTopRightIcon());
	}

}
