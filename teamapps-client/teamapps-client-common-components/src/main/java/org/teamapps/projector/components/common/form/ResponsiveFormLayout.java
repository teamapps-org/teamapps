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
package org.teamapps.projector.components.common.form;

import org.teamapps.icons.Icon;
import org.teamapps.projector.components.common.form.layoutpolicy.FormLayoutPolicy;
import org.teamapps.projector.components.common.form.layoutpolicy.FormSectionFieldPlacement;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.Label;
import org.teamapps.projector.components.common.grid.layout.GridColumn;
import org.teamapps.projector.components.common.grid.layout.GridRow;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResponsiveFormLayout {

	protected static final String LABEL_NAME_SUFFIX = "Label";
	private final ResponsiveForm responsiveForm;
	private final int minWidth;
	private final List<ResponsiveFormSection> responsiveFormSections = new ArrayList<>();
	private final ResponsiveFormConfigurationTemplate configurationTemplate;


	protected ResponsiveFormLayout(int minWidth, ResponsiveForm responsiveForm, ResponsiveFormConfigurationTemplate configurationTemplate) {
		this.responsiveForm = responsiveForm;
		this.minWidth = minWidth;
		this.configurationTemplate = configurationTemplate;
	}

	public ResponsiveFormField addField(int row, int column, String propertyName, AbstractField field) {
		ResponsiveFormSection responsiveFormSection = getSection();
		FormSectionFieldPlacement fieldPlacementTemplate = configurationTemplate.createFieldPlacementTemplate(column);
		ResponsiveFormField sectionField = new ResponsiveFormField(responsiveFormSection, field, row, column, fieldPlacementTemplate);
		responsiveFormSection.addField(sectionField);
		responsiveForm.addLayoutField(propertyName, field);
		return sectionField;
	}

	public ResponsiveFormField addField(String propertyName, AbstractField field, int column, int colSpan) {
		return addField(propertyName, field, column, colSpan, false);
	}

	private ResponsiveFormField addField(String propertyName, AbstractField field, int column, int colSpan, boolean newLine) {
		int row = getSection().getLastNonEmptyRow() + (newLine ? 1 : 0);
		ResponsiveFormField responsiveFormField = addField(row, column, propertyName, field);
		responsiveFormField.setColSpan(colSpan);
		return responsiveFormField;
	}

	public ResponsiveFormField addLabelField(String propertyName, AbstractField field) {
		return addLabelField(propertyName, field, 0);
	}

	public ResponsiveFormField addLabelField(AbstractField field) {
		return addLabelField(UUID.randomUUID().toString(), field, 0);
	}

	public ResponsiveFormField addLabelField(String propertyName, AbstractField field, int columOffset) {
		int row = getSection().getLastNonEmptyRow() + 1;
		int column = getSection().getLastNonEmptyColumnInRow(row) + columOffset + 1;
		return addField(row, column, propertyName, field);
	}

	public ResponsiveFormField addLabelField(AbstractField field, int columOffset) {
		return addLabelField(UUID.randomUUID().toString(), field, columOffset);
	}

	public LabelAndField addLabelAndField(AbstractField field) {
		return addLabelAndField(null, null, UUID.randomUUID().toString(), field, true, 0);
	}

	public LabelAndField addLabelAndField(String propertyName, AbstractField field) {
		return addLabelAndField(null, null, propertyName, field, true, 0);
	}

	public LabelAndField addLabelAndField(AbstractField field, boolean newRow) {
		return addLabelAndField(null, null, UUID.randomUUID().toString(), field, newRow, 0);
	}

	public LabelAndField addLabelAndField(String propertyName, AbstractField field, boolean newRow) {
		return addLabelAndField(null, null, propertyName, field, newRow, 0);
	}

	public LabelAndField addLabelAndField(Icon<?, ?> icon, String caption, String propertyName, AbstractField field) {
		return addLabelAndField(icon, caption, propertyName, field, true, 0);
	}

	public LabelAndField addLabelAndField(Icon<?, ?> icon, String caption, AbstractField field) {
		return addLabelAndField(icon, caption, UUID.randomUUID().toString(), field, true, 0);
	}

	public LabelAndField addLabelAndField(Icon<?, ?> icon, String caption, String propertyName, AbstractField field, boolean newRow) {
		return addLabelAndField(icon, caption, propertyName, field, newRow, 0);
	}

	public LabelAndField addLabelAndField(Icon<?, ?> icon, String caption, AbstractField field, boolean newRow) {
		return addLabelAndField(icon, caption, UUID.randomUUID().toString(), field, newRow, 0);
	}

	public LabelAndField addLabelAndField(Icon<?, ?> icon, String caption, String propertyName, AbstractField field, boolean newRow, int columnOffset) {
		Label label = null;
		if (icon != null || caption != null) {
			label = new Label(caption, icon);
			label.setTargetComponent(field);
		}
		int row = getSection().getLastNonEmptyRow();
		int column = columnOffset;
		if (newRow || row == -1) {
			row++;
		} else {
			column = getSection().getLastNonEmptyColumnInRow(row) + columnOffset + 1;
		}
		ResponsiveFormField labelField = null;
		if (label != null) {
			labelField = addComponent(row, column, label);
		}
		ResponsiveFormField responsiveFormField = addField(row, column + 1, propertyName, field);
		return new LabelAndField(labelField, responsiveFormField);
	}

	public ResponsiveFormField addComponent(int row, int column, Component field) {
		ResponsiveFormSection responsiveFormSection = getSection();
		FormSectionFieldPlacement fieldPlacementTemplate = configurationTemplate.createFieldPlacementTemplate(column);
		ResponsiveFormField sectionField = new ResponsiveFormField(responsiveFormSection, field, row, column, fieldPlacementTemplate);
		responsiveFormSection.addField(sectionField);
		responsiveForm.addLayoutComponent(field);
		return sectionField;
	}

	public ResponsiveFormField addComponent(Component field, int column, int colSpan) {
		return addComponent(field, column, colSpan, false);
	}

	public ResponsiveFormField addComponent(Component field, int column, int colSpan, boolean newLine) {
		int row = getSection().getLastNonEmptyRow() + (newLine ? 1 : 0);
		ResponsiveFormField responsiveFormField = addComponent(row, column, field);
		responsiveFormField.setColSpan(colSpan);
		return responsiveFormField;
	}

	public ResponsiveFormField addLabelComponent(Component field) {
		return addLabelComponent(field, 0);
	}

	public ResponsiveFormField addLabelComponent(Component field, int columOffset) {
		int row = getSection().getLastNonEmptyRow() + 1;
		int column = getSection().getLastNonEmptyColumnInRow(row) + columOffset + 1;
		return addComponent(row, column, field);
	}

	public LabelAndField addLabelAndComponent(Component field) {
		return addLabelAndComponent(null, null, field, true, 0);
	}

	public LabelAndField addLabelAndComponent(Component field, boolean newRow) {
		return addLabelAndComponent(null, null, field, newRow, 0);
	}

	public LabelAndField addLabelAndComponent(Icon<?, ?> icon, String caption, Component field) {
		return addLabelAndComponent(icon, caption, field, true, 0);
	}

	public LabelAndField addLabelAndComponent(Icon<?, ?> icon, String caption, Component field, boolean newRow) {
		return addLabelAndComponent(icon, caption, field, newRow, 0);
	}

	public LabelAndField addLabelAndComponent(Icon<?, ?> icon, String caption, Component field, boolean newRow, int columnOffset) {
		Label label = null;
		if (icon != null || caption != null) {
			label = new Label(caption, icon);
			label.setTargetComponent(field);
		}
		int row = getSection().getLastNonEmptyRow();
		int column = columnOffset;
		if (newRow || row == -1) {
			row++;
		} else {
			column = getSection().getLastNonEmptyColumnInRow(row) + columnOffset + 1;
		}
		ResponsiveFormField labelField = null;
		if (label != null) {
			labelField = addComponent(row, column, label);
		}
		ResponsiveFormField responsiveFormField = addComponent(row, column + 1, field);
		return new LabelAndField(labelField, responsiveFormField);
	}

	public int getLastNonEmptyRowInSection() {
		return getSection().getLastNonEmptyRow();
	}

	public int getLastNonEmptyColumnInSection() {
		return getSection().getLastNonEmptyColumn();
	}

	public int getLastNonEmptyColumnInRow(int row) {
		return getSection().getLastNonEmptyColumnInRow(row);
	}

	public void setRowConfig(int row, GridRow rowConfig) {
		getSection().setRowConfig(row, rowConfig);
	}

	public void setColumnConfig(int colum, GridColumn columnConfig) {
		getSection().setColumnConfig(colum, columnConfig);
	}

	protected ResponsiveFormSection getSection() {
		if (responsiveFormSections.isEmpty()) {
			addSection();
		}
		return responsiveFormSections.get(responsiveFormSections.size() - 1);
	}

	public ResponsiveFormSection addSection() {
		return addSection(null, null);
	}

	public ResponsiveFormSection addSection(Icon<?, ?> icon, String caption) {
		return addSection(icon, caption, null);
	}

	public ResponsiveFormSection addSection(Icon<?, ?> icon, String caption, ResponsiveFormConfigurationTemplate configurationTemplate) {
		if (configurationTemplate == null) {
			configurationTemplate = responsiveForm.getConfigurationTemplate();
		}
		ResponsiveFormSection responsiveFormSection = new ResponsiveFormSection(this, "section" + (responsiveFormSections.size() + 1), configurationTemplate);
		if (configurationTemplate.getSectionTemplate() == null || configurationTemplate.getSectionTemplate().getHeaderTemplate() == null) {
			responsiveFormSection.setHeaderTemplate(BaseTemplates.FORM_SECTION_HEADER);
		}
		if (icon != null || caption != null) {
			responsiveFormSection.setHeaderData(new BaseTemplateRecord<>(icon, caption));
		}
		responsiveFormSections.add(responsiveFormSection);
		return responsiveFormSection;
	}

	protected FormLayoutPolicy createFormLayoutPolicy() {
		FormLayoutPolicy policy = new FormLayoutPolicy();
		policy.setMinWidth(minWidth);
		for (ResponsiveFormSection responsiveFormSection : responsiveFormSections) {
			policy.addSection(responsiveFormSection.createFormSection());
		}
		return policy;
	}

	protected FormLayoutPolicy createSmallScreenLayout() {
		FormLayoutPolicy policy = new FormLayoutPolicy();
		policy.setMinWidth(0);
		for (ResponsiveFormSection responsiveFormSection : responsiveFormSections) {
			policy.addSection(responsiveFormSection.createSmallScreenFormSection());
		}
		return policy;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public static class LabelAndField {
		public ResponsiveFormField label;
		public ResponsiveFormField field;

		public LabelAndField(ResponsiveFormField label, ResponsiveFormField field) {
			this.label = label;
			this.field = field;
		}
	}
}
