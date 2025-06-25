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

import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.ux.component.field.AbstractField;

import java.util.List;

public class TextColorMarkerField extends AbstractField<TextColorMarkerFieldValue> {

	static final TextColorMarkerFieldValue EMPTY_VALUE = new TextColorMarkerFieldValue("", List.of());

	private List<TextColorMarkerDefinition> markerDefinitions;
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
		return markerDefinitions == null ? List.of(): markerDefinitions.stream()
				.map(TextColorMarkerDefinition::toUiTextColorMarkerFieldMarkerDefinition)
				.toList();
	}

	public List<TextColorMarkerDefinition> getMarkerDefinitions() {
		return markerDefinitions;
	}

	public void setMarkerDefinitions(List<TextColorMarkerDefinition> markerDefinitions,
									 TextColorMarkerFieldValue value) {
		// TODO validate definition + value (text + marker id + marker pos)
		this.markerDefinitions = markerDefinitions;
		List<UiTextColorMarkerFieldMarkerDefinition> uiMarkerDefinitions = createUiMarkerDefinitions(markerDefinitions);
		UiTextColorMarkerFieldValue uiValue = value != null ? value.toUiTextColorMarkerFieldValue() : null;
		queueCommandIfRendered(() -> new UiTextColorMarkerField.SetMarkerDefinitionsCommand(getId(), uiMarkerDefinitions, uiValue));
		setValue(value);
	}

	public void setMarkerDefinitions(List<TextColorMarkerDefinition> markerDefinitions) {
		setMarkerDefinitions(markerDefinitions, EMPTY_VALUE);
	}

	public void setMarker(int markerDefinitionId, int start, int end) {
		if (!getEditingMode().isEditable()) {
			return;
		}
		// TODO validate marker id + start/end

		//TextColorMarkerFieldValue val = new TextColorMarkerFieldValue(getValue().text(), new ArrayList<>(getValue().markers()));
		//val.markers().removeIf(m -> m.markerDefinitionId() == markerDefinitionId);
		//val.markers().add(new TextColorMarker(markerDefinitionId, start, end));
		//val.markers().sort(Comparator.comparingInt(TextColorMarker::markerDefinitionId)); // sort by ID to match the order of the UI markers
		//setValueWithoutNotifyingClient(val);
		// TODO add marker to "value" (it's updated by onValueChanged but delayed!)
		// TODO ensure it's not overriding transient text changes from UI
		queueCommandIfRendered(() -> new UiTextColorMarkerField.SetMarkerCommand(getId(), markerDefinitionId, start, end));
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
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_TEXT_COLOR_MARKER_FIELD_TEXT_SELECTED:
				if (!getEditingMode().isEditable()) break;
				UiTextColorMarkerField.TextSelectedEvent textSelectedEvent = (UiTextColorMarkerField.TextSelectedEvent) event;
				if (textSelectedEvent.getEnd() == 0) { // start == end == 0
					setCurrentSelection(null); // selection was removed
				} else {
					onTextSelected.fire(setCurrentSelection(new TextSelectionData(textSelectedEvent.getStart(), textSelectedEvent.getEnd())));
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
