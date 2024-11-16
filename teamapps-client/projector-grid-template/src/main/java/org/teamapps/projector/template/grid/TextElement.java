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
import org.teamapps.projector.format.FontStyle;
import org.teamapps.projector.format.Spacing;
import org.teamapps.projector.format.TextAlignment;
import org.teamapps.projector.template.grid.DtoAbstractGridTemplateElement;
import org.teamapps.projector.template.grid.DtoTextElement;

public class TextElement extends AbstractGridTemplateElement<TextElement> {

	protected FontStyle fontStyle;
	protected float lineHeight = 1.2f;
	protected boolean wrapLines;
	protected Spacing padding;
	protected TextAlignment textAlignment = TextAlignment.LEFT;


	public TextElement(String propertyName) {
		super(propertyName);
	}

	public TextElement(String propertyName, int row, int column) {
		super(propertyName, row, column);
	}

	public TextElement(String propertyName, int row, int column, int rowSpan, int colSpan) {
		super(propertyName, row, column, rowSpan, colSpan);
	}

	public TextElement(String propertyName, int row, int column, int rowSpan, int colSpan, HorizontalElementAlignment horizontalAlignment, VerticalElementAlignment verticalAlignment) {
		super(propertyName, row, column, rowSpan, colSpan, horizontalAlignment, verticalAlignment);
	}

	@Override
	public DtoAbstractGridTemplateElement createDtoTemplateElement() {
		DtoTextElement uiTextElement = new DtoTextElement(propertyName, row, column);
		mapAbstractGridTemplateElementAttributesToUiElement(uiTextElement);
		mapTextElementAttributesToUiElement(uiTextElement);
		return uiTextElement;
	}

	protected void mapTextElementAttributesToUiElement(DtoTextElement uiTextElement) {
		uiTextElement.setFontStyle(fontStyle != null ? fontStyle.createDtoFontStyle() : null);
		uiTextElement.setLineHeight(lineHeight);
		uiTextElement.setWrapLines(wrapLines);
		uiTextElement.setPadding(padding != null ? padding.createDtoSpacing() : null);
		uiTextElement.setTextAlignment(textAlignment);
	}

	public String getProperty() {
		return propertyName;
	}

	public TextElement setFontStyle(final FontStyle fontStyle) {
		this.fontStyle = fontStyle;
		return this;
	}

	public TextElement setFontStyle(float relativeFontSize) {
		this.fontStyle = new FontStyle(relativeFontSize);
		return this;
	}

	public TextElement setFontStyle(float relativeFontSize, Color fontColor) {
		this.fontStyle = new FontStyle(relativeFontSize, fontColor);
		return this;
	}

	public TextElement setLineHeight(final float lineHeight) {
		this.lineHeight = lineHeight;
		return this;
	}

	public TextElement setWrapLines(final boolean wrapLines) {
		this.wrapLines = wrapLines;
		return this;
	}

	public TextElement setPadding(final Spacing padding) {
		this.padding = padding;
		return this;
	}

	public FontStyle getFontStyle() {
		return fontStyle;
	}

	public float getLineHeight() {
		return lineHeight;
	}

	public boolean isWrapLines() {
		return wrapLines;
	}

	public Spacing getPadding() {
		return padding;
	}

	public TextAlignment getTextAlignment() {
		return textAlignment;
	}

	public TextElement setTextAlignment(TextAlignment textAlignment) {
		this.textAlignment = textAlignment;
		return this;
	}
}
