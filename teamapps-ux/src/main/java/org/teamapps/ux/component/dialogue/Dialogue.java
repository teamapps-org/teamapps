/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.dialogue;

import org.teamapps.event.Event;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.field.combobox.ComboBox;
import org.teamapps.ux.component.form.ResponsiveForm;
import org.teamapps.ux.component.form.ResponsiveFormConfigurationTemplate;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.SizingPolicy;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.component.grid.layout.GridColumn;
import org.teamapps.ux.component.grid.layout.GridRow;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.window.Window;


public class Dialogue extends Window {
	public Event<Boolean> onResult = new Event<>();
	private final ComboBox<BaseTemplateRecord<?>> comboBox;
	private final Button<?> okButton;
	private final Button<?> cancelButton;

	public static Event<Boolean> showOkCancel(Icon icon, String title, String text) {
		return showOkCancel(new BaseTemplateRecord<>(icon, title, text));
	}

	public static Event<Boolean> showOkCancel(BaseTemplateRecord<?> record) {
		Dialogue dialogue = new Dialogue();
		dialogue.setValues(record);
		dialogue.setIcon(record.getIcon());
		dialogue.setTitle(record.getCaption());
		dialogue.setModal(true);
		dialogue.show(250);
		return dialogue.onResult;
	}

	public static Dialogue createOkCancel(Icon icon, String title) {
		return createOkCancel(icon, title, title);
	}

	public static Dialogue createOkCancel(Icon icon, String title, String text) {
		Dialogue dialogue = new Dialogue();
		dialogue.setValues(icon, title, text);
		dialogue.setIcon(icon);
		dialogue.setTitle(title);
		dialogue.setModal(true);
		return dialogue;
	}

	public static Event<Boolean> showOk(Icon icon, String title) {
		return showOk(icon, title, title);
	}

	public static Event<Boolean> showOk(Icon icon, String title, String text) {
		Dialogue dialogue = new Dialogue();
		dialogue.getCancelButton().setVisible(false);
		dialogue.setValues(icon, title, text);
		dialogue.setIcon(icon);
		dialogue.setTitle(title);
		dialogue.setModal(false);
		dialogue.show(250);
		return dialogue.onResult;
	}

	public Dialogue() {
		ResponsiveFormConfigurationTemplate template = new ResponsiveFormConfigurationTemplate();
		template.setRowTemplate(new GridRow().setHeightPolicy(SizingPolicy.AUTO));
		template.setColumnTemplate(new GridColumn(SizingPolicy.AUTO));
		ResponsiveForm<?> responsiveForm = new ResponsiveForm<>(template);
		setContent(responsiveForm);
		setWidth(500);
		setHeight(200);
		ResponsiveFormLayout formLayout = responsiveForm.addResponsiveFormLayout(200);
		formLayout.addSection().setDrawHeaderLine(false).setMargin(new Spacing(10)).setGridGap(20);
		comboBox = new ComboBox<>();
		comboBox.setTemplate(BaseTemplate.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES);
		comboBox.setEditingMode(FieldEditingMode.READONLY);
		formLayout.addField(0, 0, "data", comboBox).setHorizontalAlignment(HorizontalElementAlignment.CENTER).setColSpan(3);
		okButton = Button.create(MaterialIcon.CHECK, "OK");
		okButton.onValueChanged.addListener((o) -> {
			close(250);
			getSessionContext().flushCommands();
			onResult.fire(true);
		});
		cancelButton = Button.create(MaterialIcon.CANCEL, "Abbrechen");
		cancelButton.onValueChanged.addListener((o) -> {
			close(250);
			getSessionContext().flushCommands();
			onResult.fire(false);
		});
		formLayout.addField(1, 1, "ok", okButton);
		formLayout.addField(1, 2, "cancel", cancelButton);
	}

	public void setValues(BaseTemplateRecord<?> record) {
		comboBox.setValue(record);
	}

	public void setValues(Icon icon, String title, String text) {
		comboBox.setValue(new BaseTemplateRecord<>(icon, title, text));
	}

	public ComboBox<BaseTemplateRecord<?>> getComboBox() {
		return comboBox;
	}

	public Button<?> getOkButton() {
		return okButton;
	}

	public Button<?> getCancelButton() {
		return cancelButton;
	}
}
