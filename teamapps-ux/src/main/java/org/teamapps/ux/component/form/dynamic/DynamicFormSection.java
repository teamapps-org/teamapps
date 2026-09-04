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

import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.Label;
import org.teamapps.ux.component.form.layoutpolicy.FormLayoutPolicy;
import org.teamapps.ux.component.form.layoutpolicy.FormSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/** A responsive section whose fields and components may be changed at runtime. */
public final class DynamicFormSection implements DynamicFormPart, DynamicFormElementContainer {

	private final DynamicForm<?> form;
	private final String id;
	private final List<DynamicFormElement<?>> elements = new ArrayList<>();
	private final DynamicFormSectionOptions options = new DynamicFormSectionOptions();
	private String title;
	private boolean active = true;

	DynamicFormSection(DynamicForm<?> form, String id, String title) {
		this.form = Objects.requireNonNull(form, "form");
		this.id = requireId(id);
		this.title = title;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public DynamicFormSection title(String title) {
		this.title = title;
		form.layoutChanged();
		return this;
	}

	public DynamicFormField addField(String propertyName, String caption, AbstractField<?> field) {
		return addField(propertyName, new Label(caption), field);
	}

	public DynamicFormField addField(String propertyName, Label label, AbstractField<?> field) {
		ensureActive();
		DynamicFormField result = new DynamicFormField(form, this, propertyName, label, field);
		form.registerField(result);
		elements.add(result);
		form.structureChanged(DynamicFormStructureChange.Type.FIELD_ADDED, propertyName);
		return result;
	}

	public DynamicFormComponent addComponent(Component component) {
		ensureActive();
		DynamicFormComponent result = new DynamicFormComponent(form, this, Objects.requireNonNull(component, "component"));
		elements.add(result);
		form.registerComponent(result);
		form.structureChanged(DynamicFormStructureChange.Type.COMPONENT_ADDED, id);
		return result;
	}

	public List<DynamicFormElement<?>> getElements() {
		return Collections.unmodifiableList(elements);
	}

	public DynamicFormSection collapsible(boolean collapsible) {
		options.setCollapsible(collapsible);
		form.layoutChanged();
		return this;
	}

	public DynamicFormSection collapsed(boolean collapsed) {
		options.setCollapsed(collapsed);
		form.layoutChanged();
		return this;
	}

	public DynamicFormSection visible(boolean visible) {
		options.setVisible(visible);
		form.layoutChanged();
		return this;
	}

	public DynamicFormSection drawHeaderLine(boolean drawHeaderLine) {
		options.setDrawHeaderLine(drawHeaderLine);
		form.layoutChanged();
		return this;

	}

	public DynamicFormSection fillRemainingHeight(boolean fillRemainingHeight) {
		options.setFillRemainingHeight(fillRemainingHeight);
		form.layoutChanged();
		return this;
	}

	public DynamicFormSection hideWhenNoVisibleFields(boolean hideWhenNoVisibleFields) {
		options.setHideWhenNoVisibleFields(hideWhenNoVisibleFields);
		form.layoutChanged();
		return this;
	}

	/** Runs after framework defaults and can customize the raw section per layout. */
	public DynamicFormSection configure(BiConsumer<DynamicFormLayout, FormSection> customizer) {
		options.addCustomizer(customizer);
		form.layoutChanged();
		return this;
	}

	public boolean remove() {
		return active && form.removePart(this, DynamicFormStructureChange.Type.SECTION_REMOVED);
	}

	@Override
	public boolean removeElement(DynamicFormElement<?> element) {
		if (!elements.remove(element)) return false;
		form.retireElement(element);
		form.structureChanged(element instanceof DynamicFormField
				? DynamicFormStructureChange.Type.FIELD_REMOVED
				: DynamicFormStructureChange.Type.COMPONENT_REMOVED,
				element instanceof DynamicFormField field ? field.getPropertyName() : id);
		return true;
	}

	@Override
	public void appendTo(FormLayoutPolicy policy, DynamicFormLayout layout) {
		policy.addSection(form.createElementSection(id, title, elements, layout, options));
	}

	@Override
	public void retire() {
		active = false;
		new ArrayList<>(elements).forEach(this::removeElement);
	}

	private void ensureActive() {
		if (!active) throw new IllegalStateException("Section has been removed: " + id);
	}

	private static String requireId(String id) {
		if (id == null || id.isBlank()) throw new IllegalArgumentException("Section id must not be blank");
		return id;
	}
}
