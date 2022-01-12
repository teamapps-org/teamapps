package org.teamapps.uisession.statistics;

import it.unimi.dsi.fastutil.objects.Object2LongMap;

public interface CountStats {

	long getCount();

	long getCountLastMinute();

	long getCountLast10Seconds();

	Object2LongMap<Class<?>> getCountByClass();

}
