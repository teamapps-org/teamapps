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
package org.teamapps.projector.component.treecomponents.datetime;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.field.DtoAbstractField;
import org.teamapps.projector.component.treecomponents.TreeComponentsLibrary;

import java.time.Instant;
import java.time.ZoneId;

@ClientObjectLibrary(value = TreeComponentsLibrary.class)
public class InstantDateTimeField extends AbstractDateTimeField<Instant> implements DtoInstantDateTimeFieldEventHandler {

	private final DtoInstantDateTimeFieldClientObjectChannel clientObjectChannel = new DtoInstantDateTimeFieldClientObjectChannel(getClientObjectChannel());

	protected ZoneId timeZoneId;

	public InstantDateTimeField() {
		super();
		this.timeZoneId = getSessionContext().getTimeZone();
	}

	@Override
	public DtoAbstractField createDto() {
		DtoInstantDateTimeField uiField = new DtoInstantDateTimeField();
		mapAbstractDateTimeFieldDtoValues(uiField);
		uiField.setTimeZoneId(timeZoneId.getId());
		return uiField;
	}

	public ZoneId getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(ZoneId timeZoneId) {
		this.timeZoneId = timeZoneId;
		clientObjectChannel.setTimeZoneId(timeZoneId.getId());
	}

	@Override
	public Instant doConvertClientValueToServerValue(JsonNode value) {
		if (value == null || value.isNull()) {
			return null;
		} else {
			return Instant.ofEpochMilli(value.longValue());
		}
	}
}
