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
package org.teamapps.projector.session.event;

import org.teamapps.projector.component.Component;
import org.teamapps.projector.KeyEventType;

public class KeyboardEvent {

	private final KeyEventType eventType;
	private final Component sourceComponent;
	private final String code;
	private final boolean isComposing;
	private final String key;
	private final String locale;
	private final int location;
	private final boolean repeat;
	private final boolean altKey;
	private final boolean ctrlKey;
	private final boolean shiftKey;
	private final boolean metaKey;

	public KeyboardEvent(KeyEventType eventType, Component sourceComponent, String code, boolean isComposing, String key, String locale, int location, boolean repeat, boolean altKey, boolean ctrlKey, boolean shiftKey, boolean metaKey) {
		this.eventType = eventType;
		this.sourceComponent = sourceComponent;
		this.code = code;
		this.isComposing = isComposing;
		this.key = key;
		this.locale = locale;
		this.location = location;
		this.repeat = repeat;
		this.altKey = altKey;
		this.ctrlKey = ctrlKey;
		this.shiftKey = shiftKey;
		this.metaKey = metaKey;
	}

	public KeyEventType getEventType() {
		return eventType;
	}

	public Component getSourceComponent() {
		return sourceComponent;
	}

	public String getCode() {
		return code;
	}

	public boolean isComposing() {
		return isComposing;
	}

	public String getKey() {
		return key;
	}

	public String getLocale() {
		return locale;
	}

	public int getLocation() {
		return location;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public boolean isAltKey() {
		return altKey;
	}

	public boolean isCtrlKey() {
		return ctrlKey;
	}

	public boolean isShiftKey() {
		return shiftKey;
	}

	public boolean isMetaKey() {
		return metaKey;
	}

	@Override
	public String toString() {
		return "KeyboardEvent{" +
				"eventType=" + eventType +
				", sourceComponent=" + sourceComponent +
				", code='" + code + '\'' +
				", isComposing=" + isComposing +
				", key='" + key + '\'' +
				", locale='" + locale + '\'' +
				", location=" + location +
				", repeat=" + repeat +
				", altKey=" + altKey +
				", ctrlKey=" + ctrlKey +
				", shiftKey=" + shiftKey +
				", metaKey=" + metaKey +
				'}';
	}
}
