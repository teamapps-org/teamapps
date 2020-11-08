package org.teamapps.icons;

import org.teamapps.icons.api.Icon;
import org.teamapps.icons.spi.IconEncoder;

import java.util.HashMap;
import java.util.Map;

public class IconEncoderDispatcher implements IconEncoderContext {

	private final IconLibraryRegistry iconLibraryRegistry;
	private final Map<Class<? extends Icon>, IconEncoder<?>> iconEncodersByIconClass = new HashMap<>();

	public IconEncoderDispatcher(IconLibraryRegistry iconLibraryRegistry) {
		this.iconLibraryRegistry = iconLibraryRegistry;
	}

	@Override
	public String encodeIcon(Icon icon) {
		IconEncoder encoder = iconEncodersByIconClass.computeIfAbsent(icon.getClass(),
				clazz -> iconLibraryRegistry.getDefaultIconEncoder(clazz));
		return iconLibraryRegistry.getLibraryName(icon) + "." + encoder.encodeIcon(icon, this);
	}

	public <I extends Icon> void setIconEncoderForIconClass(Class<I> iconClass, IconEncoder<I> iconEncoder) {
		iconLibraryRegistry.registerIconLibrary(iconClass);
		iconEncodersByIconClass.put(iconClass, iconEncoder);
	}
}
