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
package org.teamapps.projector.notification;

import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.session.SessionContext;

import java.time.Duration;

public class Notifications {

	public static void showNotification(SessionContext sessionContext, Icon icon, String caption) {
		sessionContext.runWithContext(() -> showNotification(icon, caption, null, true, Duration.ofSeconds(5), false));
	}

	public static void showNotification(SessionContext sessionContext, Icon icon, String caption, String description) {
		sessionContext.runWithContext(() -> showNotification(icon, caption, description, true, Duration.ofSeconds(5), false));
	}

	public static void showNotification(SessionContext sessionContext, NotificationPosition position, Icon icon, String caption, String description) {
		sessionContext.runWithContext(() -> showNotification(position, icon, caption, description, true, Duration.ofSeconds(5), false));
	}

	public static void showNotification(SessionContext sessionContext, Icon icon, String caption, String description, boolean dismissable, Duration displayTimeout, boolean progressBarEnabled) {
		sessionContext.runWithContext(() -> showNotification(NotificationPosition.TOP_RIGHT, icon, caption, description, dismissable, displayTimeout, progressBarEnabled));
	}

	public static void showNotification(SessionContext sessionContext, NotificationPosition position, Icon icon, String caption, String description, boolean dismissable, Duration displayTimeout, boolean progressBarEnabled) {
		sessionContext.runWithContext(() -> showNotification(position, icon, caption, description, dismissable, displayTimeout, progressBarEnabled));
	}

	public static void showNotification(Icon icon, String caption) {
		showNotification(icon, caption, null, true, Duration.ofSeconds(5), false);
	}

	public static void showNotification(Icon icon, String caption, String description) {
		showNotification(icon, caption, description, true, Duration.ofSeconds(5), false);
	}

	public static void showNotification(NotificationPosition position, Icon icon, String caption, String description) {
		showNotification(position, icon, caption, description, true, Duration.ofSeconds(5), false);
	}

	public static void showNotification(Icon icon, String caption, String description, boolean dismissable, Duration displayTimeout, boolean progressBarEnabled) {
		showNotification(NotificationPosition.TOP_RIGHT, icon, caption, description, dismissable, displayTimeout, progressBarEnabled);
	}

	public static void showNotification(NotificationPosition position, Icon icon, String caption, String description, boolean dismissable, Duration displayTimeout, boolean progressBarEnabled) {
		Notification notification = Notification.createWithIconCaptionDescription(icon, caption, description);
		notification.setDismissible(dismissable);
		notification.setProgressBarEnabled(progressBarEnabled);
		notification.show(displayTimeout, position);
	}
}