/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.icon.material;

import org.teamapps.common.format.Color;

import java.util.Arrays;

public class MaterialIconStyle {

	private final MaterialIconStyleType styleType;
	private final String[] colors;

	public MaterialIconStyle(MaterialIconStyleType styleType, String... colors) {
		this.styleType = styleType;
		this.colors = colors;
	}

	public MaterialIconStyle(MaterialIconStyleType styleType, Color... colors) {
		this.styleType = styleType;
		this.colors = Arrays.stream(colors)
				.map(Color::toHtmlColorString)
				.toArray(String[]::new);
	}

	public MaterialIconStyleType getStyleType() {
		return styleType;
	}

	public String[] getColors() {
		return colors;
	}

	public String applyStyle(String svg) {
		String styleTags = createStyleTags(colors);
		return applyStyle(svg, styleTags);
	}

	private String applyStyle(String svg, String styleTags) {
		int pos = svg.indexOf('>');
		return svg.substring(0, pos + 2) + styleTags + svg.substring(pos + 1);
	}

	private String createStyleTags(String... colors) {
		StringBuilder sb = new StringBuilder("<style>\n");
		for (int i = 1; i <= colors.length; i++) {
			sb.append(".teamapps-color-").append(i).append(" {").append("color:").append(colors[i - 1]).append("}");
		}
		sb.append("\n</style>");
		return sb.toString();
	}
}
