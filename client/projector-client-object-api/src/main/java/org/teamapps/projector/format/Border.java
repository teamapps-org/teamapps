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
package org.teamapps.projector.format;

import org.teamapps.projector.common.format.Color;

public class Border {

	private final Line top;
	private final Line left;
	private final Line bottom;
	private final Line right;
	private float borderRadius;

	public Border(Color color, float thickness, float borderRadius) {
		this(new Line(color, LineType.SOLID, thickness));
		this.borderRadius = borderRadius;
	}

	public Border(Line line) {
		this.top = line;
		this.left = line;
		this.bottom = line;
		this.right = line;
	}

	public Border(Line top, Line left, Line bottom, Line right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	public Border(Line top, Line left, Line bottom, Line right, float borderRadius) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.borderRadius = borderRadius;
	}

	public Line getTop() {
		return top;
	}

	public Line getLeft() {
		return left;
	}

	public Line getBottom() {
		return bottom;
	}

	public Line getRight() {
		return right;
	}

	public float getBorderRadius() {
		return borderRadius;
	}

	public Border setBorderRadius(float borderRadius) {
		this.borderRadius = borderRadius;
		return this;
	}

	public DtoBorder createDtoBorder() {
		DtoBorder uiBorder = new DtoBorder();
		uiBorder.setTop(top != null ? top.createDtoLine() : null);
		uiBorder.setLeft(left != null ? left.createDtoLine() : null);
		uiBorder.setBottom(bottom != null ? bottom.createDtoLine() : null);
		uiBorder.setRight(right != null ? right.createDtoLine() : null);
		uiBorder.setBorderRadius(borderRadius);
		return uiBorder;
	}
}
