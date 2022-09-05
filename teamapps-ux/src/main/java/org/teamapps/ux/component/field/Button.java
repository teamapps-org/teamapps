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
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.UiButton;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiField;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.template.Template;

@TeamAppsComponent(library = CoreComponentLibrary.class)
public class Button<RECORD> extends AbstractField<Void> {

	public final Event<Void> onClicked = new Event<>();
	public final Event<Void> onDropDownOpened = new Event<>();

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

	public static Button<BaseTemplateRecord> create(BaseTemplate template, Icon icon, String caption, Component dropDownComponent) {
		return new Button<>(template, new BaseTemplateRecord(icon, caption), dropDownComponent);
	}

	public static Button<BaseTemplateRecord> create(BaseTemplate template, Icon icon, String caption) {
		return create(template, icon, caption, null);
	}

	public static Button<BaseTemplateRecord> create(BaseTemplate template, String caption) {
		return create(template, null, caption, null);
	}

	public static Button<BaseTemplateRecord> create(Icon icon, String caption, Component dropDownComponent) {
		return create(BaseTemplate.BUTTON, icon, caption, dropDownComponent);
	}

	public static Button<BaseTemplateRecord> create(String caption, Component dropDownComponent) {
		return create(BaseTemplate.BUTTON, null, caption, dropDownComponent);
	}

	public static Button<BaseTemplateRecord> create(Icon icon, String caption) {
		return create(BaseTemplate.BUTTON, icon, caption, null);
	}

	public static Button<BaseTemplateRecord> create(String caption) {
		return create(BaseTemplate.BUTTON, null, caption, null);
	}

	@Override
	public UiField createUiClientObject() {
		Object uiRecord = createUiRecord();
		UiButton ui = new UiButton(template != null ? template.createUiTemplate() : null, uiRecord);
		mapAbstractFieldAttributesToUiField(ui);
		ui.setDropDownComponent(Component.createUiClientObjectReference(dropDownComponent));
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
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		if (event instanceof UiButton.ClickedEvent) {
			this.onClicked.fire();
		} else if (event instanceof UiButton.DropDownOpenedEvent) {
			this.onDropDownOpened.fire();
		}
	}

	public Template getTemplate() {
		return template;
	}

	public Button<RECORD> setTemplate(Template template) {
		this.template = template;
		queueCommandIfRendered(() -> new UiButton.SetTemplateCommand(template.createUiTemplate(), createUiRecord()));
		return this;
	}

	public RECORD getTemplateRecord() {
		return templateRecord;
	}

	public Button<RECORD> setTemplateRecord(RECORD templateRecord) {
		this.templateRecord = templateRecord;
		queueCommandIfRendered(() -> new UiButton.SetTemplateRecordCommand(templateRecord));
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
		queueCommandIfRendered(() -> new UiButton.SetDropDownSizeCommand(minDropDownWidth != null ? minDropDownWidth : 0, minDropDownHeight != null ? minDropDownHeight : 0));
		return this;
	}

	public Integer getMinDropDownHeight() {
		return minDropDownHeight;
	}

	public Button<RECORD> setMinDropDownHeight(Integer minDropDownHeight) {
		this.minDropDownHeight = minDropDownHeight;
		queueCommandIfRendered(() -> new UiButton.SetDropDownSizeCommand(minDropDownWidth != null ? minDropDownWidth : 0, minDropDownHeight != null ? minDropDownHeight : 0));
		return this;
	}

	public Button<RECORD> setMinDropDownSize(Integer minDropDownWidth, Integer minDropDownHeight) {
		this.minDropDownWidth = minDropDownWidth;
		this.minDropDownHeight = minDropDownHeight;
		queueCommandIfRendered(() -> new UiButton.SetDropDownSizeCommand(minDropDownWidth, minDropDownHeight));
		return this;
	}

	public boolean isOpenDropDownIfNotSet() {
		return openDropDownIfNotSet;
	}

	public Button<RECORD> setOpenDropDownIfNotSet(boolean openDropDownIfNotSet) {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
		queueCommandIfRendered(() -> new UiButton.SetOpenDropDownIfNotSetCommand(openDropDownIfNotSet));
		return this;
	}

	public Component getDropDownComponent() {
		return dropDownComponent;
	}

	public Button<RECORD> setDropDownComponent(Component dropDownComponent) {
		this.dropDownComponent = dropDownComponent;
		queueCommandIfRendered(() -> new UiButton.SetDropDownComponentCommand(Component.createUiClientObjectReference(dropDownComponent)));
		return this;
	}

	public String getOnClickJavaScript() {
		return onClickJavaScript;
	}

	public void setOnClickJavaScript(String onClickJavaScript) {
		this.onClickJavaScript = onClickJavaScript;
		queueCommandIfRendered(() -> new UiButton.SetOnClickJavaScriptCommand(onClickJavaScript));
	}
}
