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
package org.teamapps.ux.component.mobile;

import org.teamapps.dto.UiNavigationBarButton;
import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.template.BaseTemplateRecord;

public class NavigationBarButton<RECORD> {

	public Event<Void> onClick = new Event<>();

	private String clientId;
	private RECORD data;
	private boolean visible = true;

	private NavigationBar<RECORD> container;

	public NavigationBarButton(RECORD data) {
		this.data = data;
	}

	public static NavigationBarButton<BaseTemplateRecord> create(Icon icon) {
		return new NavigationBarButton<>(new BaseTemplateRecord<Void>(icon, null));
	}

	public static NavigationBarButton<BaseTemplateRecord> create(Icon icon, String caption) {
		return new NavigationBarButton<>(new BaseTemplateRecord<>(icon, caption));
	}

	public static <PAYLOAD> NavigationBarButton<BaseTemplateRecord<PAYLOAD>> create(Icon icon, PAYLOAD payload) {
		return new NavigationBarButton<>(new BaseTemplateRecord<PAYLOAD>(icon, null, payload));
	}

	public static <PAYLOAD> NavigationBarButton<BaseTemplateRecord<PAYLOAD>> create(Icon icon, String caption, PAYLOAD payload) {
		return new NavigationBarButton<>(new BaseTemplateRecord<PAYLOAD>(icon, caption, payload));
	}

	public UiNavigationBarButton createUiNavigationBarButton() {
		UiNavigationBarButton uiNavigationBarButton = new UiNavigationBarButton(clientId, data);
		uiNavigationBarButton.setVisible(visible);
		return uiNavigationBarButton;
	}

	String getClientId() {
		return clientId;
	}

	/*package-private*/ void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public RECORD getData() {
		return data;
	}

	public void setData(RECORD data) {
		this.data = data;
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

	/*package-private*/ void setContainer(NavigationBar<RECORD> container) {
		this.container = container;
	}
}
