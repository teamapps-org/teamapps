package org.teamapps.icons.spi;

/**
 * Provides the default style for an icon library.
 * <p>
 * Implementations MUST provide a default constructor!
 *
 * @param <STYLE> The style class
 * @see IconLibrary
 */
public interface DefaultStyleSupplier<STYLE> {

	STYLE getDefaultStyle();

}
