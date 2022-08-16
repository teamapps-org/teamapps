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
package org.teamapps.ux.component.panel;

import org.teamapps.common.format.Color;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiPanel;
import org.teamapps.dto.UiPanelHeaderField;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.toolbutton.ToolButton;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Panel extends AbstractComponent implements Component {

	public final Event<WindowButtonType> onWindowButtonClicked = new Event<>();

	private String title;
	private Icon icon;

	private AbstractField<?> leftHeaderField;
	private Icon leftHeaderFieldIcon;
	private int leftHeaderFieldMinWidth = 100;
	private int leftHeaderFieldMaxWidth = 300;
	private AbstractField<?> rightHeaderField;
	private Icon rightHeaderFieldIcon;
	private int rightHeaderFieldMinWidth = 100;
	private int rightHeaderFieldMaxWidth = 300;
	private HeaderComponentMinimizationPolicy headerComponentMinimizationPolicy = HeaderComponentMinimizationPolicy.LEFT_COMPONENT_FIRST;
	private boolean alwaysShowHeaderFieldIcons = false;

	private Component content;
	private boolean stretchContent = true;

	private boolean hideTitleBar;
	private Toolbar toolbar;
	private int padding = 0;
	private final List<ToolButton> toolButtons = new ArrayList<>();
	private final Set<WindowButtonType> windowButtons = new HashSet<>();

	private ObservableValue<Icon> observableIcon;
	private final Consumer<Icon> iconChangeListener = this::setIcon;
	private ObservableValue<String> observableTitle;
	private final Consumer<String> titleChangeListener = this::setTitle;
	private ObservableValue<AbstractField<?>> observableLeftHeaderField;
	private final Consumer<AbstractField<?>> leftHeaderFieldChangeListener = this::setLeftHeaderField;
	private ObservableValue<AbstractField<?>> observableRightHeaderField;
	private final Consumer<AbstractField<?>> rightHeaderFieldChangeListener = this::setRightHeaderField;


	public Panel() {
		this(null, null, null);
	}

	public Panel(Icon icon, String title) {
		this(icon, title, null);
	}

	public Panel(Icon icon, String title, Component content) {
		this.icon = icon;
		this.title = title;
		setContent(content);
	}

	public void addToolButton(ToolButton toolButton) {
		this.toolButtons.add(toolButton);
		toolButton.setParent(this);
		updateToolButtons();
	}

	public void removeToolButton(ToolButton toolButton) {
		this.toolButtons.remove(toolButton);
		toolButton.setParent(null);
		updateToolButtons();
	}

	public void setToolButtons(List<ToolButton> toolButtons) {
		this.toolButtons.clear();
		if (toolButtons != null) {
			this.toolButtons.addAll(toolButtons);
			this.toolButtons.forEach(toolButton -> toolButton.setParent(this));
		}
		updateToolButtons();
	}

	private void updateToolButtons() {
		queueCommandIfRendered(() -> new UiPanel.SetToolButtonsCommand(getId(), this.toolButtons.stream()
				.map(toolButton -> toolButton.createUiReference())
				.collect(Collectors.toList())));
	}

	public List<ToolButton> getToolButtons() {
		return toolButtons;
	}

	public void setWindowButtons(Collection<WindowButtonType> buttons) {
		this.windowButtons.clear();
		this.windowButtons.addAll(buttons);
		updateWindowButtons();
	}

	private void updateWindowButtons() {
		queueCommandIfRendered(() -> new UiPanel.SetWindowButtonsCommand(getId(), this.windowButtons.stream()
				.map(b -> b.toUiWindowButtonType())
				.collect(Collectors.toList())));
	}

	public Set<WindowButtonType> getWindowButtons() {
		return Collections.unmodifiableSet(windowButtons);
	}

	@Override
	public UiComponent createUiComponent() {
		UiPanel uiPanel = new UiPanel();
		mapUiPanelProperties(uiPanel);
		return uiPanel;
	}

	protected void mapUiPanelProperties(UiPanel uiPanel) {
		mapAbstractUiComponentProperties(uiPanel);
		uiPanel.setTitle(title);
		uiPanel.setIcon(getSessionContext().resolveIcon(icon));
		uiPanel.setLeftHeaderField(createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth));
		uiPanel.setRightHeaderField(createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth, rightHeaderFieldMaxWidth));
		uiPanel.setHeaderComponentMinimizationPolicy(headerComponentMinimizationPolicy.toUiHeaderComponentMinimizationPolicy());
		uiPanel.setHideTitleBar(hideTitleBar);
		uiPanel.setToolbar(Component.createUiClientObjectReference(toolbar));
		uiPanel.setContent(content != null ? content.createUiReference() : null);
		uiPanel.setPadding(padding);
		uiPanel.setWindowButtons(windowButtons.stream()
				.map(b -> b.toUiWindowButtonType()).collect(Collectors.toList()));
		uiPanel.setToolButtons(toolButtons.stream()
				.map(toolButton -> toolButton.createUiReference())
				.collect(Collectors.toList()));
		uiPanel.setAlwaysShowHeaderFieldIcons(alwaysShowHeaderFieldIcons);
		uiPanel.setStretchContent(stretchContent);
	}

	public UiPanelHeaderField createUiPanelHeaderField(AbstractField<?> field, Icon icon, int minWidth, int maxWidth) {
		if (field == null) {
			return null;
		}
		UiPanelHeaderField uiPanelHeaderField = new UiPanelHeaderField(field.createUiReference());
		uiPanelHeaderField.setIcon(getSessionContext().resolveIcon(icon));
		uiPanelHeaderField.setMinWidth(minWidth);
		uiPanelHeaderField.setMaxWidth(maxWidth);
		return uiPanelHeaderField;
	}

	public Panel setLeftHeaderField(AbstractField<?> field, Icon icon, int minWidth, int maxWidth) {
		if (field != null) {
			field.setParent(this);
		}
		this.leftHeaderField = field;
		this.leftHeaderFieldIcon = icon;
		this.leftHeaderFieldMinWidth = minWidth;
		this.leftHeaderFieldMaxWidth = maxWidth;
		queueCommandIfRendered(() -> new UiPanel.SetLeftHeaderFieldCommand(getId(), createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth)));
		return this;
	}

	public AbstractField<?> getLeftHeaderField() {
		return leftHeaderField;
	}

	public Panel setRightHeaderField(AbstractField<?> field, Icon icon, int minWidth, int maxWidth) {
		if (field != null) {
			field.setParent(this);
		}
		this.rightHeaderField = field;
		this.rightHeaderFieldIcon = icon;
		this.rightHeaderFieldMinWidth = minWidth;
		this.rightHeaderFieldMaxWidth = maxWidth;
		queueCommandIfRendered(() -> new UiPanel.SetRightHeaderFieldCommand(getId(), createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth,
				rightHeaderFieldMaxWidth)));
		return this;
	}

	public AbstractField<?> getRightHeaderField() {
		return rightHeaderField;
	}

	public void setContent(Component content) {
		this.content = content;
		if (content != null) {
			content.setParent(this);
		}
		queueCommandIfRendered(() -> new UiPanel.SetContentCommand(getId(), content != null ? content.createUiReference() : null));
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		if (event instanceof UiPanel.WindowButtonClickedEvent) {
			UiPanel.WindowButtonClickedEvent clickedEvent = (UiPanel.WindowButtonClickedEvent) event;
			this.onWindowButtonClicked.fire(WindowButtonType.fromUiWindowButtonType(clickedEvent.getWindowButton()));
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		queueCommandIfRendered(() -> new UiPanel.SetTitleCommand(getId(), title));
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
		queueCommandIfRendered(() -> new UiPanel.SetIconCommand(getId(), getSessionContext().resolveIcon(icon)));
	}

	public Component getContent() {
		return content;
	}

	public void setHeaderBackgroundColor(Color headerBackgroundColor) {
		this.setCssStyle("> .panel-heading", "background-color", headerBackgroundColor != null ? headerBackgroundColor.toHtmlColorString() : null);
	}

	public void setHeaderFontColor(Color headerFontColor) {
		this.setCssStyle("> .panel-heading > .panel-title", "color", headerFontColor.toHtmlColorString());
	}

	public void setBodyBackgroundColor(Color bodyBackgroundColor) {
		this.setCssStyle("> .panel-body", "background-color", bodyBackgroundColor != null ? bodyBackgroundColor.toHtmlColorString(): null);
	}

	public HeaderComponentMinimizationPolicy getHeaderComponentMinimizationPolicy() {
		return headerComponentMinimizationPolicy;
	}

	public void setHeaderComponentMinimizationPolicy(HeaderComponentMinimizationPolicy headerComponentMinimizationPolicy) {
		this.headerComponentMinimizationPolicy = headerComponentMinimizationPolicy;
		reRenderIfRendered();
	}

	public boolean isHideTitleBar() {
		return hideTitleBar;
	}

	public void setHideTitleBar(boolean hideTitleBar) {
		this.hideTitleBar = hideTitleBar;
		reRenderIfRendered();
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
		reRenderIfRendered();
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
		reRenderIfRendered();
	}

	public boolean isMaximizable() {
		return this.windowButtons.contains(WindowButtonType.MAXIMIZE_RESTORE);
	}

	public void setMaximizable(boolean maximizable) {
		if (maximizable) {
			this.windowButtons.add(WindowButtonType.MAXIMIZE_RESTORE);
		} else {
			this.windowButtons.remove(WindowButtonType.MAXIMIZE_RESTORE);
		}
		updateWindowButtons();
	}

	public void setLeftHeaderField(AbstractField<?> leftHeaderField) {
		if (this.leftHeaderField != null) {
			this.leftHeaderField.setParent(null);
		}
		this.leftHeaderField = leftHeaderField;
		leftHeaderField.setParent(this);
		queueCommandIfRendered(() -> new UiPanel.SetLeftHeaderFieldCommand(getId(), createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth)));
	}

	public Icon getLeftHeaderFieldIcon() {
		return leftHeaderFieldIcon;
	}

	public void setLeftHeaderFieldIcon(Icon leftHeaderFieldIcon) {
		this.leftHeaderFieldIcon = leftHeaderFieldIcon;
		queueCommandIfRendered(() -> new UiPanel.SetLeftHeaderFieldCommand(getId(), createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth)));
	}

	public int getLeftHeaderFieldMinWidth() {
		return leftHeaderFieldMinWidth;
	}

	public void setLeftHeaderFieldMinWidth(int leftHeaderFieldMinWidth) {
		this.leftHeaderFieldMinWidth = leftHeaderFieldMinWidth;
		queueCommandIfRendered(() -> new UiPanel.SetLeftHeaderFieldCommand(getId(), createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth)));
	}

	public int getLeftHeaderFieldMaxWidth() {
		return leftHeaderFieldMaxWidth;
	}

	public void setLeftHeaderFieldMaxWidth(int leftHeaderFieldMaxWidth) {
		this.leftHeaderFieldMaxWidth = leftHeaderFieldMaxWidth;
		queueCommandIfRendered(() -> new UiPanel.SetLeftHeaderFieldCommand(getId(), createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth)));
	}

	public void setRightHeaderField(AbstractField<?> rightHeaderField) {
		if (this.rightHeaderField != null) {
			this.rightHeaderField.setParent(null);
		}
		this.rightHeaderField = rightHeaderField;
		rightHeaderField.setParent(this);
		queueCommandIfRendered(() -> new UiPanel.SetRightHeaderFieldCommand(getId(), createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth,
				rightHeaderFieldMaxWidth)));
	}

	public Icon getRightHeaderFieldIcon() {
		return rightHeaderFieldIcon;
	}

	public void setRightHeaderFieldIcon(Icon rightHeaderFieldIcon) {
		this.rightHeaderFieldIcon = rightHeaderFieldIcon;
		queueCommandIfRendered(() -> new UiPanel.SetRightHeaderFieldCommand(getId(), createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth,
				rightHeaderFieldMaxWidth)));
	}

	public int getRightHeaderFieldMinWidth() {
		return rightHeaderFieldMinWidth;
	}

	public void setRightHeaderFieldMinWidth(int rightHeaderFieldMinWidth) {
		this.rightHeaderFieldMinWidth = rightHeaderFieldMinWidth;
		queueCommandIfRendered(() -> new UiPanel.SetRightHeaderFieldCommand(getId(), createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth,
				rightHeaderFieldMaxWidth)));
	}

	public int getRightHeaderFieldMaxWidth() {
		return rightHeaderFieldMaxWidth;
	}

	public void setRightHeaderFieldMaxWidth(int rightHeaderFieldMaxWidth) {
		this.rightHeaderFieldMaxWidth = rightHeaderFieldMaxWidth;
		queueCommandIfRendered(() -> new UiPanel.SetRightHeaderFieldCommand(getId(), createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth,
				rightHeaderFieldMaxWidth)));
	}

	public boolean isAlwaysShowHeaderFieldIcons() {
		return alwaysShowHeaderFieldIcons;
	}

	public void setAlwaysShowHeaderFieldIcons(boolean alwaysShowHeaderFieldIcons) {
		this.alwaysShowHeaderFieldIcons = alwaysShowHeaderFieldIcons;
		reRenderIfRendered();
	}

	public boolean isStretchContent() {
		return stretchContent;
	}

	public void setStretchContent(boolean stretchContent) {
		this.stretchContent = stretchContent;
		queueCommandIfRendered(() -> new UiPanel.SetStretchContentCommand(getId(), stretchContent));
	}


	public void setIcon(ObservableValue<Icon> observableIcon) {
		if (this.observableIcon != null)  {
			this.observableIcon.onChanged().removeListener(iconChangeListener);
		}
		this.observableIcon = observableIcon;
		if (this.observableIcon != null) {
			this.setIcon(observableIcon.get());
			this.observableIcon.onChanged().addListener(iconChangeListener);
		}
	}

	public void setTitle(ObservableValue<String> observableTitle) {
		if (this.observableTitle != null)  {
			this.observableTitle.onChanged().removeListener(titleChangeListener);
		}
		this.observableTitle = observableTitle;
		if (this.observableTitle != null) {
			this.setTitle(observableTitle.get());
			this.observableTitle.onChanged().addListener(titleChangeListener);
		}
	}

	public void setLeftHeaderField(ObservableValue<AbstractField<?>> observableLeftHeaderField) {
		if (this.observableLeftHeaderField != null)  {
			this.observableLeftHeaderField.onChanged().removeListener(leftHeaderFieldChangeListener);
		}
		this.observableLeftHeaderField = observableLeftHeaderField;
		if (this.observableLeftHeaderField != null) {
			this.setLeftHeaderField(observableLeftHeaderField.get());
			this.observableLeftHeaderField.onChanged().addListener(leftHeaderFieldChangeListener);
		}
	}

	public void setRightHeaderField(ObservableValue<AbstractField<?>> observableRightHeaderField) {
		if (this.observableRightHeaderField != null)  {
			this.observableRightHeaderField.onChanged().removeListener(rightHeaderFieldChangeListener);
		}
		this.observableRightHeaderField = observableRightHeaderField;
		if (this.observableRightHeaderField != null) {
			this.setRightHeaderField(observableRightHeaderField.get());
			this.observableRightHeaderField.onChanged().addListener(rightHeaderFieldChangeListener);
		}
	}
}
