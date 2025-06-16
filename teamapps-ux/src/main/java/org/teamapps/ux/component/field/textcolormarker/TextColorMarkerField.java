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

	private List<TextColorMarkerFieldMarkerDefinition> markerDefinitions;
	private boolean toolbarEnabled;

	public final Event<TextSelectedEventData> onTextSelected = new Event<>();
	public final Event<TextColorMarkerFieldValue> onTransientChange = new Event<>(); // TODO how is this different from onValueChanged? (onValueChanged is not triggered by

	@Override
	public UiTextColorMarkerField createUiComponent() {
		UiTextColorMarkerField ui = new UiTextColorMarkerField();
		mapAbstractFieldAttributesToUiField(ui);
		ui.setToolbarEnabled(toolbarEnabled);
		ui.setMarkerDefinitions(createUiMarkerDefinitions(markerDefinitions));
		return ui;
	}

	public List<TextColorMarkerFieldMarkerDefinition> getMarkerDefinitions() {
		return markerDefinitions;
	}

	public void setMarkerDefinitions(List<TextColorMarkerFieldMarkerDefinition> markerDefinitions,
									 TextColorMarkerFieldValue value) {
		this.markerDefinitions = markerDefinitions;
		List<UiTextColorMarkerFieldMarkerDefinition> uiMarkerDefinitions = createUiMarkerDefinitions(markerDefinitions);
		UiTextColorMarkerFieldValue uiValue = value != null ? value.toUiTextColorMarkerFieldValue() : null;
		queueCommandIfRendered(() -> new UiTextColorMarkerField.SetMarkerDefinitionsCommand(getId(), uiMarkerDefinitions, uiValue));
		setValue(value);
	}

	public void setMarker(int id, int start, int end) {
		queueCommandIfRendered(() -> new UiTextColorMarkerField.SetMarkerCommand(getId(), id, start, end));
		// TODO update "value" (it's updated by onTransientChange but delayed!)
		// TODO make sure it's not overriding text changes from UI which are not synced back yet
	}


	private static List<UiTextColorMarkerFieldMarkerDefinition> createUiMarkerDefinitions(List<TextColorMarkerFieldMarkerDefinition> markerDefinitions) {
		return markerDefinitions == null ? List.of(): markerDefinitions.stream()
				.map(TextColorMarkerFieldMarkerDefinition::toUiTextColorMarkerFieldMarkerDefinition)
				.toList();
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
				UiTextColorMarkerField.TextSelectedEvent textSelectedEvent = (UiTextColorMarkerField.TextSelectedEvent) event;
				onTextSelected.fire(new TextSelectedEventData(textSelectedEvent.getStart(), textSelectedEvent.getEnd()));
				break;
			case UI_TEXT_COLOR_MARKER_FIELD_TRANSIENT_CHANGE:
				UiTextColorMarkerFieldValue uiValue = ((UiTextColorMarkerField.TransientChangeEvent) event).getValue();
				TextColorMarkerFieldValue uxValue = new TextColorMarkerFieldValue(
						uiValue.getText(),
						uiValue.getMarkers().stream().map(m -> new TextMarker(m.getId(), m.getStart(), m.getEnd())).toList()
				);
				setValue(uxValue); // TODO "value" must be updated on transient change event -> however, it must not re-trigger UI value change
				onTransientChange.fire(uxValue);
				break;
			default:
				super.handleUiEvent(event);
		}
	}
}
