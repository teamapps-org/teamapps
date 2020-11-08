package org.teamapps.icons.spi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IconLibrary {

	String name();

	Class<? extends IconEncoder<?>> encoder();

	Class<? extends IconProvider> provider();

}
