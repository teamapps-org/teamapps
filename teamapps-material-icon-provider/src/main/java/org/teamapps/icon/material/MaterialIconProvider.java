/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import org.apache.commons.io.IOUtils;
import org.teamapps.icons.IconProviderContext;
import org.teamapps.icons.IconResource;
import org.teamapps.icons.IconType;
import org.teamapps.icons.spi.IconProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MaterialIconProvider implements IconProvider {

	@Override
	public IconResource getIcon(String encodedIconString, int size, IconProviderContext context) {
		// <iconName>.<styleType>.color1.color2.color3

		String[] parts = encodedIconString.split("\\.");

		String iconName = parts[0];
		MaterialIconStyleType styleType = MaterialIconStyleType.valueOf(parts[1]);
		List<String> colors = new ArrayList<>();
		for (int i = 2; i < parts.length; i++) {
			if (parts[i].length() > 0) {
				colors.add(parts[i]);
			}
		}
		MaterialIconStyle style = new MaterialIconStyle(styleType, colors.toArray(String[]::new));

		return new IconResource(getSVG(iconName, style), IconType.SVG);
	}

	private byte[] getSVG(String iconName, MaterialIconStyle style) {
		if (!iconName.endsWith(".svg")) {
			iconName += ".svg";
		}

		InputStream inputStream = getClass().getResourceAsStream("/org/teamapps/icon/material/" + style.getStyleType().getPackageName() + "/" + iconName);
		if (inputStream == null) {
			return null;
		}
		try {
			String svg = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
			svg = svg.replace("<desc>add icon - Licensed under Apache License v2.0 (http://www.apache.org/licenses/LICENSE-2.0) - Created with Iconfu.com - Derivative work of Material icons (Copyright Google Inc.)</desc>", "");
			svg = style.applyStyle(svg);
			return svg.getBytes(StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


}
