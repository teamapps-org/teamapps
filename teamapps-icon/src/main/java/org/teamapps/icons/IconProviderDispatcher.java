package org.teamapps.icons;

import org.teamapps.icons.spi.IconProvider;

import java.io.IOException;
import java.nio.file.Files;

public class IconProviderDispatcher implements IconProviderContext {

	private final IconLibraryRegistry iconLibraryRegistry;
	private final IconCache iconCache;

	public IconProviderDispatcher(IconLibraryRegistry iconLibraryRegistry) {
		this.iconLibraryRegistry = iconLibraryRegistry;
		try {
			this.iconCache = new IconCache(Files.createTempDirectory("teamapps-icon-cache").toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public IconProviderDispatcher(IconLibraryRegistry iconLibraryRegistry, IconCache iconCache) {
		this.iconLibraryRegistry = iconLibraryRegistry;
		this.iconCache = iconCache;
	}

	@Override
	public IconResource getIcon(String qualifiedEncodedIcon, int size) {
		if (iconCache != null) {
			IconResource cachedIcon = iconCache.getCachedIcon(qualifiedEncodedIcon, size);
			if (cachedIcon != null) {
				return cachedIcon;
			}
		}

		String libraryName = getLibraryName(qualifiedEncodedIcon);
		String encodedIconString = qualifiedEncodedIcon.substring(libraryName.length() + 1);
		IconProvider iconProvider = iconLibraryRegistry.getIconProvider(libraryName);
		IconResource icon = iconProvider.getIcon(encodedIconString, size, this);

		if (icon.getIconType() == IconType.PNG && icon.getSize() > 0 && icon.getSize() != size) {
			byte[] resizedIconBytes = new PngIconResizer().resizeIcon(icon.getBytes(), size);
			icon = new IconResource(resizedIconBytes, IconType.PNG, size);
		}

		return icon;
	}

	private String getLibraryName(String qualifiedEncodedIcon) {
		int firstDotIndex = qualifiedEncodedIcon.indexOf('.');
		return qualifiedEncodedIcon.substring(0, firstDotIndex);
	}

}
