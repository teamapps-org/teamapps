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
package org.teamapps.projector.components.core.panel;

import org.teamapps.common.format.Color;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.projector.dto.DtoComponent;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.dto.DtoPanel;
import org.teamapps.projector.dto.DtoPanelHeaderField;
import org.teamapps.event.Disposable;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.clientobject.ClientObject;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.components.core.CoreComponentLibrary;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.toolbutton.ToolButton;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class Panel extends AbstractComponent implements Component {

	public final ProjectorEvent<WindowButtonType> onWindowButtonClicked = createProjectorEventBoundToUiEvent(DtoPanel.WindowButtonClickedEvent.TYPE_ID);

	private String title;
	private Icon<?, ?> icon;

	private AbstractField<?> leftHeaderField;
	private Icon<?, ?> leftHeaderFieldIcon;
	private int leftHeaderFieldMinWidth = 100;
	private int leftHeaderFieldMaxWidth = 300;
	private AbstractField<?> rightHeaderField;
	private Icon<?, ?> rightHeaderFieldIcon;
	private int rightHeaderFieldMinWidth = 100;
	private int rightHeaderFieldMaxWidth = 300;
	private HeaderComponentMinimizationPolicy headerComponentMinimizationPolicy = HeaderComponentMinimizationPolicy.LEFT_COMPONENT_FIRST;
	private HeaderFieldIconVisibilityPolicy headerFieldIconVisibilityPolicy = HeaderFieldIconVisibilityPolicy.DISPLAYED_WHEN_MINIMIZED;

	private Component content;
	private boolean contentStretchingEnabled = true;

	private boolean titleBarHidden;
	private Toolbar toolbar;
	private int padding = 0;
	private final List<ToolButton> toolButtons = new ArrayList<>();
	private final Set<WindowButtonType> windowButtons = new HashSet<>();

	private Disposable iconChangeListenerDisposable;
	private Disposable titleChangeListenerDisposable;
	private Disposable leftHeaderFieldChangeListenerDisposable;
	private Disposable rightHeaderFieldChangeListenerDisposable;


	public Panel() {
		this(null, null, null);
	}

	public Panel(Icon<?, ?> icon, String title) {
		this(icon, title, null);
	}

	public Panel(Icon<?, ?> icon, String title, Component content) {
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
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> new DtoPanel.SetToolButtonsCommand(this.toolButtons.stream()
				.map(toolButton -> toolButton.createClientReference())
				.collect(Collectors.toList()))).get(), null);
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
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> new DtoPanel.SetWindowButtonsCommand(this.windowButtons.stream()
				.map(b -> b.toUiWindowButtonType())
				.collect(Collectors.toList()))).get(), null);
	}

	public Set<WindowButtonType> getWindowButtons() {
		return Collections.unmodifiableSet(windowButtons);
	}

	@Override
	public DtoComponent createConfig() {
		DtoPanel uiPanel = new DtoPanel();
		mapUiPanelProperties(uiPanel);
		return uiPanel;
	}

	protected void mapUiPanelProperties(DtoPanel uiPanel) {
		mapAbstractUiComponentProperties(uiPanel);
		uiPanel.setTitle(title);
		uiPanel.setIcon(getSessionContext().resolveIcon(icon));
		uiPanel.setLeftHeaderField(createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth));
		uiPanel.setRightHeaderField(createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth, rightHeaderFieldMaxWidth));
		uiPanel.setHeaderComponentMinimizationPolicy(headerComponentMinimizationPolicy.toDto());
		uiPanel.setTitleBarHidden(titleBarHidden);
		uiPanel.setToolbar(ClientObject.createClientReference(toolbar));
		uiPanel.setContent(content != null ? content.createClientReference() : null);
		uiPanel.setPadding(padding);
		uiPanel.setWindowButtons(windowButtons.stream()
				.map(b -> b.toUiWindowButtonType()).collect(Collectors.toList()));
		uiPanel.setToolButtons(toolButtons.stream()
				.map(toolButton -> toolButton.createClientReference())
				.collect(Collectors.toList()));
		uiPanel.setHeaderFieldIconVisibilityPolicy(headerFieldIconVisibilityPolicy.toDto());
		uiPanel.setContentStretchingEnabled(contentStretchingEnabled);
	}

	public DtoPanelHeaderField createUiPanelHeaderField(AbstractField<?> field, Icon<?, ?> icon, int minWidth, int maxWidth) {
		if (field == null) {
			return null;
		}
		DtoPanelHeaderField uiPanelHeaderField = new DtoPanelHeaderField(field.createClientReference());
		uiPanelHeaderField.setIcon(getSessionContext().resolveIcon(icon));
		uiPanelHeaderField.setMinWidth(minWidth);
		uiPanelHeaderField.setMaxWidth(maxWidth);
		return uiPanelHeaderField;
	}

	public Panel setLeftHeaderField(AbstractField<?> field, Icon<?, ?> icon, int minWidth, int maxWidth) {
		if (field != null) {
			field.setParent(this);
		}
		this.leftHeaderField = field;
		this.leftHeaderFieldIcon = icon;
		this.leftHeaderFieldMinWidth = minWidth;
		this.leftHeaderFieldMaxWidth = maxWidth;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetLeftHeaderFieldCommand(createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth)), null);
		return this;
	}

	public AbstractField<?> getLeftHeaderField() {
		return leftHeaderField;
	}

	public Panel setRightHeaderField(AbstractField<?> field, Icon<?, ?> icon, int minWidth, int maxWidth) {
		if (field != null) {
			field.setParent(this);
		}
		this.rightHeaderField = field;
		this.rightHeaderFieldIcon = icon;
		this.rightHeaderFieldMinWidth = minWidth;
		this.rightHeaderFieldMaxWidth = maxWidth;
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> new DtoPanel.SetRightHeaderFieldCommand(createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth,
				rightHeaderFieldMaxWidth))).get(), null);
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
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetContentCommand(content != null ? content.createClientReference() : null), null);
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		switch (event.getTypeId()) {
			case DtoPanel.WindowButtonClickedEvent.TYPE_ID -> {
				var clickedEvent = event.as(DtoPanel.WindowButtonClickedEventWrapper.class);
				this.onWindowButtonClicked.fire(WindowButtonType.fromUiWindowButtonType(clickedEvent.getWindowButton()));
			}
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetTitleCommand(title), null);
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public void setIcon(Icon<?, ?> icon) {
		this.icon = icon;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetIconCommand(getSessionContext().resolveIcon(icon)), null);
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
		this.setCssStyle("> .panel-body", "background-color", bodyBackgroundColor != null ? bodyBackgroundColor.toHtmlColorString() : null);
	}

	public HeaderComponentMinimizationPolicy getHeaderComponentMinimizationPolicy() {
		return headerComponentMinimizationPolicy;
	}

	public void setHeaderComponentMinimizationPolicy(HeaderComponentMinimizationPolicy headerComponentMinimizationPolicy) {
		this.headerComponentMinimizationPolicy = headerComponentMinimizationPolicy;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetHeaderComponentMinimizationPolicyCommand(headerComponentMinimizationPolicy.toDto()), null);
	}

	public boolean isTitleBarHidden() {
		return titleBarHidden;
	}

	public void setTitleBarHidden(boolean titleBarHidden) {
		this.titleBarHidden = titleBarHidden;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetTitleBarHiddenCommand(this.titleBarHidden), null);
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetToolbarCommand(toolbar.createClientReference()), null);
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetPaddingCommand(this.padding), null);
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
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetLeftHeaderFieldCommand(createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth)), null);
	}

	public Icon<?, ?> getLeftHeaderFieldIcon() {
		return leftHeaderFieldIcon;
	}

	public void setLeftHeaderFieldIcon(Icon<?, ?> leftHeaderFieldIcon) {
		this.leftHeaderFieldIcon = leftHeaderFieldIcon;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetLeftHeaderFieldCommand(createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth)), null);
	}

	public int getLeftHeaderFieldMinWidth() {
		return leftHeaderFieldMinWidth;
	}

	public void setLeftHeaderFieldMinWidth(int leftHeaderFieldMinWidth) {
		this.leftHeaderFieldMinWidth = leftHeaderFieldMinWidth;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetLeftHeaderFieldCommand(createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth)), null);
	}

	public int getLeftHeaderFieldMaxWidth() {
		return leftHeaderFieldMaxWidth;
	}

	public void setLeftHeaderFieldMaxWidth(int leftHeaderFieldMaxWidth) {
		this.leftHeaderFieldMaxWidth = leftHeaderFieldMaxWidth;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetLeftHeaderFieldCommand(createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth)), null);
	}

	public void setRightHeaderField(AbstractField<?> rightHeaderField) {
		if (this.rightHeaderField != null) {
			this.rightHeaderField.setParent(null);
		}
		this.rightHeaderField = rightHeaderField;
		rightHeaderField.setParent(this);
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> new DtoPanel.SetRightHeaderFieldCommand(createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth,
				rightHeaderFieldMaxWidth))).get(), null);
	}

	public Icon<?, ?> getRightHeaderFieldIcon() {
		return rightHeaderFieldIcon;
	}

	public void setRightHeaderFieldIcon(Icon<?, ?> rightHeaderFieldIcon) {
		this.rightHeaderFieldIcon = rightHeaderFieldIcon;
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> new DtoPanel.SetRightHeaderFieldCommand(createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth,
				rightHeaderFieldMaxWidth))).get(), null);
	}

	public int getRightHeaderFieldMinWidth() {
		return rightHeaderFieldMinWidth;
	}

	public void setRightHeaderFieldMinWidth(int rightHeaderFieldMinWidth) {
		this.rightHeaderFieldMinWidth = rightHeaderFieldMinWidth;
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> new DtoPanel.SetRightHeaderFieldCommand(createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth,
				rightHeaderFieldMaxWidth))).get(), null);
	}

	public int getRightHeaderFieldMaxWidth() {
		return rightHeaderFieldMaxWidth;
	}

	public void setRightHeaderFieldMaxWidth(int rightHeaderFieldMaxWidth) {
		this.rightHeaderFieldMaxWidth = rightHeaderFieldMaxWidth;
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> new DtoPanel.SetRightHeaderFieldCommand(createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth,
				rightHeaderFieldMaxWidth))).get(), null);
	}

	public HeaderFieldIconVisibilityPolicy getHeaderFieldIconVisibilityPolicy() {
		return headerFieldIconVisibilityPolicy;
	}

	public void setHeaderFieldIconVisibilityPolicy(HeaderFieldIconVisibilityPolicy headerFieldIconVisibilityPolicy) {
		this.headerFieldIconVisibilityPolicy = headerFieldIconVisibilityPolicy;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetHeaderFieldIconVisibilityPolicyCommand(this.headerFieldIconVisibilityPolicy.toDto()), null);
	}

	public boolean isContentStretchingEnabled() {
		return contentStretchingEnabled;
	}

	public void setContentStretchingEnabled(boolean contentStretchingEnabled) {
		this.contentStretchingEnabled = contentStretchingEnabled;
		getClientObjectChannel().sendCommandIfRendered(new DtoPanel.SetContentStretchingEnabledCommand(contentStretchingEnabled), null);
	}


	public void setIcon(ObservableValue<Icon<?, ?>> observableIcon) {
		if (iconChangeListenerDisposable != null) {
			iconChangeListenerDisposable.dispose();
		}
		if (observableIcon != null) {
			this.setIcon(observableIcon.get());
			this.iconChangeListenerDisposable = observableIcon.onChanged().addListener(this::setIcon);
		}
	}

	public void setTitle(ObservableValue<String> observableTitle) {
		if (titleChangeListenerDisposable != null) {
			titleChangeListenerDisposable.dispose();
		}
		if (observableTitle != null) {
			this.setTitle(observableTitle.get());
			this.titleChangeListenerDisposable = observableTitle.onChanged().addListener(this::setTitle);
		}
	}

	public void setLeftHeaderField(ObservableValue<AbstractField<?>> observableLeftHeaderField) {
		if (leftHeaderFieldChangeListenerDisposable != null) {
			leftHeaderFieldChangeListenerDisposable.dispose();
		}
		if (observableLeftHeaderField != null) {
			this.setLeftHeaderField(observableLeftHeaderField.get());
			this.leftHeaderFieldChangeListenerDisposable = observableLeftHeaderField.onChanged().addListener(this::setLeftHeaderField);
		}
	}

	public void setRightHeaderField(ObservableValue<AbstractField<?>> observableRightHeaderField) {
		if (rightHeaderFieldChangeListenerDisposable != null) {
			rightHeaderFieldChangeListenerDisposable.dispose();
		}
		if (observableRightHeaderField != null) {
			this.setRightHeaderField(observableRightHeaderField.get());
			this.rightHeaderFieldChangeListenerDisposable = observableRightHeaderField.onChanged().addListener(this::setRightHeaderField);
		}
	}
}
