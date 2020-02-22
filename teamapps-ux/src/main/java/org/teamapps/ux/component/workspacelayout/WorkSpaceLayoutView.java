/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.ux.component.workspacelayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.UiClientObjectReference;
import org.teamapps.dto.UiWorkSpaceLayoutView;
import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.panel.WindowButtonType;
import org.teamapps.ux.component.splitpane.SplitSizePolicy;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.workspacelayout.definition.ViewDefinition;
import org.teamapps.ux.session.SessionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WorkSpaceLayoutView {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkSpaceLayout.class);

	private WorkSpaceLayout workSpaceLayout;
	private Event<Void> onRemoved = new Event<>();

	private final String id = UUID.randomUUID().toString();
	private final Panel panel;
	private WorkSpaceLayoutViewGroup viewGroup;

	private final boolean lazyLoading;
	private boolean closeable;
	private String tabTitle;
	private boolean visible = true;

	public WorkSpaceLayoutView(WorkSpaceLayout workSpaceLayout, Icon icon, String title, Component component, boolean closeable, boolean lazyLoading) {
		this(workSpaceLayout, new Panel(icon, title, component), null, closeable, lazyLoading);
	}

	public WorkSpaceLayoutView(WorkSpaceLayout workSpaceLayout, Panel panel, String tabTitle, boolean closeable, boolean lazyLoading) {
		this.workSpaceLayout = workSpaceLayout;
		this.panel = panel;
		panel.setParent(workSpaceLayout);
		this.tabTitle = tabTitle;
		this.closeable = closeable;
		this.lazyLoading = lazyLoading;
	}

	public WorkSpaceLayoutView(WorkSpaceLayout workSpaceLayout, Icon icon, String title, Component component) {
		this(workSpaceLayout, icon, title, component, true, false);
	}

	public ViewDefinition createViewDefinition() {
		return new ViewDefinition(id, closeable, visible); // TODO visible
	}

	public void setRelativeWidth(float width) {
		setSize(width, false, true);
	}

	public void setAbsoluteWidth(int width) {
		setSize(width, false, false);
	}

	public void setRelativeHeight(float height) {
		setSize(height, true, true);
	}

	public void setAbsoluteHeight(int height) {
		setSize(height, true, false);
	}

	private void setSize(float size, boolean isHeight, boolean isRelative) {
		if (viewGroup != null) {
			WorkSpaceLayoutItem currentItem = viewGroup;
			WorkSpaceLayoutSplitPane splitPane = viewGroup.getParent();
			while (splitPane != null) {
				boolean isFirstChild = splitPane.getFirstChild() == currentItem;
				if (splitPane.getSplitDirection() == (isHeight ? SplitDirection.HORIZONTAL : SplitDirection.VERTICAL)) {
					if (isRelative) {
						splitPane.setSizing(SplitSizePolicy.RELATIVE, isFirstChild ? size : 1 - size);
					} else {
						splitPane.setSizing(isFirstChild ? SplitSizePolicy.FIRST_FIXED : SplitSizePolicy.LAST_FIXED, size);
					}
					break;
				}
				currentItem = splitPane;
				splitPane = splitPane.getParent();
			}
		}
	}

	private SessionContext getSessionContext() {
		return workSpaceLayout.getSessionContext();
	}

	public UiWorkSpaceLayoutView createUiView() {
		String icon = getSessionContext().resolveIcon(panel.getIcon());
		String title = null;
		if (tabTitle != null) {
			title = tabTitle;
		} else {
			title = panel.getTitle();
		}

		UiClientObjectReference uiPanel = null;
		if (!lazyLoading) {
			uiPanel = panel.createUiReference();
		}
		UiWorkSpaceLayoutView view = new UiWorkSpaceLayoutView(getId(), icon, title, uiPanel);
		view.setTabCloseable(closeable);
		view.setLazyLoading(lazyLoading);
		view.setVisible(visible);
		return view;
	}

	public Component getComponent() {
		return panel.getContent();
	}

	public void setComponent(Component component) {
		panel.setContent(component);
	}

	public Icon getIcon() {
		return panel.getIcon();
	}

	public void setIcon(Icon icon) {
		panel.setIcon(icon);
	}

	public String getTabTitle() {
		return tabTitle != null ? tabTitle : panel.getTitle();
	}

	public String getPanelTitle() {
		return panel.getTitle();
	}

	public void setPanelTitle(String title) {
		panel.setTitle(title);
	}

	public Toolbar getToolbar() {
		return panel.getToolbar();
	}

	public void setToolbar(Toolbar toolbar) {
		panel.setToolbar(toolbar);
	}

	public WorkSpaceLayoutView setLeftHeaderField(AbstractField<?> field, Icon icon, int minWidth, int maxWidth) {
		panel.setLeftHeaderField(field, icon, minWidth, maxWidth);
		return this;
	}

	public AbstractField<?> getLeftHeaderField() {
		return panel.getLeftHeaderField();
	}

	public WorkSpaceLayoutView setRightHeaderField(AbstractField<?> field, Icon icon, int minWidth, int maxWidth) {
		panel.setRightHeaderField(field, icon, minWidth, maxWidth);
		return this;
	}

	public AbstractField<?> getRightHeaderField() {
		return panel.getRightHeaderField();
	}

	public Panel getPanel() {
		return panel;
	}

	/*package-private*/ String getId() {
		return id;
	}

	public void select() {
		if (this.viewGroup != null) {
			this.viewGroup.setSelectedView(this);
		}
	}

	public void remove() {
		if (viewGroup != null) {
			this.viewGroup.removeView(this);
		}
	}

	/*package-private*/ void fireOnRemoved() {
		this.onRemoved.fire(null);
	}

	public WorkSpaceLayoutViewGroup getViewGroup() {
		return viewGroup;
	}

	/*package-private*/ void setViewGroup(WorkSpaceLayoutViewGroup viewGroup) {
		this.viewGroup = viewGroup;
	}

	public boolean isSelected() {
		return viewGroup != null && viewGroup.getSelectedView() == this;
	}

	public boolean isCloseable() {
		return closeable;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setTabTitle(String tabTitle) {
		this.tabTitle = tabTitle;
		if (workSpaceLayout != null) {
			workSpaceLayout.handleViewAttributeChangedViaApi(this);
		}
	}

	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
		if (workSpaceLayout != null) {
			workSpaceLayout.handleViewAttributeChangedViaApi(this);
		}
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		this.workSpaceLayout.handleViewAttributeChangedViaApi(this);
	}

	public void updateWindowButtons(boolean showButtons) {
		if (!showButtons) {
			this.panel.setWindowButtons(Collections.emptyList());
		} else {
			List<WindowButtonType> buttons = new ArrayList<>();
			buttons.add(WindowButtonType.MINIMIZE);
			buttons.add(WindowButtonType.MAXIMIZE_RESTORE);
			if (closeable) {
				buttons.add(WindowButtonType.CLOSE);
			}
			this.panel.setWindowButtons(buttons);
		}
	}
}
