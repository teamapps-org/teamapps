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
package org.teamapps.projector.components.trivial.datetime;

import org.teamapps.dto.DtoAbstractField;
import org.teamapps.projector.components.trivial.TrivialComponentsLibrary;
import org.teamapps.projector.components.trivial.dto.DtoInstantDateTimeField;
import org.teamapps.ux.component.annotations.ProjectorComponent;

import java.time.Instant;
import java.time.ZoneId;

@ProjectorComponent(library = TrivialComponentsLibrary.class)
public class InstantDateTimeField extends AbstractDateTimeField<Instant> {

	protected ZoneId timeZoneId;

	public InstantDateTimeField() {
		super();
		this.timeZoneId = getSessionContext().getTimeZone();
	}

	@Override
	public DtoAbstractField createDto() {
		DtoInstantDateTimeField uiField = new DtoInstantDateTimeField();
		mapAbstractDateTimeFieldUiValues(uiField);
		uiField.setTimeZoneId(timeZoneId.getId());
		return uiField;
	}

	public ZoneId getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(ZoneId timeZoneId) {
		this.timeZoneId = timeZoneId;
		sendCommandIfRendered(() -> new DtoInstantDateTimeField.SetTimeZoneIdCommand(timeZoneId.getId()));
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
