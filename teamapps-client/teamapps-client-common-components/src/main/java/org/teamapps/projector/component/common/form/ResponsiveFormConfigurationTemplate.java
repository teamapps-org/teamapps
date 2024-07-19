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
package org.teamapps.projector.component.common.form;

import org.teamapps.projector.component.common.form.layoutpolicy.FormSection;
import org.teamapps.projector.component.common.form.layoutpolicy.FormSectionFieldPlacement;
import org.teamapps.projector.component.common.grid.layout.GridColumn;
import org.teamapps.projector.component.common.grid.layout.GridRow;
import org.teamapps.projector.format.*;
import org.teamapps.ux.component.format.*;
import org.teamapps.ux.format.*;

import java.util.HashMap;
import java.util.Map;

public class ResponsiveFormConfigurationTemplate {

	private FormSection sectionTemplate;
	private FormSectionFieldPlacement placementTemplate;
	private Map<Integer, FormSectionFieldPlacement> placementTemplateByColumn = new HashMap<>();
	private GridRow rowTemplate;
	private GridRow emptyRowTemplate;
	private GridRow singleColumnRowTemplate;
	private GridColumn columnTemplate;
	private GridColumn emptyColumnTemplate;
	private GridColumn singleColumnColumnTemplate;
	private Map<Integer, GridColumn> columnTemplateByColumn = new HashMap<>();

	public static ResponsiveFormConfigurationTemplate createDefaultTwoColumnTemplate(int fieldMinWidth, int fieldMaxWidth) {
		return createDefaultTwoColumnTemplate(0, fieldMinWidth, fieldMaxWidth);
	}

	public static ResponsiveFormConfigurationTemplate createDefaultTwoColumnTemplate(int minLabelWidth, int fieldMinWidth, int fieldMaxWidth) {
		ResponsiveFormConfigurationTemplate template = new ResponsiveFormConfigurationTemplate();

		template.setSectionTemplate(
				new FormSection(null)
				.setCollapsible(true)
				.setPadding(new Spacing(5))
				.setMargin(new Spacing(5))
				.setGridGap(0));

		template.setRowTemplate(
				new GridRow(new SizingPolicy(SizeType.AUTO, 0, 0), 2, 2));
		template.setEmptyRowTemplate(
				new GridRow(new SizingPolicy(SizeType.AUTO, 0, 0), 0, 0));

		template.setPlacementTemplate(
				new FormSectionFieldPlacement()
				.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
				.setVerticalAlignment(VerticalElementAlignment.CENTER));

		FormSectionFieldPlacement labelPlacement = new FormSectionFieldPlacement()
				.setHorizontalAlignment(HorizontalElementAlignment.LEFT)
				.setVerticalAlignment(VerticalElementAlignment.CENTER)
				.setMinWidth(minLabelWidth);

		FormSectionFieldPlacement fieldPlacement = new FormSectionFieldPlacement()
				.setHorizontalAlignment(HorizontalElementAlignment.STRETCH)
				.setVerticalAlignment(VerticalElementAlignment.CENTER)
				.setMinWidth(fieldMinWidth)
				.setMaxWidth(fieldMaxWidth);

		template.setPlacementTemplateByColumn(0, labelPlacement);
		template.setPlacementTemplateByColumn(1, fieldPlacement);
		template.setPlacementTemplateByColumn(2, labelPlacement);
		template.setPlacementTemplateByColumn(3, fieldPlacement);

		template.setColumnTemplate(
				new GridColumn(new SizingPolicy(SizeType.AUTO, 0, 0), 0, 10));
		template.setColumnTemplateByColumn(1,
				new GridColumn(new SizingPolicy(SizeType.FRACTION, 1, 0), 0, 10));

		return template;
	}

	public static ResponsiveFormConfigurationTemplate createDefault() {
		ResponsiveFormConfigurationTemplate template = new ResponsiveFormConfigurationTemplate();

		template.setSectionTemplate(
				new FormSection(null)
				.setCollapsible(true)
				.setPadding(new Spacing(5))
				.setMargin(new Spacing(5))
				.setGridGap(0));

		template.setPlacementTemplate(
				new FormSectionFieldPlacement()
				.setHorizontalAlignment(HorizontalElementAlignment.STRETCH)
				.setVerticalAlignment(VerticalElementAlignment.CENTER));

		template.setRowTemplate(
				new GridRow(new SizingPolicy(SizeType.AUTO, 0, 0), 2, 2));
		template.setEmptyRowTemplate(
				new GridRow(new SizingPolicy(SizeType.AUTO, 0, 0), 0, 0));

		template.setColumnTemplate(
				new GridColumn(new SizingPolicy(SizeType.FRACTION, 1, 0), 0, 10));

		return template;
	}

	public FormSectionFieldPlacement createFieldPlacementTemplate(int column) {
		FormSectionFieldPlacement fieldPlacement = placementTemplateByColumn.get(column);
		if (fieldPlacement == null && placementTemplate != null) {
			fieldPlacement = placementTemplate.createCopy();
		}
		if (fieldPlacement == null) {
			fieldPlacement = new FormSectionFieldPlacement(null, 0,0);
		}
		return fieldPlacement;
	}

	public GridRow createRowTemplate(boolean emptyCell, boolean singleColumnLayout) {
		if (singleColumnLayout && singleColumnRowTemplate != null) {
			return new GridRow(singleColumnRowTemplate);
		}
		GridRow row = null;
		if (rowTemplate != null) {
			row = new GridRow(rowTemplate);
		}
		if (emptyCell && emptyRowTemplate != null) {
			row = emptyRowTemplate;
		}
		if (row == null) {
			row = new GridRow();
		}
		return row;
	}

	public GridColumn createColumnTemplate(int column, boolean emptyCell, boolean singleColumnLayout) {
		if (singleColumnLayout && singleColumnColumnTemplate != null) {
			return singleColumnColumnTemplate;
		}
		GridColumn colTemplate = null;
		GridColumn templateByColumn = columnTemplateByColumn.get(column);
		if (templateByColumn != null) {
			colTemplate = new GridColumn(templateByColumn);
		}
		if (colTemplate == null && columnTemplate != null) {
			colTemplate = new GridColumn(columnTemplate);
		}
		if (emptyCell && emptyColumnTemplate != null) {
			colTemplate = emptyColumnTemplate;
		}
		if (colTemplate == null) {
			colTemplate = new GridColumn();
		}
		return colTemplate;
	}

	public FormSection getSectionTemplate() {
		return sectionTemplate;
	}

	public void setSectionTemplate(FormSection sectionTemplate) {
		this.sectionTemplate = sectionTemplate;
	}

	public FormSectionFieldPlacement getPlacementTemplate() {
		return placementTemplate;
	}

	public void setPlacementTemplate(FormSectionFieldPlacement placementTemplate) {
		this.placementTemplate = placementTemplate;
	}

	public void setPlacementTemplateByColumn(int column, FormSectionFieldPlacement placementTemplate) {
		placementTemplateByColumn.put(column, placementTemplate);
	}


	public GridRow getRowTemplate() {
		return rowTemplate;
	}

	public void setRowTemplate(GridRow rowTemplate) {
		this.rowTemplate = rowTemplate;
	}

	public GridRow getEmptyRowTemplate() {
		return emptyRowTemplate;
	}

	public void setEmptyRowTemplate(GridRow emptyRowTemplate) {
		this.emptyRowTemplate = emptyRowTemplate;
	}

	public GridRow getSingleColumnRowTemplate() {
		return singleColumnRowTemplate;
	}

	public void setSingleColumnRowTemplate(GridRow singleColumnRowTemplate) {
		this.singleColumnRowTemplate = singleColumnRowTemplate;
	}

	public GridColumn getColumnTemplate() {
		return columnTemplate;
	}

	public void setColumnTemplate(GridColumn columnTemplate) {
		this.columnTemplate = columnTemplate;
	}

	public void setColumnTemplateByColumn(int column,  GridColumn columnTemplate) {
		columnTemplateByColumn.put(column, columnTemplate);
	}

	public GridColumn getEmptyColumnTemplate() {
		return emptyColumnTemplate;
	}

	public void setEmptyColumnTemplate(GridColumn emptyColumnTemplate) {
		this.emptyColumnTemplate = emptyColumnTemplate;
	}

	public GridColumn getSingleColumnColumnTemplate() {
		return singleColumnColumnTemplate;
	}

	public void setSingleColumnColumnTemplate(GridColumn singleColumnColumnTemplate) {
		this.singleColumnColumnTemplate = singleColumnColumnTemplate;
	}

}
