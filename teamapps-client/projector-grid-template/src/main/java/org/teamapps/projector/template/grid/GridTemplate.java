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
package org.teamapps.projector.template.grid;

import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.format.Border;
import org.teamapps.projector.format.FontStyle;
import org.teamapps.projector.format.Spacing;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.dto.DtoAbstractGridTemplateElement;
import org.teamapps.projector.template.grid.dto.DtoGridColumn;
import org.teamapps.projector.template.grid.dto.DtoGridRow;
import org.teamapps.projector.template.grid.dto.DtoGridTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = GridTemplateLibrary.class)
public class GridTemplate implements Template {

	private int minWidth = 0;
	private int maxWidth = 0;
	private int minHeight = 0;
	private int maxHeight = 0;
	private Spacing padding;
	private int gridGap = 0;
	private Color backgroundColor;
	private Border border;
	private String ariaLabelProperty = "ariaLabel";
	private String titleProperty = "title";

	List<GridColumn> columns = new ArrayList<>();
	List<GridRow> rows = new ArrayList<>();
	List<AbstractGridTemplateElement<?>> elements = new ArrayList<>();

	public GridTemplate() {

	}

	public GridTemplate(int minWidth, int maxWidth, int minHeight, int maxHeight, Spacing padding, int gridGap) {
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.padding = padding;
		this.gridGap = gridGap;
	}

	public List<String> getPropertyNames() {
		ArrayList<String> propertyNames = elements.stream()
				.flatMap(element -> element.getPropertyNames().stream())
				.distinct()
				.collect(Collectors.toCollection(ArrayList::new));
		if (ariaLabelProperty != null) {
			propertyNames.add(ariaLabelProperty);
		}
		if (titleProperty != null) {
			propertyNames.add(titleProperty);
		}
		return propertyNames;
	}

	public GridTemplate addColumn(GridColumn column) {
		columns.add(column);
		return this;
	}

	public GridTemplate addColumn(SizingPolicy widthPolicy) {
		columns.add(new GridColumn(widthPolicy, 0, 0));
		return this;
	}

	public GridTemplate addColumn(SizingPolicy widthPolicy, int leftPadding, int rightPadding) {
		columns.add(new GridColumn(widthPolicy, leftPadding, rightPadding));
		return this;
	}

	public GridTemplate addColumn(SizeType type, float widthValue, int minAbsoluteWidth, int leftPadding, int rightPadding) {
		columns.add(new GridColumn(new SizingPolicy(type, widthValue, minAbsoluteWidth), leftPadding, rightPadding));
		return this;
	}

	public GridTemplate addRow(SizingPolicy height) {
		rows.add(new GridRow(height, 0, 0));
		return this;
	}

	public GridTemplate addRow(GridRow row) {
		rows.add(row);
		return this;
	}

	public GridTemplate addRow(SizingPolicy heightPolicy, int topPadding, int bottomPadding) {
		rows.add(new GridRow(heightPolicy, topPadding, bottomPadding));
		return this;
	}

	public GridTemplate addRow(SizeType type, float heightValue, int minAbsoluteHeight, int topPadding, int bottomPadding) {
		rows.add(new GridRow(new SizingPolicy(type, heightValue, minAbsoluteHeight), topPadding, bottomPadding));
		return this;
	}

	public GridTemplate setMinHeight(final int minHeight) {
		this.minHeight = minHeight;
		return this;
	}

	public GridTemplate setMaxHeight(final int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	public GridTemplate setColumns(final List<GridColumn> columns) {
		this.columns = columns;
		return this;
	}

	public GridTemplate setRows(final List<GridRow> rows) {
		this.rows = rows;
		return this;
	}

	public GridTemplate setElements(final List<AbstractGridTemplateElement<?>> elements) {
		this.elements = elements;
		return this;
	}

	public GridTemplate addElement(final AbstractGridTemplateElement element) {
		this.elements.add(element);
		return this;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public List<GridColumn> getColumns() {
		return columns;
	}

	public List<GridRow> getRows() {
		return rows;
	}

	public List<AbstractGridTemplateElement<?>> getElements() {
		return elements;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public GridTemplate setMinWidth(int minWidth) {
		this.minWidth = minWidth;
		return this;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public GridTemplate setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	public Spacing getPadding() {
		return padding;
	}

	public GridTemplate setPadding(Spacing padding) {
		this.padding = padding;
		return this;
	}

	public int getGridGap() {
		return gridGap;
	}

	public GridTemplate setGridGap(int gridGap) {
		this.gridGap = gridGap;
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public GridTemplate setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Border getBorder() {
		return border;
	}

	public GridTemplate setBorder(Border border) {
		this.border = border;
		return this;
	}

	public String getAriaLabelProperty() {
		return ariaLabelProperty;
	}

	public GridTemplate setAriaLabelProperty(String ariaLabelProperty) {
		this.ariaLabelProperty = ariaLabelProperty;
		return this;
	}

	public String getTitleProperty() {
		return titleProperty;
	}

	public GridTemplate setTitleProperty(String titleProperty) {
		this.titleProperty = titleProperty;
		return this;
	}

	@Override
	public DtoGridTemplate createConfig() {
		List<DtoGridColumn> uiColumns = columns.stream()
				.map(column -> column != null ? column.createUiGridColumn() : null)
				.collect(Collectors.toList());
		List<DtoGridRow> uiRows = rows.stream()
				.map(row -> row != null ? row.createUiGridRow() : null)
				.collect(Collectors.toList());
		List<DtoAbstractGridTemplateElement> uiTemplateElements = elements.stream()
				.map(element -> element != null ? element.createUiTemplateElement() : null)
				.collect(Collectors.toList());
		DtoGridTemplate uiGridTemplate = new DtoGridTemplate(uiColumns, uiRows, uiTemplateElements);
		uiGridTemplate.setMinWidth(minWidth);
		uiGridTemplate.setMaxWidth(maxWidth);
		uiGridTemplate.setMinHeight(minHeight);
		uiGridTemplate.setMaxHeight(maxHeight);
		if (padding != null) {
			uiGridTemplate.setPadding(padding.createUiSpacing());
		}
		uiGridTemplate.setGridGap(gridGap);
		uiGridTemplate.setBorder(border != null ? border.createUiBorder() : null);
		uiGridTemplate.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		uiGridTemplate.setAriaLabelProperty(ariaLabelProperty);
		uiGridTemplate.setTitleProperty(titleProperty);
		return uiGridTemplate;
	}

	public Template createDarkThemeTemplate() {
			GridTemplate orig = (GridTemplate) this;
			GridTemplate tpl = new GridTemplate(orig.getMinWidth(), orig.getMaxWidth(), orig.getMinHeight(), orig.getMaxHeight(), orig.getPadding(), orig.getGridGap());
			tpl.setRows(orig.getRows());
			tpl.setColumns(orig.getColumns());
			List<AbstractGridTemplateElement<?>> darkModeElements = new ArrayList<>();
			orig.getElements().forEach(element -> {
				if (element instanceof FloatingElement) {
					FloatingElement floatingElement = (FloatingElement) element;
					List<AbstractGridTemplateElement<?>> elements = floatingElement.getElements();
					List<AbstractGridTemplateElement<?>> newElements = new ArrayList<>();
					for (AbstractGridTemplateElement<?> fltElement : elements) {
						newElements.add(convertElementToDarkMode(fltElement));
					}
					FloatingElement newFloatingElement = new FloatingElement(floatingElement.getRow(), floatingElement.getColumn()).setElements(newElements);
					darkModeElements.add(newFloatingElement);
				} else {
					darkModeElements.add(convertElementToDarkMode(element));
				}
			});
			tpl.setElements(darkModeElements);
			return tpl;
	}

	private static AbstractGridTemplateElement<?> convertElementToDarkMode(AbstractGridTemplateElement<?> element) {
		if (element instanceof TextElement) {
			TextElement txt = (TextElement) element;
			TextElement newTextElement = new TextElement(txt.getProperty(), txt.getRow(), txt.getColumn(), txt.getRowSpan(), txt.getColSpan(), txt.getHorizontalAlignment(), txt.getVerticalAlignment());
			newTextElement.setPadding(txt.getPadding());
			newTextElement.setMargin(txt.getMargin());
			newTextElement.setTextAlignment(txt.getTextAlignment());
			newTextElement.setLineHeight(txt.getLineHeight());
			FontStyle style = txt.getFontStyle();
			if (style == null) {
				FontStyle newFontStyle = new FontStyle();
				newTextElement.setFontStyle(newFontStyle);
				return newTextElement;
			}
			Color fontColor = style.getFontColor();
			Color newColor = RgbaColor.WHITE;
			if (fontColor != null) {
				newColor = convertColorToDarkMode(fontColor);
			} else {
				newColor = null;
			}
			FontStyle newFontStyle = new FontStyle(style.getRelativeFontSize(), newColor, style.getBackgroundColor(), style.isBold(), style.isUnderline(), style.isItalic());
			newTextElement.setFontStyle(newFontStyle);
			return newTextElement;
		}
		return element;
	}

	static Color convertColorToDarkMode(Color color) {
		if (color.equals(RgbaColor.BLACK)) {
			return RgbaColor.WHITE;
		}
		if (color.equals(RgbaColor.WHITE)) {
			//todo: replace badge color as well
		}
		if (color.equals(RgbaColor.GRAY)) {
			return RgbaColor.LIGHT_GRAY;
		}
		return RgbaColor.WHITE;
	}

}
