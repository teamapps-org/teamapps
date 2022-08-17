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
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiNotification;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.TemplateField;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;

public class Notification extends AbstractComponent {
	public final Event<Void> onOpened = new Event<>();
	public final Event<Boolean> onClosed = new Event<>();

	private boolean showing;

	private Color backgroundColor = null;
	private Spacing padding = null;
	private int displayTimeInMillis = 3000;
	private boolean dismissible = true;
	private boolean showProgressBar = true;

	private Component content;

	public Notification() {
	}

	public Notification(Component content) {
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

	public UiNotification createUiComponent() {
		UiNotification ui = new UiNotification();
		mapAbstractUiComponentProperties(ui);
		ui.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		ui.setPadding(padding != null ? padding.createUiSpacing() : null);
		ui.setDisplayTimeInMillis(displayTimeInMillis);
		ui.setDismissible(dismissible);
		ui.setProgressBarVisible(showProgressBar);
		ui.setContent(content != null ? content.createUiReference() : null);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		if (event instanceof UiNotification.OpenedEvent) {
			this.showing = true;
			onOpened.fire(null);
		} else if (event instanceof UiNotification.ClosedEvent) {
			this.showing = false;
			onClosed.fire(((UiNotification.ClosedEvent) event).getByUser());
		}
	}

	public void close() {
		queueCommandIfRendered(() -> new UiNotification.CloseCommand());
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Notification setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiComponent()));
		return this;
	}

	public Spacing getPadding() {
		return padding;
	}

	public Notification setPadding(Spacing padding) {
		this.padding = padding;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiComponent()));
		return this;
	}

	public int getDisplayTimeInMillis() {
		return displayTimeInMillis;
	}

	public Notification setDisplayTimeInMillis(int displayTimeInMillis) {
		this.displayTimeInMillis = displayTimeInMillis;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiComponent()));
		return this;
	}

	public boolean isDismissible() {
		return dismissible;
	}

	public Notification setDismissible(boolean dismissible) {
		this.dismissible = dismissible;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiComponent()));
		return this;
	}

	public boolean isShowProgressBar() {
		return showProgressBar;
	}

	public Notification setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiComponent()));
		return this;
	}

	public Component getContent() {
		return content;
	}

	public Notification setContent(Component content) {
		this.content = content;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiComponent()));
		return this;
	}

	public boolean isShowing() {
		return showing;
	}
}
