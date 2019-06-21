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
package org.teamapps.ux.component.mobile;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiComponentReference;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiMobileLayout;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.Container;
import org.teamapps.ux.component.toolbar.Toolbar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MobileLayout extends AbstractComponent implements Container {

	protected Toolbar toolbar;
	protected final Set<Component> views = new HashSet<>();
	protected Component activeComponent;
	protected NavigationBar navigationBar;


	public MobileLayout() {
		super();
	}

	@Override
	public UiComponent createUiComponent() {
		UiMobileLayout uiMobileLayout = new UiMobileLayout();
		mapAbstractUiComponentProperties(uiMobileLayout);
		if (activeComponent != null) {
			uiMobileLayout.setInitialViewId(activeComponent.getId());
		}
		if (toolbar != null) {
			uiMobileLayout.setToolbar(toolbar.createUiComponentReference());
		}
		List<UiComponentReference> uiComponents = views.stream()
				.map(component -> component.createUiComponentReference())
				.collect(Collectors.toList());
		uiMobileLayout.setViews(uiComponents);
		uiMobileLayout.setNavigationBar(navigationBar != null ? navigationBar.createUiComponentReference() : null);
		return uiMobileLayout;
	}

	@Override
	protected void doDestroy() {

	}

	@Override
	public void handleUiEvent(UiEvent event) {
	}

	/**
	 * May be used for client-side performance reasons.
	 */
	// TODO #componentRef still needed after component reference implementation??
	public void preloadView(Component component) {
		views.add(component);
		component.setParent(this);
		queueCommandIfRendered(() -> new UiMobileLayout.AddViewCommand(getId(), component.createUiComponentReference()));
	}

	// TODO rename to setActiveComponent()
	public void showView(Component component, MobileLayoutAnimation animation) {
		activeComponent = component;
		if (!views.contains(activeComponent)) {
			preloadView(component);
		}
		queueCommandIfRendered(() -> new UiMobileLayout.ShowViewCommand(getId(), component.getId(), animation.toUiMobileLayoutAnimation()));
	}

	public void removeView(Component component) {
		if (views.contains(component)) {
			views.remove(component);
			queueCommandIfRendered(() -> new UiMobileLayout.RemoveViewCommand(getId(), component.getId()));
		}
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
	}

	public Component getActiveComponent() {
		return activeComponent;
	}

	public NavigationBar getNavigationBar() {
		return navigationBar;
	}

	public void setNavigationBar(NavigationBar navigationBar) {
		this.navigationBar = navigationBar;
		if (navigationBar != null) {
			navigationBar.setParent(this);
		}
		queueCommandIfRendered(() -> new UiMobileLayout.SetNavigationBarCommand(getId(), navigationBar != null ? navigationBar.createUiComponentReference() : null));
	}

	@Override
	public boolean isChildVisible(Component child) {
		return this.isEffectivelyVisible() && (child == navigationBar || child == activeComponent || child == toolbar);
	}
}
