/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.TemplateField;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;

import static org.teamapps.util.UiUtil.createUiColor;

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

	public static Notification createWithIconAndCaption(Icon icon, String text) {
		return createWithIconAndTextAndDescription(icon, text, null);
	}

	public static Notification createWithIconAndTextAndDescription(Icon icon, String text, String description) {
		TemplateField<BaseTemplateRecord<Void>> templateField = new TemplateField<>(BaseTemplate.LIST_ITEM_MEDIUM_ICON_TWO_LINES);
		templateField.setValue(new BaseTemplateRecord<>(icon, text, description));
		Notification notification = new Notification();
		notification.setContent(templateField);
		return notification;
	}

	public UiNotification createUiComponent() {
		UiNotification ui = new UiNotification();
		mapAbstractUiComponentProperties(ui);
		ui.setBackgroundColor(createUiColor(backgroundColor));
		ui.setPadding(padding != null ? padding.createUiSpacing() : null);
		ui.setDisplayTimeInMillis(displayTimeInMillis);
		ui.setDismissible(dismissible);
		ui.setShowProgressBar(showProgressBar);
		ui.setContent(content != null ? content.createUiReference() : null);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_NOTIFICATION_OPENED: {
				this.showing = true;
				onOpened.fire(null);
				break;
			}
			case UI_NOTIFICATION_CLOSED: {
				this.showing = false;
				onClosed.fire(((UiNotification.ClosedEvent) event).getByUser());
				break;
			}
		}
	}

	public void close() {
		queueCommandIfRendered(() -> new UiNotification.CloseCommand(getId()));
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Notification setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(getId(), createUiComponent()));
		return this;
	}

	public Spacing getPadding() {
		return padding;
	}

	public Notification setPadding(Spacing padding) {
		this.padding = padding;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(getId(), createUiComponent()));
		return this;
	}

	public int getDisplayTimeInMillis() {
		return displayTimeInMillis;
	}

	public Notification setDisplayTimeInMillis(int displayTimeInMillis) {
		this.displayTimeInMillis = displayTimeInMillis;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(getId(), createUiComponent()));
		return this;
	}

	public boolean isDismissible() {
		return dismissible;
	}

	public Notification setDismissible(boolean dismissible) {
		this.dismissible = dismissible;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(getId(), createUiComponent()));
		return this;
	}

	public boolean isShowProgressBar() {
		return showProgressBar;
	}

	public Notification setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(getId(), createUiComponent()));
		return this;
	}

	public Component getContent() {
		return content;
	}

	public Notification setContent(Component content) {
		this.content = content;
		queueCommandIfRendered(() -> new UiNotification.UpdateCommand(getId(), createUiComponent()));
		return this;
	}

	public boolean isShowing() {
		return showing;
	}
}
