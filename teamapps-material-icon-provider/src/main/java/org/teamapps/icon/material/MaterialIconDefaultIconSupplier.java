package org.teamapps.icon.material;

import org.teamapps.icons.spi.DefaultStyleSupplier;

public class MaterialIconDefaultIconSupplier implements DefaultStyleSupplier<MaterialIconStyle> {

	@Override
	public MaterialIconStyle getDefaultStyle() {
		return MaterialIconStyles.PLAIN_SHADOW_BLUE_700;
	}
	
}
