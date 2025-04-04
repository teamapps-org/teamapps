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
package org.teamapps.projector.component.core.field;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.field.DtoAbstractField;
import org.teamapps.projector.component.core.CoreComponentLibrary;
import org.teamapps.projector.component.core.DtoButton;
import org.teamapps.projector.component.core.DtoButtonClientObjectChannel;
import org.teamapps.projector.component.core.DtoButtonEventHandler;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class Button extends AbstractField<Void> implements DtoButtonEventHandler {

	private final DtoButtonClientObjectChannel clientObjectChannel = new DtoButtonClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Void> onClick = new ProjectorEvent<>(clientObjectChannel::toggleClickEvent);
	public final ProjectorEvent<Void> onDropDownOpened = new ProjectorEvent<>(clientObjectChannel::toggleDropDownOpenedEvent);


	private Template template; // null: toString!
	private Object templateRecord;
	private PropertyProvider<Object> propertyProvider = new BeanPropertyExtractor<>();

	private boolean openDropDownIfNotSet = false;
	private Component dropDownComponent;
	private Integer minDropDownWidth = null;
	private Integer minDropDownHeight = 300;

	private String onClickJavaScript;

	public Button(Template template, Object templateRecord, Component dropDownComponent) {
		super();
		this.template = template;
		this.templateRecord = templateRecord;
		this.dropDownComponent = dropDownComponent;
	}

	public Button(Template template, Object templateRecord) {
		this(template, templateRecord, null);
	}

	public static Button create(Template template, Object templateRecord) {
		return new Button(template, templateRecord, null);
	}

	public static Button create(Template template, Icon icon, String caption, Component dropDownComponent) {
		return new Button(template, new BaseTemplateRecord<>(icon, caption), dropDownComponent);
	}

	public static Button create(Template template, Icon icon, String caption) {
		return create(template, icon, caption, null);
	}

	public static Button create(Template template, String caption) {
		return create(template, null, caption, null);
	}

	public static Button create(Icon icon, String caption, Component dropDownComponent) {
		return create(BaseTemplates.BUTTON, icon, caption, dropDownComponent);
	}

	public static Button create(String caption, Component dropDownComponent) {
		return create(BaseTemplates.BUTTON, null, caption, dropDownComponent);
	}

	public static Button create(Icon icon, String caption) {
		return create(BaseTemplates.BUTTON, icon, caption, null);
	}

	public static Button create(String caption) {
		return create(BaseTemplates.BUTTON, null, caption, null);
	}

	@Override
	public DtoAbstractField createDto() {
		DtoButton ui = new DtoButton();
		mapAbstractFieldAttributesToUiField(ui);
		ui.setTemplate(template);
		ui.setTemplateRecord(createDtoRecord());
		ui.setDropDownComponent(dropDownComponent);
		ui.setMinDropDownWidth(minDropDownWidth != null ? minDropDownWidth : 0);
		ui.setMinDropDownHeight(minDropDownHeight != null ? minDropDownHeight : 0);
		ui.setOpenDropDownIfNotSet(this.openDropDownIfNotSet);
		ui.setOnClickJavaScript(onClickJavaScript);
		return ui;
	}

	private Object createDtoRecord() {
		Object uiRecord;
		if (template != null) {
			uiRecord = propertyProvider.getValues(templateRecord, template.getPropertyNames());
		} else {
			uiRecord = templateRecord.toString();
		}
		return uiRecord;
	}

	@Override
	public void handleClick() {
		if (isVisible()) {
			this.onClick.fire();
		}
	}

	@Override
	public void handleDropDownOpened() {
		if (isVisible()) {
			this.onDropDownOpened.fire();
		}
	}

	public Template getTemplate() {
		return template;
	}

	public Button setTemplate(Template template) {
		this.template = template;
		clientObjectChannel.setTemplate(template, templateRecord);
		return this;
	}

	public Object getTemplateRecord() {
		return templateRecord;
	}

	public Button setTemplateRecord(Object templateRecord) {
		this.templateRecord = templateRecord;
		clientObjectChannel.setTemplateRecord(templateRecord);
		return this;
	}

	public Button setColor(Color color) {
		this.setCssStyle(".btn", "background-color", color != null ? color.toHtmlColorString() : null);
		return this;
	}

	public PropertyProvider<Object> getPropertyProvider() {
		return propertyProvider;
	}

	public void setPropertyProvider(PropertyProvider<Object> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	public void setPropertyExtractor(PropertyExtractor<Object> propertyExtractor) {
		this.propertyProvider = propertyExtractor;
	}

	public Integer getMinDropDownWidth() {
		return minDropDownWidth;
	}

	public Button setMinDropDownWidth(Integer minDropDownWidth) {
		this.minDropDownWidth = minDropDownWidth;
		clientObjectChannel.setDropDownSize(minDropDownWidth != null ? minDropDownWidth : 0, minDropDownHeight != null ? minDropDownHeight : 0);
		return this;
	}

	public Integer getMinDropDownHeight() {
		return minDropDownHeight;
	}

	public Button setMinDropDownHeight(Integer minDropDownHeight) {
		this.minDropDownHeight = minDropDownHeight;
		clientObjectChannel.setDropDownSize(minDropDownWidth != null ? minDropDownWidth : 0, minDropDownHeight != null ? minDropDownHeight : 0);
		return this;
	}

	public Button setMinDropDownSize(Integer minDropDownWidth, Integer minDropDownHeight) {
		this.minDropDownWidth = minDropDownWidth;
		this.minDropDownHeight = minDropDownHeight;
		clientObjectChannel.setDropDownSize(minDropDownWidth, minDropDownHeight);
		return this;
	}

	public boolean isOpenDropDownIfNotSet() {
		return openDropDownIfNotSet;
	}

	public Button setOpenDropDownIfNotSet(boolean openDropDownIfNotSet) {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
		clientObjectChannel.setOpenDropDownIfNotSet(openDropDownIfNotSet);
		return this;
	}

	public Component getDropDownComponent() {
		return dropDownComponent;
	}

	public Button setDropDownComponent(Component dropDownComponent) {
		this.dropDownComponent = dropDownComponent;
		clientObjectChannel.setDropDownComponent(dropDownComponent);
		return this;
	}

	public void closeDropDown() {
		clientObjectChannel.closeDropDown();
	}

	public String getOnClickJavaScript() {
		return onClickJavaScript;
	}

	public void setOnClickJavaScript(String onClickJavaScript) {
		this.onClickJavaScript = onClickJavaScript;
		clientObjectChannel.setOnClickJavaScript(onClickJavaScript);
	}

	@Override
	public Void doConvertClientValueToServerValue(JsonNode value) {
		return null;
	}
}
