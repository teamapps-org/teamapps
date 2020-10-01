/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.field.datetime;

import org.teamapps.dto.AbstractUiDateField;
import org.teamapps.dto.UiEvent;
import org.teamapps.event.Event;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.SpecialKey;
import org.teamapps.ux.component.field.TextInputHandlingField;

public abstract class AbstractDateField<FIELD extends AbstractDateField<FIELD, VALUE>, VALUE> extends AbstractField<VALUE> implements TextInputHandlingField {
	public final Event<String> onTextInput = new Event<>();
	public final Event<SpecialKey> onSpecialKeyPressed = new Event<>();
	protected boolean showDropDownButton = true;
	protected boolean showClearButton = false;
	protected boolean favorPastDates = false; // TODO: fix in trivial-components!!!
	protected String dateFormat = null; // if null, UiConfiguration.dateFormat applies

	public AbstractDateField() {
		super();
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		defaultHandleTextInputEvent(event);
	}

	protected void mapAbstractDateFieldUiValues(AbstractUiDateField dateField) {
		mapAbstractFieldAttributesToUiField(dateField);
		dateField.setShowDropDownButton(showDropDownButton);
		dateField.setFavorPastDates(favorPastDates);
		dateField.setDateFormat(dateFormat);
		dateField.setShowClearButton(showClearButton);
	}

	public boolean isShowDropDownButton() {
		return showDropDownButton;
	}

	public void setShowDropDownButton(boolean showDropDownButton) {
		this.showDropDownButton = showDropDownButton;
		queueCommandIfRendered(() -> new AbstractUiDateField.SetShowDropDownButtonCommand(getId(), showDropDownButton));
	}

	public boolean isFavorPastDates() {
		return favorPastDates;
	}

	public void setFavorPastDates(boolean favorPastDates) {
		this.favorPastDates = favorPastDates;
		queueCommandIfRendered(() -> new AbstractUiDateField.SetFavorPastDatesCommand(getId(), favorPastDates));
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
		queueCommandIfRendered(() -> new AbstractUiDateField.SetDateFormatCommand(getId(), dateFormat));
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public void setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		queueCommandIfRendered(() -> new AbstractUiDateField.SetShowClearButtonCommand(getId(), showClearButton));
	}

	@Override
	public Event<String> onTextInput() {
		return onTextInput;
	}

	@Override
	public Event<SpecialKey> onSpecialKeyPressed() {
		return onSpecialKeyPressed;
	}
}
