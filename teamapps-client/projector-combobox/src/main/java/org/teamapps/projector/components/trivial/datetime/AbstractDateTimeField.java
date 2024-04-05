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

import com.ibm.icu.util.ULocale;
import org.teamapps.projector.components.trivial.TrivialComponentsLibrary;
import org.teamapps.projector.components.trivial.dto.DtoAbstractDateTimeField;
import org.teamapps.ux.component.annotations.ProjectorComponent;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.session.DateTimeFormatDescriptor;

import java.util.Locale;

@ProjectorComponent(library = TrivialComponentsLibrary.class)
public abstract class AbstractDateTimeField<VALUE> extends AbstractField<VALUE> {

	private boolean showDropDownButton = true;
	private boolean favorPastDates = false;
	private ULocale locale;
	private DateTimeFormatDescriptor dateFormat;
	private DateTimeFormatDescriptor timeFormat;

	public AbstractDateTimeField() {
		super();
		this.locale = getSessionContext().getULocale();
		this.dateFormat = getSessionContext().getConfiguration().getDateFormat();
		this.timeFormat = getSessionContext().getConfiguration().getTimeFormat();
	}

	protected void mapAbstractDateTimeFieldUiValues(DtoAbstractDateTimeField uiField) {
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setShowDropDownButton(showDropDownButton);
		uiField.setFavorPastDates(favorPastDates);
		uiField.setLocale(locale.toLanguageTag());
		uiField.setDateFormat(dateFormat.toDateTimeFormatDescriptor());
		uiField.setTimeFormat(timeFormat.toDateTimeFormatDescriptor());
	}

	public boolean isShowDropDownButton() {
		return showDropDownButton;
	}

	public void setShowDropDownButton(boolean showDropDownButton) {
		this.showDropDownButton = showDropDownButton;
		sendCommandIfRendered(() -> new DtoAbstractDateTimeField.SetShowDropDownButtonCommand(showDropDownButton));
	}

	public boolean isFavorPastDates() {
		return favorPastDates;
	}

	public void setFavorPastDates(boolean favorPastDates) {
		this.favorPastDates = favorPastDates;
		sendCommandIfRendered(() -> new DtoAbstractDateTimeField.SetFavorPastDatesCommand(favorPastDates));
	}

	public Locale getLocale() {
		return locale.toLocale();
	}

	public ULocale getULocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		setULocale(ULocale.forLocale(locale));
	}

	public void setULocale(ULocale locale) {
		this.locale = locale;
		sendCommandIfRendered(() -> new DtoAbstractDateTimeField.SetLocaleAndFormatsCommand(locale.toLanguageTag(), dateFormat.toDateTimeFormatDescriptor(), timeFormat.toDateTimeFormatDescriptor()));
	}

	public DateTimeFormatDescriptor getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateTimeFormatDescriptor dateFormat) {
		this.dateFormat = dateFormat;
		sendCommandIfRendered(() -> new DtoAbstractDateTimeField.SetLocaleAndFormatsCommand(locale.toLanguageTag(), dateFormat.toDateTimeFormatDescriptor(), timeFormat.toDateTimeFormatDescriptor()));
	}

	public DateTimeFormatDescriptor getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(DateTimeFormatDescriptor timeFormat) {
		this.timeFormat = timeFormat;
		sendCommandIfRendered(() -> new DtoAbstractDateTimeField.SetLocaleAndFormatsCommand(locale.toLanguageTag(), dateFormat.toDateTimeFormatDescriptor(), timeFormat.toDateTimeFormatDescriptor()));
	}

}
