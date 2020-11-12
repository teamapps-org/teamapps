package org.teamapps.icons;

import org.teamapps.icons.spi.IconDecoder;
import org.teamapps.icons.spi.IconEncoder;
import org.teamapps.icons.spi.IconLoader;

import java.io.IOException;
import java.nio.file.Files;

public class IconProvider implements IconLoaderContext, IconDecoderContext {

	private final IconLibraryRegistry iconLibraryRegistry;
	private final IconCache iconCache;

	public IconProvider(IconLibraryRegistry iconLibraryRegistry) {
		this.iconLibraryRegistry = iconLibraryRegistry;
		try {
			this.iconCache = new IconCache(Files.createTempDirectory("teamapps-icon-cache").toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public IconProvider(IconLibraryRegistry iconLibraryRegistry, IconCache iconCache) {
		this.iconLibraryRegistry = iconLibraryRegistry;
		this.iconCache = iconCache;
	}

	@Override
	public Icon<?, ?> decodeIcon(String qualifiedEncodedIcon) {
		String libraryName = getLibraryName(qualifiedEncodedIcon);
		String encodedIconString = qualifiedEncodedIcon.substring(libraryName.length() + 1);
		IconDecoder iconDecoder = iconLibraryRegistry.getIconDecoder(libraryName);
		return iconDecoder.decodeIcon(encodedIconString, this);
	}

	public IconResource loadIcon(String qualifiedEncodedIcon, int size) {
		if (iconCache != null) {
			IconResource cachedIcon = iconCache.getCachedIcon(qualifiedEncodedIcon, size);
			if (cachedIcon != null) {
				return cachedIcon;
			}
		}

		String libraryName = getLibraryName(qualifiedEncodedIcon);
		String encodedIconString = qualifiedEncodedIcon.substring(libraryName.length() + 1);
		IconDecoder iconDecoder = iconLibraryRegistry.getIconDecoder(libraryName);
		Icon icon = iconDecoder.decodeIcon(encodedIconString, this);

		IconResource iconResource = loadIconWithoutCaching(icon, size);
		if (iconResource == null) {
			return null;
		}

		if (iconCache != null) {
			iconCache.putIcon(qualifiedEncodedIcon, size, iconResource);
		}

		return iconResource;
	}

	@Override
	public IconResource loadIcon(Icon<?, ?> icon, int size) {
		return loadIconWithoutCaching(icon, size);
	}

	private IconResource loadIconWithoutCaching(Icon<?, ?> icon, int size) {
		String libraryName = getLibraryName(icon);
		IconLoader iconLoader = iconLibraryRegistry.getIconLoader(libraryName);
		IconResource iconResource = iconLoader.loadIcon(icon, size, this);
		if (iconResource == null) {
			return null;
		}

		if (iconResource.getIconType() == IconType.PNG && iconResource.getSize() > 0 && iconResource.getSize() != size) {
			byte[] resizedIconBytes = new PngIconResizer().resizeIcon(iconResource.getBytes(), size);
			iconResource = new IconResource(resizedIconBytes, IconType.PNG, size);
		}
		return iconResource;
	}

	public <I extends Icon<I, S>, S> IconEncoder<I, S> getIconEncoder(Class<I> iconClass) {
		return iconLibraryRegistry.getIconEncoder(iconClass);
	}

	public <I extends Icon<I, S>, S> String getLibraryName(Icon icon) {
		return iconLibraryRegistry.getLibraryName(icon);
	}

	public <I extends Icon<I, S>, S> void registerIconLibrary(Class<I> iconClass) {
		iconLibraryRegistry.registerIconLibrary(iconClass);
	}

	public <I extends Icon<I, S>, S> void registerIconLibrary(Class<I> iconClass, String libraryName, IconEncoder<I, S> iconEncoder, IconDecoder<I, S> iconDecoder, IconLoader<I, S> iconLoader, S defaultStyle) {
		iconLibraryRegistry.registerIconLibrary(iconClass, libraryName, iconEncoder, iconDecoder, iconLoader, defaultStyle);
	}

	public <I extends Icon<I, S>, S> S getDefaultStyle(Class<I> iconClass) {
		return iconLibraryRegistry.getDefaultStyle(iconClass);
	}

	private String getLibraryName(String qualifiedEncodedIcon) {
		int firstDotIndex = qualifiedEncodedIcon.indexOf('.');
		return qualifiedEncodedIcon.substring(0, firstDotIndex);
	}
}
