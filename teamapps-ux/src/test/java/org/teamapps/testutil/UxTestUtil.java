/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.ext.ParamConverterProvider;
import org.mockito.Mockito;
import org.teamapps.icons.SessionIconProvider;
import org.teamapps.server.UxServerContext;
import org.teamapps.uisession.UiSession;
import org.teamapps.util.threading.CloseableExecutor;
import org.teamapps.ux.session.ClientInfo;
import org.teamapps.ux.session.CurrentSessionContextTestUtil;
import org.teamapps.ux.session.SessionConfiguration;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.ux.session.navigation.Location;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static org.teamapps.common.TeamAppsVersion.TEAMAPPS_VERSION;

public class UxTestUtil {

	public static CompletableFuture<Void> doWithMockedSessionContext(Runnable runnable) {
		SessionContext sessionContext = createDummySessionContext();
		return sessionContext.runWithContext(runnable);
	}

	public static SessionContext createDummySessionContext() {
		final ClientInfo clientInfo = new ClientInfo("ip", 1024, 768, 1000, 700, "en", false, "Europe/Berlin", 120, Collections.emptyList(), "userAgentString", Mockito.mock(Location.class), Collections.emptyMap(), TEAMAPPS_VERSION);
		return new SessionContext(
				Mockito.mock(UiSession.class),
				CloseableExecutor.fromExecutorService(Executors.newSingleThreadExecutor()),
				clientInfo, SessionConfiguration.createForClientInfo(clientInfo), Mockito.mock(HttpSession.class),
				Mockito.mock(UxServerContext.class),
				Mockito.mock(SessionIconProvider.class),
				"",
				Mockito.mock(ParamConverterProvider.class)
		);
	}

	public static void runWithSessionContext(SessionContext sessionContext, Runnable runnable) {
		CurrentSessionContextTestUtil.set(sessionContext);
		try {
			runnable.run();
		} finally {
			CurrentSessionContextTestUtil.unset();
		}
	}
}
