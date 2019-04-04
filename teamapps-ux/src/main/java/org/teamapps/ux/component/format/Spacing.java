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
package org.teamapps.ux.component.format;

import org.teamapps.dto.UiSpacing;
import org.teamapps.ux.component.absolutelayout.Length;
import org.teamapps.ux.component.absolutelayout.SizeUnit;

public class Spacing {

	private final Length top;
	private final Length right;
	private final Length bottom;
	private final Length left;

	public Spacing(int value) {
		this(new Length(value, SizeUnit.PIXEL), new Length(value, SizeUnit.PIXEL), new Length(value, SizeUnit.PIXEL), new Length(value, SizeUnit.PIXEL));
	}

	public Spacing(int verticalSpace, int horizontalSpace) {
		this(new Length(verticalSpace, SizeUnit.PIXEL), new Length(horizontalSpace, SizeUnit.PIXEL), new Length(verticalSpace, SizeUnit.PIXEL), new Length(horizontalSpace, SizeUnit.PIXEL));
	}

	public Spacing(int top, int right, int bottom, int left) {
		this(new Length(top, SizeUnit.PIXEL), new Length(right, SizeUnit.PIXEL), new Length(bottom, SizeUnit.PIXEL), new Length(left, SizeUnit.PIXEL));
	}

	public Spacing(Length top, Length right, Length bottom, Length left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	public Length getTop() {
		return top;
	}

	public Length getRight() {
		return right;
	}

	public Length getBottom() {
		return bottom;
	}

	public Length getLeft() {
		return left;
	}

	public UiSpacing createUiSpacing() {
		if (top.getUnit() != SizeUnit.PIXEL
				|| left.getUnit() != SizeUnit.PIXEL
				|| bottom.getUnit() != SizeUnit.PIXEL
				|| right.getUnit() != SizeUnit.PIXEL) {
			throw new IllegalArgumentException("UiSpacing currently only supports pixel values!");
		}
		UiSpacing uiSpacing = new UiSpacing();
		uiSpacing.setTop(top.getSize());
		uiSpacing.setRight(right.getSize());
		uiSpacing.setBottom(bottom.getSize());
		uiSpacing.setLeft(left.getSize());
		return uiSpacing;
	}

	public String toCssString() {
		return top.toCssString() + " " + right.toCssString() + " " + bottom.toCssString() + " " + left.toCssString();
	}
}
