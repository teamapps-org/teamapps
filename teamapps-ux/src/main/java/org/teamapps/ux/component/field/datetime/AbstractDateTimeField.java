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

import org.teamapps.dto.AbstractUiDateTimeField;
import org.teamapps.ux.component.field.AbstractField;

public abstract class AbstractDateTimeField<FIELD extends AbstractDateTimeField<FIELD, VALUE>, VALUE> extends AbstractField<VALUE> {

	private boolean showDropDownButton = true;
	private boolean favorPastDates = false; // TODO: fix in trivial-components!!!
	private String dateFormat = null; // if null, the SessionContext's value applies
	private String timeFormat = null; // if null, the SessionContext's value applies

	public AbstractDateTimeField() {
		super();
	}

	protected void mapAbstractDateTimeFieldUiValues(AbstractUiDateTimeField uiField) {
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setShowDropDownButton(showDropDownButton);
		uiField.setFavorPastDates(favorPastDates);
		uiField.setDateFormat(dateFormat);
		uiField.setTimeFormat(timeFormat);
	}

	public boolean isShowDropDownButton() {
		return showDropDownButton;
	}

	public void setShowDropDownButton(boolean showDropDownButton) {
		this.showDropDownButton = showDropDownButton;
		queueCommandIfRendered(() -> new AbstractUiDateTimeField.SetShowDropDownButtonCommand(getId(), showDropDownButton));
	}

	public boolean isFavorPastDates() {
		return favorPastDates;
	}

	public void setFavorPastDates(boolean favorPastDates) {
		this.favorPastDates = favorPastDates;
		queueCommandIfRendered(() -> new AbstractUiDateTimeField.SetFavorPastDatesCommand(getId(), favorPastDates));
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
		queueCommandIfRendered(() -> new AbstractUiDateTimeField.SetDateFormatCommand(getId(), dateFormat));
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
		queueCommandIfRendered(() -> new AbstractUiDateTimeField.SetTimeFormatCommand(getId(), timeFormat));
	}

	@Override
	protected void doDestroy() {
		// nothing to do
	}
}
