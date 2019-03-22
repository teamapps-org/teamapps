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
package org.teamapps.ux.component.field.datetime;

import org.teamapps.dto.UiField;
import org.teamapps.dto.UiInstantDateTimeField;

import java.time.Instant;

public class InstantDateTimeField extends AbstractDateTimeField<InstantDateTimeField, Instant> {

	protected String timeZoneId = null; // if null, the SessionContext's value applies

	public InstantDateTimeField() {
		super();
	}

	@Override
	public UiField createUiComponent() {
		UiInstantDateTimeField uiField = new UiInstantDateTimeField(getId());
		mapAbstractDateTimeFieldUiValues(uiField);
		uiField.setTimeZoneId(timeZoneId);
		return uiField;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
		queueCommandIfRendered(() -> new UiInstantDateTimeField.SetTimeZoneIdCommand(getId(), timeZoneId));
	}

	@Override
	public Instant convertUiValueToUxValue(Object value) {
		if (value == null) {
			return null;
		} else {
			return Instant.ofEpochMilli(((Number) value).longValue());
		}
	}

}
