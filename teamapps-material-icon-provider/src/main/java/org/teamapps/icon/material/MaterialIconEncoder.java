package org.teamapps.icon.material;

import org.teamapps.icons.IconEncoderContext;
import org.teamapps.icons.spi.IconEncoder;

public class MaterialIconEncoder implements IconEncoder<MaterialIcon, MaterialIconStyle> {

	private final MaterialIconStyle defaultStyle;

	public MaterialIconEncoder() {
		this(MaterialIconStyles.PLAIN_SHADOW_BLUE_700);
	}

	public MaterialIconEncoder(MaterialIconStyle defaultStyle) {
		this.defaultStyle = defaultStyle;
	}

	@Override
	public MaterialIconEncoder withDefaultStyle(MaterialIconStyle style) {
		return new MaterialIconEncoder(style);
	}

	public MaterialIconStyle getDefaultStyle() {
		return defaultStyle;
	}

	@Override
	public String encodeIcon(MaterialIcon icon, IconEncoderContext context) {
		MaterialIconStyle style = icon.getStyle() != null ? icon.getStyle() : defaultStyle;
		return icon.getIconName() + "." + style.getStyleType() + "." + String.join(".", style.getColors());
	}

}
