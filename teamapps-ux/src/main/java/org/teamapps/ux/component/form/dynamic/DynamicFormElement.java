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
import org.teamapps.ux.component.form.layoutpolicy.FormSectionFieldPlacement;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.VerticalElementAlignment;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/** Shared fluent placement options for fields and arbitrary components. */
public abstract class DynamicFormElement<SELF extends DynamicFormElement<SELF>> {

	static final int FULL_WIDTH = Integer.MAX_VALUE;

	final DynamicForm<?> form;
	private final DynamicFormElementContainer container;
	private final NavigableMap<Integer, Integer> columnSpans = new TreeMap<>();
	private int defaultColumnSpan = 1;
	private int minWidth = 1;
	private int maxWidth;
	private int minHeight;
	private int maxHeight;
	private HorizontalElementAlignment horizontalAlignment = HorizontalElementAlignment.STRETCH;
	private VerticalElementAlignment verticalAlignment;
	private BiConsumer<DynamicFormLayout, FormSectionFieldPlacement> placementCustomizer = (layout, placement) -> {};
	private boolean active = true;

	DynamicFormElement(DynamicForm<?> form, DynamicFormElementContainer container) {
		this.form = Objects.requireNonNull(form, "form");
		this.container = Objects.requireNonNull(container, "container");
	}

	public abstract Component getComponent();

	public boolean isActive() {
		return active;
	}

	public int getColumnSpan() {
		return defaultColumnSpan;
	}

	public SELF columnSpan(int columnSpan) {
		this.defaultColumnSpan = requireSpan(columnSpan);
		form.layoutChanged();
		return self();
	}

	public SELF fullWidth() {
		this.defaultColumnSpan = FULL_WIDTH;
		form.layoutChanged();
		return self();
	}

	/** Applies from this min-width until a more specific span is configured. */
	public SELF columnSpanAt(int minWidth, int columnSpan) {
		if (minWidth < 0) throw new IllegalArgumentException("minWidth must be >= 0");
		columnSpans.put(minWidth, requireSpan(columnSpan));
		form.layoutChanged();
		return self();
	}

	public SELF columnSpanAt(DynamicFormLayouts.Breakpoint breakpoint, int columnSpan) {
		return columnSpanAt(Objects.requireNonNull(breakpoint, "breakpoint").getMinWidth(), columnSpan);
	}

	public SELF fullWidthAt(int minWidth) {
		if (minWidth < 0) throw new IllegalArgumentException("minWidth must be >= 0");
		columnSpans.put(minWidth, FULL_WIDTH);
		form.layoutChanged();
		return self();
	}

	public SELF fullWidthAt(DynamicFormLayouts.Breakpoint breakpoint) {
		return fullWidthAt(Objects.requireNonNull(breakpoint, "breakpoint").getMinWidth());
	}

	public SELF minWidth(int minWidth) {
		this.minWidth = requireNonNegative("minWidth", minWidth);
		form.layoutChanged();
		return self();
	}

	public SELF maxWidth(int maxWidth) {
		this.maxWidth = requireNonNegative("maxWidth", maxWidth);
		form.layoutChanged();
		return self();
	}

	public SELF minHeight(int minHeight) {
		this.minHeight = requireNonNegative("minHeight", minHeight);
		form.layoutChanged();
		return self();
	}

	public SELF maxHeight(int maxHeight) {
		this.maxHeight = requireNonNegative("maxHeight", maxHeight);
		form.layoutChanged();
		return self();
	}

	public SELF horizontalAlignment(HorizontalElementAlignment horizontalAlignment) {
		this.horizontalAlignment = Objects.requireNonNull(horizontalAlignment, "horizontalAlignment");
		form.layoutChanged();
		return self();
	}

	public SELF verticalAlignment(VerticalElementAlignment verticalAlignment) {
		this.verticalAlignment = Objects.requireNonNull(verticalAlignment, "verticalAlignment");
		form.layoutChanged();
		return self();
	}

	/** Runs last and can alter every raw placement property for a particular layout. */
	public SELF configurePlacement(BiConsumer<DynamicFormLayout, FormSectionFieldPlacement> customizer) {
		BiConsumer<DynamicFormLayout, FormSectionFieldPlacement> next = Objects.requireNonNull(customizer, "customizer");
		this.placementCustomizer = this.placementCustomizer.andThen(next);
		form.layoutChanged();
		return self();
	}

	public boolean remove() {
		return active && container.removeElement(this);
	}

	int resolveColumnSpan(DynamicFormLayout layout) {
		var entry = columnSpans.floorEntry(layout.getMinWidth());
		int span = entry != null ? entry.getValue() : defaultColumnSpan;
		return span == FULL_WIDTH ? layout.getFieldColumns() : Math.min(span, layout.getFieldColumns());
	}

	void configurePlacement(DynamicFormLayout layout, FormSectionFieldPlacement placement) {
		placement.setMinWidth(minWidth)
				.setMaxWidth(maxWidth)
				.setMinHeight(minHeight)
				.setMaxHeight(maxHeight)
				.setHorizontalAlignment(horizontalAlignment);
		if (verticalAlignment != null) placement.setVerticalAlignment(verticalAlignment);
		placementCustomizer.accept(layout, placement);
	}

	void markRetired() {
		active = false;
	}

	@SuppressWarnings("unchecked")
	private SELF self() {
		return (SELF) this;
	}

	private static int requireSpan(int value) {
		if (value < 1) throw new IllegalArgumentException("columnSpan must be >= 1");
		return value;
	}

	private static int requireNonNegative(String name, int value) {
		if (value < 0) throw new IllegalArgumentException(name + " must be >= 0");
		return value;
	}
}
