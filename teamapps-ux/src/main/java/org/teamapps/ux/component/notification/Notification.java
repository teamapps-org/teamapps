/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.dto.UiEntranceAnimation;
import org.teamapps.dto.UiExitAnimation;
import org.teamapps.dto.UiNotification;
import org.teamapps.icons.api.Icon;
import org.teamapps.common.format.Color;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.template.Template;

import static org.teamapps.util.UiUtil.createUiColor;

public class Notification<RECORD> {

	private Template template;
	private RECORD record;
	private PropertyExtractor<RECORD> propertyExtractor = new BeanPropertyExtractor<>();
	private NotificationPosition position = NotificationPosition.TOP_RIGHT;
	private Color backgroundColor = Color.WHITE;
	private int displayTimeInMillis = 3000;
	private boolean dismissable = true;
	private boolean showProgressBar = true;
	private UiEntranceAnimation entranceAnimation = UiEntranceAnimation.FADE_IN;
	private UiExitAnimation exitAnimation = UiExitAnimation.FADE_OUT;

	/**
	 * Used to display a simple message (text only).
	 * @param message Normally a String, but may be any object (using toString()).
	 */
	public Notification(RECORD message) {
		this.template = null;
		this.record = message;
	}

	public Notification(Template template, RECORD record) {
		this.template = template;
		this.record = record;
	}

	public static Notification createWithIconAndCaption(Icon icon, String text) {
		return new Notification<>(BaseTemplate.NOTIFICATION_ICON_CAPTION, new BaseTemplateRecord(icon, text));
	}

	public static Notification createWithIconAndTextAndDescription(Icon icon, String text, String description) {
		return new Notification<>(BaseTemplate.NOTIFICATION_ICON_CAPTION_DESCRIPTION, new BaseTemplateRecord(icon, text, description));
	}

	public UiNotification createUiNotification() {
		Object values;
		if (template != null) {
			values = propertyExtractor.getValues(record, template.getDataKeys());
		} else {
			values = "" + record;
		}
		UiNotification uiNotification = new UiNotification(this.template != null ? this.template.createUiTemplate() : null, values);
		uiNotification.setPosition(position.toUiNotificationPosition());
		uiNotification.setBackgroundColor(createUiColor(backgroundColor));
		uiNotification.setDisplayTimeInMillis(displayTimeInMillis);
		uiNotification.setDismissable(dismissable);
		uiNotification.setShowProgressBar(showProgressBar);
		uiNotification.setEntranceAnimation(entranceAnimation);
		uiNotification.setExitAnimation(exitAnimation);
		return uiNotification;
	}

	public NotificationPosition getPosition() {
		return position;
	}

	public Notification setPosition(NotificationPosition position) {
		this.position = position;
		return this;
	}

	public Template getTemplate() {
		return template;
	}

	public Notification setTemplate(Template template) {
		this.template = template;
		return this;
	}

	public RECORD getRecord() {
		return record;
	}

	public Notification setRecord(RECORD record) {
		this.record = record;
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Notification setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public int getDisplayTimeInMillis() {
		return displayTimeInMillis;
	}

	public Notification setDisplayTimeInMillis(int displayTimeInMillis) {
		this.displayTimeInMillis = displayTimeInMillis;
		return this;
	}

	public boolean isDismissable() {
		return dismissable;
	}

	public Notification setDismissable(boolean dismissable) {
		this.dismissable = dismissable;
		return this;
	}

	public boolean isShowProgressBar() {
		return showProgressBar;
	}

	public Notification setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
		return this;
	}

	public UiEntranceAnimation getEntranceAnimation() {
		return entranceAnimation;
	}

	public Notification setEntranceAnimation(UiEntranceAnimation entranceAnimation) {
		this.entranceAnimation = entranceAnimation;
		return this;
	}

	public UiExitAnimation getExitAnimation() {
		return exitAnimation;
	}

	public Notification setExitAnimation(UiExitAnimation exitAnimation) {
		this.exitAnimation = exitAnimation;
		return this;
	}
}
