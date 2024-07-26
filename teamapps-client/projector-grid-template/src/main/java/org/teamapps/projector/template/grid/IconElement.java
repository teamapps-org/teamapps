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

import org.teamapps.projector.template.grid.DtoAbstractGridTemplateElement;
import org.teamapps.projector.template.grid.DtoIconElement;

public class IconElement extends AbstractGridTemplateElement<IconElement> {

	protected int size;

	public IconElement(String propertyName, int size) {
		super(propertyName);
		this.size = size;
	}

	public IconElement(String propertyName, int row, int column, int size) {
		super(propertyName, row, column);
		this.size = size;
	}

	public IconElement(String propertyName, int row, int column, int rowSpan, int colSpan, int size) {
		super(propertyName, row, column, rowSpan, colSpan);
		this.size = size;
	}

	public IconElement(String propertyName, int row, int column, int rowSpan, int colSpan, HorizontalElementAlignment horizontalAlignment, VerticalElementAlignment verticalAlignment, int size) {
		super(propertyName, row, column, rowSpan, colSpan, horizontalAlignment, verticalAlignment);
		this.size = size;
	}

	@Override
	public DtoAbstractGridTemplateElement createUiTemplateElement() {
		DtoIconElement uiIconElement = new DtoIconElement(propertyName, row, column, size);
		mapAbstractGridTemplateElementAttributesToUiElement(uiIconElement);
		return uiIconElement;
	}

	public IconElement setSize(final int size) {
		this.size = size;
		return this;
	}

	public int getSize() {
		return size;
	}

}
