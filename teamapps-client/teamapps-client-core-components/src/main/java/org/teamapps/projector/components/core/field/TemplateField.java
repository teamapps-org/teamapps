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
package org.teamapps.projector.components.core.field;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.components.core.CoreComponentLibrary;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.dto.*;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.field.AbstractField;
import org.teamapps.projector.template.Template;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class TemplateField<RECORD> extends AbstractField<RECORD> implements DtoTemplateFieldEventHandler {

	public final ProjectorEvent<Void> onClicked = createProjectorEventBoundToUiEvent(DtoTemplateField.ClickedEvent.TYPE_ID);

	private Template template;
	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();

	private final DtoTemplateFieldClientObjectChannel clientObjectChannel = new DtoTemplateFieldClientObjectChannel(getClientObjectChannel());

	public TemplateField(Template template) {
		this.template = template;
	}

	public TemplateField(Template template, RECORD value) {
		this.template = template;
		this.setValue(value);
	}

	@Override
	public void handleClicked() {
		onClicked.fire();
	}

	@Override
	public DtoTemplateField createConfig() {
		DtoTemplateField ui = new DtoTemplateField();
		mapAbstractFieldAttributesToUiField(ui);
		ui.setTemplate(template);
		ui.setValue(createUiRecord(getValue()));
		return ui;
	}

	public RECORD convertClientValueToServerValue(Object value) {
		return getValue();
	}

	public Object convertServerValueToClientValue(RECORD record) {
		if (record == null) {
			return null;
		} else {
			return createUiRecord(record);
		}
	}

	@Override
	public RECORD doConvertClientValueToServerValue(JsonWrapper value) {
		return getValue(); // the ui does not pass the value anyway!
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
		clientObjectChannel.update(createConfig());
		return this;
	}

	public PropertyProvider<RECORD> getPropertyProvider() {
		return propertyProvider;
	}

	public TemplateField<RECORD> setPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.propertyProvider = propertyProvider;
		clientObjectChannel.update(createConfig());
		return this;
	}

	public TemplateField<RECORD> setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		return this.setPropertyProvider(propertyExtractor);
	}

}