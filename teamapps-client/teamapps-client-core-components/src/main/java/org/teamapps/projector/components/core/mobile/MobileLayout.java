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
package org.teamapps.projector.components.core.mobile;

import org.teamapps.projector.dto.DtoComponent;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.dto.DtoMobileLayout;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.components.core.CoreComponentLibrary;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.ux.component.animation.PageTransition;
import org.teamapps.ux.component.toolbar.Toolbar;

import java.util.function.Supplier;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class MobileLayout extends AbstractComponent implements Component {

	protected Toolbar toolbar;
	protected Component content;
	protected NavigationBar navigationBar;

	public MobileLayout() {
		super();
	}

	@Override
	public DtoComponent createConfig() {
		DtoMobileLayout uiMobileLayout = new DtoMobileLayout();
		mapAbstractUiComponentProperties(uiMobileLayout);
		if (content != null) {
			uiMobileLayout.setInitialView(content.createClientReference());
		}
		if (toolbar != null) {
			uiMobileLayout.setToolbar(toolbar.createClientReference());
		}
		uiMobileLayout.setNavigationBar(navigationBar != null ? navigationBar.createClientReference() : null);
		return uiMobileLayout;
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
	}

	public void setContent(Component component) {
		setContent(component, null, 0);
	}

	public void setContent(Component component, PageTransition animation, int animationDuration) {
		if (this.content != component) {
			content = component;
			component.setParent(this);
			getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> new DtoMobileLayout.ShowViewCommand(component.createClientReference(), animation != null ? animation.toUiPageTransition() : null,
					animationDuration)).get(), null);
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
		getClientObjectChannel().sendCommandIfRendered(new DtoMobileLayout.SetNavigationBarCommand(navigationBar != null ? navigationBar.createClientReference() : null), null);
	}

}
