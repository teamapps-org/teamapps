/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
package org.teamapps.ux.component.absolutelayout;

public class AbsolutePosition {

	private final Length top;
	private final Length right;
	private final Length bottom;
	private final Length left;
	private final Length width;
	private final Length height;
	private final int zIndex;

	public AbsolutePosition(Length top, Length right, Length bottom, Length left, Length width, Length height, int zIndex) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
		this.width = width;
		this.height = height;
		this.zIndex = zIndex;
	}

	public static AbsolutePosition fromPixelTopRightBottomLeft(float top, float right, float bottom, float left, int zIndex) {
		return new AbsolutePosition(new Length(top), new Length(right), new Length(bottom), new Length(left), null, null, zIndex);
	}

	public static AbsolutePosition fromRelativeTopRightBottomLeft(float top, float right, float bottom, float left, int zIndex) {
		return new AbsolutePosition(new Length(top, SizeUnit.PERCENT), new Length(right, SizeUnit.PERCENT), new Length(bottom, SizeUnit.PERCENT), new Length(left, SizeUnit.PERCENT), null, null, zIndex);
	}

	public static AbsolutePosition fromRelativeDimensions(float top, float left, float width, float height, int zIndex) {
		return new AbsolutePosition(new Length(top, SizeUnit.PERCENT), null, null, new Length(left, SizeUnit.PERCENT), new Length(width, SizeUnit.PERCENT), new Length(height, SizeUnit.PERCENT), zIndex);
	}

	public static AbsolutePosition fromRelativeRightBottomDimensions(float right, float bottom, float width, float height, int zIndex) {
		return new AbsolutePosition(null, new Length(right, SizeUnit.PERCENT), new Length(bottom, SizeUnit.PERCENT), null, new Length(width, SizeUnit.PERCENT), new Length(height, SizeUnit.PERCENT), zIndex);
	}

	public static AbsolutePosition fromPixelDimensions(Length top, Length left, Length width, Length height, int zIndex) {
		return new AbsolutePosition(top, null, null, left, width, height, zIndex);
	}

	public static AbsolutePosition fullSizeAsRelativeDimensions(int zIndex) {
		return fromRelativeDimensions(0, 0, 100, 100, 0);
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

	public Length getWidth() {
		return width;
	}

	public Length getHeight() {
		return height;
	}

	public int getZIndex() {
		return zIndex;
	}
}
