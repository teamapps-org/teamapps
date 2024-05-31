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
import org.teamapps.icons.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.components.core.CoreComponentLibrary;
import org.teamapps.projector.components.core.toolbar.Toolbar;
import org.teamapps.projector.components.core.toolbutton.ToolButton;
import org.teamapps.projector.dto.*;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.field.AbstractField;

import java.util.*;

import static org.teamapps.commons.util.CollectionCastUtil.castList;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class Panel extends AbstractComponent implements DtoPanelEventHandler {

	private final DtoPanelClientObjectChannel clientObjectChannel = new DtoPanelClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<WindowButtonType> onWindowButtonClicked = new ProjectorEvent<>(clientObjectChannel::toggleWindowButtonClickedEvent);

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
		updateToolButtons();
	}

	public void removeToolButton(ToolButton toolButton) {
		this.toolButtons.remove(toolButton);
		updateToolButtons();
	}

	public void setToolButtons(List<ToolButton> toolButtons) {
		this.toolButtons.clear();
		if (toolButtons != null) {
			this.toolButtons.addAll(toolButtons);
		}
		updateToolButtons();
	}

	private void updateToolButtons() {
		clientObjectChannel.setToolButtons(castList(this.toolButtons));
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
		clientObjectChannel.setWindowButtons(List.copyOf(this.windowButtons));
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
		uiPanel.setToolbar(toolbar);
		uiPanel.setContent(content != null ? content : null);
		uiPanel.setPadding(padding);
		uiPanel.setWindowButtons(List.copyOf(windowButtons));
		uiPanel.setToolButtons(castList(toolButtons));
		uiPanel.setHeaderFieldIconVisibilityPolicy(headerFieldIconVisibilityPolicy.toDto());
		uiPanel.setContentStretchingEnabled(contentStretchingEnabled);
	}

	public DtoPanelHeaderField createUiPanelHeaderField(AbstractField<?> field, Icon<?, ?> icon, int minWidth, int maxWidth) {
		if (field == null) {
			return null;
		}
		DtoPanelHeaderField uiPanelHeaderField = new DtoPanelHeaderField(field);
		uiPanelHeaderField.setIcon(getSessionContext().resolveIcon(icon));
		uiPanelHeaderField.setMinWidth(minWidth);
		uiPanelHeaderField.setMaxWidth(maxWidth);
		return uiPanelHeaderField;
	}

	public Panel setLeftHeaderField(AbstractField<?> field, Icon<?, ?> icon, int minWidth, int maxWidth) {
		this.leftHeaderField = field;
		this.leftHeaderFieldIcon = icon;
		this.leftHeaderFieldMinWidth = minWidth;
		this.leftHeaderFieldMaxWidth = maxWidth;
		clientObjectChannel.setLeftHeaderField(createUiPanelHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth));
		return this;
	}

	public AbstractField<?> getLeftHeaderField() {
		return leftHeaderField;
	}

	public Panel setRightHeaderField(AbstractField<?> field, Icon<?, ?> icon, int minWidth, int maxWidth) {
		this.rightHeaderField = field;
		this.rightHeaderFieldIcon = icon;
		this.rightHeaderFieldMinWidth = minWidth;
		this.rightHeaderFieldMaxWidth = maxWidth;
		clientObjectChannel.setRightHeaderField(createUiPanelHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth, rightHeaderFieldMaxWidth));
		return this;
	}

	public AbstractField<?> getRightHeaderField() {
		return rightHeaderField;
	}

	public void setContent(Component content) {
		this.content = content;
		clientObjectChannel.setContent(content);
	}

	@Override
	public void handleEvent(String name, JsonWrapper eventObject) {
		new DtoWindowEventMethodInvoker(this).handleEvent(name, eventObject);
	}

	@Override
	public void handleWindowButtonClicked(WindowButtonType windowButton) {
		this.onWindowButtonClicked.fire(windowButton);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		clientObjectChannel.setTitle(title);
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public void setIcon(Icon<?, ?> icon) {
		this.icon = icon;
		clientObjectChannel.setIcon(getSessionContext().resolveIcon(icon));
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
		clientObjectChannel.setHeaderComponentMinimizationPolicy(headerComponentMinimizationPolicy.toDto());
	}

	public boolean isTitleBarHidden() {
		return titleBarHidden;
	}

	public void setTitleBarHidden(boolean titleBarHidden) {
		this.titleBarHidden = titleBarHidden;
		clientObjectChannel.setTitleBarHidden(this.titleBarHidden);
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
		clientObjectChannel.setToolbar(toolbar);
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
		clientObjectChannel.setPadding(this.padding);
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
		setLeftHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth);
	}

	public Icon<?, ?> getLeftHeaderFieldIcon() {
		return leftHeaderFieldIcon;
	}

	public void setLeftHeaderFieldIcon(Icon<?, ?> leftHeaderFieldIcon) {
		setLeftHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth);
	}

	public int getLeftHeaderFieldMinWidth() {
		return leftHeaderFieldMinWidth;
	}

	public void setLeftHeaderFieldMinWidth(int leftHeaderFieldMinWidth) {
		setLeftHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth);
	}

	public int getLeftHeaderFieldMaxWidth() {
		return leftHeaderFieldMaxWidth;
	}

	public void setLeftHeaderFieldMaxWidth(int leftHeaderFieldMaxWidth) {
		setLeftHeaderField(leftHeaderField, leftHeaderFieldIcon, leftHeaderFieldMinWidth, leftHeaderFieldMaxWidth);
	}

	public void setRightHeaderField(AbstractField<?> rightHeaderField) {
		setRightHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth, rightHeaderFieldMaxWidth);
	}

	public Icon<?, ?> getRightHeaderFieldIcon() {
		return rightHeaderFieldIcon;
	}

	public void setRightHeaderFieldIcon(Icon<?, ?> rightHeaderFieldIcon) {
		setRightHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth, rightHeaderFieldMaxWidth);

	}

	public int getRightHeaderFieldMinWidth() {
		return rightHeaderFieldMinWidth;
	}

	public void setRightHeaderFieldMinWidth(int rightHeaderFieldMinWidth) {
		setRightHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth, rightHeaderFieldMaxWidth);
	}

	public int getRightHeaderFieldMaxWidth() {
		return rightHeaderFieldMaxWidth;
	}

	public void setRightHeaderFieldMaxWidth(int rightHeaderFieldMaxWidth) {
		setRightHeaderField(rightHeaderField, rightHeaderFieldIcon, rightHeaderFieldMinWidth, rightHeaderFieldMaxWidth);
	}

	public HeaderFieldIconVisibilityPolicy getHeaderFieldIconVisibilityPolicy() {
		return headerFieldIconVisibilityPolicy;
	}

	public void setHeaderFieldIconVisibilityPolicy(HeaderFieldIconVisibilityPolicy headerFieldIconVisibilityPolicy) {
		this.headerFieldIconVisibilityPolicy = headerFieldIconVisibilityPolicy;
		clientObjectChannel.setHeaderFieldIconVisibilityPolicy(this.headerFieldIconVisibilityPolicy.toDto());
	}

	public boolean isContentStretchingEnabled() {
		return contentStretchingEnabled;
	}

	public void setContentStretchingEnabled(boolean contentStretchingEnabled) {
		this.contentStretchingEnabled = contentStretchingEnabled;
		clientObjectChannel.setContentStretchingEnabled(contentStretchingEnabled);
	}
}
