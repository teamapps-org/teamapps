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
package org.teamapps.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.teamapps.icons.api.IconTheme;
import org.teamapps.server.UxServerContext;
import org.teamapps.ux.session.ClientInfo;
import org.teamapps.ux.session.CommandDispatcher;
import org.teamapps.ux.session.SimpleSessionContext;
import org.teamapps.uisession.QualifiedUiSessionId;

import java.util.Collections;

public class UxTestUtil {

	public static void doWithMockedSessionContext(Runnable runnable) {
		SimpleSessionContext sessionContext = createDummySessionContext();
		sessionContext.runWithContext(runnable);
	}

	@NotNull
	public static SimpleSessionContext createDummySessionContext() {
		return new SimpleSessionContext(
				new QualifiedUiSessionId("httpSessionId", "uiSessionId"),
				new ClientInfo("ip", null, 1024, 768, 1000, 700, "en", false, "Europe/Berlin", 120, Collections.emptyList(), "userAgentString", "", Collections.emptyMap()),
				Mockito.mock(CommandDispatcher.class),
				Mockito.mock(UxServerContext.class),
				Mockito.mock(IconTheme.class),
				Mockito.mock(ObjectMapper.class)
		);
	}

}
