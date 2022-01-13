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

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiToolAccordion;
import org.teamapps.dto.UiToolbarButtonGroup;

import java.util.List;
import java.util.stream.Collectors;

public class ToolAccordion extends AbstractToolContainer {

	public ToolAccordion() {
	}

	@Override
	public UiComponent createUiComponent() {
		List<UiToolbarButtonGroup> leftUiButtonGroups = buttonGroups.stream()
				.filter(group -> !group.isRightSide())
				.sorted()
				.map(group -> group.createUiToolbarButtonGroup())
				.collect(Collectors.toList());
		List<UiToolbarButtonGroup> rightUiButtonGroups = buttonGroups.stream()
				.filter(group -> group.isRightSide())
				.sorted()
				.map(group -> group.createUiToolbarButtonGroup())
				.collect(Collectors.toList());
		UiToolAccordion uiToolAccordion = new UiToolAccordion(leftUiButtonGroups, rightUiButtonGroups);
		mapAbstractUiComponentProperties(uiToolAccordion);
		return uiToolAccordion;
	}

}
