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

import org.teamapps.common.format.Color;
import org.teamapps.icons.Icon;
import org.teamapps.projector.animation.EntranceAnimation;
import org.teamapps.projector.animation.ExitAnimation;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.essential.field.TemplateField;
import org.teamapps.projector.component.notification.DtoNotification;
import org.teamapps.projector.component.notification.DtoNotificationClientObjectChannel;
import org.teamapps.projector.component.notification.DtoNotificationEventHandler;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.format.Spacing;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

import java.time.Duration;

@ClientObjectLibrary(value = NotificationLibrary.class)
public class Notification extends AbstractComponent implements DtoNotificationEventHandler {

	private final DtoNotificationClientObjectChannel clientObjectChannel = new DtoNotificationClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Void> onOpened = new ProjectorEvent<>(clientObjectChannel::toggleOpenedEvent);
	public final ProjectorEvent<Boolean> onClosed = new ProjectorEvent<>(clientObjectChannel::toggleClosedEvent);

	private boolean showing;

	private Color backgroundColor = null;
	private Spacing padding = null;
	private boolean dismissible = true;
	private boolean progressBarEnabled = true;

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

	public static Notification createWithIconCaptionDescription(Icon<?, ?> icon, String caption, String description) {
		TemplateField<BaseTemplateRecord<Void>> templateField = new TemplateField<>(BaseTemplates.NOTIFICATION_ICON_CAPTION_DESCRIPTION);
		templateField.setValue(new BaseTemplateRecord<>(icon, caption, description));
		Notification notification = new Notification();
		notification.setContent(templateField);
		return notification;
	}

	public DtoNotification createConfig() {
		DtoNotification ui = new DtoNotification();
		mapAbstractConfigProperties(ui);
		ui.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		ui.setPadding(padding != null ? padding.createUiSpacing() : null);
		ui.setDismissible(dismissible);
		ui.setProgressBarVisible(progressBarEnabled);
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

	public boolean isDismissible() {
		return dismissible;
	}

	public Notification setDismissible(boolean dismissible) {
		this.dismissible = dismissible;
		clientObjectChannel.update(createConfig());
		return this;
	}

	public boolean isProgressBarEnabled() {
		return progressBarEnabled;
	}

	public Notification setProgressBarEnabled(boolean progressBarEnabled) {
		this.progressBarEnabled = progressBarEnabled;
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

	public void showTillDismissed() {
		show(Duration.ZERO);
	}

	public void show(Duration timeout) {
		show(timeout, NotificationPosition.TOP_RIGHT);
	}

	public void show(Duration timeout, NotificationPosition position) {
		show(timeout, position, EntranceAnimation.SLIDE_IN_LEFT, ExitAnimation.SLIDE_OUT_DOWN);
	}

	public void show(Duration timeout, NotificationPosition position, EntranceAnimation entranceAnimation, ExitAnimation exitAnimation) {
		clientObjectChannel.forceRender();
		clientObjectChannel.show(position, entranceAnimation, exitAnimation, timeout.toMillis());
	}

	public boolean isShowing() {
		return showing;
	}

}
