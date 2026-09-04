/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
package org.teamapps.ux.component.form.dynamic;

import org.teamapps.ux.component.form.layoutpolicy.FormSection;

import java.util.Objects;
import java.util.function.BiConsumer;

final class DynamicFormSectionOptions {
	private boolean collapsible = true;
	private boolean collapsed;
	private boolean visible = true;
	private boolean drawHeaderLine = true;
	private boolean fillRemainingHeight;
	private boolean hideWhenNoVisibleFields;
	private BiConsumer<DynamicFormLayout, FormSection> customizer = (layout, section) -> {};

	boolean isCollapsible() {
		return collapsible;
	}

	void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}

	boolean isCollapsed() {
		return collapsed;
	}

	void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	boolean isVisible() {
		return visible;
	}

	void setVisible(boolean visible) {
		this.visible = visible;
	}

	boolean isDrawHeaderLine() {
		return drawHeaderLine;
	}

	void setDrawHeaderLine(boolean drawHeaderLine) {
		this.drawHeaderLine = drawHeaderLine;
	}

	boolean isFillRemainingHeight() {
		return fillRemainingHeight;
	}

	void setFillRemainingHeight(boolean fillRemainingHeight) {
		this.fillRemainingHeight = fillRemainingHeight;
	}

	boolean isHideWhenNoVisibleFields() {
		return hideWhenNoVisibleFields;
	}

	void setHideWhenNoVisibleFields(boolean hideWhenNoVisibleFields) {
		this.hideWhenNoVisibleFields = hideWhenNoVisibleFields;
	}

	void addCustomizer(BiConsumer<DynamicFormLayout, FormSection> customizer) {
		this.customizer = this.customizer.andThen(Objects.requireNonNull(customizer, "customizer"));
	}

	void applyProperties(FormSection section) {
		section.setCollapsible(collapsible)
				.setCollapsed(collapsed)
				.setVisible(visible)
				.setDrawHeaderLine(drawHeaderLine)
				.setFillRemainingHeight(fillRemainingHeight)
				.setHideWhenNoVisibleFields(hideWhenNoVisibleFields);
	}

	void customize(DynamicFormLayout layout, FormSection section) {
		customizer.accept(layout, section);
	}
}
