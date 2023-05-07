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
package org.teamapps.ux.format;

import org.teamapps.common.format.RgbaColor;
import org.teamapps.common.format.Color;
import org.teamapps.dto.DtoLine;

public class Line {

	private Color color = RgbaColor.BLACK;
	private LineType type = LineType.SOLID;
	private float thickness = 1;

	public Line(Color color) {
		this.color = color;
	}

	public Line(Color color, LineType type, float thickness) {
		this.color = color;
		this.type = type;
		this.thickness = thickness;
	}

	public float getThickness() {
		return thickness;
	}

	public LineType getType() {
		return type;
	}

	public Color getColor() {
		return color;
	}

	public DtoLine createUiLine() {
		DtoLine dtoLine = new DtoLine();
		dtoLine.setThickness(thickness);
		dtoLine.setColor(color!= null ? color.toHtmlColorString() : null);
		dtoLine.setType(type != null ? type.createUiLineType() : null);
		return dtoLine;
	}
}
