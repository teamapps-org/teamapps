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
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.components.trivial.TrivialComponentsLibrary;
import org.teamapps.projector.components.trivial.dto.DtoLocalDate;
import org.teamapps.projector.components.trivial.dto.DtoLocalDateField;
import org.teamapps.projector.components.trivial.dto.DtoLocalDateFieldDropDownMode;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.field.AbstractField;
import org.teamapps.projector.components.core.field.SpecialKey;
import org.teamapps.projector.components.core.field.TextInputHandlingField;
import org.teamapps.projector.session.config.DateTimeFormatDescriptor;

import java.time.LocalDate;
import java.util.Locale;

@ClientObjectLibrary(value = TrivialComponentsLibrary.class)
public class LocalDateField extends AbstractField<LocalDate> {

	public enum DropDownMode {
		CALENDAR,
		CALENDAR_SUGGESTION_LIST,
		SUGGESTION_LIST
	}

	public final ProjectorEvent<String> onTextInput = createProjectorEventBoundToUiEvent(DtoLocalDateField.TextInputEvent.TYPE_ID);
	public final ProjectorEvent<SpecialKey> onSpecialKeyPressed = createProjectorEventBoundToUiEvent(DtoLocalDateField.SpecialKeyPressedEvent.TYPE_ID);

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
	public DtoLocalDateField createConfig() {
		DtoLocalDateField dateField = new DtoLocalDateField();
		mapAbstractFieldAttributesToUiField(dateField);
		dateField.setShowDropDownButton(showDropDownButton);
		dateField.setFavorPastDates(favorPastDates);
		dateField.setLocale(locale.toLanguageTag());
		dateField.setDateFormat(dateFormat.toDateTimeFormatDescriptor());
		dateField.setShowClearButton(showClearButton);
		dateField.setDefaultSuggestionDate(this.convertServerValueToClientValue(defaultSuggestionDate));
		dateField.setShuffledFormatSuggestionsEnabled(shuffledFormatSuggestionsEnabled);
		dateField.setDropDownMode(DtoLocalDateFieldDropDownMode.valueOf(dropDownMode.name()));
		dateField.setPlaceholderText(this.emptyText);
		return dateField;
	}

	@Override
	public LocalDate convertClientValueToServerValue(Object value) {
		if (value == null) {
			return null;
		} else {
			DtoLocalDate uiLocalDate = (DtoLocalDate) value;
			return LocalDate.of(uiLocalDate.getYear(), uiLocalDate.getMonth(), uiLocalDate.getDay());
		}
	}

	@Override
	public DtoLocalDate convertServerValueToClientValue(LocalDate localDate) {
		return localDate != null ? new DtoLocalDate(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()) : null;
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		super.handleUiEvent(name, params);
		defaultHandleTextInputEvent(event);
	}

	public boolean isShowDropDownButton() {
		return showDropDownButton;
	}

	public void setShowDropDownButton(boolean showDropDownButton) {
		this.showDropDownButton = showDropDownButton;
		clientObjectChannel.update(this.createConfig());
	}

	public boolean isFavorPastDates() {
		return favorPastDates;
	}

	public void setFavorPastDates(boolean favorPastDates) {
		this.favorPastDates = favorPastDates;
		clientObjectChannel.update(this.createConfig());
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
		clientObjectChannel.update(this.createConfig());
	}

	public DateTimeFormatDescriptor getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateTimeFormatDescriptor dateFormat) {
		this.dateFormat = dateFormat;
		clientObjectChannel.update(this.createConfig());
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public void setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		clientObjectChannel.update(this.createConfig());
	}

	@Override
	public ProjectorEvent<String> onTextInput() {
		return onTextInput;
	}

	@Override
	public ProjectorEvent<SpecialKey> onSpecialKeyPressed() {
		return onSpecialKeyPressed;
	}

	public LocalDate getDefaultSuggestionDate() {
		return defaultSuggestionDate;
	}

	public void setDefaultSuggestionDate(LocalDate defaultSuggestionDate) {
		this.defaultSuggestionDate = defaultSuggestionDate;
		clientObjectChannel.update(this.createConfig());
	}

	public boolean isShuffledFormatSuggestionsEnabled() {
		return shuffledFormatSuggestionsEnabled;
	}

	public void setShuffledFormatSuggestionsEnabled(boolean shuffledFormatSuggestionsEnabled) {
		this.shuffledFormatSuggestionsEnabled = shuffledFormatSuggestionsEnabled;
		clientObjectChannel.update(this.createConfig());
	}

	public DropDownMode getDropDownMode() {
		return dropDownMode;
	}

	public void setDropDownMode(DropDownMode dropDownMode) {
		this.dropDownMode = dropDownMode;
		clientObjectChannel.update(this.createConfig());
	}

	public String getEmptyText() {
		return emptyText;
	}

	public void setEmptyText(String emptyText) {
		this.emptyText = emptyText;
		clientObjectChannel.update(this.createConfig());
	}
}