/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.notification;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiNotificationBarItem;
import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.animation.EntranceAnimation;
import org.teamapps.ux.component.animation.ExitAnimation;
import org.teamapps.ux.component.animation.RepeatableAnimation;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.session.SessionContext;

import java.util.UUID;

public class NotificationBarItem {

	public final Event<Void> onClicked = new Event<>();
	public final Event<NotificationBarItemClosedEvent.ClosingReason> onClosed = new Event<>();

	private final String uiId = UUID.randomUUID().toString();

	private final Icon icon;
	private final String text;

	private final RepeatableAnimation iconAnimation;

	private final boolean dismissible;
	private final int displayTimeInMillis; // <= 0: display until user closes it actively
	private final boolean progressBarVisible;

	private final Color backgroundColor;
	private final Color borderColor;
	private final Color textColor;
	private final Spacing padding;

	private final EntranceAnimation entranceAnimation;
	private final ExitAnimation exitAnimation;

	public NotificationBarItem(Icon icon, String text) {
		this(icon, text, true);
	}

	public NotificationBarItem(Icon icon, String text, boolean dismissible) {
		this(icon, text, dismissible, 0, false);
	}

	public NotificationBarItem(Icon icon, String text, boolean dismissible, int displayTimeInMillis, boolean progressBarVisible) {
		this(icon, text, dismissible, displayTimeInMillis, progressBarVisible, null, null, null, null, null);
	}

	public NotificationBarItem(Icon icon, String text, boolean dismissible, int displayTimeInMillis, boolean progressBarVisible, Color backgroundColor, Color borderColor, Color textColor,
	                           Spacing padding, RepeatableAnimation iconAnimation) {
		this(icon, text, dismissible, displayTimeInMillis, progressBarVisible, backgroundColor, borderColor, textColor, padding, iconAnimation,
				EntranceAnimation.FADE_IN_DOWN, ExitAnimation.FADE_OUT_UP);
	}

	public NotificationBarItem(Icon icon, String text, boolean dismissible, int displayTimeInMillis, boolean progressBarVisible, Color backgroundColor, Color borderColor, Color textColor,
	                           Spacing padding, RepeatableAnimation iconAnimation, EntranceAnimation entranceAnimation, ExitAnimation exitAnimation) {
		this.icon = icon;
		this.text = text;
		this.dismissible = dismissible;
		this.displayTimeInMillis = displayTimeInMillis;
		this.progressBarVisible = progressBarVisible;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.textColor = textColor;
		this.padding = padding;
		this.iconAnimation = iconAnimation;
		this.entranceAnimation = entranceAnimation;
		this.exitAnimation = exitAnimation;
	}

	public UiNotificationBarItem toUiNotificationBarItem() {
		UiNotificationBarItem ui = new UiNotificationBarItem();
		ui.setId(uiId);
		ui.setIcon(SessionContext.current().resolveIcon(icon));
		ui.setIconAnimation(iconAnimation != null ? iconAnimation.toUiRepeatableAnimation() : null);
		ui.setText(text);
		ui.setDismissible(dismissible);
		ui.setDisplayTimeInMillis(displayTimeInMillis);
		ui.setProgressBarVisible(progressBarVisible);
		ui.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		ui.setBorderColor(borderColor != null ? borderColor.toHtmlColorString() : null);
		ui.setTextColor(textColor != null ? textColor.toHtmlColorString() : null);
		ui.setPadding(padding != null ? padding.createUiSpacing() : null);
		return ui;
	}

	public String getUiId() {
		return uiId;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getText() {
		return text;
	}

	public boolean isDismissible() {
		return dismissible;
	}

	public int getDisplayTimeInMillis() {
		return displayTimeInMillis;
	}

	public boolean isProgressBarVisible() {
		return progressBarVisible;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public Spacing getPadding() {
		return padding;
	}

	public RepeatableAnimation getIconAnimation() {
		return iconAnimation;
	}

}
