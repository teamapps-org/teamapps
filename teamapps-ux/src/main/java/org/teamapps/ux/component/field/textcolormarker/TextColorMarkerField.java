/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.ux.component.field.textcolormarker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.FieldEditingMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TextColorMarkerField extends AbstractField<TextColorMarkerFieldValue> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TextColorMarkerField.class);

	static final TextColorMarkerFieldValue EMPTY_VALUE = new TextColorMarkerFieldValue("", List.of());

	private List<TextColorMarkerDefinition> markerDefinitions = List.of();
	private boolean toolbarEnabled = true;
	private TextSelectionData currentSelection;
	private TextSelectionData lastSelection;

	public final Event<TextSelectionData> onTextSelected = new Event<>();

	@Override
	public UiTextColorMarkerField createUiComponent() {
		UiTextColorMarkerField ui = new UiTextColorMarkerField();
		mapAbstractFieldAttributesToUiField(ui);
		ui.setToolbarEnabled(toolbarEnabled);
		ui.setMarkerDefinitions(createUiMarkerDefinitions(markerDefinitions));
		return ui;
	}

	private static List<UiTextColorMarkerFieldMarkerDefinition> createUiMarkerDefinitions(List<TextColorMarkerDefinition> markerDefinitions) {
		return markerDefinitions.stream()
				.map(TextColorMarkerDefinition::toUiTextColorMarkerFieldMarkerDefinition)
				.toList();
	}

	public List<TextColorMarkerDefinition> getMarkerDefinitions() {
		return markerDefinitions;
	}

	/**
	 * @param markerDefinitions list of possible markers for this field
	 * @param value the field text and its markers
	 */
	public void setMarkerDefinitions(List<TextColorMarkerDefinition> markerDefinitions,
									 TextColorMarkerFieldValue value) {
		this.markerDefinitions = markerDefinitions == null ? List.of() : markerDefinitions;
		List<UiTextColorMarkerFieldMarkerDefinition> uiMarkerDefinitions = createUiMarkerDefinitions(this.markerDefinitions);
		UiTextColorMarkerFieldValue uiValue = value != null ? value.toUiTextColorMarkerFieldValue() : null;
		queueCommandIfRendered(() -> new UiTextColorMarkerField.SetMarkerDefinitionsCommand(getId(), uiMarkerDefinitions, uiValue));
		setValue(value);
	}

	/**
	 * @param markerDefinitions list of possible markers for this field
	 */
	public void setMarkerDefinitions(List<TextColorMarkerDefinition> markerDefinitions) {
		setMarkerDefinitions(markerDefinitions, EMPTY_VALUE);
	}

	/**
	 * @param markerDefinitionId reference for the marker definition
	 * @param start text start position of the marker
	 * @param end text end position of the marker
	 * @throws IllegalTextColorMarkerException if no matching definition was found
	 */
	public void setMarker(int markerDefinitionId, int start, int end) {
		TextColorMarker marker = new TextColorMarker(markerDefinitionId, start, end);
		validateMarker(marker);

		TextColorMarkerFieldValue val = new TextColorMarkerFieldValue(getValue().text(), new ArrayList<>(getValue().markers()));
		val.markers().removeIf(m -> m.markerDefinitionId() == markerDefinitionId);
		val.markers().add(marker);
		val.markers().sort(Comparator.comparingInt(TextColorMarker::markerDefinitionId)); // sort by ID to match the order of the UI markers
		setValue(val);
	}

	private TextSelectionData setCurrentSelection(TextSelectionData selection) {
		lastSelection = currentSelection;
		return currentSelection = selection;
	}

	public TextSelectionData getCurrentSelection() {
		return currentSelection;
	}

	public TextSelectionData getLastSelection() {
		return lastSelection;
	}

	public boolean isToolbarEnabled() {
		return toolbarEnabled;
	}

	public void setToolbarEnabled(boolean toolbarEnabled) {
		this.toolbarEnabled = toolbarEnabled;
		queueCommandIfRendered(() -> new UiTextColorMarkerField.SetToolbarEnabledCommand(getId(), toolbarEnabled));
	}

	@Override
	public void setValue(TextColorMarkerFieldValue value) {
		validateMarkers(value);
		super.setValue(value);
	}

	private void validateMarkers(TextColorMarkerFieldValue value) {
		if (value != null && value.markers() != null) {
			value.markers().forEach(this::validateMarker);
		}
	}

	private void validateMarker(TextColorMarker marker) {
		if (marker == null) {
			throw new IllegalTextColorMarkerException("Marker must not be null");
		}
		if (markerDefinitions.stream().noneMatch(def -> def.id() == marker.markerDefinitionId())) {
			throw new IllegalTextColorMarkerException("Marker definition ID must exist: " + marker.markerDefinitionId());
		}
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_TEXT_COLOR_MARKER_FIELD_TEXT_SELECTED:
				UiTextColorMarkerField.TextSelectedEvent textSelectedEvent = (UiTextColorMarkerField.TextSelectedEvent) event;
				if (textSelectedEvent.getEnd() == 0) { // start == end == 0
					setCurrentSelection(null); // selection was removed
				} else if (getEditingMode() != FieldEditingMode.DISABLED) {
					onTextSelected.fire(setCurrentSelection(new TextSelectionData(textSelectedEvent.getStart(), textSelectedEvent.getEnd())));
				}
				break;
			case UI_FIELD_VALUE_CHANGED:
				if (getEditingMode() != FieldEditingMode.DISABLED) {
					applyValueFromUi(((UiField.ValueChangedEvent) event).getValue());
					validate();
				} else {
					LOGGER.warn("Got valueChanged event from disabled field {} {}", this.getClass().getSimpleName(), getDebuggingId());
				}
				break;
			default:
				super.handleUiEvent(event);
		}
	}

	@Override
	public TextColorMarkerFieldValue convertUiValueToUxValue(Object value) {
		return value == null ? null : new TextColorMarkerFieldValue(
				((UiTextColorMarkerFieldValue) value).getText(),
				((UiTextColorMarkerFieldValue) value).getMarkers().stream().map(
						m -> new TextColorMarker(m.getMarkerDefinitionId(), m.getStart(), m.getEnd())
				).toList()
		);
	}

	@Override
	public Object convertUxValueToUiValue(TextColorMarkerFieldValue value) {
		return value == null ? null : value.toUiTextColorMarkerFieldValue();
	}
}
