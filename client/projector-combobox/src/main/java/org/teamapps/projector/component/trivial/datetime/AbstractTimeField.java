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
package org.teamapps.projector.component.trivial.datetime;

import org.teamapps.projector.component.trivial.DtoAbstractTimeField;
import org.teamapps.projector.component.trivial.DtoAbstractTimeFieldClientObjectChannel;
import org.teamapps.projector.component.trivial.DtoAbstractTimeFieldEventHandler;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.session.config.DateTimeFormatDescriptor;

import java.util.Locale;

public abstract class AbstractTimeField<VALUE> extends AbstractField<VALUE> implements DtoAbstractTimeFieldEventHandler {

	private final DtoAbstractTimeFieldClientObjectChannel clientObjectChannel = new DtoAbstractTimeFieldClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<String> onTextInput = new ProjectorEvent<>(clientObjectChannel::toggleTextInputEvent);

	private boolean showDropDownButton = true;
	private boolean showClearButton = false;
	private Locale locale;
	private DateTimeFormatDescriptor timeFormat;

	public AbstractTimeField() {
		super();
		this.locale = getSessionContext().getLocale();
		this.timeFormat = getSessionContext().getTimeFormat();
	}

	@Override
	public void handleTextInput(String enteredString) {
		onTextInput.fire(enteredString);
	}

	public void mapAbstractTimeFieldUiValues(DtoAbstractTimeField uiTimeField) {
		mapAbstractFieldAttributesToUiField(uiTimeField);
		uiTimeField.setShowDropDownButton(isShowDropDownButton());
		uiTimeField.setLocale(locale.toLanguageTag());
		uiTimeField.setTimeFormat(timeFormat.toDateTimeFormatDescriptor());
		uiTimeField.setShowClearButton(isShowClearButton());
	}

	public boolean isShowDropDownButton() {
		return showDropDownButton;
	}

	public void setShowDropDownButton(boolean showDropDownButton) {
		this.showDropDownButton = showDropDownButton;
		clientObjectChannel.setShowDropDownButton(showDropDownButton);
	}


	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
		clientObjectChannel.setLocaleAndTimeFormat(locale.toLanguageTag(), timeFormat.toDateTimeFormatDescriptor());
	}

	public DateTimeFormatDescriptor getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(DateTimeFormatDescriptor timeFormat) {
		this.timeFormat = timeFormat;
		clientObjectChannel.setLocaleAndTimeFormat(locale.toLanguageTag(), timeFormat.toDateTimeFormatDescriptor());
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public void setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		clientObjectChannel.setShowClearButton(showClearButton);
	}

}
