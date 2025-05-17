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

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.treecomponents.TreeComponentsLibrary;
import org.teamapps.projector.session.config.DateTimeFormatDescriptor;

import java.util.Locale;

@ClientObjectLibrary(value = TreeComponentsLibrary.class)
public abstract class AbstractDateTimeField<VALUE> extends AbstractField<VALUE> implements DtoAbstractDateTimeFieldEventHandler {

	private final DtoAbstractDateTimeFieldClientObjectChannel clientObjectChannel = new DtoAbstractDateTimeFieldClientObjectChannel(getClientObjectChannel());

	private boolean showDropDownButton = true;
	private boolean favorPastDates = false;
	private Locale locale;
	private DateTimeFormatDescriptor dateFormat;
	private DateTimeFormatDescriptor timeFormat;

	public AbstractDateTimeField() {
		super();
		this.locale = getSessionContext().getLocale();
		this.dateFormat = getSessionContext().getDateFormat();
		this.timeFormat = getSessionContext().getTimeFormat();
	}

	protected void mapAbstractDateTimeFieldDtoValues(DtoAbstractDateTimeField uiField) {
		mapAbstractFieldAttributes(uiField);
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
		clientObjectChannel.setShowDropDownButton(showDropDownButton);
	}

	public boolean isFavorPastDates() {
		return favorPastDates;
	}

	public void setFavorPastDates(boolean favorPastDates) {
		this.favorPastDates = favorPastDates;
		clientObjectChannel.setFavorPastDates(favorPastDates);
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
		clientObjectChannel.setLocaleAndFormats(locale.toLanguageTag(), dateFormat.toDateTimeFormatDescriptor(), timeFormat.toDateTimeFormatDescriptor());
	}

	public DateTimeFormatDescriptor getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateTimeFormatDescriptor dateFormat) {
		this.dateFormat = dateFormat;
		clientObjectChannel.setLocaleAndFormats(locale.toLanguageTag(), dateFormat.toDateTimeFormatDescriptor(), timeFormat.toDateTimeFormatDescriptor());
	}

	public DateTimeFormatDescriptor getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(DateTimeFormatDescriptor timeFormat) {
		this.timeFormat = timeFormat;
		clientObjectChannel.setLocaleAndFormats(locale.toLanguageTag(), dateFormat.toDateTimeFormatDescriptor(), timeFormat.toDateTimeFormatDescriptor());
	}

}
