package org.teamapps.icons.spi;

import org.teamapps.icons.Icon;
import org.teamapps.icons.IconLoaderContext;
import org.teamapps.icons.IconResource;

/**
 * Loads icons, providing them in binary form.
 *
 * @param <ICON>  The icon class this encoder can handle.
 * @param <STYLE> The style class these icons support.
 */
public interface IconLoader<ICON extends Icon<ICON, STYLE>, STYLE> {

	/**
	 * Loads an icon.
	 * <p>
	 * The specified icon MUST have a style set (unless the STYLE type is {@link Void}). Callers need to ensure this!
	 * <p>
	 * Implementations MAY therefore assume the icon's to be styled.
	 *
	 * @param size The size of the requested icon.
	 * @return The icon in binary form and type and size of the icon (as {@link IconResource}), or null, if the icon could not be loaded.
	 */
	IconResource loadIcon(ICON icon, int size, IconLoaderContext context);

}
