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
import org.teamapps.dto.UiEventWrapper;
import org.teamapps.dto.UiNotification;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;
import org.teamapps.ux.component.field.TemplateField;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;

@TeamAppsComponent(library = CoreComponentLibrary.class)
public class Notification extends AbstractComponent {
	public final ProjectorEvent<Void> onOpened = createProjectorEventBoundToUiEvent(UiNotification.OpenedEvent.TYPE_ID);
	public final ProjectorEvent<Boolean> onClosed = createProjectorEventBoundToUiEvent(UiNotification.ClosedEvent.TYPE_ID);

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

	public UiNotification createUiClientObject() {
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
	public void handleUiEvent(UiEventWrapper event) {
		switch (event.getTypeId()) {
			case UiNotification.OpenedEvent.TYPE_ID -> {
				this.showing = true;
				onOpened.fire(null);
			}
			case UiNotification.ClosedEvent.TYPE_ID -> {
				var e = event.as(UiNotification.ClosedEventWrapper.class);
				this.showing = false;
				onClosed.fire(e.getByUser());
			}
		}
	}

	public void close() {
		sendCommandIfRendered(() -> new UiNotification.CloseCommand());
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Notification setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		sendCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiClientObject()));
		return this;
	}

	public Spacing getPadding() {
		return padding;
	}

	public Notification setPadding(Spacing padding) {
		this.padding = padding;
		sendCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiClientObject()));
		return this;
	}

	public int getDisplayTimeInMillis() {
		return displayTimeInMillis;
	}

	public Notification setDisplayTimeInMillis(int displayTimeInMillis) {
		this.displayTimeInMillis = displayTimeInMillis;
		sendCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiClientObject()));
		return this;
	}

	public boolean isDismissible() {
		return dismissible;
	}

	public Notification setDismissible(boolean dismissible) {
		this.dismissible = dismissible;
		sendCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiClientObject()));
		return this;
	}

	public boolean isShowProgressBar() {
		return showProgressBar;
	}

	public Notification setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
		sendCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiClientObject()));
		return this;
	}

	public Component getContent() {
		return content;
	}

	public Notification setContent(Component content) {
		this.content = content;
		sendCommandIfRendered(() -> new UiNotification.UpdateCommand(createUiClientObject()));
		return this;
	}

	public boolean isShowing() {
		return showing;
	}
}
