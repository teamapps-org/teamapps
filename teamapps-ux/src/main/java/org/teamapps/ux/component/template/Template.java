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
package org.teamapps.ux.component.template;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiTemplate;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.ux.component.format.FontStyle;
import org.teamapps.ux.component.template.gridtemplate.AbstractTemplateElement;
import org.teamapps.ux.component.template.gridtemplate.FloatingElement;
import org.teamapps.ux.component.template.gridtemplate.GridTemplate;
import org.teamapps.ux.component.template.gridtemplate.TextElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Template {

	UiTemplate createUiTemplate();

	List<String> getPropertyNames();


	// === static methods ===

	static Map<String, UiTemplate> createUiTemplates(Map<String, ? extends Template> templates) {
		return templates.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().createUiTemplate()));
	}

	default Template createDarkThemeTemplate() {
		if (this instanceof BaseTemplate) {
			BaseTemplate baseTemplate = (BaseTemplate) this;
			return baseTemplate.getTemplate().createDarkThemeTemplate();
		}
		if (this instanceof GridTemplate) {
			GridTemplate orig = (GridTemplate) this;
			GridTemplate tpl = new GridTemplate(orig.getMinWidth(), orig.getMaxWidth(), orig.getMinHeight(), orig.getMaxHeight(), orig.getPadding(), orig.getGridGap());
			tpl.setRows(orig.getRows());
			tpl.setColumns(orig.getColumns());
			List<AbstractTemplateElement<?>> darkModeElements = new ArrayList<>();
			orig.getElements().forEach(element -> {
				if (element instanceof FloatingElement) {
					FloatingElement floatingElement = (FloatingElement) element;
					List<AbstractTemplateElement<?>> elements = floatingElement.getElements();
					List<AbstractTemplateElement<?>> newElements = new ArrayList<>();
					for (AbstractTemplateElement<?> fltElement : elements) {
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
		return this;
	}

	static AbstractTemplateElement<?> convertElementToDarkMode(AbstractTemplateElement<?> element) {
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
