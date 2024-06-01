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
package org.teamapps.projector.components.essential.mobile;

import org.teamapps.projector.animation.PageTransition;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.components.essential.CoreComponentLibrary;
import org.teamapps.projector.components.essential.dto.DtoMobileLayout;
import org.teamapps.projector.components.essential.dto.DtoMobileLayoutClientObjectChannel;
import org.teamapps.projector.components.essential.dto.DtoMobileLayoutEventHandler;
import org.teamapps.projector.components.essential.toolbar.Toolbar;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class MobileLayout extends AbstractComponent implements DtoMobileLayoutEventHandler {

	private final DtoMobileLayoutClientObjectChannel clientObjectChannel = new DtoMobileLayoutClientObjectChannel(getClientObjectChannel());

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
			uiMobileLayout.setInitialView(content);
		}
		if (toolbar != null) {
			uiMobileLayout.setToolbar(toolbar);
		}
		uiMobileLayout.setNavigationBar(navigationBar != null ? navigationBar : null);
		return uiMobileLayout;
	}

	public void setContent(Component component) {
		setContent(component, null, 0);
	}

	public void setContent(Component component, PageTransition animation, int animationDuration) {
		if (this.content != component) {
			content = component;
			clientObjectChannel.showView(component, animation, animationDuration);
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
		clientObjectChannel.setNavigationBar(navigationBar);
	}

}
