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

import org.teamapps.common.format.Color;
import org.teamapps.event.Event;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.field.FieldMessage;
import org.teamapps.ux.component.field.validator.MultiFieldValidator;
import org.teamapps.ux.component.form.AbstractForm;
import org.teamapps.ux.component.form.layoutpolicy.FormLayoutPolicy;
import org.teamapps.ux.component.form.layoutpolicy.FormSection;
import org.teamapps.ux.component.form.layoutpolicy.FormSectionFieldPlacement;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.SizeType;
import org.teamapps.ux.component.format.SizingPolicy;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.component.format.VerticalElementAlignment;
import org.teamapps.ux.component.grid.layout.GridColumn;
import org.teamapps.ux.component.grid.layout.GridRow;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A runtime-mutable responsive form backed by the existing {@code UiGridForm} protocol.
 * New fields are registered in place. Removed fields are disabled, hidden and omitted
 * from subsequent policies; the client retains those tombstones until the session ends.
 */
public class DynamicForm<RECORD> extends AbstractForm<RECORD> {

	public final Event<DynamicFormStructureChange> onStructureChanged = new Event<>();

	private final List<DynamicFormPart> parts = new ArrayList<>();
	private final List<DynamicFormLayout> layouts = new ArrayList<>();
	private final Map<String, AbstractField<?>> activeFields = new LinkedHashMap<>();
	private final List<MultiFieldValidator> activeMultiFieldValidators = new ArrayList<>();
	private int labelWidth;
	private int labelFieldGap;
	private int fieldGroupGap;
	private int columnGutter;
	private int sectionInset;
	private int sectionVerticalMargin;
	private Color sectionBackgroundColor;
	private BiConsumer<DynamicFormLayout, FormSection> sectionCustomizer;
	private BiConsumer<DynamicFormLayout, FormLayoutPolicy> policyCustomizer;
	private int retainedFields;
	private int retainedComponents;
	private int updateDepth;
	private boolean layoutUpdatePending;
	private boolean readOnly;

	public DynamicForm() {
		this(new Builder<>());
	}

	private DynamicForm(Builder<RECORD> builder) {
		layouts.addAll(normalizeLayouts(builder.layouts));
		labelWidth = builder.labelWidth;
		labelFieldGap = builder.labelFieldGap;
		fieldGroupGap = builder.fieldGroupGap;
		columnGutter = builder.columnGutter;
		sectionInset = builder.sectionInset;
		sectionVerticalMargin = builder.sectionVerticalMargin;
		sectionBackgroundColor = builder.sectionBackgroundColor;
		sectionCustomizer = builder.sectionCustomizer;
		policyCustomizer = builder.policyCustomizer;
		setCssStyle(null, "width", "100%");
		setCssStyle(null, "min-width", "0");
	}

	public static <RECORD> Builder<RECORD> builder() {
		return new Builder<>();
	}

	public DynamicFormSection addSection(String id, String title) {
		ensureUniquePartId(id);
		DynamicFormSection section = new DynamicFormSection(this, id, title);
		parts.add(section);
		structureChanged(DynamicFormStructureChange.Type.SECTION_ADDED, id);
		return section;
	}

	public DynamicFormRepeater addRepeater(String id, String title) {
		ensureUniquePartId(id);
		DynamicFormRepeater repeater = new DynamicFormRepeater(this, id, title);
		parts.add(repeater);
		structureChanged(DynamicFormStructureChange.Type.REPEATER_ADDED, id);
		return repeater;
	}

	public List<DynamicFormLayout> getLayouts() {
		return effectiveLayouts();
	}

	public DynamicForm<RECORD> setLayouts(List<DynamicFormLayout> layouts) {
		List<DynamicFormLayout> normalized = normalizeLayouts(layouts);
		this.layouts.clear();
		this.layouts.addAll(normalized);
		structureChanged(DynamicFormStructureChange.Type.LAYOUT_CHANGED, "layouts");
		return this;
	}

	public DynamicForm<RECORD> putLayout(DynamicFormLayout layout) {
		Objects.requireNonNull(layout, "layout");
		layouts.removeIf(existing -> existing.getMinWidth() == layout.getMinWidth());
		layouts.add(layout);
		layouts.sort(Comparator.comparingInt(DynamicFormLayout::getMinWidth));
		structureChanged(DynamicFormStructureChange.Type.LAYOUT_CHANGED, String.valueOf(layout.getMinWidth()));
		return this;
	}

	public DynamicForm<RECORD> removeLayout(int minWidth) {
		if (minWidth == 0) throw new IllegalArgumentException("The layout at minWidth 0 cannot be removed");
		if (layouts.size() == 1) throw new IllegalStateException("A DynamicForm needs at least one layout");
		boolean removed = layouts.removeIf(layout -> layout.getMinWidth() == minWidth);
		if (removed) structureChanged(DynamicFormStructureChange.Type.LAYOUT_CHANGED, String.valueOf(minWidth));
		return this;
	}

	/** Coalesces several structural mutations into one policy update. */
	public DynamicForm<RECORD> update(Runnable mutations) {
		Objects.requireNonNull(mutations, "mutations");
		updateDepth++;
		try {
			mutations.run();
		} finally {
			updateDepth--;
			if (updateDepth == 0) flushLayoutUpdate();
		}
		return this;
	}

	public int getRetainedFieldCount() {
		return retainedFields;
	}

	public int getRetainedComponentCount() {
		return retainedComponents;
	}

	public DynamicForm<RECORD> setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		FieldEditingMode mode = readOnly ? FieldEditingMode.READONLY : FieldEditingMode.EDITABLE;
		activeFields.values().forEach(field -> field.setEditingMode(mode));
		return this;
	}

	@Override
	public List<FormLayoutPolicy> getLayoutPolicies() {
		List<FormLayoutPolicy> result = new ArrayList<>();
		for (DynamicFormLayout layout : effectiveLayouts()) {
			FormLayoutPolicy policy = new FormLayoutPolicy().setMinWidth(layout.getMinWidth());
			parts.forEach(part -> part.appendTo(policy, layout));
			policyCustomizer.accept(layout, policy);
			result.add(policy);
		}
		return result;
	}

	@Override
	public List<AbstractField<?>> getFields() {
		return new ArrayList<>(activeFields.values());
	}

	@Override
	public Map<String, AbstractField<?>> getFieldsMap() {
		return Map.copyOf(activeFields);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> AbstractField<V> getFieldByPropertyName(String propertyName) {
		return (AbstractField<V>) activeFields.get(propertyName);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void applyRecordValuesToFields(RECORD record) {
		Map<String, Object> values = getPropertyProvider().getValues(record, activeFields.keySet());
		values.forEach((propertyName, value) -> {
			AbstractField field = activeFields.get(propertyName);
			if (field != null) field.setValue(value);
		});
	}

	@Override
	public void applyFieldValuesToRecord(RECORD record) {
		Map<String, Object> values = new HashMap<>();
		activeFields.forEach((propertyName, field) -> values.put(propertyName, field.getValue()));
		getPropertyInjector().setValues(record, values);
	}

	@Override
	public void addMultiFieldValidator(MultiFieldValidator multiFieldValidator) {
		activeMultiFieldValidators.add(Objects.requireNonNull(multiFieldValidator, "multiFieldValidator"));
	}

	@Override
	public FieldMessage.Severity validate() {
		return java.util.stream.Stream.concat(
				activeFields.values().stream().flatMap(field -> field.validate().stream()),
				activeMultiFieldValidators.stream().flatMap(validator -> validator.validate().stream()))
				.map(FieldMessage::getSeverity)
				.max(Comparator.comparing(Enum::ordinal))
				.orElse(null);
	}

	void registerField(DynamicFormField field) {
		AbstractField<?> existing = activeFields.get(field.getPropertyName());
		if (existing != null) {
			throw new IllegalArgumentException("Duplicate active property name: " + field.getPropertyName());
		}
		activeFields.put(field.getPropertyName(), field.getField());
		addField(field.getPropertyName(), field.getField());
		addComponent(field.getLabel());
		if (readOnly) field.getField().setEditingMode(FieldEditingMode.READONLY);
	}

	void registerComponent(DynamicFormComponent component) {
		addComponent(component.getComponent());
	}

	void registerAuxiliaryComponent(Component component) {
		addComponent(component);
	}

	void retireAuxiliaryComponent(Component component) {
		component.setVisible(false);
		retainedComponents++;
	}

	void retireElement(DynamicFormElement<?> element) {
		if (!element.isActive()) return;
		element.markRetired();
		if (element instanceof DynamicFormField field) {
			activeFields.remove(field.getPropertyName(), field.getField());
			field.getField().setEditingMode(FieldEditingMode.DISABLED);
			field.getField().setVisible(false);
			field.getLabel().setVisible(false);
			retainedFields++;
			retainedComponents++;
		} else {
			element.getComponent().setVisible(false);
			retainedComponents++;
		}
	}

	boolean removePart(DynamicFormPart part, DynamicFormStructureChange.Type type) {
		if (!parts.contains(part)) return false;
		update(() -> {
			parts.remove(part);
			part.retire();
			structureChanged(type, part.getId());
		});
		return true;
	}

	void structureChanged(DynamicFormStructureChange.Type type, String elementId) {
		layoutUpdatePending = true;
		if (updateDepth == 0) flushLayoutUpdate();
		onStructureChanged.fire(new DynamicFormStructureChange(type, elementId,
				activeFields.size(), retainedFields, retainedComponents));
	}

	void layoutChanged() {
		structureChanged(DynamicFormStructureChange.Type.LAYOUT_CHANGED, "layout");
	}

	FormSection createElementSection(String id, String title, List<? extends DynamicFormElement<?>> source,
									 DynamicFormLayout layout, DynamicFormSectionOptions options) {
		FormSection section = createBaseSection(id, title, layout, options);
		List<DynamicFormElement<?>> elements = new ArrayList<>();
		for (DynamicFormElement<?> element : source) {
			if (element.isActive()) elements.add(element);
		}
		for (int column = 0; column < layout.getFieldColumns(); column++) {
			if (layout.getLabelPosition() == DynamicFormLayout.LabelPosition.BESIDE) {
				section.addColumn(column(SizingPolicy.fixed(labelWidth + columnGutter)));
			}
			section.addColumn(column(null));
		}

		List<PackedElement> packed = pack(elements, layout);
		int logicalRows = packed.stream().mapToInt(PackedElement::row).max().orElse(0) + 1;
		if (layout.getLabelPosition() == DynamicFormLayout.LabelPosition.ABOVE) {
			for (int row = 0; row < logicalRows; row++) {
				section.addRow(new GridRow(SizingPolicy.AUTO, row == 0 ? 0 : fieldGroupGap, labelFieldGap));
				section.addRow(new GridRow(SizingPolicy.AUTO, 0, 0));
			}
		} else {
			for (int row = 0; row < logicalRows; row++) {
				section.addRow(new GridRow(SizingPolicy.AUTO, row == 0 ? 0 : fieldGroupGap, 0));
			}
		}

		for (PackedElement packedElement : packed) {
			DynamicFormElement<?> element = packedElement.element();
			if (element instanceof DynamicFormField field) {
				placeField(section, layout, packedElement, field);
			} else {
				placeComponent(section, layout, packedElement, element);
			}
		}
		finishSection(layout, section, options);
		return section;
	}

	FormSection createBaseSection(String id, String title, DynamicFormLayout layout,
								  DynamicFormSectionOptions options) {
		int horizontalPadding = Math.max(0, sectionInset - columnGutter / 2);
		FormSection section = new FormSection(id)
				.setPadding(new Spacing(sectionInset, horizontalPadding, sectionInset, horizontalPadding))
				.setMargin(new Spacing(sectionVerticalMargin, 0, sectionVerticalMargin, 0))
				.setGridGap(0)
				.setBackgroundColor(sectionBackgroundColor)
				.setDebuggingId(id + "-" + layout.getMinWidth());
		if (title != null && !title.isBlank()) {
			section.setHeaderTemplate(BaseTemplate.FORM_SECTION_HEADER)
					.setHeaderData(new BaseTemplateRecord<>(title));
		}
		options.applyProperties(section);
		return section;
	}

	void finishSection(DynamicFormLayout layout, FormSection section, DynamicFormSectionOptions options) {
		sectionCustomizer.accept(layout, section);
		options.customize(layout, section);
	}

	GridColumn column(SizingPolicy width) {
		SizingPolicy resolved = width != null ? width : new SizingPolicy(SizeType.FRACTION, 1, 1);
		return new GridColumn(resolved, columnGutter / 2, columnGutter / 2);
	}

	FormSectionFieldPlacement placement(Component component, int row, int column) {
		return new FormSectionFieldPlacement(component, row, column)
				.setMinWidth(1)
				.setHorizontalAlignment(HorizontalElementAlignment.STRETCH)
				.setVerticalAlignment(VerticalElementAlignment.CENTER);
	}

	int getLabelFieldGap() {
		return labelFieldGap;
	}

	int getFieldGroupGap() {
		return fieldGroupGap;
	}

	private void placeField(FormSection section, DynamicFormLayout layout, PackedElement packed,
							DynamicFormField field) {
		if (layout.getLabelPosition() == DynamicFormLayout.LabelPosition.ABOVE) {
			int row = packed.row() * 2;
			FormSectionFieldPlacement labelPlacement = placement(field.getLabel(), row, packed.column())
					.setColSpan(packed.span())
					.setVerticalAlignment(VerticalElementAlignment.BOTTOM);
			field.configureLabelPlacement(layout, labelPlacement);
			section.addPlacement(labelPlacement);
			FormSectionFieldPlacement fieldPlacement = placement(field.getField(), row + 1, packed.column())
					.setColSpan(packed.span())
					.setVerticalAlignment(VerticalElementAlignment.TOP);
			field.configurePlacement(layout, fieldPlacement);
			section.addPlacement(fieldPlacement);
		} else {
			int column = packed.column() * 2;
			FormSectionFieldPlacement labelPlacement = placement(field.getLabel(), packed.row(), column)
					.setVerticalAlignment(VerticalElementAlignment.CENTER);
			field.configureLabelPlacement(layout, labelPlacement);
			section.addPlacement(labelPlacement);
			FormSectionFieldPlacement fieldPlacement = placement(field.getField(), packed.row(), column + 1)
					.setColSpan(packed.span() * 2 - 1);
			field.configurePlacement(layout, fieldPlacement);
			section.addPlacement(fieldPlacement);
		}
	}

	private void placeComponent(FormSection section, DynamicFormLayout layout, PackedElement packed,
								DynamicFormElement<?> element) {
		int row = layout.getLabelPosition() == DynamicFormLayout.LabelPosition.ABOVE ? packed.row() * 2 : packed.row();
		int column = layout.getLabelPosition() == DynamicFormLayout.LabelPosition.ABOVE ? packed.column() : packed.column() * 2;
		int columnSpan = layout.getLabelPosition() == DynamicFormLayout.LabelPosition.ABOVE ? packed.span() : packed.span() * 2;
		FormSectionFieldPlacement placement = placement(element.getComponent(), row, column).setColSpan(columnSpan);
		if (layout.getLabelPosition() == DynamicFormLayout.LabelPosition.ABOVE) placement.setRowSpan(2);
		element.configurePlacement(layout, placement);
		section.addPlacement(placement);
	}

	private List<PackedElement> pack(List<DynamicFormElement<?>> elements, DynamicFormLayout layout) {
		List<PackedElement> result = new ArrayList<>();
		int row = 0;
		int column = 0;
		for (DynamicFormElement<?> element : elements) {
			int span = element.resolveColumnSpan(layout);
			if (column + span > layout.getFieldColumns()) {
				row++;
				column = 0;
			}
			result.add(new PackedElement(element, row, column, span));
			column += span;
			if (column == layout.getFieldColumns()) {
				row++;
				column = 0;
			}
		}
		return result;
	}

	private void ensureUniquePartId(String id) {
		if (id == null || id.isBlank()) throw new IllegalArgumentException("Part id must not be blank");
		if (parts.stream().anyMatch(part -> part.getId().equals(id))) {
			throw new IllegalArgumentException("Duplicate form part id: " + id);
		}
	}

	private void flushLayoutUpdate() {
		if (!layoutUpdatePending) return;
		layoutUpdatePending = false;
		updateLayoutPolicies();
	}

	private static List<DynamicFormLayout> normalizeLayouts(List<DynamicFormLayout> layouts) {
		if (layouts == null || layouts.isEmpty()) throw new IllegalArgumentException("At least one layout is required");
		Map<Integer, DynamicFormLayout> byWidth = new LinkedHashMap<>();
		for (DynamicFormLayout layout : layouts) {
			DynamicFormLayout value = Objects.requireNonNull(layout, "layout");
			if (byWidth.put(value.getMinWidth(), value) != null) {
				throw new IllegalArgumentException("Duplicate layout minWidth: " + value.getMinWidth());
			}
		}
		if (!byWidth.containsKey(0)) {
			throw new IllegalArgumentException("A DynamicForm layout set must contain minWidth 0");
		}
		return byWidth.values().stream().sorted(Comparator.comparingInt(DynamicFormLayout::getMinWidth)).toList();
	}

	private List<DynamicFormLayout> effectiveLayouts() {
		Map<Integer, DynamicFormLayout> byWidth = new LinkedHashMap<>();
		layouts.forEach(layout -> byWidth.put(layout.getMinWidth(), layout));
		parts.stream()
				.filter(DynamicFormRepeater.class::isInstance)
				.map(DynamicFormRepeater.class::cast)
				.mapToInt(DynamicFormRepeater::getTableMinWidth)
				.filter(minWidth -> minWidth != Integer.MAX_VALUE && !byWidth.containsKey(minWidth))
				.forEach(minWidth -> {
					DynamicFormLayout inherited = layouts.stream()
							.filter(layout -> layout.getMinWidth() <= minWidth)
							.max(Comparator.comparingInt(DynamicFormLayout::getMinWidth))
							.orElseThrow();
					byWidth.put(minWidth, new DynamicFormLayout(minWidth,
							inherited.getFieldColumns(), inherited.getLabelPosition()));
				});
		return byWidth.values().stream().sorted(Comparator.comparingInt(DynamicFormLayout::getMinWidth)).toList();
	}

	private record PackedElement(DynamicFormElement<?> element, int row, int column, int span) {}

	public static final class Builder<RECORD> {
		private List<DynamicFormLayout> layouts = DynamicFormLayouts.hybrid();
		private int labelWidth = 128;
		private int labelFieldGap = 4;
		private int fieldGroupGap = 12;
		private int columnGutter = 12;
		private int sectionInset = 16;
		private int sectionVerticalMargin = 8;
		private Color sectionBackgroundColor = Color.WHITE;
		private BiConsumer<DynamicFormLayout, FormSection> sectionCustomizer = (layout, section) -> {};
		private BiConsumer<DynamicFormLayout, FormLayoutPolicy> policyCustomizer = (layout, policy) -> {};
		private final List<Consumer<DynamicForm<RECORD>>> contentBuilders = new ArrayList<>();

		public Builder<RECORD> layouts(List<DynamicFormLayout> layouts) {
			this.layouts = normalizeLayouts(layouts);
			return this;
		}

		public Builder<RECORD> section(String id, String title, Consumer<DynamicFormSection> content) {
			Objects.requireNonNull(content, "content");
			contentBuilders.add(form -> content.accept(form.addSection(id, title)));
			return this;
		}

		public Builder<RECORD> repeater(String id, String title, Consumer<DynamicFormRepeater> content) {
			Objects.requireNonNull(content, "content");
			contentBuilders.add(form -> content.accept(form.addRepeater(id, title)));
			return this;
		}

		public Builder<RECORD> labelWidth(int labelWidth) {
			this.labelWidth = positive("labelWidth", labelWidth);
			return this;
		}

		public Builder<RECORD> labelFieldGap(int labelFieldGap) {
			this.labelFieldGap = nonNegative("labelFieldGap", labelFieldGap);
			return this;
		}

		public Builder<RECORD> fieldGroupGap(int fieldGroupGap) {
			this.fieldGroupGap = nonNegative("fieldGroupGap", fieldGroupGap);
			return this;
		}

		public Builder<RECORD> columnGutter(int columnGutter) {
			this.columnGutter = nonNegative("columnGutter", columnGutter);
			return this;
		}

		public Builder<RECORD> sectionInset(int sectionInset) {
			this.sectionInset = nonNegative("sectionInset", sectionInset);
			return this;
		}

		public Builder<RECORD> sectionVerticalMargin(int sectionVerticalMargin) {
			this.sectionVerticalMargin = nonNegative("sectionVerticalMargin", sectionVerticalMargin);
			return this;
		}

		public Builder<RECORD> sectionBackgroundColor(Color color) {
			this.sectionBackgroundColor = color;
			return this;
		}

		/** Runs after standard section generation and before a section-specific customizer. */
		public Builder<RECORD> configureSections(BiConsumer<DynamicFormLayout, FormSection> customizer) {
			this.sectionCustomizer = this.sectionCustomizer.andThen(Objects.requireNonNull(customizer, "customizer"));
			return this;
		}

		/** Runs last for each generated policy and exposes all raw sections and placements. */
		public Builder<RECORD> configurePolicies(BiConsumer<DynamicFormLayout, FormLayoutPolicy> customizer) {
			this.policyCustomizer = this.policyCustomizer.andThen(Objects.requireNonNull(customizer, "customizer"));
			return this;
		}

		public DynamicForm<RECORD> build() {
			DynamicForm<RECORD> form = new DynamicForm<>(this);
			form.update(() -> contentBuilders.forEach(builder -> builder.accept(form)));
			return form;
		}

		private static int positive(String name, int value) {
			if (value < 1) throw new IllegalArgumentException(name + " must be >= 1");
			return value;
		}

		private static int nonNegative(String name, int value) {
			if (value < 0) throw new IllegalArgumentException(name + " must be >= 0");
			return value;
		}
	}
}
