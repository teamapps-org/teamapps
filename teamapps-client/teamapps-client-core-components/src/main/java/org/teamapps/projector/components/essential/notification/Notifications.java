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
package org.teamapps.projector.components.essential.notification;

import org.teamapps.icons.Icon;
import org.teamapps.projector.animation.EntranceAnimation;
import org.teamapps.projector.animation.ExitAnimation;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.components.essential.CoreComponentLibrary;
import org.teamapps.projector.components.essential.dto.NotificationPosition;
import org.teamapps.projector.session.SessionContext;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class Notifications {

	public static void showNotification(SessionContext sessionContext, Notification notification, NotificationPosition position, EntranceAnimation entranceAnimation, ExitAnimation exitAnimation) {
		// TODO
//		runWithContext(() -> {
//			sendStaticCommand(Notification.class, DtoNotification.ShowNotificationCommand.CMD_NAME, new DtoNotification.ShowNotificationCommand(notification, position.toUiNotificationPosition(), entranceAnimation.toUiEntranceAnimation(), exitAnimation.toUiExitAnimation()).getParameters());
//		});
	}

	public static void showNotification(SessionContext sessionContext, Notification notification, NotificationPosition position) {
//		runWithContext(() -> {
//			showNotification(notification, position, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}

	public static void showNotification(SessionContext sessionContext, Icon<?, ?> icon, String caption) {
//		runWithContext(() -> {
//			Notification notification = Notification.createWithIconAndCaption(icon, caption);
//			notification.setDismissible(true);
//			notification.setShowProgressBar(false);
//			notification.setDisplayTimeInMillis(5000);
//			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}

	public static void showNotification(SessionContext sessionContext, Icon<?, ?> icon, String caption, String description) {
//		runWithContext(() -> {
//			Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
//			notification.setDismissible(true);
//			notification.setShowProgressBar(false);
//			notification.setDisplayTimeInMillis(5000);
//			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}

	public static void showNotification(SessionContext sessionContext, Icon<?, ?> icon, String caption, String description, boolean dismissable, int displayTimeInMillis, boolean showProgress) {
//		runWithContext(() -> {
//			Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
//			notification.setDismissible(dismissable);
//			notification.setDisplayTimeInMillis(displayTimeInMillis);
//			notification.setShowProgressBar(showProgress);
//			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}

	public static void showNotification(Notification notification, NotificationPosition position, EntranceAnimation entranceAnimation, ExitAnimation exitAnimation) {
		// TODO
//		runWithContext(() -> {
//			sendStaticCommand(Notification.class, DtoNotification.ShowNotificationCommand.CMD_NAME, new DtoNotification.ShowNotificationCommand(notification, position.toUiNotificationPosition(), entranceAnimation.toUiEntranceAnimation(), exitAnimation.toUiExitAnimation()).getParameters());
//		});
	}

	public static void showNotification(Notification notification, NotificationPosition position) {
//		runWithContext(() -> {
//			showNotification(notification, position, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}

	public static void showNotification(Icon<?, ?> icon, String caption) {
//		runWithContext(() -> {
//			Notification notification = Notification.createWithIconAndCaption(icon, caption);
//			notification.setDismissible(true);
//			notification.setShowProgressBar(false);
//			notification.setDisplayTimeInMillis(5000);
//			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}

	public static void showNotification(Icon<?, ?> icon, String caption, String description) {
//		runWithContext(() -> {
//			Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
//			notification.setDismissible(true);
//			notification.setShowProgressBar(false);
//			notification.setDisplayTimeInMillis(5000);
//			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}

	public static void showNotification(Icon<?, ?> icon, String caption, String description, boolean dismissable, int displayTimeInMillis, boolean showProgress) {
//		runWithContext(() -> {
//			Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
//			notification.setDismissible(dismissable);
//			notification.setDisplayTimeInMillis(displayTimeInMillis);
//			notification.setShowProgressBar(showProgress);
//			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}
}
