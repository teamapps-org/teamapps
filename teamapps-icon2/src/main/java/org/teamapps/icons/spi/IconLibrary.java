package org.teamapps.icons.spi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IconLibrary {

	/**
	 * @return The name of this icon library. Should consist only of characters, digits and underscores. No "." allowed!
	 */
	String name();

	/**
	 * @return The {@link IconEncoder} that handles these icons.
	 */
	Class<? extends IconEncoder> encoder();

	/**
	 * @return The {@link IconProvider} that handles these icons.
	 */
	Class<? extends IconProvider> provider();

}
