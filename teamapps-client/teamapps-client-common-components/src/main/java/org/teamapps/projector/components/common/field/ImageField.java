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
package org.teamapps.projector.components.common.field;

import org.teamapps.common.format.RgbaColor;
import org.teamapps.common.format.Color;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.ProjectorComponent;
import org.teamapps.projector.format.Length;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.projector.format.Border;
import org.teamapps.ux.ImageSizing;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class ImageField extends AbstractField<String> {

	private Length width = Length.AUTO; 
	private Length height = Length.AUTO;
	private Border border;
	private ImageSizing imageSizing = ImageSizing.CONTAIN;
	private Color backgroundColor = RgbaColor.TRANSPARENT;

	public ImageField() {
		super();
	}

	@Override
	public DtoImageField createDto() {
		DtoImageField uiImageField = new DtoImageField();
		mapAbstractFieldAttributesToUiField(uiImageField);
		uiImageField.setWidth(width.toCssString());
		uiImageField.setHeight(height.toCssString());
		uiImageField.setBorder(border != null ? border.createUiBorder() : null);
		uiImageField.setImageSizing(imageSizing.toUiImageSizing());
		uiImageField.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString(): null);
		return uiImageField;
	}

	public Length getWidth() {
		return width;
	}

	public ImageField setWidth(Length width) {
		this.width = width;
		sendCommandIfRendered(() -> new DtoImageField.UpdateCommand(createDto()));
		return this;
	}

	public Length getHeight() {
		return height;
	}

	public ImageField setHeight(Length height) {
		this.height = height;
		sendCommandIfRendered(() -> new DtoImageField.UpdateCommand(createDto()));
		return this;
	}

	public Border getBorder() {
		return border;
	}

	public ImageField setBorder(Border border) {
		this.border = border;
		sendCommandIfRendered(() -> new DtoImageField.UpdateCommand(createDto()));
		return this;
	}

	public ImageSizing getImageSizing() {
		return imageSizing;
	}

	public ImageField setImageSizing(ImageSizing imageSizing) {
		this.imageSizing = imageSizing;
		sendCommandIfRendered(() -> new DtoImageField.UpdateCommand(createDto()));
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		sendCommandIfRendered(() -> new DtoImageField.UpdateCommand(createDto()));
	}
}