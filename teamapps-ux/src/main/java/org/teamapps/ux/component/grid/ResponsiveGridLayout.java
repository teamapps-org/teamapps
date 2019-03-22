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
package org.teamapps.ux.component.grid;

import org.jetbrains.annotations.NotNull;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiResponsiveGridLayout;
import org.teamapps.dto.UiResponsiveGridLayoutPolicy;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Container;
import org.teamapps.ux.component.grid.layout.GridLayoutDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponsiveGridLayout extends AbstractComponent implements Container {

	private Map<Integer, GridLayoutDefinition> layoutDefinitionsByMinWidth = new HashMap<>();

	@Override
	public UiComponent createUiComponent() {
		List<UiResponsiveGridLayoutPolicy> layoutPolicies = createUiLayoutPolicies();
		UiResponsiveGridLayout uiResponsiveGridLayout = new UiResponsiveGridLayout(getId(), layoutPolicies);
		mapAbstractUiComponentProperties(uiResponsiveGridLayout);
		return uiResponsiveGridLayout;
	}

	@NotNull
	private List<UiResponsiveGridLayoutPolicy> createUiLayoutPolicies() {
		return layoutDefinitionsByMinWidth.entrySet().stream()
					.map(entry -> new UiResponsiveGridLayoutPolicy(entry.getKey(), entry.getValue().createUiGridLayout()))
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
		queueCommandIfRendered(() -> new UiResponsiveGridLayout.UpdateLayoutPoliciesCommand(getId(), createUiLayoutPolicies()));
	}


	@Override
	public void handleUiEvent(UiEvent event) {
		// none
	}

	@Override
	protected void doDestroy() {
		// nothing to do
	}
}
