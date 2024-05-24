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
package org.teamapps.projector.components.workspacelayout.definition;

import org.teamapps.projector.components.workspacelayout.WorkSpaceLayout;
import org.teamapps.projector.components.workspacelayout.WorkSpaceLayoutItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class LayoutItemDefinition {
	private final String id;

	public LayoutItemDefinition(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public abstract List<ViewDefinition> getAllViews();

	public Set<String> getAllLayoutPositions() {
		Set<String> positionSet = new HashSet<>();
		for (LayoutItemDefinition itemDefinition : getSelfAndAncestors()) {
			if (itemDefinition instanceof ViewGroupDefinition) {
				String layoutPosition = itemDefinition.getId();
				positionSet.add(layoutPosition);
			}
		}
		return positionSet;
	}

	public boolean containsLayoutPosition(String layoutPosition) {
		return getAllLayoutPositions().contains(layoutPosition);
	}

	public List<ViewDefinition> getAllViews(boolean visible) {
		return getAllViews().stream()
				.filter(viewDefinition -> viewDefinition.isVisible())
				.collect(Collectors.toList());
	}

	public abstract List<LayoutItemDefinition> getSelfAndAncestors();

	public abstract WorkSpaceLayoutItem createHeavyWeightItem(WorkSpaceLayout workSpaceLayout);

}
