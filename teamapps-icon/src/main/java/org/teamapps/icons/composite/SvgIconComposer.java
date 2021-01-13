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
package org.teamapps.icons.composite;

import java.nio.charset.StandardCharsets;

public class SvgIconComposer {

	public byte[] compose(byte[] baseIcon, byte[] bottomRight, byte[] bottomLeft, byte[] topLeft, byte[] topRight) {
		String svg = createSvg(baseIcon);
		svg = svg.substring(0, svg.lastIndexOf("</svg>"));

		if (bottomRight != null) {
			String svgBottomRight = createSvg(bottomRight).replaceAll("teamapps-color", "teamapps-color-bottom-right");
			svg += "\n" + "<g style=\"transform:translate(50%, 50%) scale(0.5)\">" + svgBottomRight + "</g>\n";
		}

		if (bottomLeft != null) {
			String svgBottomLeft = createSvg(bottomLeft).replaceAll("teamapps-color", "teamapps-color-bottom-left");
			svg += "\n" + "<g style=\"transform:translate(0, 50%) scale(0.5)\">" + svgBottomLeft + "</g>\n";
		}

		if (topLeft != null) {
			String svgBottomRight = createSvg(topLeft).replaceAll("teamapps-color", "teamapps-color-top-left");
			svg += "\n" + "<g style=\"transform:translate(0, 0) scale(0.5)\">" + svgBottomRight + "</g>\n";
		}

		if (topRight != null) {
			String svgTopRight = createSvg(topRight).replaceAll("teamapps-color", "teamapps-color-top-right");
			svg += "\n" + "<g style=\"transform:translate(50%, 0) scale(0.5)\">" + svgTopRight + "</g>\n";
		}

		return (svg + "</svg>").getBytes(StandardCharsets.UTF_8);
	}

	private String createSvg(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}

}
