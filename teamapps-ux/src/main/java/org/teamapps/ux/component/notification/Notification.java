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
package org.teamapps.ux.component.notification;

import org.teamapps.common.format.Color;
import org.teamapps.dto.JsonWrapper;
import org.teamapps.dto.DtoNotification;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.animation.EntranceAnimation;
import org.teamapps.ux.component.animation.ExitAnimation;
import org.teamapps.ux.component.annotations.ProjectorComponent;
import org.teamapps.ux.component.field.TemplateField;
import org.teamapps.ux.format.Spacing;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class Notification extends AbstractComponent {
	public final ProjectorEvent<Void> onOpened = createProjectorEventBoundToUiEvent(DtoNotification.OpenedEvent.TYPE_ID);
	public final ProjectorEvent<Boolean> onClosed = createProjectorEventBoundToUiEvent(DtoNotification.ClosedEvent.TYPE_ID);

	private boolean showing;

	private Color backgroundColor = null;
	private Spacing padding = null;
	private int displayTimeInMillis = 3000;
	private boolean dismissible = true;
	private boolean showProgressBar = true;

	private org.teamapps.ux.component.Component content;

	public Notification() {
	}

	public Notification(org.teamapps.ux.component.Component content) {
		this.content = content;
	}

	public static Notification createWithIconAndCaption(Icon<?, ?> icon, String text) {
		TemplateField<BaseTemplateRecord<Void>> templateField = new TemplateField<>(BaseTemplate.NOTIFICATION_ICON_CAPTION);
		templateField.setValue(new BaseTemplateRecord<>(icon, text));
		Notification notification = new Notification();
		notification.setContent(templateField);
		return notification;
	}

	public static Notification createWithIconAndTextAndDescription(Icon<?, ?> icon, String text, String description) {
		TemplateField<BaseTemplateRecord<Void>> templateField = new TemplateField<>(BaseTemplate.NOTIFICATION_ICON_CAPTION_DESCRIPTION);
		templateField.setValue(new BaseTemplateRecord<>(icon, text, description));
		Notification notification = new Notification();
		notification.setContent(templateField);
		return notification;
	}

	public DtoNotification createConfig() {
		DtoNotification ui = new DtoNotification();
		mapAbstractUiComponentProperties(ui);
		ui.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		ui.setPadding(padding != null ? padding.createUiSpacing() : null);
		ui.setDisplayTimeInMillis(displayTimeInMillis);
		ui.setDismissible(dismissible);
		ui.setProgressBarVisible(showProgressBar);
		ui.setContent(content != null ? content.createClientReference() : null);
		return ui;
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		switch (event.getTypeId()) {
			case DtoNotification.OpenedEvent.TYPE_ID -> {
				this.showing = true;
				onOpened.fire(null);
			}
			case DtoNotification.ClosedEvent.TYPE_ID -> {
				var e = event.as(DtoNotification.ClosedEventWrapper.class);
				this.showing = false;
				onClosed.fire(e.getByUser());
			}
		}
	}

	public void close() {
		sendCommandIfRendered(() -> new DtoNotification.CloseCommand());
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Notification setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		sendCommandIfRendered(() -> new DtoNotification.UpdateCommand(createConfig()));
		return this;
	}

	public Spacing getPadding() {
		return padding;
	}

	public Notification setPadding(Spacing padding) {
		this.padding = padding;
		sendCommandIfRendered(() -> new DtoNotification.UpdateCommand(createConfig()));
		return this;
	}

	public int getDisplayTimeInMillis() {
		return displayTimeInMillis;
	}

	public Notification setDisplayTimeInMillis(int displayTimeInMillis) {
		this.displayTimeInMillis = displayTimeInMillis;
		sendCommandIfRendered(() -> new DtoNotification.UpdateCommand(createConfig()));
		return this;
	}

	public boolean isDismissible() {
		return dismissible;
	}

	public Notification setDismissible(boolean dismissible) {
		this.dismissible = dismissible;
		sendCommandIfRendered(() -> new DtoNotification.UpdateCommand(createConfig()));
		return this;
	}

	public boolean isShowProgressBar() {
		return showProgressBar;
	}

	public Notification setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
		sendCommandIfRendered(() -> new DtoNotification.UpdateCommand(createConfig()));
		return this;
	}

	public org.teamapps.ux.component.Component getContent() {
		return content;
	}

	public Notification setContent(org.teamapps.ux.component.Component content) {
		this.content = content;
		sendCommandIfRendered(() -> new DtoNotification.UpdateCommand(createConfig()));
		return this;
	}

	public void show() {
		// TODO
	}

	public boolean isShowing() {
		return showing;
	}


	public void showNotification(Notification notification, NotificationPosition position, EntranceAnimation entranceAnimation, ExitAnimation exitAnimation) {
		// TODO
//		runWithContext(() -> {
//			sendStaticCommand(Notification.class, DtoNotification.ShowNotificationCommand.CMD_NAME, new DtoNotification.ShowNotificationCommand(notification, position.toUiNotificationPosition(), entranceAnimation.toUiEntranceAnimation(), exitAnimation.toUiExitAnimation()).getParameters());
//		});
	}

	public void showNotification(Notification notification, NotificationPosition position) {
//		runWithContext(() -> {
//			showNotification(notification, position, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}

	public void showNotification(Icon<?, ?> icon, String caption) {
//		runWithContext(() -> {
//			Notification notification = Notification.createWithIconAndCaption(icon, caption);
//			notification.setDismissible(true);
//			notification.setShowProgressBar(false);
//			notification.setDisplayTimeInMillis(5000);
//			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}

	public void showNotification(Icon<?, ?> icon, String caption, String description) {
//		runWithContext(() -> {
//			Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
//			notification.setDismissible(true);
//			notification.setShowProgressBar(false);
//			notification.setDisplayTimeInMillis(5000);
//			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}

	public void showNotification(Icon<?, ?> icon, String caption, String description, boolean dismissable, int displayTimeInMillis, boolean showProgress) {
//		runWithContext(() -> {
//			Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
//			notification.setDismissible(dismissable);
//			notification.setDisplayTimeInMillis(displayTimeInMillis);
//			notification.setShowProgressBar(showProgress);
//			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
//		});
	}
}
