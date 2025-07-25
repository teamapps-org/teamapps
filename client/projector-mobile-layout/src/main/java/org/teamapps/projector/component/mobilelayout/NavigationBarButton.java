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
package org.teamapps.projector.component.mobilelayout;

import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

public class NavigationBarButton<RECORD> {

	public ProjectorEvent<Void> onClick = new ProjectorEvent<>();

	private String clientId;
	private final Template template;
	private final RECORD record;
	private boolean visible = true;

	private NavigationBar container;

	public NavigationBarButton(Template template, RECORD record) {
		this.template = template;
		this.record = record;
	}

	public static NavigationBarButton<BaseTemplateRecord<Void>> create(Icon icon) {
		return new NavigationBarButton<>(BaseTemplates.NAVIGATION_BAR_ICON_ONLY, new BaseTemplateRecord<Void>(icon, null));
	}

	public static <PAYLOAD> NavigationBarButton<BaseTemplateRecord<PAYLOAD>> create(Icon icon, PAYLOAD payload) {
		return new NavigationBarButton<>(BaseTemplates.NAVIGATION_BAR_ICON_ONLY, new BaseTemplateRecord<PAYLOAD>(icon, null, payload));
	}

	public DtoNavigationBarButton createDtoNavigationBarButton() {
		DtoNavigationBarButton uiNavigationBarButton = new DtoNavigationBarButton();
		uiNavigationBarButton.setId(clientId);
		uiNavigationBarButton.setTemplate(template);
		uiNavigationBarButton.setData(record);
		uiNavigationBarButton.setVisible(visible);
		return uiNavigationBarButton;
	}

	public RECORD getRecord() {
		return record;
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
