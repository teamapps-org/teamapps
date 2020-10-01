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
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.VerticalElementAlignment;

import java.util.EnumMap;
import java.util.Map;

import static org.teamapps.ux.component.grid.bootstrap.BootstrapishBreakpoint.*;

public class BootstrapishPlacement {

	private final Component component;

	private final Map<BootstrapishBreakpoint, BootstrapishSizing> sizings = new EnumMap<>(BootstrapishBreakpoint.class);

	private VerticalElementAlignment verticalAlignment = null; // fall back to default defined on row or layout
	private HorizontalElementAlignment horizontalAlignment = null; // fall back to default defined on row or layout

	public BootstrapishPlacement(Component component) {
		sizings.put(XS, new BootstrapishSizing(XS, 0, BootstrapishSizing.COL_SPAN_FULL_WIDTH));
		this.component = component;
	}

	public VerticalElementAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	public BootstrapishPlacement setVerticalAlignment(VerticalElementAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
		return this;
	}

	public HorizontalElementAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public BootstrapishPlacement setHorizontalAlignment(HorizontalElementAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	// =======

	private BootstrapishSizing getOrCreateSizing(BootstrapishBreakpoint breakpoint) {
		BootstrapishSizing sizing = sizings.computeIfAbsent(breakpoint, responsiveBreakpoint -> new BootstrapishSizing(breakpoint));

		BootstrapishSizing smallerSizing = sizings.get(XS);
		for (var i = 1; i < BootstrapishBreakpoint.values().length; i++) {
			BootstrapishSizing nextSizing = sizings.get(BootstrapishBreakpoint.values()[i]);
			if (nextSizing != null) {
				nextSizing.setSmallerSizing(smallerSizing);
				smallerSizing = nextSizing;
			}
		}

		return sizing;
	}

	public BootstrapishPlacement colXs(int colSpan) {
		getOrCreateSizing(XS).setColSpan(colSpan);
		return this;
	}

	public BootstrapishPlacement colSm(int colSpan) {
		getOrCreateSizing(SM).setColSpan(colSpan);
		return this;
	}

	public BootstrapishPlacement colMd(int colSpan) {
		getOrCreateSizing(MD).setColSpan(colSpan);
		return this;
	}

	public BootstrapishPlacement colLg(int colSpan) {
		getOrCreateSizing(LG).setColSpan(colSpan);
		return this;
	}

	public BootstrapishPlacement colXl(int colSpan) {
		getOrCreateSizing(XL).setColSpan(colSpan);
		return this;
	}

	public BootstrapishPlacement offsetXs(int offset) {
		getOrCreateSizing(XS).setOffset(offset);
		return this;
	}

	public BootstrapishPlacement offsetSm(int offset) {
		getOrCreateSizing(SM).setOffset(offset);
		return this;
	}

	public BootstrapishPlacement offsetMd(int offset) {
		getOrCreateSizing(MD).setOffset(offset);
		return this;
	}

	public BootstrapishPlacement offsetLg(int offset) {
		getOrCreateSizing(LG).setOffset(offset);
		return this;
	}

	public BootstrapishPlacement offsetXl(int offset) {
		getOrCreateSizing(XL).setOffset(offset);
		return this;
	}

	// =======

	public Component getComponent() {
		return component;
	}

	public Map<BootstrapishBreakpoint, BootstrapishSizing> getSizings() {
		return sizings;
	}

	public BootstrapishSizing getSizingForBreakPoint(BootstrapishBreakpoint breakpoint) {
		int breakpointOrdinal = breakpoint.ordinal();
		while (breakpointOrdinal > 0) {
			BootstrapishSizing sizing = sizings.get(BootstrapishBreakpoint.values()[breakpointOrdinal]);
			if (sizing != null) {
				return sizing;
			}
			breakpointOrdinal--;
		}
		return sizings.get(XS);
	}

	// =======

	public static class ChainBuilder {
		private final BootstrapishPlacement placement;
		private final BootstrapishRow.ChainBuilder parent;

		public ChainBuilder(Component component, BootstrapishRow.ChainBuilder parent) {
			this.placement = new BootstrapishPlacement(component);
			this.parent = parent;
		}

		public ChainBuilder addPlacement(Component component) {
			parent.addPlacement(placement);
			return parent.addPlacement(component);
		}

		public BootstrapishRow.ChainBuilder addRow() {
			parent.addPlacement(placement);
			return parent.addRow();
		}

		public BootstrapishGridLayout done() {
			parent.addPlacement(placement);
			return parent.done();
		}

		public ChainBuilder colXs(int colSpan) {
			placement.colXs(colSpan);
			return this;
		}

		public ChainBuilder colSm(int colSpan) {
			placement.colSm(colSpan);
			return this;
		}

		public ChainBuilder colMd(int colSpan) {
			placement.colMd(colSpan);
			return this;
		}

		public ChainBuilder colLg(int colSpan) {
			placement.colLg(colSpan);
			return this;
		}

		public ChainBuilder colXl(int colSpan) {
			placement.colXl(colSpan);
			return this;
		}

		public ChainBuilder offsetXs(int offset) {
			placement.offsetXs(offset);
			return this;
		}

		public ChainBuilder offsetSm(int offset) {
			placement.offsetSm(offset);
			return this;
		}

		public ChainBuilder offsetMd(int offset) {
			placement.offsetMd(offset);
			return this;
		}

		public ChainBuilder offsetLg(int offset) {
			placement.offsetLg(offset);
			return this;
		}

		public ChainBuilder offsetXl(int offset) {
			placement.offsetXl(offset);
			return this;
		}

		public ChainBuilder verticalAlignment(VerticalElementAlignment verticalAlignment) {
			placement.setVerticalAlignment(verticalAlignment);
			return this;
		}

		public ChainBuilder horizontalAlignment(HorizontalElementAlignment horizontalAlignment) {
			placement.setHorizontalAlignment(horizontalAlignment);
			return this;
		}


	}
}
