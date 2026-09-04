/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
package org.teamapps.ux.component.form.dynamic;

import java.util.ArrayList;
import java.util.List;

import static org.teamapps.ux.component.form.dynamic.DynamicFormLayout.LabelPosition.ABOVE;
import static org.teamapps.ux.component.form.dynamic.DynamicFormLayout.LabelPosition.BESIDE;

/** Ready-to-use responsive policies and a small builder for custom breakpoint sets. */
public final class DynamicFormLayouts {

	public enum Breakpoint {
		XS(0), SM(576), MD(768), LG(992), XL(1200), XXL(1400), WIDE(1600), ULTRA(1900);

		private final int minWidth;

		Breakpoint(int minWidth) {
			this.minWidth = minWidth;
		}

		public int getMinWidth() {
			return minWidth;
		}
	}

	private DynamicFormLayouts() {
	}

	/** Mobile stacked, one/two label-field pairs, then three/four top-label columns. */
	public static List<DynamicFormLayout> hybrid() {
		return builder()
				.at(Breakpoint.XS, 1, ABOVE)
				.at(Breakpoint.SM, 1, BESIDE)
				.at(900, 2, BESIDE)
				.at(Breakpoint.XXL, 3, ABOVE)
				.at(Breakpoint.WIDE, 4, ABOVE)
				.build();
	}

	public static List<DynamicFormLayout> standard() {
		return builder().at(Breakpoint.XS, 1, ABOVE).at(Breakpoint.SM, 1, BESIDE).build();
	}

	public static List<DynamicFormLayout> labelFieldPairs() {
		return builder()
				.at(Breakpoint.XS, 1, ABOVE)
				.at(Breakpoint.SM, 1, BESIDE)
				.at(900, 2, BESIDE)
				.at(Breakpoint.XXL, 3, BESIDE)
				.at(Breakpoint.ULTRA, 4, BESIDE)
				.build();
	}

	public static List<DynamicFormLayout> topLabels() {
		return builder()
				.at(Breakpoint.XS, 1, ABOVE)
				.at(Breakpoint.SM, 2, ABOVE)
				.at(Breakpoint.LG, 3, ABOVE)
				.at(Breakpoint.XXL, 4, ABOVE)
				.build();
	}

	public static List<DynamicFormLayout> bootstrapBreakpoints() {
		return builder()
				.at(Breakpoint.XS, 1, ABOVE)
				.at(Breakpoint.SM, 1, BESIDE)
				.at(Breakpoint.MD, 2, ABOVE)
				.at(Breakpoint.LG, 2, BESIDE)
				.at(Breakpoint.XL, 3, ABOVE)
				.at(Breakpoint.XXL, 4, ABOVE)
				.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private final List<DynamicFormLayout> layouts = new ArrayList<>();

		public Builder at(int minWidth, int fieldColumns, DynamicFormLayout.LabelPosition labelPosition) {
			layouts.removeIf(layout -> layout.getMinWidth() == minWidth);
			layouts.add(new DynamicFormLayout(minWidth, fieldColumns, labelPosition));
			return this;
		}

		public Builder at(Breakpoint breakpoint, int fieldColumns, DynamicFormLayout.LabelPosition labelPosition) {
			return at(breakpoint.getMinWidth(), fieldColumns, labelPosition);
		}

		public List<DynamicFormLayout> build() {
			if (layouts.isEmpty()) {
				throw new IllegalStateException("At least one layout is required");
			}
			if (layouts.stream().noneMatch(layout -> layout.getMinWidth() == 0)) {
				throw new IllegalStateException("A layout at minWidth 0 is required");
			}
			return layouts.stream().sorted((left, right) -> Integer.compare(left.getMinWidth(), right.getMinWidth())).toList();
		}
	}
}
