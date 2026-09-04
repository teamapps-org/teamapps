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
import org.teamapps.ux.component.form.layoutpolicy.FormSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/** One runtime-mutable row of a {@link DynamicFormRepeater}. */
public final class DynamicFormRow implements DynamicFormElementContainer {

	private final DynamicFormRepeater repeater;
	private final DynamicForm<?> form;
	private final long id;
	private final List<DynamicFormField> fields = new ArrayList<>();
	private final DynamicFormSectionOptions cardOptions = new DynamicFormSectionOptions();
	private String title;
	private DynamicFormComponent action;
	private boolean active = true;

	DynamicFormRow(DynamicFormRepeater repeater, long id, String title) {
		this.repeater = Objects.requireNonNull(repeater, "repeater");
		this.form = repeater.getForm();
		this.id = id;
		this.title = title;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public DynamicFormRow title(String title) {
		this.title = title;
		form.layoutChanged();
		return this;
	}

	/** Uses the corresponding repeater column caption. */
	public DynamicFormField addField(String propertyName, AbstractField<?> field) {
		return addField(propertyName, repeater.getCaption(fields.size()), field);
	}

	public DynamicFormField addField(String propertyName, String caption, AbstractField<?> field) {
		return addField(propertyName, new Label(caption), field);
	}

	public DynamicFormField addField(String propertyName, Label label, AbstractField<?> field) {
		ensureActive();
		if (fields.size() >= repeater.getColumns().size()) {
			throw new IllegalStateException("Add a repeater column before field " + (fields.size() + 1));
		}
		DynamicFormField result = new DynamicFormField(form, this, propertyName, label, field);
		form.registerField(result);
		fields.add(result);
		form.structureChanged(DynamicFormStructureChange.Type.FIELD_ADDED, propertyName);
		return result;
	}

	public DynamicFormComponent action(Component component) {
		ensureActive();
		Objects.requireNonNull(component, "component");
		form.update(() -> {
			if (action != null) removeElement(action);
			action = new DynamicFormComponent(form, this, component);
			action.fullWidth().horizontalAlignment(org.teamapps.ux.component.format.HorizontalElementAlignment.LEFT);
			form.registerComponent(action);
			form.structureChanged(DynamicFormStructureChange.Type.COMPONENT_ADDED,
					repeater.getId() + ".row." + id + ".action");
		});
		return action;
	}

	public List<DynamicFormField> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public DynamicFormComponent getAction() {
		return action;
	}

	public Map<String, Object> getValues() {
		Map<String, Object> result = new LinkedHashMap<>();
		fields.forEach(field -> result.put(field.getPropertyName(), field.getField().getValue()));
		return result;
	}

	public DynamicFormRow collapsible(boolean collapsible) {
		cardOptions.setCollapsible(collapsible);
		form.layoutChanged();
		return this;
	}

	public DynamicFormRow collapsed(boolean collapsed) {
		cardOptions.setCollapsed(collapsed);
		form.layoutChanged();
		return this;
	}

	public DynamicFormRow configureCard(BiConsumer<DynamicFormLayout, FormSection> customizer) {
		cardOptions.addCustomizer(customizer);
		form.layoutChanged();
		return this;
	}

	public boolean remove() {
		return active && repeater.removeRow(this);
	}

	@Override
	public boolean removeElement(DynamicFormElement<?> element) {
		if (element == action) {
			action = null;
		} else if (!fields.remove(element)) {
			return false;
		}
		form.retireElement(element);
		form.structureChanged(element instanceof DynamicFormField
				? DynamicFormStructureChange.Type.FIELD_REMOVED
				: DynamicFormStructureChange.Type.COMPONENT_REMOVED,
				element instanceof DynamicFormField field ? field.getPropertyName() : repeater.getId());
		return true;
	}

	void retire() {
		active = false;
		new ArrayList<>(fields).forEach(this::removeElement);
		if (action != null) removeElement(action);
	}

	List<DynamicFormElement<?>> getCardElements() {
		List<DynamicFormElement<?>> result = new ArrayList<>(fields);
		if (action != null) result.add(action);
		return result;
	}

	DynamicFormSectionOptions getCardOptions() {
		return cardOptions;
	}

	private void ensureActive() {
		if (!active) throw new IllegalStateException("Row has been removed: " + id);
	}
}
