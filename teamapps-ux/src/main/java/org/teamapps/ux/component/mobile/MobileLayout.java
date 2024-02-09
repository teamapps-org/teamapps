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

import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoMobileLayout;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.annotations.ProjectorComponent;
import org.teamapps.ux.component.animation.PageTransition;
import org.teamapps.ux.component.toolbar.Toolbar;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class MobileLayout extends AbstractComponent implements org.teamapps.ux.component.Component {

	protected Toolbar toolbar;
	protected org.teamapps.ux.component.Component content;
	protected NavigationBar navigationBar;

	public MobileLayout() {
		super();
	}

	@Override
	public DtoComponent createDto() {
		DtoMobileLayout uiMobileLayout = new DtoMobileLayout();
		mapAbstractUiComponentProperties(uiMobileLayout);
		if (content != null) {
			uiMobileLayout.setInitialView(content.createDtoReference());
		}
		if (toolbar != null) {
			uiMobileLayout.setToolbar(toolbar.createDtoReference());
		}
		uiMobileLayout.setNavigationBar(navigationBar != null ? navigationBar.createDtoReference() : null);
		return uiMobileLayout;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
	}

	public void setContent(org.teamapps.ux.component.Component component) {
		setContent(component, null, 0);
	}

	public void setContent(org.teamapps.ux.component.Component component, PageTransition animation, int animationDuration) {
		if (this.content != component) {
			content = component;
			component.setParent(this);
			sendCommandIfRendered(() -> new DtoMobileLayout.ShowViewCommand(component.createDtoReference(), animation != null ? animation.toUiPageTransition() : null,
					animationDuration));
		}
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
	}

	public org.teamapps.ux.component.Component getContent() {
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
		sendCommandIfRendered(() -> new DtoMobileLayout.SetNavigationBarCommand(navigationBar != null ? navigationBar.createDtoReference() : null));
	}

}
