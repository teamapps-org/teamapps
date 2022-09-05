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
package org.teamapps.ux.component.field.datetime;

import com.ibm.icu.util.ULocale;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiLocalDate;
import org.teamapps.dto.UiLocalDateField;
import org.teamapps.dto.UiLocalDateFieldDropDownMode;
import org.teamapps.event.Event;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.SpecialKey;
import org.teamapps.ux.component.field.TextInputHandlingField;
import org.teamapps.ux.session.DateTimeFormatDescriptor;

import java.time.LocalDate;
import java.util.Locale;

@TeamAppsComponent(library = CoreComponentLibrary.class)
public class LocalDateField extends AbstractField<LocalDate> implements TextInputHandlingField {

	public enum DropDownMode {
		CALENDAR,
		CALENDAR_SUGGESTION_LIST,
		SUGGESTION_LIST
	}

	public final Event<String> onTextInput = new Event<>();
	public final Event<SpecialKey> onSpecialKeyPressed = new Event<>();

	private boolean showDropDownButton = true;
	private boolean showClearButton = false;
	private boolean favorPastDates = false; // TODO: fix in trivial-components!!!
	private ULocale locale;
	private DateTimeFormatDescriptor dateFormat;
	private LocalDate defaultSuggestionDate;
	private boolean shuffledFormatSuggestionsEnabled = false;
	private DropDownMode dropDownMode = DropDownMode.CALENDAR_SUGGESTION_LIST;
	private String emptyText;

	public LocalDateField() {
		this.locale = getSessionContext().getULocale();
		this.dateFormat = getSessionContext().getConfiguration().getDateFormat();
	}

	@Override
	public UiLocalDateField createUiClientObject() {
		UiLocalDateField dateField = new UiLocalDateField();
		mapAbstractFieldAttributesToUiField(dateField);
		dateField.setShowDropDownButton(showDropDownButton);
		dateField.setFavorPastDates(favorPastDates);
		dateField.setLocale(locale.toLanguageTag());
		dateField.setDateFormat(dateFormat.toDateTimeFormatDescriptor());
		dateField.setShowClearButton(showClearButton);
		dateField.setDefaultSuggestionDate(convertUxValueToUiValue(defaultSuggestionDate));
		dateField.setShuffledFormatSuggestionsEnabled(shuffledFormatSuggestionsEnabled);
		dateField.setDropDownMode(UiLocalDateFieldDropDownMode.valueOf(dropDownMode.name()));
		dateField.setPlaceholderText(this.emptyText);
		return dateField;
	}

	@Override
	public LocalDate convertUiValueToUxValue(Object value) {
		if (value == null) {
			return null;
		} else {
			UiLocalDate uiLocalDate = (UiLocalDate) value;
			return LocalDate.of(uiLocalDate.getYear(), uiLocalDate.getMonth(), uiLocalDate.getDay());
		}
	}

	@Override
	public UiLocalDate convertUxValueToUiValue(LocalDate localDate) {
		return localDate != null ? new UiLocalDate(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()) : null;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		defaultHandleTextInputEvent(event);
	}

	public boolean isShowDropDownButton() {
		return showDropDownButton;
	}

	public void setShowDropDownButton(boolean showDropDownButton) {
		this.showDropDownButton = showDropDownButton;
		queueCommandIfRendered(() -> new UiLocalDateField.UpdateCommand(this.createUiClientObject()));
	}

	public boolean isFavorPastDates() {
		return favorPastDates;
	}

	public void setFavorPastDates(boolean favorPastDates) {
		this.favorPastDates = favorPastDates;
		queueCommandIfRendered(() -> new UiLocalDateField.UpdateCommand(this.createUiClientObject()));
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
		queueCommandIfRendered(() -> new UiLocalDateField.UpdateCommand(this.createUiClientObject()));
	}

	public DateTimeFormatDescriptor getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateTimeFormatDescriptor dateFormat) {
		this.dateFormat = dateFormat;
		queueCommandIfRendered(() -> new UiLocalDateField.UpdateCommand(this.createUiClientObject()));
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public void setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		queueCommandIfRendered(() -> new UiLocalDateField.UpdateCommand(this.createUiClientObject()));
	}

	@Override
	public Event<String> onTextInput() {
		return onTextInput;
	}

	@Override
	public Event<SpecialKey> onSpecialKeyPressed() {
		return onSpecialKeyPressed;
	}

	public LocalDate getDefaultSuggestionDate() {
		return defaultSuggestionDate;
	}

	public void setDefaultSuggestionDate(LocalDate defaultSuggestionDate) {
		this.defaultSuggestionDate = defaultSuggestionDate;
		queueCommandIfRendered(() -> new UiLocalDateField.UpdateCommand(this.createUiClientObject()));
	}

	public boolean isShuffledFormatSuggestionsEnabled() {
		return shuffledFormatSuggestionsEnabled;
	}

	public void setShuffledFormatSuggestionsEnabled(boolean shuffledFormatSuggestionsEnabled) {
		this.shuffledFormatSuggestionsEnabled = shuffledFormatSuggestionsEnabled;
		queueCommandIfRendered(() -> new UiLocalDateField.UpdateCommand(this.createUiClientObject()));
	}

	public DropDownMode getDropDownMode() {
		return dropDownMode;
	}

	public void setDropDownMode(DropDownMode dropDownMode) {
		this.dropDownMode = dropDownMode;
		queueCommandIfRendered(() -> new UiLocalDateField.UpdateCommand(this.createUiClientObject()));
	}

	public String getEmptyText() {
		return emptyText;
	}

	public void setEmptyText(String emptyText) {
		this.emptyText = emptyText;
		queueCommandIfRendered(() -> new UiLocalDateField.UpdateCommand(this.createUiClientObject()));
	}
}
