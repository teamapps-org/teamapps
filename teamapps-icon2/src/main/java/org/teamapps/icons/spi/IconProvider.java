package org.teamapps.icons.spi;

import org.teamapps.icons.IconProviderContext;
import org.teamapps.icons.IconResource;

public interface IconProvider {

	/**
	 * Must return an icon, even if the requested size is not available. In this case, a larger or smaller icon should be returned.
	 * (The resizing will be done by the browser.)
	 * 
	 * @param encodedIconString
	 * @param size
	 * @return
	 */
	IconResource getIcon(String encodedIconString, int size, IconProviderContext context);

}
