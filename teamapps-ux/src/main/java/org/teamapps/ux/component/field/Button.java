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
import org.teamapps.projector.dto.DtoButton;
import org.teamapps.projector.dto.DtoAbstractField;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.projector.clientobject.ClientObject;
import org.teamapps.projector.field.AbstractField;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;

import java.util.function.Supplier;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class Button<RECORD> extends AbstractField<Void> {

	public final ProjectorEvent<Void> onClicked = createProjectorEventBoundToUiEvent(DtoButton.ClickedEvent.TYPE_ID);
	public final ProjectorEvent<Void> onDropDownOpened = createProjectorEventBoundToUiEvent(DtoButton.DropDownOpenedEvent.TYPE_ID);

	private Template template; // null: toString!
	private RECORD templateRecord;
	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();

	private boolean openDropDownIfNotSet = false;
	private Component dropDownComponent;
	private Integer minDropDownWidth = null;
	private Integer minDropDownHeight = 300;

	private String onClickJavaScript;

	public Button(Template template, RECORD templateRecord, Component dropDownComponent) {
		super();
		this.template = template;
		this.templateRecord = templateRecord;
		this.dropDownComponent = dropDownComponent;
	}

	public Button(Template template, RECORD templateRecord) {
		this(template, templateRecord, null);
	}

	public static Button<BaseTemplateRecord<?>> create(BaseTemplate template, Icon<?, ?> icon, String caption, Component dropDownComponent) {
		return new Button<>(template, new BaseTemplateRecord<>(icon, caption), dropDownComponent);
	}

	public static Button<BaseTemplateRecord<?>> create(BaseTemplate template, Icon<?, ?> icon, String caption) {
		return create(template, icon, caption, null);
	}

	public static Button<BaseTemplateRecord<?>> create(BaseTemplate template, String caption) {
		return create(template, null, caption, null);
	}

	public static Button<BaseTemplateRecord<?>> create(Icon<?, ?> icon, String caption, Component dropDownComponent) {
		return create(BaseTemplate.BUTTON, icon, caption, dropDownComponent);
	}

	public static Button<BaseTemplateRecord<?>> create(String caption, Component dropDownComponent) {
		return create(BaseTemplate.BUTTON, null, caption, dropDownComponent);
	}

	public static Button<BaseTemplateRecord<?>> create(Icon<?, ?> icon, String caption) {
		return create(BaseTemplate.BUTTON, icon, caption, null);
	}

	public static Button<BaseTemplateRecord<?>> create(String caption) {
		return create(BaseTemplate.BUTTON, null, caption, null);
	}

	@Override
	public DtoAbstractField createConfig() {
		Object uiRecord = createUiRecord();
		DtoButton ui = new DtoButton(template != null ? template.createClientReference() : null, uiRecord);
		mapAbstractFieldAttributesToUiField(ui);
		ui.setDropDownComponent(ClientObject.createClientReference(dropDownComponent));
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
	public void handleUiEvent(String name, JsonWrapper params) {
		super.handleUiEvent(name, params);
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
		getClientObjectChannel().sendCommandIfRendered(new DtoButton.SetTemplateCommand(template.createClientReference(), createUiRecord()), null);
		return this;
	}

	public RECORD getTemplateRecord() {
		return templateRecord;
	}

	public Button<RECORD> setTemplateRecord(RECORD templateRecord) {
		this.templateRecord = templateRecord;
		getClientObjectChannel().sendCommandIfRendered(new DtoButton.SetTemplateRecordCommand(templateRecord), null);
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
		getClientObjectChannel().sendCommandIfRendered(new DtoButton.SetDropDownSizeCommand(minDropDownWidth != null ? minDropDownWidth : 0, minDropDownHeight != null ? minDropDownHeight : 0), null);
		return this;
	}

	public Integer getMinDropDownHeight() {
		return minDropDownHeight;
	}

	public Button<RECORD> setMinDropDownHeight(Integer minDropDownHeight) {
		this.minDropDownHeight = minDropDownHeight;
		getClientObjectChannel().sendCommandIfRendered(new DtoButton.SetDropDownSizeCommand(minDropDownWidth != null ? minDropDownWidth : 0, minDropDownHeight != null ? minDropDownHeight : 0), null);
		return this;
	}

	public Button<RECORD> setMinDropDownSize(Integer minDropDownWidth, Integer minDropDownHeight) {
		this.minDropDownWidth = minDropDownWidth;
		this.minDropDownHeight = minDropDownHeight;
		getClientObjectChannel().sendCommandIfRendered(new DtoButton.SetDropDownSizeCommand(minDropDownWidth, minDropDownHeight), null);
		return this;
	}

	public boolean isOpenDropDownIfNotSet() {
		return openDropDownIfNotSet;
	}

	public Button<RECORD> setOpenDropDownIfNotSet(boolean openDropDownIfNotSet) {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
		getClientObjectChannel().sendCommandIfRendered(new DtoButton.SetOpenDropDownIfNotSetCommand(openDropDownIfNotSet), null);
		return this;
	}

	public Component getDropDownComponent() {
		return dropDownComponent;
	}

	public Button<RECORD> setDropDownComponent(Component dropDownComponent) {
		this.dropDownComponent = dropDownComponent;
		getClientObjectChannel().sendCommandIfRendered(new DtoButton.SetDropDownComponentCommand(ClientObject.createClientReference(dropDownComponent)), null);
		return this;
	}

	public void closeDropDown() {
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) DtoButton.CloseDropDownCommand::new).get(), null);
	}

	public String getOnClickJavaScript() {
		return onClickJavaScript;
	}

	public void setOnClickJavaScript(String onClickJavaScript) {
		this.onClickJavaScript = onClickJavaScript;
		getClientObjectChannel().sendCommandIfRendered(new DtoButton.SetOnClickJavaScriptCommand(onClickJavaScript), null);
	}
}
