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
package org.teamapps.testutil;

import jakarta.servlet.http.HttpSession;
import org.mockito.Mockito;
import org.teamapps.icons.SessionIconProvider;
import org.teamapps.projector.server.UxServerContext;
import org.teamapps.uisession.UiSessionImpl;
import org.teamapps.projector.clientobject.ComponentLibraryRegistry;
import org.teamapps.projector.session.ClientInfo;
import org.teamapps.projector.session.SessionContext;

import java.net.URL;
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
		final ClientInfo clientInfo = new ClientInfo("ip", 1024, 768, 1000, 700, "en", false, "Europe/Berlin", 120, Collections.emptyList(), "userAgentString", Mockito.mock(URL.class), Collections.emptyMap(), TEAMAPPS_VERSION);
		ComponentLibraryRegistry componentLibraryRegistryMock = Mockito.mock(ComponentLibraryRegistry.class);
		Mockito.when(componentLibraryRegistryMock.getComponentLibraryForClientObject(Mockito.any())).thenReturn(Mockito.mock(ComponentLibraryRegistry.ComponentLibraryInfo.class));
		return new SessionContext(
				Mockito.mock(UiSessionImpl.class),
				Executors.newSingleThreadExecutor(),
				clientInfo, Mockito.mock(HttpSession.class),
				Mockito.mock(UxServerContext.class),
				Mockito.mock(SessionIconProvider.class),
				componentLibraryRegistryMock
		);
	}

}
