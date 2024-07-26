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

import org.teamapps.projector.format.AlignItems;
import org.teamapps.projector.format.JustifyContent;
import org.teamapps.projector.template.grid.DtoAbstractGridTemplateElement;
import org.teamapps.projector.template.grid.DtoFloatingElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FloatingElement extends AbstractGridTemplateElement<FloatingElement> {

	protected List<AbstractGridTemplateElement<?>> elements = new ArrayList<>();
	protected boolean wrap;
	protected AlignItems alignItems = AlignItems.CENTER;
	protected JustifyContent justifyContent = JustifyContent.START;


	public FloatingElement() {
		super(null);
	}

	public FloatingElement(int row, int column) {
		super(null, row, column);
	}

	@Override
	public DtoAbstractGridTemplateElement createUiTemplateElement() {
		List<DtoAbstractGridTemplateElement> uiElements = elements.stream()
				.map(element -> element != null ? element.createUiTemplateElement() : null)
				.collect(Collectors.toList());
		DtoFloatingElement uiFloatingElement = new DtoFloatingElement(propertyName, row, column, uiElements);
		mapAbstractGridTemplateElementAttributesToUiElement(uiFloatingElement);
		uiFloatingElement.setWrap(wrap);
		uiFloatingElement.setAlignItems(alignItems != null ? AlignItems.valueOf(alignItems.name()) : null);
		uiFloatingElement.setJustifyContent(justifyContent != null ? JustifyContent.valueOf(justifyContent.name()) : null);
		return uiFloatingElement;
	}

	public FloatingElement addElement(AbstractGridTemplateElement<?> element) {
		elements.add(element);
		return this;
	}

	public FloatingElement setElements(final List<AbstractGridTemplateElement<?>> elements) {
		this.elements = elements;
		return this;
	}

	public List<AbstractGridTemplateElement<?>> getElements() {
		return elements;
	}

	public boolean isWrap() {
		return wrap;
	}

	public FloatingElement setWrap(boolean wrap) {
		this.wrap = wrap;
		return this;
	}

	public AlignItems getAlignItems() {
		return alignItems;
	}

	public FloatingElement setAlignItems(AlignItems alignItems) {
		this.alignItems = alignItems;
		return this;
	}

	public JustifyContent getJustifyContent() {
		return justifyContent;
	}

	public FloatingElement setJustifyContent(JustifyContent justifyContent) {
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
