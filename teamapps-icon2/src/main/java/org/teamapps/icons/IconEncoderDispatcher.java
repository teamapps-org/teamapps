package org.teamapps.icons;

import org.teamapps.icons.api.Icon;
import org.teamapps.icons.spi.IconEncoder;

import java.util.HashMap;
import java.util.Map;

public class IconEncoderDispatcher implements IconEncoderContext {

	private final IconLibraryRegistry iconLibraryRegistry;
	private final Map<Class<? extends Icon<?, ?>>, IconEncoder<?, ?>> iconEncodersByIconClass = new HashMap<>();

	public IconEncoderDispatcher(IconLibraryRegistry iconLibraryRegistry) {
		this.iconLibraryRegistry = iconLibraryRegistry;
	}

	@Override
	public String encodeIcon(Icon<?, ?> icon) {
		var encoder = iconEncodersByIconClass.computeIfAbsent((Class) icon.getClass(),
				iconClass -> iconLibraryRegistry.getDefaultIconEncoder((Class) iconClass));
		return iconLibraryRegistry.getLibraryName(icon) + "." + encoder.encodeIcon(icon, this);
	}

	public <I extends Icon<I, ?>> void setIconEncoderForIconClass(Class<I> iconClass, IconEncoder<I, ?> iconEncoder) {
		iconLibraryRegistry.registerIconLibrary(iconClass);
		iconEncodersByIconClass.put(iconClass, iconEncoder);
	}

	public <I extends Icon<I, S>, S> void setDefaultStyleForIconClass(Class<I> iconClass, S defaultStyle) {
		iconLibraryRegistry.registerIconLibrary(iconClass);
		iconEncodersByIconClass.put(iconClass, iconLibraryRegistry.getDefaultIconEncoder(iconClass).withDefaultStyle(defaultStyle));
	}
}
