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
package org.teamapps.ux.component.grid;

import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoResponsiveGridLayout;
import org.teamapps.dto.DtoResponsiveGridLayoutPolicy;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.grid.layout.GridLayoutDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponsiveGridLayout extends AbstractComponent {

	private final Map<Integer, GridLayoutDefinition> layoutDefinitionsByMinWidth = new HashMap<>();

	@Override
	public DtoComponent createDto() {
		List<DtoResponsiveGridLayoutPolicy> layoutPolicies = createUiLayoutPolicies();
		DtoResponsiveGridLayout uiResponsiveGridLayout = new DtoResponsiveGridLayout(layoutPolicies);
		mapAbstractUiComponentProperties(uiResponsiveGridLayout);
		return uiResponsiveGridLayout;
	}

	private List<DtoResponsiveGridLayoutPolicy> createUiLayoutPolicies() {
		return layoutDefinitionsByMinWidth.entrySet().stream()
					.map(entry -> new DtoResponsiveGridLayoutPolicy(entry.getKey(), entry.getValue().createUiGridLayout()))
					.collect(Collectors.toList());
	}

	public void addLayoutPolicy(int minWidth, GridLayoutDefinition layout) {
		this.layoutDefinitionsByMinWidth.put(minWidth, layout);
		layout.getPlacements().stream()
				.flatMap(placement -> placement.getComponents().stream())
				.forEach(component -> component.setParent(this));
		refreshLayout();
	}

	public void refreshLayout() {
		sendCommandIfRendered(() -> new DtoResponsiveGridLayout.UpdateLayoutPoliciesCommand(createUiLayoutPolicies()));
	}


	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		// none
	}

}
