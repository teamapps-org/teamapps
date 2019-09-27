package org.teamapps.app;

import java.time.ZoneId;

public interface UserTimeZoneProvider<USER> {

	ZoneId getUserTimeZone(USER user, ZoneId userAgentTimeZone);

}
