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
package org.teamapps.ux.component.grid.bootstrap;

import org.junit.Assert;
import org.junit.Test;
import org.teamapps.testutil.UxTestUtil;
import org.teamapps.ux.component.dummy.DummyComponent;

public class BootstrapishGridLayoutTest {

	@Test
	public void testApi() throws Exception {
		UxTestUtil.doWithMockedSessionContext(() -> {
			BootstrapishGridLayout layout = new BootstrapishGridLayout()
					.addRow()
					.addPlacement(new DummyComponent()).offsetMd(6).colMd(6)
					.addPlacement(new DummyComponent()).offsetMd(3).colMd(3)
					.addRow()
					.addPlacement(new DummyComponent()).offsetSm(3).colSm(3)
					.addPlacement(new DummyComponent()).offsetSm(2).colSm(4)
					.addPlacement(new DummyComponent()).offsetSm(2).colSm(4)
					.done();

			Assert.assertEquals(2, layout.getRows().size());
			Assert.assertEquals(2, layout.getRows().get(0).getPlacements().size());
			Assert.assertEquals(3, layout.getRows().get(1).getPlacements().size());
		});
	}
}
