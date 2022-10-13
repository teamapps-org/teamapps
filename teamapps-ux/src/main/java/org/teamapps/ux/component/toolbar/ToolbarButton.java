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
package org.teamapps.ux.component.toolbar;

import org.teamapps.common.format.Color;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.UiToolbar;
import org.teamapps.dto.UiToolbarButton;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.template.Template;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class ToolbarButton {

	public final Event<UiToolbar.ToolbarButtonClickEvent> onClick = new Event<>();

	private ToolbarButtonGroup toolbarButtonGroup;
	private final String clientId = UUID.randomUUID().toString();
	private String debuggingId;

	private final Object record;

	private Template template; // if null, will fallback to toolbarButtonGroup's template
	private PropertyProvider propertyProvider; // if null, will fallback to toolbarButtonGroup's valueExtractor

	// ===== HACKS =====
	private String openNewTabWithUrl;
	private Component togglesFullScreenOnComponent;
	private Component startPlaybackComponent;
	// ===== END HACKS =====

	private Supplier<Component> dropDownComponentSupplier;
	private boolean eagerDropDownRendering = false;
	private int droDownPanelWidth;

	private boolean visible = true;

	private Color backgroundColor;
	private Color hoverBackgroundColor;

	public ToolbarButton(Object record) {
		this(null, record, null, null, null, 0);
	}

	public ToolbarButton(Template template, Object record) {
		this(template, record, null, null, null, 0);
	}

	public ToolbarButton(Template template, Object record, Component dropDownView) {
		this(template, record, null, null, dropDownView, 0);
	}

	private ToolbarButton(Template template, Object record, String openNewTabWithUrl, Component togglesFullScreenOnComponent, Component dropDownView, int droDownPanelWidth) {
		this.template = template;
		this.record = record;
		this.openNewTabWithUrl = openNewTabWithUrl;
		this.togglesFullScreenOnComponent = togglesFullScreenOnComponent;
		if (dropDownView != null) {
			dropDownComponentSupplier = () -> dropDownView;
		}
		this.droDownPanelWidth = droDownPanelWidth;
	}

	public static ToolbarButton create(Icon icon, String caption, String description) {
		return new ToolbarButton(null, new BaseTemplateRecord(icon, caption, description));
	}

	public static ToolbarButton createLarge(Icon icon, String caption, String description) {
		return new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON, new BaseTemplateRecord(icon, caption, description));
	}

	public static ToolbarButton createLargeDarkTheme(Icon icon, String caption, String description) {
		return new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON.createDarkThemeTemplate(), new BaseTemplateRecord(icon, caption, description));
	}

	public static ToolbarButton createSmall(Icon icon, String caption) {
		return createSmall(icon, caption, null);
	}

	public static ToolbarButton createSmall(Icon icon, String caption, String description) {
		return new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_SMALL, new BaseTemplateRecord(icon, caption, description));
	}

	public static ToolbarButton createSmallDarkTheme(Icon icon, String caption, String description) {
		return new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_SMALL.createDarkThemeTemplate(), new BaseTemplateRecord(icon, caption, description));
	}

	public static ToolbarButton createTiny(Icon icon, String caption) {
		return new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_TINY, new BaseTemplateRecord(icon, caption));
	}

	public static ToolbarButton createOpenNewTabButton(Template template, BaseTemplateRecord Object, String newTabUrl) {
		return new ToolbarButton(template, Object, newTabUrl, null, null, 0);
	}

	public static ToolbarButton createFullScreenToggleButton(BaseTemplateRecord Object, Component togglesFullScreenOnComponent) {
		return new ToolbarButton(null, Object, null, togglesFullScreenOnComponent, null, 0);
	}

	public static ToolbarButton createFullScreenToggleButton(Template template, BaseTemplateRecord Object, Component togglesFullScreenOnComponent) {
		return new ToolbarButton(template, Object, null, togglesFullScreenOnComponent, null, 0);
	}

	public UiToolbarButton createUiToolbarButton() {
		Template template = getAppliedTemplate();
		Map<String, Object> values = getAppliedPropertyProvider().getValues(record, template.getPropertyNames());

		UiToolbarButton ui = new UiToolbarButton(clientId, template.createUiTemplate(), values);
		if (this.eagerDropDownRendering && this.dropDownComponentSupplier != null) {
			ui.setDropDownComponent(dropDownComponentSupplier.get().createUiReference());
		}
		ui.setHasDropDown(this.dropDownComponentSupplier != null);
		ui.setDropDownPanelWidth(droDownPanelWidth > 0 ? droDownPanelWidth : 450);
		ui.setVisible(visible);
		ui.setOpenNewTabWithUrl(openNewTabWithUrl);
		ui.setTogglesFullScreenOnComponent(togglesFullScreenOnComponent != null ? togglesFullScreenOnComponent.createUiReference() : null);
		ui.setStartPlaybackComponent(startPlaybackComponent != null ? startPlaybackComponent.createUiReference() : null);
		ui.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		ui.setHoverBackgroundColor(hoverBackgroundColor != null ? hoverBackgroundColor.toHtmlColorString() : null);
		ui.setDebuggingId(debuggingId);
		return ui;
	}

	public ToolbarButton setVisible(boolean visible) {
		boolean oldValue = this.visible;
		this.visible = visible;
		if (oldValue != visible && this.toolbarButtonGroup != null) {
			toolbarButtonGroup.handleButtonVisibilityChange(this.clientId, visible);
		}
		return this;
	}

	public ToolbarButton setOpenNewTabWithUrl(String openNewTabWithUrl) {
		this.openNewTabWithUrl = openNewTabWithUrl;
		return this;
	}

	public ToolbarButton setTogglesFullScreenOnComponent(Component togglesFullScreenOnComponent) {
		this.togglesFullScreenOnComponent = togglesFullScreenOnComponent;
		return this;
	}

	public Component getStartPlaybackComponent() {
		return startPlaybackComponent;
	}

	public ToolbarButton setStartPlaybackComponent(Component startPlaybackComponent) {
		this.startPlaybackComponent = startPlaybackComponent;
		return this;
	}

	/*package-private*/ String getClientId() {
		return clientId;
	}


	public Object getRecord() {
		return record;
	}

	public ToolbarButtonGroup getToolbarButtonGroup() {
		return toolbarButtonGroup;
	}

	public String getOpenNewTabWithUrl() {
		return openNewTabWithUrl;
	}

	public Component getTogglesFullScreenOnComponent() {
		return togglesFullScreenOnComponent;
	}

	public int getDroDownPanelWidth() {
		return droDownPanelWidth;
	}

	public boolean isDropDownLazyInitializing() {
		return dropDownComponentSupplier != null;
	}

	public boolean isVisible() {
		return visible;
	}

	public ToolbarButton setToolbarButtonGroup(ToolbarButtonGroup toolbarButtonGroup) {
		this.toolbarButtonGroup = toolbarButtonGroup;
		return this;
	}

	public ToolbarButton setDroDownPanelWidth(int droDownPanelWidth) {
		this.droDownPanelWidth = droDownPanelWidth;
		return this;
	}

	public ToolbarButton setDropDownComponent(Component dropDownComponent) {
		this.dropDownComponentSupplier = () -> dropDownComponent;
		return this;
	}

	public ToolbarButton updateDropDownComponent(Component dropDownComponent) {
		this.dropDownComponentSupplier = () -> dropDownComponent;
		this.toolbarButtonGroup.handleDropDownComponentUpdate(this, dropDownComponent);
		return this;
	}

	public boolean isEagerDropDownRendering() {
		return eagerDropDownRendering;
	}

	public ToolbarButton setEagerDropDownRendering(boolean eagerDropDownRendering) {
		this.eagerDropDownRendering = eagerDropDownRendering;
		return this;
	}

	public Template getTemplate() {
		return template;
	}

	public ToolbarButton setTemplate(Template template) {
		this.template = template;
		return this;
	}

	public Template getAppliedTemplate() {
		if (this.template != null) {
			return this.template;
		} else if (toolbarButtonGroup != null && toolbarButtonGroup.getAppliedTemplate() != null) {
			return toolbarButtonGroup.getAppliedTemplate();
		} else {
			return BaseTemplate.TOOLBAR_BUTTON;
		}
	}

	public PropertyProvider getPropertyProvider() {
		return propertyProvider;
	}

	public ToolbarButton setPropertyProvider(PropertyProvider propertyProvider) {
		this.propertyProvider = propertyProvider;
		return this;
	}

	public ToolbarButton setPropertyExtractor(PropertyExtractor propertyExtractor) {
		this.setPropertyProvider(propertyExtractor);
		return this;
	}

	public PropertyProvider getAppliedPropertyProvider() {
		return this.propertyProvider != null ? this.propertyProvider : toolbarButtonGroup.getAppliedPropertyProvider();
	}

	public Supplier<Component> getDropDownComponentSupplier() {
		return this.dropDownComponentSupplier;
	}

	/*package-private*/ Component getDropDownComponent() {
		return this.dropDownComponentSupplier != null ? this.dropDownComponentSupplier.get() : null;
	}

	public ToolbarButton setDropDownComponentSupplier(Supplier<Component> dropDownComponentSupplier) {
		this.dropDownComponentSupplier = dropDownComponentSupplier;
		return this;
	}

	public void closeDropDown() {
		toolbarButtonGroup.handleCloseDropdown(this);
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public ToolbarButton setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		if (toolbarButtonGroup != null){
			toolbarButtonGroup.handleColorChange(this.clientId, backgroundColor, hoverBackgroundColor);
		}
		return this;
	}

	public Color getHoverColor() {
		return hoverBackgroundColor;
	}

	public ToolbarButton setHoverBackgroundColor(Color hoverBackgroundColor) {
		this.hoverBackgroundColor = hoverBackgroundColor;
		if (toolbarButtonGroup != null){
			toolbarButtonGroup.handleColorChange(this.clientId, backgroundColor, hoverBackgroundColor);
		}
		return this;
	}

	public String getDebuggingId() {
		return debuggingId;
	}

	public void setDebuggingId(String debuggingId) {
		this.debuggingId = debuggingId;
	}
}
