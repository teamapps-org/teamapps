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

import org.teamapps.icons.Icon;
import org.teamapps.projector.dto.DtoNavigationBarButton;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

public class NavigationBarButton {

	public ProjectorEvent<Void> onClick = new ProjectorEvent<>();

	private String clientId;
	private final Template template;
	private final Object data;
	private boolean visible = true;

	private NavigationBar container;

	public NavigationBarButton(Template template, Object data) {
		this.template = template;
		this.data = data;
	}

	public static NavigationBarButton create(Icon<?, ?> icon) {
		return new NavigationBarButton(BaseTemplates.NAVIGATION_BAR_ICON_ONLY, new BaseTemplateRecord<Void>(icon, null));
	}

	public DtoNavigationBarButton createUiNavigationBarButton() {
		DtoNavigationBarButton uiNavigationBarButton = new DtoNavigationBarButton(clientId, template, data);
		uiNavigationBarButton.setVisible(visible);
		return uiNavigationBarButton;
	}

	String getClientId() {
		return clientId;
	}

	/*package-private*/ void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (container != null) {
			container.handleButtonVisibilityChanged(this);
		}
	}

	/*package-private*/ void setContainer(NavigationBar container) {
		this.container = container;
	}
}
