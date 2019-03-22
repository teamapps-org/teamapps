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

public class Spacing {

	private final int top;
	private final int right;
	private final int bottom;
	private final int left;

	public Spacing(int value) {
		top = value;
		right = value;
		bottom = value;
		left = value;
	}

	public Spacing(int verticalSpace, int horizontalSpace) {
		right = horizontalSpace;
		left = horizontalSpace;
		top = verticalSpace;
		bottom = verticalSpace;
	}

	public Spacing(int top, int right, int bottom, int left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public int getRight() {
		return right;
	}

	public int getBottom() {
		return bottom;
	}

	public int getLeft() {
		return left;
	}

	public UiSpacing createUiSpacing() {
		UiSpacing uiSpacing = new UiSpacing();
		uiSpacing.setTop(top);
		uiSpacing.setRight(right);
		uiSpacing.setBottom(bottom);
		uiSpacing.setLeft(left);
		return uiSpacing;
	}
}
