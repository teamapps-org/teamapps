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

import org.teamapps.dto.DtoClientRecord;
import org.teamapps.dto.JsonWrapper;
import org.teamapps.dto.DtoTemplateField;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.annotations.ProjectorComponent;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.data.extraction.BeanPropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyProvider;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class TemplateField<RECORD> extends AbstractField<RECORD> {

	public final ProjectorEvent<Void> onClicked = createProjectorEventBoundToUiEvent(DtoTemplateField.ClickedEvent.TYPE_ID);

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
	public void handleUiEvent(String name, JsonWrapper params) {
		super.handleUiEvent(name, params);
		switch (event.getTypeId()) {
			case DtoTemplateField.ClickedEvent.TYPE_ID -> {
				var e = event.as(DtoTemplateField.ClickedEventWrapper.class);
				onClicked.fire();
			}
		}
	}

	@Override
	public DtoTemplateField createConfig() {
		DtoTemplateField ui = new DtoTemplateField();
		mapAbstractFieldAttributesToUiField(ui);
		ui.setTemplate(template.createClientReference());
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

	private DtoClientRecord createUiRecord(RECORD record) {
		if (record == null) {
			return null;
		}
		DtoClientRecord uiClientRecord = new DtoClientRecord();
		uiClientRecord.setValues(propertyProvider.getValues(record, template.getPropertyNames()));
		return uiClientRecord;
	}

	public Template getTemplate() {
		return template;
	}

	public TemplateField<RECORD> setTemplate(Template template) {
		this.template = template;
		sendCommandIfRendered(() -> new DtoTemplateField.UpdateCommand(createConfig()));
		return this;
	}

	public PropertyProvider<RECORD> getPropertyProvider() {
		return propertyProvider;
	}

	public TemplateField<RECORD> setPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.propertyProvider = propertyProvider;
		sendCommandIfRendered(() -> new DtoTemplateField.UpdateCommand(createConfig()));
		return this;
	}

	public TemplateField<RECORD> setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		return this.setPropertyProvider(propertyExtractor);
	}
}
