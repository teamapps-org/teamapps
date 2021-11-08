package org.teamapps.icons.cache;

import org.teamapps.icons.IconResource;

public class NoOpIconCache implements IconCache {

	@Override
	public IconResource getIcon(String encodedIconString, int size) {
		return null;
	}

	@Override
	public void putIcon(String encodedIconString, int size, IconResource iconResource) {
		// do not cache
	}
	
}
