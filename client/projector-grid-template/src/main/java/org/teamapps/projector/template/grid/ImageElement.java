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

import org.teamapps.projector.format.Border;
import org.teamapps.projector.format.BoxShadow;
import org.teamapps.projector.format.Spacing;

public class ImageElement extends AbstractGridTemplateElement {

	protected int width;
	protected int height;
	protected Border border;
	protected Spacing padding;
	protected BoxShadow shadow;
	protected ImageSizing imageSizing = ImageSizing.COVER;

	public ImageElement(String propertyName, int width, int height) {
		super(propertyName);
		this.width = width;
		this.height = height;
	}

	public ImageElement(String propertyName, int row, int column, int width, int height) {
		super(propertyName, row, column);
		this.width = width;
		this.height = height;
	}

	public ImageElement(String propertyName, int row, int column, int rowSpan, int colSpan, int width, int height) {
		super(propertyName, row, column, rowSpan, colSpan);
		this.width = width;
		this.height = height;
	}

	public ImageElement(String propertyName, int row, int column, int rowSpan, int colSpan, HorizontalElementAlignment horizontalAlignment, VerticalElementAlignment verticalAlignment, int width, int height) {
		super(propertyName, row, column, rowSpan, colSpan, horizontalAlignment, verticalAlignment);
		this.width = width;
		this.height = height;
	}

	public ImageElement(String propertyName, int width, int height, Border border, Spacing padding, BoxShadow shadow) {
		super(propertyName);
		this.width = width;
		this.height = height;
		this.border = border;
		this.padding = padding;
		this.shadow = shadow;
	}

	public ImageElement(String propertyName, int row, int column, int width, int height, Border border, Spacing padding, BoxShadow shadow) {
		super(propertyName, row, column);
		this.width = width;
		this.height = height;
		this.border = border;
		this.padding = padding;
		this.shadow = shadow;
	}

	public ImageElement(String propertyName, int row, int column, int rowSpan, int colSpan, int width, int height, Border border, Spacing padding, BoxShadow shadow) {
		super(propertyName, row, column, rowSpan, colSpan);
		this.width = width;
		this.height = height;
		this.border = border;
		this.padding = padding;
		this.shadow = shadow;
	}

	public ImageElement setWidth(final int width) {
		this.width = width;
		return this;
	}

	public ImageElement setHeight(final int height) {
		this.height = height;
		return this;
	}

	public ImageElement setBorder(final Border border) {
		this.border = border;
		return this;
	}

	public ImageElement setPadding(final Spacing padding) {
		this.padding = padding;
		return this;
	}

	public ImageElement setShadow(final BoxShadow shadow) {
		this.shadow = shadow;
		return this;
	}

	public ImageElement setHorizontalAlignment(final HorizontalElementAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	public AbstractGridTemplateElement setVerticalAlignment(final VerticalElementAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
		return this;
	}

	public AbstractGridTemplateElement setMargin(final Spacing margin) {
		this.margin = margin;
		return this;
	}

	@Override
	public DtoAbstractGridTemplateElement createDtoTemplateElement() {
		DtoImageElement uiImageElement = new DtoImageElement();
		mapAbstractGridTemplateElementAttributesToUiElement(uiImageElement);
		uiImageElement.setProperty(propertyName);
		uiImageElement.setRow(row);
		uiImageElement.setColumn(column);
		uiImageElement.setWidth(width);
		uiImageElement.setHeight(height);
		uiImageElement.setBorder(border != null ? border.createDtoBorder() : null);
		uiImageElement.setPadding(padding != null ? padding.createDtoSpacing() : null);
		uiImageElement.setShadow(shadow != null ? shadow.createDtoShadow() : null);
		uiImageElement.setImageSizing(imageSizing.toUiImageSizing());
		return uiImageElement;
	}

	public ImageElement setRow(final int row) {
		this.row = row;
		return this;
	}

	public ImageElement setColumn(final int column) {
		this.column = column;
		return this;
	}

	public ImageElement setRowSpan(final int rowSpan) {
		this.rowSpan = rowSpan;
		return this;
	}

	public ImageElement setColSpan(final int colSpan) {
		this.colSpan = colSpan;
		return this;
	}


	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Border getBorder() {
		return border;
	}

	public Spacing getPadding() {
		return padding;
	}

	public BoxShadow getShadow() {
		return shadow;
	}

	public ImageSizing getImageSizing() {
		return imageSizing;
	}

	public ImageElement setImageSizing(ImageSizing imageSizing) {
		this.imageSizing = imageSizing;
		return this;
	}
}
