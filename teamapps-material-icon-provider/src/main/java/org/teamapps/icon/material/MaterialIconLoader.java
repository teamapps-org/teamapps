package org.teamapps.icon.material;

import org.apache.commons.io.IOUtils;
import org.teamapps.icons.IconLoaderContext;
import org.teamapps.icons.IconResource;
import org.teamapps.icons.IconType;
import org.teamapps.icons.spi.IconLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MaterialIconLoader implements IconLoader<MaterialIcon, MaterialIconStyle> {

	@Override
	public IconResource loadIcon(MaterialIcon icon, int size, IconLoaderContext context) {
		return new IconResource(getSVG(icon.getIconName(), icon.getStyle()), IconType.SVG);
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
