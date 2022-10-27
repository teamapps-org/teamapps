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
package org.teamapps.ux.session.navigation;

import org.junit.Test;
import org.teamapps.testutil.UxTestUtil;
import org.teamapps.ux.session.SessionContext;

import static org.mockito.Mockito.*;

public class RelativeRoutingHandlerRegistrationPointTest {

	@Test
	public void registerRouter() {
		RoutingHandler handlerMock = mock(RoutingHandler.class);
		SessionContext sessionContextMock = mock(SessionContext.class);
		when(sessionContextMock.registerRoutingHandler("my/{path}/x", false, handlerMock, false))
				.thenReturn(mock(RoutingHandlerRegistration.class));

		SubRouter rrrp = new SubRouter("my-prefix", sessionContextMock);

		UxTestUtil.runWithSessionContext(sessionContextMock, () -> {
			rrrp.registerRoutingHandler("my/{path}/x", false, handlerMock, false);
		});

		verify(sessionContextMock, times(1)).registerRoutingHandler("/my-prefix/my/{path}/x", false, handlerMock, false);
	}
}
