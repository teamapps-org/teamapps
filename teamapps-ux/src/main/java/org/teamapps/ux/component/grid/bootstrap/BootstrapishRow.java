/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.grid.bootstrap;

import org.teamapps.ux.component.Component;

import java.util.ArrayList;
import java.util.List;

public class BootstrapishRow {

	private final BootstrapishGridLayout layout;
	private List<BootstrapishPlacement> placements = new ArrayList<>();

	public BootstrapishRow(BootstrapishGridLayout layout) {
		this.layout = layout;
	}

	public BootstrapishRow addPlacement(BootstrapishPlacement placement) {
		placements.add(placement);
		placement.getComponent().setParent(layout);
		return this;
	}

	public List<BootstrapishPlacement> getPlacements() {
		return placements;
	}

	public void setPlacements(List<BootstrapishPlacement> placements) {
		this.placements = placements;
	}

	public static class ChainBuilder {
		private final BootstrapishGridLayout parent;
		private final BootstrapishRow row;

		public ChainBuilder(BootstrapishGridLayout parent) {
			this.parent = parent;
			this.row = new BootstrapishRow(parent);
		}

		public BootstrapishPlacement.ChainBuilder addPlacement(Component component) {
			return new BootstrapishPlacement.ChainBuilder(component, this);
		}

		/*package-private*/ void addPlacement(BootstrapishPlacement placement) {
			row.addPlacement(placement);
		}

		public ChainBuilder addRow() {
			parent.addRow(row);
			return parent.addRow();
		}

		public BootstrapishGridLayout done() {
			parent.addRow(row);
			return parent;
		}

	}
}
