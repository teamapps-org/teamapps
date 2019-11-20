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
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.field.combobox.ComboBox;
import org.teamapps.ux.component.form.ResponsiveForm;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.window.Window;

public class FormDialogue extends Window {

	public Event<Boolean> onResult = new Event<>();
	private ComboBox<BaseTemplateRecord<?>> comboBox;
	private Button<?> okButton;
	private Button<?> cancelButton;
	private Integer buttonLineIndex;
	private ResponsiveFormLayout formLayout;

	public static FormDialogue create(Icon icon, String title, String text) {
		return new FormDialogue(icon, title, text);
	}

	public FormDialogue(Icon icon, String title, String text) {
		setIcon(icon);
		setTitle(title);
		setWidth(550);
		setHeight(350);
		ResponsiveForm<?> responsiveForm = new ResponsiveForm<>(0, 200, 0);
		formLayout = responsiveForm.addResponsiveFormLayout(450);
		formLayout.addSection().setDrawHeaderLine(false).setCollapsible(false).setMargin(new Spacing(10)).setGridGap(20);
		comboBox = new ComboBox<>();
		comboBox.setTemplate(BaseTemplate.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES);
		comboBox.setEditingMode(FieldEditingMode.READONLY);
		comboBox.setValue(new BaseTemplateRecord<>(icon, title, text));
		formLayout.addField(0, 0, "header", comboBox).setHorizontalAlignment(HorizontalElementAlignment.LEFT).setColSpan(3);
		setContent(responsiveForm);
	}

	public void addField(Icon icon, String caption, AbstractField<?> field) {
		formLayout.addLabelAndField(icon, caption, field).field.setColSpan(2);
	}

	public void addOkCancelButtons(String okCaption, String cancelCaption) {
		addOkButton(okCaption);
		addCancelButton(cancelCaption);
	}

	public Button<?> addOkButton(String caption) {
		okButton = Button.create(MaterialIcon.CHECK, caption);
		okButton.onValueChanged.addListener((o) -> {
			onResult.fire(true);
		});

		formLayout.addField(getButtonLineIndex(), 1, "ok", okButton);
		return okButton;
	}

	public Button<?> addCancelButton(String caption) {
		cancelButton = Button.create(MaterialIcon.CANCEL, caption);
		cancelButton.onValueChanged.addListener((o) -> {
			close(250);
			getSessionContext().flushCommands();
			onResult.fire(false);
		});
		formLayout.addField(getButtonLineIndex(), 2, "cancel", cancelButton);
		return cancelButton;
	}

	private int getButtonLineIndex() {
		if (buttonLineIndex == null) {
			buttonLineIndex = formLayout.getLastNonEmptyRowInSection() + 1;
		}
		return buttonLineIndex;
	}
}
