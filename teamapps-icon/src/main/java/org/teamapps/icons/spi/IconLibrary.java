package org.teamapps.icons.spi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines an IconLibrary. Icon classes SHOULD be annotated with this annotation.
 * <p>
 * All of the classes specified in this annotation MUST provide a default constructor!
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IconLibrary {

	/**
	 * @return The name of this icon library. Should consist only of characters, digits and underscores. No "." allowed!
	 */
	String name();

	/**
	 * @return The {@link IconEncoder} class that handles these icons.
	 */
	Class<? extends IconEncoder> encoder();

	/**
	 * @return The {@link IconDecoder} class that handles these icons.
	 */
	Class<? extends IconDecoder> decoder();

	/**
	 * @return The {@link IconLoader} class that handles these icons.
	 */
	Class<? extends IconLoader> loader();

	/**
	 * @return A supplier class supplying the default style for this icon library.
	 */
	Class<? extends DefaultStyleSupplier> defaultStyleSupplier();

}
