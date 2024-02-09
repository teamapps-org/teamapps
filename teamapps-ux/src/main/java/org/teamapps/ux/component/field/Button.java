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

import org.teamapps.common.format.Color;
import org.teamapps.dto.DtoButton;
import org.teamapps.dto.DtoField;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.ClientObject;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.annotations.ProjectorComponent;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.data.extraction.BeanPropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyProvider;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class Button<RECORD> extends AbstractField<Void> {

	public final ProjectorEvent<Void> onClicked = createProjectorEventBoundToUiEvent(DtoButton.ClickedEvent.TYPE_ID);
	public final ProjectorEvent<Void> onDropDownOpened = createProjectorEventBoundToUiEvent(DtoButton.DropDownOpenedEvent.TYPE_ID);

	private Template template; // null: toString!
	private RECORD templateRecord;
	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();

	private boolean openDropDownIfNotSet = false;
	private org.teamapps.ux.component.Component dropDownComponent;
	private Integer minDropDownWidth = null;
	private Integer minDropDownHeight = 300;

	private String onClickJavaScript;

	public Button(Template template, RECORD templateRecord, org.teamapps.ux.component.Component dropDownComponent) {
		super();
		this.template = template;
		this.templateRecord = templateRecord;
		this.dropDownComponent = dropDownComponent;
	}

	public Button(Template template, RECORD templateRecord) {
		this(template, templateRecord, null);
	}

	public static Button<BaseTemplateRecord<?>> create(BaseTemplate template, Icon<?, ?> icon, String caption, org.teamapps.ux.component.Component dropDownComponent) {
		return new Button<>(template, new BaseTemplateRecord<>(icon, caption), dropDownComponent);
	}

	public static Button<BaseTemplateRecord<?>> create(BaseTemplate template, Icon<?, ?> icon, String caption) {
		return create(template, icon, caption, null);
	}

	public static Button<BaseTemplateRecord<?>> create(BaseTemplate template, String caption) {
		return create(template, null, caption, null);
	}

	public static Button<BaseTemplateRecord<?>> create(Icon<?, ?> icon, String caption, org.teamapps.ux.component.Component dropDownComponent) {
		return create(BaseTemplate.BUTTON, icon, caption, dropDownComponent);
	}

	public static Button<BaseTemplateRecord<?>> create(String caption, org.teamapps.ux.component.Component dropDownComponent) {
		return create(BaseTemplate.BUTTON, null, caption, dropDownComponent);
	}

	public static Button<BaseTemplateRecord<?>> create(Icon<?, ?> icon, String caption) {
		return create(BaseTemplate.BUTTON, icon, caption, null);
	}

	public static Button<BaseTemplateRecord<?>> create(String caption) {
		return create(BaseTemplate.BUTTON, null, caption, null);
	}

	@Override
	public DtoField createDto() {
		Object uiRecord = createUiRecord();
		DtoButton ui = new DtoButton(template != null ? template.createDtoReference() : null, uiRecord);
		mapAbstractFieldAttributesToUiField(ui);
		ui.setDropDownComponent(ClientObject.createDtoReference(dropDownComponent));
		ui.setMinDropDownWidth(minDropDownWidth != null ? minDropDownWidth : 0);
		ui.setMinDropDownHeight(minDropDownHeight != null ? minDropDownHeight : 0);
		ui.setOpenDropDownIfNotSet(this.openDropDownIfNotSet);
		ui.setOnClickJavaScript(onClickJavaScript);
		return ui;
	}

	private Object createUiRecord() {
		Object uiRecord;
		if (template != null) {
			uiRecord = propertyProvider.getValues(templateRecord, template.getPropertyNames());
		} else {
			uiRecord = templateRecord.toString();
		}
		return uiRecord;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		super.handleUiEvent(event);
		switch (event.getTypeId()) {
			case DtoButton.ClickedEvent.TYPE_ID -> {
				var e = event.as(DtoButton.ClickedEventWrapper.class);
				this.onClicked.fire();
			}
			case DtoButton.DropDownOpenedEvent.TYPE_ID -> {
				var e = event.as(DtoButton.DropDownOpenedEventWrapper.class);
				this.onDropDownOpened.fire();
			}
		}
	}

	public Template getTemplate() {
		return template;
	}

	public Button<RECORD> setTemplate(Template template) {
		this.template = template;
		sendCommandIfRendered(() -> new DtoButton.SetTemplateCommand(template.createDtoReference(), createUiRecord()));
		return this;
	}

	public RECORD getTemplateRecord() {
		return templateRecord;
	}

	public Button<RECORD> setTemplateRecord(RECORD templateRecord) {
		this.templateRecord = templateRecord;
		sendCommandIfRendered(() -> new DtoButton.SetTemplateRecordCommand(templateRecord));
		return this;
	}

	public Button<RECORD> setColor(Color color) {
		this.setCssStyle(".btn", "background-color", color != null ? color.toHtmlColorString() : null);
		return this;
	}

	public PropertyProvider<RECORD> getPropertyProvider() {
		return propertyProvider;
	}

	public void setPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	public void setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.propertyProvider = propertyExtractor;
	}

	public Integer getMinDropDownWidth() {
		return minDropDownWidth;
	}

	public Button<RECORD> setMinDropDownWidth(Integer minDropDownWidth) {
		this.minDropDownWidth = minDropDownWidth;
		sendCommandIfRendered(() -> new DtoButton.SetDropDownSizeCommand(minDropDownWidth != null ? minDropDownWidth : 0, minDropDownHeight != null ? minDropDownHeight : 0));
		return this;
	}

	public Integer getMinDropDownHeight() {
		return minDropDownHeight;
	}

	public Button<RECORD> setMinDropDownHeight(Integer minDropDownHeight) {
		this.minDropDownHeight = minDropDownHeight;
		sendCommandIfRendered(() -> new DtoButton.SetDropDownSizeCommand(minDropDownWidth != null ? minDropDownWidth : 0, minDropDownHeight != null ? minDropDownHeight : 0));
		return this;
	}

	public Button<RECORD> setMinDropDownSize(Integer minDropDownWidth, Integer minDropDownHeight) {
		this.minDropDownWidth = minDropDownWidth;
		this.minDropDownHeight = minDropDownHeight;
		sendCommandIfRendered(() -> new DtoButton.SetDropDownSizeCommand(minDropDownWidth, minDropDownHeight));
		return this;
	}

	public boolean isOpenDropDownIfNotSet() {
		return openDropDownIfNotSet;
	}

	public Button<RECORD> setOpenDropDownIfNotSet(boolean openDropDownIfNotSet) {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
		sendCommandIfRendered(() -> new DtoButton.SetOpenDropDownIfNotSetCommand(openDropDownIfNotSet));
		return this;
	}

	public org.teamapps.ux.component.Component getDropDownComponent() {
		return dropDownComponent;
	}

	public Button<RECORD> setDropDownComponent(org.teamapps.ux.component.Component dropDownComponent) {
		this.dropDownComponent = dropDownComponent;
		sendCommandIfRendered(() -> new DtoButton.SetDropDownComponentCommand(ClientObject.createDtoReference(dropDownComponent)));
		return this;
	}

	public void closeDropDown() {
		sendCommandIfRendered(DtoButton.CloseDropDownCommand::new);
	}

	public String getOnClickJavaScript() {
		return onClickJavaScript;
	}

	public void setOnClickJavaScript(String onClickJavaScript) {
		this.onClickJavaScript = onClickJavaScript;
		sendCommandIfRendered(() -> new DtoButton.SetOnClickJavaScriptCommand(onClickJavaScript));
	}
}
