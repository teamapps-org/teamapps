package org.teamapps.icon.material;

import org.teamapps.icons.IconEncoderContext;
import org.teamapps.icons.spi.IconEncoder;

public class MaterialIconEncoder implements IconEncoder<MaterialIcon, MaterialIconStyle> {

	@Override
	public String encodeIcon(MaterialIcon icon, IconEncoderContext context) {
		MaterialIconStyle style = icon.getStyle();
		if (style == null) {
			return icon.getIconName();
		} else {
			return icon.getIconName() + "." + style.getStyleType() + "." + String.join(".", style.getColors());
		}
	}

}
