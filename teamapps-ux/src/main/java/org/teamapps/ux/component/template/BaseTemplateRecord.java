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
package org.teamapps.ux.component.template;

import org.teamapps.icons.Icon;

public class BaseTemplateRecord<PAYLOAD> implements PayloadProvider<PAYLOAD> {

	private Icon icon;
	private String image;
	private String caption;
	private String description;
	private String badge;
	private String ariaLabel;
	private String title;

	private PAYLOAD payload;

	public BaseTemplateRecord() {
	}

	public BaseTemplateRecord(String caption) {
		this(null, null, caption, null, (String) null);
	}

	public BaseTemplateRecord(Icon icon) {
		this(icon, null);
	}

	public BaseTemplateRecord(String caption, PAYLOAD payload) {
		this(null, null, caption, null, null, payload);
	}

	public BaseTemplateRecord(Icon icon, String caption) {
		this(icon, null, caption, null, null);
	}

	public BaseTemplateRecord(Icon icon, String caption, PAYLOAD payload) {
		this(icon, null, caption, null, null, payload);
	}

	public BaseTemplateRecord(Icon icon, String caption, String description) {
		this(icon, null, caption, description, null);
	}

	public BaseTemplateRecord(Icon icon, String caption, String description, PAYLOAD payload) {
		this(icon, null, caption, description, null, payload);
	}

	public BaseTemplateRecord(Icon icon, String caption, String description, String badge) {
		this(icon, null, caption, description, badge);
	}

	public BaseTemplateRecord(String image, String caption) {
		this(null, image, caption, null, (String) null);
	}

	public BaseTemplateRecord(String image, String caption, PAYLOAD payload) {
		this(image, caption, null, payload);
	}

	public BaseTemplateRecord(String image, String caption, String description) {
		this(null, image, caption, description, (String) null);
	}

	public BaseTemplateRecord(String image, String caption, String description, PAYLOAD payload) {
		this(null, image, caption, description, null, payload);
	}

	public BaseTemplateRecord(String image, String caption, String description, String badge) {
		this(null, image, caption, description, badge);
	}

	public BaseTemplateRecord(String image, String caption, String description, String badge, PAYLOAD payload) {
		this(null, image, caption, description, null, payload);
	}

	public BaseTemplateRecord(Icon icon, String image, String caption, String description, String badge) {
		this(icon, image, caption, description, badge, null);
	}

	public BaseTemplateRecord(Icon icon, String image, String caption, String description, String badge, PAYLOAD payload) {
		this.icon = icon;
		this.image = image;
		this.caption = caption;
		this.description = description;
		this.badge = badge;
		this.payload = payload;
	}

	@Override
	public String toString() {
		return caption != null ? caption : "<no caption>";
	}

	public Icon getIcon() {
		return icon;
	}

	public BaseTemplateRecord<PAYLOAD> setIcon(Icon icon) {
		this.icon = icon;
		return this;
	}

	public String getImage() {
		return image;
	}

	public BaseTemplateRecord<PAYLOAD> setImage(String image) {
		this.image = image;
		return this;
	}

	public String getCaption() {
		return caption;
	}

	public BaseTemplateRecord<PAYLOAD> setCaption(String caption) {
		this.caption = caption;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public BaseTemplateRecord<PAYLOAD> setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getBadge() {
		return badge;
	}

	public BaseTemplateRecord<PAYLOAD> setBadge(String badge) {
		this.badge = badge;
		return this;
	}

	public PAYLOAD getPayload() {
		return payload;
	}

	public BaseTemplateRecord<PAYLOAD> setPayload(PAYLOAD payload) {
		this.payload = payload;
		return this;
	}

	public String getAriaLabel() {
		return ariaLabel;
	}

	public void setAriaLabel(String ariaLabel) {
		this.ariaLabel = ariaLabel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
