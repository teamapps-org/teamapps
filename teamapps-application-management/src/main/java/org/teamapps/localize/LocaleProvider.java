package org.teamapps.localize;

import java.util.Locale;

public interface LocaleProvider<USER> {

	Locale getUserLocale(USER user, Locale userAgentLocale);
}
