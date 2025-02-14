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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.treecomponents.TreeComponentsLibrary;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.projector.session.config.DateTimeFormatDescriptor;

import java.time.LocalDate;
import java.util.Locale;

@ClientObjectLibrary(value = TreeComponentsLibrary.class)
public class LocalDateField extends AbstractField<LocalDate> implements DtoLocalDateFieldEventHandler {

	private final DtoLocalDateFieldClientObjectChannel clientObjectChannel = new DtoLocalDateFieldClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<String> onTextInput = new ProjectorEvent<>(clientObjectChannel::toggleTextInputEvent);

	private boolean showDropDownButton = true;
	private boolean showClearButton = false;
	private boolean favorPastDates = false; // TODO: fix in trivial-components!!!
	private Locale locale;
	private DateTimeFormatDescriptor dateFormat;
	private LocalDate defaultSuggestionDate;
	private boolean shuffledFormatSuggestionsEnabled = false;
	private DateFieldDropDownMode dropDownMode = DateFieldDropDownMode.CALENDAR_SUGGESTION_LIST;
	private String emptyText;

	public LocalDateField() {
		this.locale = getSessionContext().getLocale();
		this.dateFormat = getSessionContext().getDateFormat();
	}

	@Override
	public DtoLocalDateField createDto() {
		DtoLocalDateField dateField = new DtoLocalDateField();
		mapAbstractFieldAttributesToUiField(dateField);
		dateField.setShowDropDownButton(showDropDownButton);
		dateField.setFavorPastDates(favorPastDates);
		dateField.setLocale(locale.toLanguageTag());
		dateField.setDateFormat(dateFormat.toDateTimeFormatDescriptor());
		dateField.setShowClearButton(showClearButton);
		dateField.setDefaultSuggestionDate(this.convertServerValueToClientValue(defaultSuggestionDate));
		dateField.setShuffledFormatSuggestionsEnabled(shuffledFormatSuggestionsEnabled);
		dateField.setDropDownMode(dropDownMode);
		dateField.setPlaceholderText(this.emptyText);
		return dateField;
	}

	@Override
	public void handleTextInput(String enteredString) {
		onTextInput.fire(enteredString);
	}

	@Override
	public LocalDate doConvertClientValueToServerValue(JsonNode value) {
		try {
			DtoLocalDate uiLocalDate = SessionContext.current().getObjectMapper().treeToValue(value, DtoLocalDate.class);
			return LocalDate.of(uiLocalDate.getYear(), uiLocalDate.getMonth(), uiLocalDate.getDay());
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	@Override
	public DtoLocalDate convertServerValueToClientValue(LocalDate localDate) {
		if (localDate != null) {
			DtoLocalDate dtoLocalDate = new DtoLocalDate();
			dtoLocalDate.setYear(localDate.getYear());
			dtoLocalDate.setMonth(localDate.getMonthValue());
			dtoLocalDate.setDay(localDate.getDayOfMonth());
			return dtoLocalDate;
		}
		return null;
	}

	public boolean isShowDropDownButton() {
		return showDropDownButton;
	}

	public void setShowDropDownButton(boolean showDropDownButton) {
		this.showDropDownButton = showDropDownButton;
		clientObjectChannel.update(this.createDto());
	}

	public boolean isFavorPastDates() {
		return favorPastDates;
	}

	public void setFavorPastDates(boolean favorPastDates) {
		this.favorPastDates = favorPastDates;
		clientObjectChannel.update(this.createDto());
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
		clientObjectChannel.update(this.createDto());
	}

	public DateTimeFormatDescriptor getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateTimeFormatDescriptor dateFormat) {
		this.dateFormat = dateFormat;
		clientObjectChannel.update(this.createDto());
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public void setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		clientObjectChannel.update(this.createDto());
	}

	public LocalDate getDefaultSuggestionDate() {
		return defaultSuggestionDate;
	}

	public void setDefaultSuggestionDate(LocalDate defaultSuggestionDate) {
		this.defaultSuggestionDate = defaultSuggestionDate;
		clientObjectChannel.update(this.createDto());
	}

	public boolean isShuffledFormatSuggestionsEnabled() {
		return shuffledFormatSuggestionsEnabled;
	}

	public void setShuffledFormatSuggestionsEnabled(boolean shuffledFormatSuggestionsEnabled) {
		this.shuffledFormatSuggestionsEnabled = shuffledFormatSuggestionsEnabled;
		clientObjectChannel.update(this.createDto());
	}

	public DateFieldDropDownMode getDropDownMode() {
		return dropDownMode;
	}

	public void setDropDownMode(DateFieldDropDownMode dropDownMode) {
		this.dropDownMode = dropDownMode;
		clientObjectChannel.update(this.createDto());
	}

	public String getEmptyText() {
		return emptyText;
	}

	public void setEmptyText(String emptyText) {
		this.emptyText = emptyText;
		clientObjectChannel.update(this.createDto());
	}
}
