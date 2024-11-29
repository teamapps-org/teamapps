package org.teamapps.icon.material;

import org.apache.commons.io.IOUtils;
import org.teamapps.icons.*;
import org.teamapps.icons.spi.IconLibrary;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MaterialIconLibrary implements IconLibrary<MaterialIcon> {
	@Override
	public String getName() {
		return "material";
	}

	@Override
	public Class<MaterialIcon> getIconClass() {
		return MaterialIcon.class;
	}

	@Override
	public IconStyle<MaterialIcon> getDefaultStyle() {
		return MaterialIconStyles.PLAIN_SHADOW_BLUE_700;
	}

	@Override
	public String encodeIcon(MaterialIcon icon, IconEncoderContext context) {
		MaterialIconStyle style = icon.getStyle();
		if (style == null) {
			return icon.getIconName();
		} else {
			return icon.getIconName() + "." + style.getStyleType() + "." + String.join(".", style.getColors());
		}
	}

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

	@Override
	public IconResource loadIcon(MaterialIcon icon, int size, IconLoaderContext context) {
		return new IconResource(getSVG(icon.getIconName(), icon.getStyle()), IconType.SVG);
	}

	private byte[] getSVG(String iconName, MaterialIconStyle style) {
		if (!iconName.endsWith(".svg")) {
			iconName += ".svg";
		}

		try (InputStream inputStream = getClass().getResourceAsStream("/org/teamapps/icon/material/" + style.getStyleType().getPackageName() + "/" + iconName)) {
			if (inputStream == null) {
				return null;
			}
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
