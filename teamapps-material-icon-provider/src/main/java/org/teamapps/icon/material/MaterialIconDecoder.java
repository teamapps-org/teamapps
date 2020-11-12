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

import org.teamapps.icons.IconDecoderContext;
import org.teamapps.icons.spi.IconDecoder;

import java.util.ArrayList;
import java.util.List;

public class MaterialIconDecoder implements IconDecoder<MaterialIcon, MaterialIconStyle> {

	@Override
	public MaterialIcon decodeIcon(String encodedIconString, IconDecoderContext context) {
		String[] parts = encodedIconString.split("\\.");

		String iconName = parts[0];

		if (parts.length == 1) {
			return MaterialIcon.forName(iconName);
		} else {
			MaterialIconStyleType styleType = MaterialIconStyleType.valueOf(parts[1]);
			List<String> colors = new ArrayList<>();
			for (int i = 2; i < parts.length; i++) {
				if (parts[i].length() > 0) {
					colors.add(parts[i]);
				}
			}
			MaterialIconStyle style = new MaterialIconStyle(styleType, colors.toArray(String[]::new));

			return MaterialIcon.forName(iconName).withStyle(style);
		}
	}

}
