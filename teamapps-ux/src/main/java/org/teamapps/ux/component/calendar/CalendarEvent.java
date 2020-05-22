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
package org.teamapps.ux.component.calendar;

import org.teamapps.common.format.Color;
import org.teamapps.icons.api.Icon;

import java.time.Instant;

public interface CalendarEvent {

	long getStart();

	default Instant getStartInstant() {
		return Instant.ofEpochMilli(getStart());
	}

	long getEnd();

	default Instant getEndInstant() {
		return Instant.ofEpochMilli(getEnd());
	}

	boolean isAllDay();

	/**
	 * In case the event is rendered without a template, use this icon.
 	 */
	default Icon getIcon() {
		return null;
	}

	/**
	 * In case the event is rendered without a template, use this title.
	 */
	default String getTitle() {
		return null;
	}

	Color getBackgroundColor();

	Color getBorderColor();

	CalendarEventRenderingStyle getRendering();

	boolean isAllowDragOperations();
}
