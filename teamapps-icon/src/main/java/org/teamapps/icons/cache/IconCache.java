package org.teamapps.icons.cache;

import org.teamapps.icons.IconResource;

public interface IconCache {

	IconResource getIcon(String encodedIconString, int size);

	void putIcon(String encodedIconString, int size, IconResource iconResource);
	
}
