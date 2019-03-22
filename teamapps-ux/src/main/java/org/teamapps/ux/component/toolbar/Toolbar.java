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
package org.teamapps.ux.component.toolbar;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiToolbar;
import org.teamapps.dto.UiToolbarButtonGroup;

import java.util.List;
import java.util.stream.Collectors;

public class Toolbar extends AbstractToolContainer {

	private String logoImageUrl;

	public Toolbar() {
	}

	@Override
	public UiComponent createUiComponent() {
		List<UiToolbarButtonGroup> uiButtonGroups = toolbarButtonGroups.stream()
				.sorted()
				.map(group -> group.createUiToolbarButtonGroup())
				.collect(Collectors.toList());
		UiToolbar uiToolbar = new UiToolbar(getId(), uiButtonGroups);
		mapAbstractUiComponentProperties(uiToolbar);
		uiToolbar.setLogoImage(logoImageUrl);
		return uiToolbar;
	}

	public String getLogoImageUrl() {
		return logoImageUrl;
	}

	public void setLogoImageUrl(String logoImageUrl) {
		this.logoImageUrl = logoImageUrl;
	}
}
