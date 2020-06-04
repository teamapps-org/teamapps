package org.teamapps.ux.icon;

import java.util.Arrays;
import java.util.List;

public interface IconBundle {

	static IconBundle create(IconBundleEntry[] entries) {
		return () -> Arrays.asList(entries);
	}

	List<IconBundleEntry> getEntries();

}
