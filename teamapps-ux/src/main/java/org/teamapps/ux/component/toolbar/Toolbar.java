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
package org.teamapps.ux.component.toolbar;

import org.teamapps.projector.dto.DtoComponent;
import org.teamapps.projector.dto.DtoToolbar;
import org.teamapps.projector.dto.DtoToolbarButtonGroup;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.projector.clientobject.ProjectorComponent;

import java.util.List;
import java.util.stream.Collectors;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class Toolbar extends AbstractToolContainer {

	private String logoImageUrl;

	public Toolbar() {
	}

	@Override
	public DtoComponent createConfig() {
		List<DtoToolbarButtonGroup> leftUiButtonGroups = buttonGroups.stream()
				.filter(group -> !group.isRightSide())
				.sorted()
				.map(group -> group.createUiToolbarButtonGroup())
				.collect(Collectors.toList());
		List<DtoToolbarButtonGroup> rightUiButtonGroups = buttonGroups.stream()
				.filter(group -> group.isRightSide())
				.sorted()
				.map(group -> group.createUiToolbarButtonGroup())
				.collect(Collectors.toList());
		DtoToolbar uiToolbar = new DtoToolbar(leftUiButtonGroups, rightUiButtonGroups);
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
