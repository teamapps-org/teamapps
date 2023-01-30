/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.ux.component.template.gridtemplate;

import org.teamapps.dto.AbstractUiTemplateElement;
import org.teamapps.dto.UiCssAlignItems;
import org.teamapps.dto.UiCssJustifyContent;
import org.teamapps.dto.UiFloatingElement;
import org.teamapps.ux.css.CssAlignItems;
import org.teamapps.ux.css.CssJustifyContent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FloatingElement extends AbstractTemplateElement<FloatingElement> {

	protected List<AbstractTemplateElement<?>> elements = new ArrayList<>();
	protected boolean wrap;
	protected CssAlignItems alignItems = CssAlignItems.CENTER;
	protected CssJustifyContent justifyContent = CssJustifyContent.START;


	public FloatingElement() {
		super(null);
	}

	public FloatingElement(int row, int column) {
		super(null, row, column);
	}

	@Override
	public AbstractUiTemplateElement createUiTemplateElement() {
		List<AbstractUiTemplateElement> uiElements = elements.stream()
				.map(element -> element != null ? element.createUiTemplateElement() : null)
				.collect(Collectors.toList());
		UiFloatingElement uiFloatingElement = new UiFloatingElement(propertyName, row, column, uiElements);
		mapAbstractTemplateElementAttributesToUiElement(uiFloatingElement);
		uiFloatingElement.setWrap(wrap);
		uiFloatingElement.setAlignItems(alignItems != null ? UiCssAlignItems.valueOf(alignItems.name()) : null);
		uiFloatingElement.setJustifyContent(justifyContent != null ? UiCssJustifyContent.valueOf(justifyContent.name()) : null);
		return uiFloatingElement;
	}

	public FloatingElement addElement(AbstractTemplateElement<?> element) {
		elements.add(element);
		return this;
	}

	public FloatingElement setElements(final List<AbstractTemplateElement<?>> elements) {
		this.elements = elements;
		return this;
	}

	public List<AbstractTemplateElement<?>> getElements() {
		return elements;
	}

	public boolean isWrap() {
		return wrap;
	}

	public FloatingElement setWrap(boolean wrap) {
		this.wrap = wrap;
		return this;
	}

	public CssAlignItems getAlignItems() {
		return alignItems;
	}

	public FloatingElement setAlignItems(CssAlignItems alignItems) {
		this.alignItems = alignItems;
		return this;
	}

	public CssJustifyContent getJustifyContent() {
		return justifyContent;
	}

	public FloatingElement setJustifyContent(CssJustifyContent justifyContent) {
		this.justifyContent = justifyContent;
		return this;
	}

	@Override
	public List<String> getPropertyNames() {
		return elements.stream()
				.flatMap(element -> element.getPropertyNames().stream())
				.collect(Collectors.toList());
	}

}
