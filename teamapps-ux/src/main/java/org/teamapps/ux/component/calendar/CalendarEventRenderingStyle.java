/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import org.teamapps.dto.UiCalendarEventRenderingStyle;

public enum CalendarEventRenderingStyle { // see http://fullcalendar.io/docs/event_rendering/Background_Events/
	/**
	 * normal event display
	 */
	DEFAULT,
	/**
	 * only show colored shadow for the event. this may useful for subtly showing a secondary calendar with anonymous events inside the main one.
	 */
	BACKGROUND,
	/**
	 * highlight the whole calendar except the time period of the event.
	 */
	INVERSE_BACKGROUND;

	public UiCalendarEventRenderingStyle toUiCalendarEventRenderingStyle() {
		return UiCalendarEventRenderingStyle.valueOf(this.name());
	}
}
