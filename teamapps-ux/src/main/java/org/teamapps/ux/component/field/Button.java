/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import org.teamapps.dto.UiButton;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiField;
import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.common.format.Color;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.template.Template;

public class Button<RECORD> extends AbstractField<Boolean> {

	public final Event<Void> onDropDownOpened = new Event<>();

	private Template template = BaseTemplate.FORM_BUTTON; // null: toString!
	private RECORD templateRecord;
	private PropertyExtractor<RECORD> propertyExtractor = new BeanPropertyExtractor<>();

	private boolean openDropDownIfNotSet = false;
	private Component dropDownComponent;
	private Integer minDropDownWidth = null;
	private Integer minDropDownHeight = 300;

	public Button(Template template, RECORD templateRecord, Component dropDownComponent) {
		super();
		this.template = template;
		this.templateRecord = templateRecord;
		this.dropDownComponent = dropDownComponent;
	}

	public Button(Template template, RECORD templateRecord) {
		this(template, templateRecord, null);
	}

	public static Button<BaseTemplateRecord> create(Icon icon, String caption, Component dropDownComponent) {
		return new Button<>(BaseTemplate.FORM_BUTTON, new BaseTemplateRecord(icon, caption), dropDownComponent);
	}

	public static Button<BaseTemplateRecord> create(String caption, Component dropDownComponent) {
		return new Button<>(BaseTemplate.FORM_BUTTON, new BaseTemplateRecord(caption), dropDownComponent);
	}

	public static Button<BaseTemplateRecord> create(Icon icon, String caption) {
		return new Button<>(BaseTemplate.FORM_BUTTON, new BaseTemplateRecord(icon, caption));
	}

	public static Button<BaseTemplateRecord> create(String caption) {
		return new Button<>(BaseTemplate.FORM_BUTTON, new BaseTemplateRecord(caption));
	}

	@Override
	public UiField createUiComponent() {
		Object uiRecord = createUiRecord();
		UiButton button = new UiButton(getTemplate().createUiTemplate(), uiRecord);
		mapAbstractFieldAttributesToUiField(button);
		button.setDropDownComponent(Component.createUiClientObjectReference(dropDownComponent));
		button.setMinDropDownWidth(minDropDownWidth != null ? minDropDownWidth : 0);
		button.setMinDropDownHeight(minDropDownHeight != null ? minDropDownHeight : 0);
		button.setOpenDropDownIfNotSet(this.openDropDownIfNotSet);
		return button;
	}

	private Object createUiRecord() {
		Object uiRecord;
		if (template != null) {
			uiRecord = propertyExtractor.getValues(templateRecord, template.getDataKeys());
		} else {
			uiRecord = templateRecord.toString();
		}
		return uiRecord;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		switch (event.getUiEventType()) {
			case UI_BUTTON_DROP_DOWN_OPENED:
				this.onDropDownOpened.fire(null);
				break;
		}
	}

	public Template getTemplate() {
		return template;
	}

	public Button<RECORD> setTemplate(Template template) {
		this.template = template;
		queueCommandIfRendered(() -> new UiButton.SetTemplateCommand(getId(), template.createUiTemplate(), createUiRecord()));
		return this;
	}

	public RECORD getTemplateRecord() {
		return templateRecord;
	}

	public Button<RECORD> setTemplateRecord(RECORD templateRecord) {
		this.templateRecord = templateRecord;
		queueCommandIfRendered(() -> new UiButton.SetTemplateRecordCommand(getId(), templateRecord));
		return this;
	}

	public Button<RECORD> setColor(Color color) {
		this.setCssStyle(".btn", "background-color", color.toHtmlColorString());
		return this;
	}

	public PropertyExtractor<RECORD> getPropertyExtractor() {
		return propertyExtractor;
	}

	public void setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.propertyExtractor = propertyExtractor;
	}

	public Integer getMinDropDownWidth() {
		return minDropDownWidth;
	}

	public Button<RECORD> setMinDropDownWidth(Integer minDropDownWidth) {
		this.minDropDownWidth = minDropDownWidth;
		queueCommandIfRendered(() -> new UiButton.SetDropDownSizeCommand(getId(), minDropDownWidth != null ? minDropDownWidth : 0, minDropDownHeight != null ? minDropDownHeight : 0));
		return this;
	}

	public Integer getMinDropDownHeight() {
		return minDropDownHeight;
	}

	public Button<RECORD> setMinDropDownHeight(Integer minDropDownHeight) {
		this.minDropDownHeight = minDropDownHeight;
		queueCommandIfRendered(() -> new UiButton.SetDropDownSizeCommand(getId(), minDropDownWidth != null ? minDropDownWidth : 0, minDropDownHeight != null ? minDropDownHeight : 0));
		return this;
	}

	public Button<RECORD> setMinDropDownSize(Integer minDropDownWidth, Integer minDropDownHeight) {
		this.minDropDownWidth = minDropDownWidth;
		this.minDropDownHeight = minDropDownHeight;
		queueCommandIfRendered(() -> new UiButton.SetDropDownSizeCommand(getId(), minDropDownWidth, minDropDownHeight));
		return this;
	}

	public boolean isOpenDropDownIfNotSet() {
		return openDropDownIfNotSet;
	}

	public Button<RECORD> setOpenDropDownIfNotSet(boolean openDropDownIfNotSet) {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
		queueCommandIfRendered(() -> new UiButton.SetOpenDropDownIfNotSetCommand(getId(), openDropDownIfNotSet));
		return this;
	}

	public Component getDropDownComponent() {
		return dropDownComponent;
	}

	public Button<RECORD> setDropDownComponent(Component dropDownComponent) {
		this.dropDownComponent = dropDownComponent;
		queueCommandIfRendered(() -> new UiButton.SetDropDownComponentCommand(getId(), Component.createUiClientObjectReference(dropDownComponent)));
		return this;
	}

	@Override
	protected void doDestroy() {
		if (this.dropDownComponent != null) {
			this.dropDownComponent.destroy();
		}
	}
}
