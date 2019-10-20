package org.teamapps.config;

import java.util.Locale;

public interface LocaleProvider<USER> {

	Locale getUserLocale(USER user, Locale userAgentLocale);
}
