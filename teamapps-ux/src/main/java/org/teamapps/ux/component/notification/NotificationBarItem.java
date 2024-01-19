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
package org.teamapps.ux.component.notification;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiNotificationBarItem;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.animation.EntranceAnimation;
import org.teamapps.ux.component.animation.ExitAnimation;
import org.teamapps.ux.component.animation.RepeatableAnimation;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.session.SessionContext;

import java.util.UUID;

public class NotificationBarItem {

	public final Event<Void> onClicked = new Event<>();
	public final Event<Void> onActionLinkClicked = new Event<>();
	public final Event<NotificationBarItemClosedEvent.ClosingReason> onClosed = new Event<>();

	private final String uiId = UUID.randomUUID().toString();

	interface NotificationBarItemChangeListener {
		void handleChagned();
	}

	private NotificationBarItemChangeListener listener;

	private Icon<?, ?> icon;
	private String text;
	private String actionLinkText;
	private RepeatableAnimation iconAnimation;
	private boolean dismissible;
	private int displayTimeInMillis; // <= 0: display until user closes it actively
	private boolean progressBarVisible;
	private Color backgroundColor;
	private Color borderColor;
	private Color textColor;
	private Color actionLinkColor;
	private Spacing padding;
	private EntranceAnimation entranceAnimation;
	private ExitAnimation exitAnimation;

	public NotificationBarItem() {
	}

	public NotificationBarItem(String text) {
		this(null, text, true);
	}

	public NotificationBarItem(Icon<?, ?> icon, String text) {
		this(icon, text, true);
	}

	public NotificationBarItem(Icon<?, ?> icon, String text, boolean dismissible) {
		this(icon, text, dismissible, 0, false);
	}

	public NotificationBarItem(Icon<?, ?> icon, String text, boolean dismissible, int displayTimeInMillis, boolean progressBarVisible) {
		this(icon, text, dismissible, displayTimeInMillis, progressBarVisible, null, null, null, null, null);
	}

	public NotificationBarItem(Icon<?, ?> icon, String text, boolean dismissible, int displayTimeInMillis, boolean progressBarVisible, Color backgroundColor, Color borderColor, Color textColor,
							   Spacing padding, RepeatableAnimation iconAnimation) {
		this(icon, text, dismissible, displayTimeInMillis, progressBarVisible, backgroundColor, borderColor, textColor, padding, iconAnimation,
				EntranceAnimation.FADE_IN_DOWN, ExitAnimation.FADE_OUT_UP);
	}

	public NotificationBarItem(Icon<?, ?> icon, String text, boolean dismissible, int displayTimeInMillis, boolean progressBarVisible, Color backgroundColor, Color borderColor, Color textColor,
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
		ui.setActionLinkText(actionLinkText);
		ui.setDismissible(dismissible);
		ui.setDisplayTimeInMillis(displayTimeInMillis);
		ui.setProgressBarVisible(progressBarVisible);
		ui.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		ui.setBorderColor(borderColor != null ? borderColor.toHtmlColorString() : null);
		ui.setTextColor(textColor != null ? textColor.toHtmlColorString() : null);
		ui.setActionLinkColor(actionLinkColor != null ? actionLinkColor.toHtmlColorString() : null);
		ui.setPadding(padding != null ? padding.createUiSpacing() : null);
		return ui;
	}

	public String getUiId() {
		return uiId;
	}

	public void setListener(NotificationBarItemChangeListener listener) {
		this.listener = listener;
	}

	private void fireChangeEvent() {
		if (listener != null) {
			listener.handleChagned();
		}
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public NotificationBarItem setIcon(Icon<?, ?> icon) {
		this.icon = icon;
		fireChangeEvent();
		return this;
	}

	public String getText() {
		return text;
	}

	public NotificationBarItem setText(String text) {
		this.text = text;
		fireChangeEvent();
		return this;
	}

	public String getActionLinkText() {
		return actionLinkText;
	}

	public NotificationBarItem setActionLinkText(String actionLinkText) {
		this.actionLinkText = actionLinkText;
		fireChangeEvent();
		return this;
	}

	public RepeatableAnimation getIconAnimation() {
		return iconAnimation;
	}

	public NotificationBarItem setIconAnimation(RepeatableAnimation iconAnimation) {
		this.iconAnimation = iconAnimation;
		fireChangeEvent();
		return this;
	}

	public boolean isDismissible() {
		return dismissible;
	}

	public NotificationBarItem setDismissible(boolean dismissible) {
		this.dismissible = dismissible;
		fireChangeEvent();
		return this;
	}

	public int getDisplayTimeInMillis() {
		return displayTimeInMillis;
	}

	public NotificationBarItem setDisplayTimeInMillis(int displayTimeInMillis) {
		this.displayTimeInMillis = displayTimeInMillis;
		fireChangeEvent();
		return this;
	}

	public boolean isProgressBarVisible() {
		return progressBarVisible;
	}

	public NotificationBarItem setProgressBarVisible(boolean progressBarVisible) {
		this.progressBarVisible = progressBarVisible;
		fireChangeEvent();
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public NotificationBarItem setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		fireChangeEvent();
		return this;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public NotificationBarItem setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		fireChangeEvent();
		return this;
	}

	public Color getTextColor() {
		return textColor;
	}

	public NotificationBarItem setTextColor(Color textColor) {
		this.textColor = textColor;
		fireChangeEvent();
		return this;
	}

	public Color getActionLinkColor() {
		return actionLinkColor;
	}

	public NotificationBarItem setActionLinkColor(Color actionLinkColor) {
		this.actionLinkColor = actionLinkColor;
		fireChangeEvent();
		return this;
	}

	public Spacing getPadding() {
		return padding;
	}

	public NotificationBarItem setPadding(Spacing padding) {
		this.padding = padding;
		fireChangeEvent();
		return this;
	}

	public EntranceAnimation getEntranceAnimation() {
		return entranceAnimation;
	}

	public NotificationBarItem setEntranceAnimation(EntranceAnimation entranceAnimation) {
		this.entranceAnimation = entranceAnimation;
		fireChangeEvent();
		return this;
	}

	public ExitAnimation getExitAnimation() {
		return exitAnimation;
	}

	public NotificationBarItem setExitAnimation(ExitAnimation exitAnimation) {
		this.exitAnimation = exitAnimation;
		fireChangeEvent();
		return this;
	}

}
