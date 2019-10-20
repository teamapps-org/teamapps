package org.teamapps.app.multi;

import org.teamapps.app.ComponentBuilder;

public interface ApplicationLaunchInfoProvider<USER> {

	ApplicationLaunchInfo getApplicationLaunchInfo(USER user, ComponentBuilder componentBuilder);
}
