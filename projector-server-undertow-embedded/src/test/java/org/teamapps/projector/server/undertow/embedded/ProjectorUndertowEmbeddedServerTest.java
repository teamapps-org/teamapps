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
package org.teamapps.projector.server.undertow.embedded;

import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.icon.material.MaterialIconStyles;
import org.teamapps.projector.notification.NotificationPosition;
import org.teamapps.projector.notification.Notifications;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.projector.server.webcontroller.WebController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProjectorUndertowEmbeddedServerTest {

	private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

	public static void main(String[] args) throws Exception {
		WebController controller = (SessionContext sessionContext) -> {
			EXECUTOR.scheduleAtFixedRate(() -> {
				for (NotificationPosition pos : NotificationPosition.values()) {
					Notifications.showNotification(sessionContext, pos, MaterialIcon.MESSAGE.withStyle(MaterialIconStyles.GRADIENT_RED), "Hello World", null);
				}
			}, 2, 2, TimeUnit.SECONDS);
		};
		ProjectorUndertowEmbeddedServer.builder(controller)
				.build()
				.start();
	}

}
