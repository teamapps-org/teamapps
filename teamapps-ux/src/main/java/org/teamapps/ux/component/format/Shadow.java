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

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiShadow;

import static org.teamapps.util.UiUtil.createUiColor;

public class Shadow {

	private final float offsetX;
	private final float offsetY;
	private final float blur;
	private final float spread;
	private final Color color;

	public static Shadow withSize(float size) {
		return new Shadow(0, 0, size, 0, Color.BLACK);
	}

	public Shadow(float offsetX, float offsetY, float blur, float spread, Color color) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.blur = blur;
		this.spread = spread;
		this.color = color;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public float getBlur() {
		return blur;
	}

	public float getSpread() {
		return spread;
	}

	public Color getColor() {
		return color;
	}

	public UiShadow createUiShadow() {
		return new UiShadow(color != null ? createUiColor(color) : null, blur)
				.setOffsetX(offsetX)
				.setOffsetY(offsetY)
				.setSpread(spread);
	}

	public String toCssString() {
		return offsetX + "px " + offsetY + "px " + blur + "px " + spread + "px " + color.toHtmlColorString();
	}
}
