/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.field.FieldMessage;
import org.teamapps.ux.component.field.TemplateField;
import org.teamapps.ux.component.flexcontainer.FlexSizeUnit;
import org.teamapps.ux.component.flexcontainer.FlexSizingPolicy;
import org.teamapps.ux.component.flexcontainer.HorizontalLayout;
import org.teamapps.ux.component.form.ResponsiveForm;
import org.teamapps.ux.component.form.ResponsiveFormConfigurationTemplate;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.form.ResponsiveFormSection;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormDialogue extends Window {

	public Event<Boolean> onResult = new Event<>();
	public Event<Void> onOk = new Event<>();
	public Event<Void> onCancel = new Event<>();

	private final TemplateField<BaseTemplateRecord<?>> titleField;
	private Integer buttonLineIndex;
	private final ResponsiveFormLayout formLayout;
	private HorizontalLayout buttonRow;
	private final List<AbstractField<?>> fields = new ArrayList<>();

	private boolean autoCloseOnOk = true;

	public static FormDialogue create(Icon icon, String title, String text) {
		return new FormDialogue(icon, title, text);
	}

	public FormDialogue(Icon icon, String title, String text) {
		this(icon, null, title, text);
	}

	public FormDialogue(Icon icon, String imageUrl, String title, String text) {
		this(icon, imageUrl, title, text, ResponsiveFormConfigurationTemplate.createDefaultTwoColumnTemplate(0, 200, 0));
	}

	public FormDialogue(Icon icon, String imageUrl, String title, String text, ResponsiveFormConfigurationTemplate configurationTemplate) {
		setIcon(icon);
		setTitle(title);
		setWidth(550);
		setHeight(350);
		ResponsiveForm<?> responsiveForm = new ResponsiveForm<>(configurationTemplate);
		formLayout = responsiveForm.addResponsiveFormLayout(450);
		formLayout.addSection().setDrawHeaderLine(false).setCollapsible(false).setMargin(new Spacing(10)).setGridGap(20);
		titleField = new TemplateField<>(BaseTemplate.LIST_ITEM_VERY_LARGE_ICON_TWO_LINES, new BaseTemplateRecord<>(icon, imageUrl, title, text, null));
		formLayout.addField(0, 0, "header", titleField).setHorizontalAlignment(HorizontalElementAlignment.LEFT).setColSpan(2);
		setContent(responsiveForm);
	}

	public ResponsiveFormSection addSection(Icon<?, ?> icon, String caption)  {
		return formLayout.addSection(icon, caption);
	}

	public void addField(Icon icon, String caption, AbstractField<?> field) {
		formLayout.addLabelAndField(icon, caption, field).field.setColSpan(2);
		this.fields.add(field);
	}

	public void addOkCancelButtons(String okCaption, String cancelCaption) {
		addOkButton(okCaption);
		addCancelButton(cancelCaption);
	}

	public void addOkCancelButtons(Icon okIcon, String okCaption, Icon cancelIcon, String cancelCaption) {
		addOkButton(okIcon, okCaption);
		addCancelButton(cancelIcon, cancelCaption);
	}

	public Button<?> addOkButton(String caption) {
		return addOkButton(MaterialIcon.CHECK, caption);
	}

	public Button<?> addOkButton(Icon icon, String caption) {
		createButtonRowIfNotExists();

		Button<?> okButton = Button.create(icon, caption);
		okButton.onClicked.addListener(() -> {
			List<FieldMessage> errorMessages = fields.stream()
					.flatMap(f -> f.validate().stream())
					.filter(validationMessag -> validationMessag.getSeverity() == FieldMessage.Severity.ERROR)
					.collect(Collectors.toList());
			if (errorMessages.size() == 0) {
				onResult.fire(true);
				onOk.fire();
				if (autoCloseOnOk) {
					close(250);
				}
			}
		});
		buttonRow.addComponent(okButton, new FlexSizingPolicy(0, FlexSizeUnit.AUTO, 1, 1));
		return okButton;
	}

	private void createButtonRowIfNotExists() {
		if (buttonRow == null) {
			formLayout.addSection().setCollapsible(false).setDrawHeaderLine(false);
			buttonRow = new HorizontalLayout();
			formLayout.addLabelAndComponent(buttonRow).field.setColSpan(2);
		}
	}

	public Button<?> addCancelButton(String caption) {
		return addCancelButton(MaterialIcon.CANCEL, caption);
	}

	public Button<?> addCancelButton(Icon icon, String caption) {
		createButtonRowIfNotExists();

		Button<?> cancelButton = Button.create(icon, caption);
		cancelButton.setCssStyle("margin-left", "10px"); // TODO #css
		cancelButton.onClicked.addListener(() -> {
			close(250);
			onResult.fire(false);
			onCancel.fire();
		});
		buttonRow.addComponent(cancelButton, new FlexSizingPolicy(0, FlexSizeUnit.AUTO, 1, 1));
		return cancelButton;
	}

	private int getButtonLineIndex() {
		if (buttonLineIndex == null) {
			buttonLineIndex = formLayout.getLastNonEmptyRowInSection() + 1;
		}
		return buttonLineIndex;
	}

	public boolean isAutoCloseOnOk() {
		return autoCloseOnOk;
	}

	public void setAutoCloseOnOk(boolean autoCloseOnOk) {
		this.autoCloseOnOk = autoCloseOnOk;
	}
}
