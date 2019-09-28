package org.teamapps.config;

import java.time.ZoneId;

public interface TimeZoneProvider<USER> {

	ZoneId getUserTimeZone(USER user, ZoneId userAgentTimeZone);

}
