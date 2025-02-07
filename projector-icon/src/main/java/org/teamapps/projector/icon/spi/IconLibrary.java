package org.teamapps.projector.icon.spi;

import org.teamapps.projector.icon.*;

/**
 * Icon library.
 * <p>
 * Implementations MUST provide a default constructor!
 *
 * @param <I>
 */
public interface IconLibrary<I extends Icon> {

	/**
	 * @return The name of this icon library. Should consist only of characters, digits and underscores. No "." allowed!
	 */
	String getName();

	Class<I> getIconClass();

	/**
	 * Get the default style for this icon library.
	 *
	 * @see org.teamapps.projector.icon.spi.annotation.IconLibrary
	 */
	IconStyle<I> getDefaultStyle();

	/**
	 * Encodes an icon instance as a string. This encoded string contains information about the icon and its style.
	 * The library name should not be part of this string.
	 * <p>
	 * The string representation may be an arbitrary string, as long as it is an allowed string for URL path segments.
	 * It should be safe to use <code>a-z A-Z 0-9 . - _ ~ ! $ ' ( ) * + , ; = : @</code>.
	 * If the String contains parenthesis ("(" or ")"), it must make sure to close any opening one of them.
	 * <p>
	 * Note that this method MUST support encoding unstyled icons, i.e. icons that have no style set (null).
	 *
	 * @param icon    The icon to encode.
	 * @param context the icon encoder context
	 * @return The encoded icon
	 */
	String encodeIcon(I icon, IconEncoderContext context);

	/**
	 * Produces an icon instance from an encoded icon {@link String} such as produced by {@link #encodeIcon(I, IconEncoderContext)}.
	 * <p>
	 * Implementations MUST support unstyled icons.
	 *
	 * @param encodedIconString The encoded icon String as produced by the corresponding {@link IconEncoder}.
	 * @return The icon.
	 */
	I decodeIcon(String encodedIconString, IconDecoderContext context);

	/**
	 * Used for loading actual binary icons. Provides an {@link IconResource} for icon instance.
	 * <p>
	 * The specified icon MUST have a style set (unless the STYLE type is {@link Void}). Callers need to ensure this!
	 * <p>
	 * Implementations MAY therefore assume the icon's to be styled.
	 *
	 * @param size The size of the requested icon.
	 * @return The icon in binary form and type and size of the icon (as {@link IconResource}), or null, if the icon could not be loaded.
	 */
	IconResource loadIcon(I icon, int size, IconLoaderContext context);
}
