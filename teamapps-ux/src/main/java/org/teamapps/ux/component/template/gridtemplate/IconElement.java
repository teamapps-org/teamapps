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

import org.teamapps.dto.AbstractUiTemplateElement;
import org.teamapps.dto.UiIconElement;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.VerticalElementAlignment;

public class IconElement extends AbstractTemplateElement<IconElement> {

	protected int size;

	public IconElement(String dataKey, int size) {
		super(dataKey);
		this.size = size;
	}

	public IconElement(String dataKey, int row, int column, int size) {
		super(dataKey, row, column);
		this.size = size;
	}

	public IconElement(String dataKey, int row, int column, int rowSpan, int colSpan, int size) {
		super(dataKey, row, column, rowSpan, colSpan);
		this.size = size;
	}

	public IconElement(String dataKey, int row, int column, int rowSpan, int colSpan, HorizontalElementAlignment horizontalAlignment, VerticalElementAlignment verticalAlignment, int size) {
		super(dataKey, row, column, rowSpan, colSpan, horizontalAlignment, verticalAlignment);
		this.size = size;
	}

	@Override
	public AbstractUiTemplateElement createUiTemplateElement() {
		UiIconElement uiIconElement = new UiIconElement(dataKey, row, column, size);
		mapAbstractTemplateElementAttributesToUiElement(uiIconElement);
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
