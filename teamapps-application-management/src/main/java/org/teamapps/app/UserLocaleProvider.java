package org.teamapps.app;

import java.util.Locale;

public interface UserLocaleProvider<USER> {

	Locale getUserLocale(USER user, Locale userAgentLocale);
}
