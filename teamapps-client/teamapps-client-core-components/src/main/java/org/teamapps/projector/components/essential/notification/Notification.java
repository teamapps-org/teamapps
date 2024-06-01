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

import org.teamapps.common.format.Color;
import org.teamapps.icons.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.components.essential.CoreComponentLibrary;
import org.teamapps.projector.components.essential.dto.DtoNotification;
import org.teamapps.projector.components.essential.dto.DtoNotificationClientObjectChannel;
import org.teamapps.projector.components.essential.dto.DtoNotificationEventHandler;
import org.teamapps.projector.components.essential.field.TemplateField;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.format.Spacing;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class Notification extends AbstractComponent implements DtoNotificationEventHandler {

	private final DtoNotificationClientObjectChannel clientObjectChannel = new DtoNotificationClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Void> onOpened = new ProjectorEvent<>(clientObjectChannel::toggleOpenedEvent);
	public final ProjectorEvent<Boolean> onClosed = new ProjectorEvent<>(clientObjectChannel::toggleClosedEvent);

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
		TemplateField<BaseTemplateRecord<Void>> templateField = new TemplateField<>(BaseTemplates.NOTIFICATION_ICON_CAPTION);
		templateField.setValue(new BaseTemplateRecord<>(icon, text));
		Notification notification = new Notification();
		notification.setContent(templateField);
		return notification;
	}

	public static Notification createWithIconAndTextAndDescription(Icon<?, ?> icon, String text, String description) {
		TemplateField<BaseTemplateRecord<Void>> templateField = new TemplateField<>(BaseTemplates.NOTIFICATION_ICON_CAPTION_DESCRIPTION);
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
		ui.setContent(content != null ? content : null);
		return ui;
	}

	@Override
	public void handleOpened() {
		this.showing = true;
		onOpened.fire(null);
	}

	@Override
	public void handleClosed(boolean byUser) {
		this.showing = false;
		onClosed.fire(byUser);
	}

	public void close() {
		clientObjectChannel.close();
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Notification setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		clientObjectChannel.update(createConfig());
		return this;
	}

	public Spacing getPadding() {
		return padding;
	}

	public Notification setPadding(Spacing padding) {
		this.padding = padding;
		clientObjectChannel.update(createConfig());
		return this;
	}

	public int getDisplayTimeInMillis() {
		return displayTimeInMillis;
	}

	public Notification setDisplayTimeInMillis(int displayTimeInMillis) {
		this.displayTimeInMillis = displayTimeInMillis;
		clientObjectChannel.update(createConfig());
		return this;
	}

	public boolean isDismissible() {
		return dismissible;
	}

	public Notification setDismissible(boolean dismissible) {
		this.dismissible = dismissible;
		clientObjectChannel.update(createConfig());
		return this;
	}

	public boolean isShowProgressBar() {
		return showProgressBar;
	}

	public Notification setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
		clientObjectChannel.update(createConfig());
		return this;
	}

	public Component getContent() {
		return content;
	}

	public Notification setContent(Component content) {
		this.content = content;
		clientObjectChannel.update(createConfig());
		return this;
	}

	public void show() {
		// TODO
	}

	public boolean isShowing() {
		return showing;
	}

}
