package org.teamapps.icons.spi;

import org.teamapps.icons.IconProviderContext;
import org.teamapps.icons.IconResource;

/**
 * Responsible for providing icons in binary form for encoded icon {@link String}s.
 * The encoded icon Strings are provided by the corresponding {@link IconEncoder}.
 */
public interface IconProvider {

	/**
	 * Provides an icon in binary form for encoded icon {@link String}s.
	 *
	 * Must return an icon, even if the requested size is not available. In this case, a larger or smaller icon should be returned.
	 * (The resizing will be done elsewhere.)
	 * 
	 * @param encodedIconString The encoded icon String as produced by the corresponding {@link IconEncoder}.
	 * @param size The size of the requested icon.
	 * @return The icon in binary form and type of the icon (as {@link IconResource}).
	 */
	IconResource getIcon(String encodedIconString, int size, IconProviderContext context);

}
