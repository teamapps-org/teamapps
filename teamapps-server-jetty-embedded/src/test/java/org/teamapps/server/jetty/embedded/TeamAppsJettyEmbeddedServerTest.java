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
package org.teamapps.server.jetty.embedded;

import org.teamapps.common.format.Color;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.field.FieldMessage;
import org.teamapps.ux.component.field.textcolormarker.*;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.toolbutton.ToolButton;
import org.teamapps.ux.session.SessionContext;

import java.util.List;
import java.util.Locale;

public class TeamAppsJettyEmbeddedServerTest {

	public static void main(String[] args) throws Exception {
		TeamAppsJettyEmbeddedServer.builder((SessionContext sessionContext) -> {
					sessionContext.setLocale(Locale.US);
					RootPanel rootPanel = sessionContext.addRootPanel();

					Panel panel = new Panel(null, "Hallo");
					ToolButton toolButton = new ToolButton(MaterialIcon.SETTINGS, "toolbox");
					panel.addToolButton(toolButton);
					ToolButton readonlyButton = new ToolButton(MaterialIcon.FLASH_ON, "readonly");
					panel.addToolButton(readonlyButton);

					TextColorMarkerField field = new TextColorMarkerField();

					field.setMarkerDefinitions(List.of(
							new TextColorMarkerDefinition(1, Color.RED, Color.BLUE, "ja so wat! \n ach ne!"),
							new TextColorMarkerDefinition(9, null, Color.YELLOW, "marker")
					), new TextColorMarkerFieldValue("Hallooo \n Welt", List.of(new TextColorMarker(9, 2, 5))));

					//field.addCustomFieldMessage(FieldMessage.Severity.ERROR, "Dingens");

					field.onTextSelected.addListener(event ->
						field.addCustomFieldMessage(FieldMessage.Severity.INFO, "text selected: " + event.start() + "-" + event.end())
					);

					field.onValueChanged.addListener(event ->
						field.addCustomFieldMessage(FieldMessage.Severity.INFO, "value changed: " + event.text() + " " + renderMarkersToString(event))
					);

					field.onBlur.addListener(() ->
						field.addCustomFieldMessage(FieldMessage.Severity.INFO, "field blur")
					);

					field.onFocus.addListener(() ->
						field.addCustomFieldMessage(FieldMessage.Severity.INFO, "field focus")
					);


					field.getMarkerDefinitions().forEach(def -> {
						ToolButton markButton = new ToolButton(MaterialIcon.CROP_SQUARE, String.valueOf(def.id()));
						markButton.onClick.addListener(() -> {
							TextSelectionData selection = field.getLastSelection();
							if (selection == null) return;
							field.setMarker(def.id(), selection.start(), selection.end());
							field.addCustomFieldMessage(FieldMessage.Severity.INFO,  "set marker: " + def.id() + "=" + selection.start() + "-" + selection.end());
							field.addCustomFieldMessage(FieldMessage.Severity.INFO,  "value after set marker: " + field.getValue().text() + " " + renderMarkersToString(field.getValue()));
						});
						panel.addToolButton(markButton);
					});

					toolButton.onClick.addListener(() -> {
						field.setToolbarEnabled(!field.isToolbarEnabled());
						field.addCustomFieldMessage(FieldMessage.Severity.INFO, "toolbar was " + (field.isToolbarEnabled() ? "enabled" : "disabled"));
					});
					readonlyButton.onClick.addListener(() ->
							field.setEditingMode(field.getEditingMode() == FieldEditingMode.READONLY ? FieldEditingMode.EDITABLE : FieldEditingMode.READONLY));

					panel.setContent(field);

					rootPanel.setContent(panel);
				})
				.setPort(8082)
				.build()
				.start();
	}

	private static String renderMarkersToString(TextColorMarkerFieldValue value) {
		return value.markers().isEmpty() ? null :value.markers().stream()
				.map(m -> new StringBuilder().append(m.markerDefinitionId()).append("=").append(m.start()).append("-").append(m.end()))
				.reduce((m, n) -> m.append(", ").append(n))
				.get().toString();
	}

}
