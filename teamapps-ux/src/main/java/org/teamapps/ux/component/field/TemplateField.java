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
package org.teamapps.ux.component.field;

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.UiClientRecord;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiTemplateField;
import org.teamapps.event.Event;
import org.teamapps.ux.component.template.Template;

public class TemplateField<RECORD> extends AbstractField<RECORD> {

	public final Event<Void> onClicked = new Event<>();

	private Template template;
	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();

	public TemplateField(Template template) {
		this.template = template;
	}

	public TemplateField(Template template, RECORD value) {
		this.template = template;
		this.setValue(value);
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		if (event instanceof UiTemplateField.ClickedEvent) {
			onClicked.fire();
		}
	}

	@Override
	public UiTemplateField createUiComponent() {
		UiTemplateField ui = new UiTemplateField();
		mapAbstractFieldAttributesToUiField(ui);
		ui.setTemplate(template.createUiTemplate());
		ui.setValue(createUiRecord(getValue()));
		return ui;
	}

	public RECORD convertUiValueToUxValue(Object value) {
		return getValue();
	}

	public Object convertUxValueToUiValue(RECORD record) {
		if (record == null) {
			return null;
		} else {
			return createUiRecord(record);
		}
	}

	private UiClientRecord createUiRecord(RECORD record) {
		if (record == null) {
			return null;
		}
		UiClientRecord uiClientRecord = new UiClientRecord();
		uiClientRecord.setValues(propertyProvider.getValues(record, template.getPropertyNames()));
		return uiClientRecord;
	}

	public Template getTemplate() {
		return template;
	}

	public TemplateField<RECORD> setTemplate(Template template) {
		this.template = template;
		queueCommandIfRendered(() -> new UiTemplateField.UpdateCommand(getId(), createUiComponent()));
		return this;
	}

	public PropertyProvider<RECORD> getPropertyProvider() {
		return propertyProvider;
	}

	public TemplateField<RECORD> setPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.propertyProvider = propertyProvider;
		queueCommandIfRendered(() -> new UiTemplateField.UpdateCommand(getId(), createUiComponent()));
		return this;
	}

	public TemplateField<RECORD> setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		return this.setPropertyProvider(propertyExtractor);
	}
}
