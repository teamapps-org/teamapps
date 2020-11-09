package org.teamapps.icons.spi;

import org.teamapps.icons.Icon;
import org.teamapps.icons.IconEncoderContext;

/**
 * Responsible for creating {@link String} representations for icons. These strings will get parsed by the corresponding
 * {@link IconProvider}.
 *
 * A String representation may be an arbitrary String, as long as it is an allowed String for URL path segments.
 * It should be safe to use <code>a-z A-Z 0-9 . - _ ~ ! $ & ' ( ) * + , ; = : @</code>.
 * If the String contains parenthesis ("(" or ")"), it must make sure to close any opening one of them.
 *
 * IconEncoders need to provide a default constructor!
 *
 * @param <ICON> The icon class this encoder can handle.
 * @param <STYLE> The style class these icons support.
 */
public interface IconEncoder<ICON extends Icon<ICON, STYLE>, STYLE> {

	/**
	 * Creates a string representation of the provided icon.
	 * The string representation may be an arbitrary string, as long as it is an allowed string for URL path segments.
	 *
	 * It should be safe to use <code>a-z A-Z 0-9 . - _ ~ ! $ & ' ( ) * + , ; = : @</code>.
	 * If the String contains parenthesis ("(" or ")"), it must make sure to close any opening one of them.
	 *
	 * @param icon
	 * @param context
	 * @return
	 */
	String encodeIcon(ICON icon, IconEncoderContext context);

	/**
	 * Creates another encoder with the provided default style, i.e., the style that the new encoder must apply
	 * when encoding an unstyled icon.
	 *
	 * @param style the style that the new encoder must apply when encoding an unstyled icon
	 * @return a new IconEncoder instance with the specified default style
	 */
	IconEncoder<ICON, STYLE> withDefaultStyle(STYLE style);

}
