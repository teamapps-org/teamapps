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

import java.util.Objects;

/**
 * One container-width rule of a {@link DynamicForm}.
 * Columns are logical field columns. Beside-label layouts create two physical
 * CSS grid columns per logical column.
 */
public final class DynamicFormLayout {

	public enum LabelPosition {
		ABOVE,
		BESIDE
	}

	private final int minWidth;
	private final int fieldColumns;
	private final LabelPosition labelPosition;

	public DynamicFormLayout(int minWidth, int fieldColumns, LabelPosition labelPosition) {
		if (minWidth < 0) {
			throw new IllegalArgumentException("minWidth must be >= 0");
		}
		if (fieldColumns < 1 || fieldColumns > 12) {
			throw new IllegalArgumentException("fieldColumns must be between 1 and 12");
		}
		this.minWidth = minWidth;
		this.fieldColumns = fieldColumns;
		this.labelPosition = Objects.requireNonNull(labelPosition, "labelPosition");
	}

	public static DynamicFormLayout of(int minWidth, int fieldColumns, LabelPosition labelPosition) {
		return new DynamicFormLayout(minWidth, fieldColumns, labelPosition);
	}

	public static DynamicFormLayout of(DynamicFormLayouts.Breakpoint breakpoint, int fieldColumns,
									   LabelPosition labelPosition) {
		return new DynamicFormLayout(Objects.requireNonNull(breakpoint, "breakpoint").getMinWidth(),
				fieldColumns, labelPosition);
	}

	public int getMinWidth() {
		return minWidth;
	}

	public int getFieldColumns() {
		return fieldColumns;
	}

	public LabelPosition getLabelPosition() {
		return labelPosition;
	}

	public int getGridColumns() {
		return fieldColumns * (labelPosition == LabelPosition.BESIDE ? 2 : 1);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DynamicFormLayout that)) return false;
		return minWidth == that.minWidth && fieldColumns == that.fieldColumns && labelPosition == that.labelPosition;
	}

	@Override
	public int hashCode() {
		return Objects.hash(minWidth, fieldColumns, labelPosition);
	}

	@Override
	public String toString() {
		return "DynamicFormLayout{" + minWidth + "px, " + fieldColumns + " field columns, " + labelPosition + '}';
	}
}
