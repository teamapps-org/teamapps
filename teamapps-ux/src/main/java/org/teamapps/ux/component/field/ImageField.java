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

import org.teamapps.dto.UiField;
import org.teamapps.dto.UiImageField;
import org.teamapps.ux.component.format.Border;
import org.teamapps.ux.component.format.ImageSizing;

public class ImageField extends AbstractField<String> {

	private int width; // 0 = the width will be determined by the component's content and context
	private int height; // 0 = the height will be determined by the component's content and context
	private Border border;
	private ImageSizing imageSizing = ImageSizing.CONTAIN;

	public ImageField() {
		super();
	}

	@Override
	public UiField createUiComponent() {
		UiImageField uiImageField = new UiImageField(getId());
		mapAbstractFieldAttributesToUiField(uiImageField);
		uiImageField.setWidth(width);
		uiImageField.setHeight(height);
		uiImageField.setBorder(border != null ? border.createUiBorder() : null);
		uiImageField.setImageSizing(imageSizing.toUiImageSizing());
		return uiImageField;
	}

	@Override
	protected void doDestroy() {
		// nothing to do
	}

	public int getWidth() {
		return width;
	}

	public ImageField setWidth(int width) {
		this.width = width;
		queueCommandIfRendered(() -> new UiImageField.SetSizeCommand(getId(), width, height));
		return this;
	}

	public int getHeight() {
		return height;
	}

	public ImageField setHeight(int height) {
		this.height = height;
		queueCommandIfRendered(() -> new UiImageField.SetSizeCommand(getId(), width, height));
		return this;
	}

	public Border getBorder() {
		return border;
	}

	public ImageField setBorder(Border border) {
		this.border = border;
		queueCommandIfRendered(() -> new UiImageField.SetBorderCommand(getId(), border != null ? border.createUiBorder() : null));
		return this;
	}

	public ImageSizing getImageSizing() {
		return imageSizing;
	}

	public ImageField setImageSizing(ImageSizing imageSizing) {
		this.imageSizing = imageSizing;
		queueCommandIfRendered(() -> new UiImageField.SetImageSizingCommand(getId(), imageSizing.toUiImageSizing()));
		return this;
	}
}
