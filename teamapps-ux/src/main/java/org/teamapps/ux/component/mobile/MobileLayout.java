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
package org.teamapps.ux.component.mobile;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEventWrapper;
import org.teamapps.dto.UiMobileLayout;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;
import org.teamapps.ux.component.animation.PageTransition;
import org.teamapps.ux.component.toolbar.Toolbar;

@TeamAppsComponent(library = CoreComponentLibrary.class)
public class MobileLayout extends AbstractComponent implements Component {

	protected Toolbar toolbar;
	protected Component content;
	protected NavigationBar navigationBar;

	public MobileLayout() {
		super();
	}

	@Override
	public UiComponent createUiClientObject() {
		UiMobileLayout uiMobileLayout = new UiMobileLayout();
		mapAbstractUiComponentProperties(uiMobileLayout);
		if (content != null) {
			uiMobileLayout.setInitialView(content.createUiReference());
		}
		if (toolbar != null) {
			uiMobileLayout.setToolbar(toolbar.createUiReference());
		}
		uiMobileLayout.setNavigationBar(navigationBar != null ? navigationBar.createUiReference() : null);
		return uiMobileLayout;
	}

	@Override
	public void handleUiEvent(UiEventWrapper event) {
	}

	public void preloadView(Component component) {
		component.render();
	}

	public void setContent(Component component) {
		setContent(component, null, 0);
	}

	public void setContent(Component component, PageTransition animation, int animationDuration) {
		if (this.content != component) {
			content = component;
			component.setParent(this);
			sendCommandIfRendered(() -> new UiMobileLayout.ShowViewCommand(component.createUiReference(), animation != null ? animation.toUiPageTransition() : null,
					animationDuration));
		}
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
	}

	public Component getContent() {
		return content;
	}

	public NavigationBar getNavigationBar() {
		return navigationBar;
	}

	public void setNavigationBar(NavigationBar navigationBar) {
		this.navigationBar = navigationBar;
		if (navigationBar != null) {
			navigationBar.setParent(this);
		}
		sendCommandIfRendered(() -> new UiMobileLayout.SetNavigationBarCommand(navigationBar != null ? navigationBar.createUiReference() : null));
	}

}
