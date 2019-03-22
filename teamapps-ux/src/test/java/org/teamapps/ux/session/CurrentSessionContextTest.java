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
package org.teamapps.ux.session;

import org.junit.Assert;
import org.junit.Test;
import org.teamapps.testutil.UxTestUtil;

public class CurrentSessionContextTest {

	@Test
	public void test() throws Exception {
		SimpleSessionContext c1 = UxTestUtil.createDummySessionContext();
		SimpleSessionContext c2 = UxTestUtil.createDummySessionContext();

		Assert.assertNull(CurrentSessionContext.getOrNull());
		CurrentSessionContext.pushContext(c1);
		Assert.assertEquals(c1, CurrentSessionContext.getOrNull());
		Assert.assertEquals(c1, CurrentSessionContext.get());
		CurrentSessionContext.pushContext(c1);
		Assert.assertEquals(c1, CurrentSessionContext.getOrNull());
		Assert.assertEquals(c1, CurrentSessionContext.get());
		CurrentSessionContext.pushContext(c2);
		Assert.assertEquals(c2, CurrentSessionContext.getOrNull());
		Assert.assertEquals(c2, CurrentSessionContext.get());
		CurrentSessionContext.pushContext(c1);
		Assert.assertEquals(c1, CurrentSessionContext.getOrNull());
		Assert.assertEquals(c1, CurrentSessionContext.get());

		CurrentSessionContext.popContext();
		Assert.assertEquals(c2, CurrentSessionContext.getOrNull());
		Assert.assertEquals(c2, CurrentSessionContext.get());
		CurrentSessionContext.popContext();
		Assert.assertEquals(c1, CurrentSessionContext.getOrNull());
		Assert.assertEquals(c1, CurrentSessionContext.get());
		CurrentSessionContext.popContext();
		Assert.assertEquals(c1, CurrentSessionContext.getOrNull());
		Assert.assertEquals(c1, CurrentSessionContext.get());
		CurrentSessionContext.popContext();
		Assert.assertNull(CurrentSessionContext.getOrNull());
	}
}
