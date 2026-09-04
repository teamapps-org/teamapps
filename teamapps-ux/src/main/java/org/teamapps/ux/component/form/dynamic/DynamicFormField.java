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

import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.Label;
import org.teamapps.ux.component.form.layoutpolicy.FormSectionFieldPlacement;

import java.util.Objects;
import java.util.function.BiConsumer;

/** A registered form field together with its responsive label and placement options. */
public final class DynamicFormField extends DynamicFormElement<DynamicFormField> {

	private final String propertyName;
	private final Label label;
	private final AbstractField<?> field;
	private BiConsumer<DynamicFormLayout, FormSectionFieldPlacement> labelPlacementCustomizer = (layout, placement) -> {};

	DynamicFormField(DynamicForm<?> form, DynamicFormElementContainer container, String propertyName,
					 Label label, AbstractField<?> field) {
		super(form, container);
		this.propertyName = requirePropertyName(propertyName);
		this.label = Objects.requireNonNull(label, "label");
		this.field = Objects.requireNonNull(field, "field");
		label.setTargetComponent(field);
		label.setCssStyle(null, "white-space", "normal");
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Label getLabel() {
		return label;
	}

	public AbstractField<?> getField() {
		return field;
	}

	@Override
	public AbstractField<?> getComponent() {
		return field;
	}

	public DynamicFormField configureLabelPlacement(
			BiConsumer<DynamicFormLayout, FormSectionFieldPlacement> customizer) {
		this.labelPlacementCustomizer = this.labelPlacementCustomizer.andThen(
				Objects.requireNonNull(customizer, "customizer"));
		form.layoutChanged();
		return this;
	}

	void configureLabelPlacement(DynamicFormLayout layout, FormSectionFieldPlacement placement) {
		labelPlacementCustomizer.accept(layout, placement);
	}

	private static String requirePropertyName(String propertyName) {
		if (propertyName == null || propertyName.isBlank()) {
			throw new IllegalArgumentException("propertyName must not be blank");
		}
		return propertyName;
	}
}
