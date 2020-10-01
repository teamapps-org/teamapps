/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.template.gridtemplate;

import org.teamapps.common.format.Color;
import org.teamapps.dto.AbstractUiTemplateElement;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.component.format.VerticalElementAlignment;

import java.util.Collections;
import java.util.List;

public abstract class AbstractTemplateElement<C extends AbstractTemplateElement> {

	protected String dataKey;
	protected int row;
	protected int column;
	protected int rowSpan = 1;
	protected int colSpan = 1;
	protected HorizontalElementAlignment horizontalAlignment = HorizontalElementAlignment.STRETCH;
	protected VerticalElementAlignment verticalAlignment = VerticalElementAlignment.CENTER;
	protected Spacing margin;
	protected Color backgroundColor;

	public AbstractTemplateElement(String dataKey) {
		this.dataKey = dataKey;
	}

	public AbstractTemplateElement(String dataKey, int row, int column) {
		this.dataKey = dataKey;
		this.row = row;
		this.column = column;
	}

	public AbstractTemplateElement(String dataKey, int row, int column, int rowSpan, int colSpan) {
		this.dataKey = dataKey;
		this.row = row;
		this.column = column;
		this.rowSpan = rowSpan;
		this.colSpan = colSpan;
	}

	public AbstractTemplateElement(String dataKey, int row, int column, int rowSpan, int colSpan, HorizontalElementAlignment horizontalAlignment, VerticalElementAlignment verticalAlignment) {
		this.dataKey = dataKey;
		this.row = row;
		this.column = column;
		this.rowSpan = rowSpan;
		this.colSpan = colSpan;
		this.horizontalAlignment = horizontalAlignment;
		this.verticalAlignment = verticalAlignment;
	}

	public C setRow(final int row) {
		this.row = row;
		return (C) this;
	}

	public C setColumn(final int column) {
		this.column = column;
		return (C) this;
	}

	public C setRowSpan(final int rowSpan) {
		this.rowSpan = rowSpan;
		return (C) this;
	}

	public C setColSpan(final int colSpan) {
		this.colSpan = colSpan;
		return (C) this;
	}

	public C setDataKey(final String dataKey) {
		this.dataKey = dataKey;
		return (C) this;
	}

	public String getDataKey() {
		return dataKey;
	}

	public List<String> getDataKeys() {
		return Collections.singletonList(dataKey);
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public int getRowSpan() {
		return rowSpan;
	}

	public int getColSpan() {
		return colSpan;
	}

	public HorizontalElementAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public AbstractTemplateElement<C> setHorizontalAlignment(HorizontalElementAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	public VerticalElementAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	public AbstractTemplateElement<C> setVerticalAlignment(VerticalElementAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
		return this;
	}

	public Spacing getMargin() {
		return margin;
	}

	public AbstractTemplateElement<C> setMargin(Spacing margin) {
		this.margin = margin;
		return this;
	}

	public abstract AbstractUiTemplateElement createUiTemplateElement();

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public AbstractTemplateElement<C> setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	protected void mapAbstractTemplateElementAttributesToUiElement(AbstractUiTemplateElement uiElement) {
		uiElement.setRowSpan(rowSpan);
		uiElement.setColSpan(colSpan);
		uiElement.setHorizontalAlignment(horizontalAlignment.toUiHorizontalElementAlignment());
		uiElement.setVerticalAlignment(verticalAlignment.toUiVerticalElementAlignment());
		uiElement.setMargin(margin != null ? margin.createUiSpacing() : null);
		uiElement.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
	}
}
