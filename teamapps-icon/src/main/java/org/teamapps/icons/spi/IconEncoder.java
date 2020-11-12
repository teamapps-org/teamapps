package org.teamapps.icons.spi;

import org.teamapps.icons.Icon;
import org.teamapps.icons.IconEncoderContext;

/**
 * Responsible for creating {@link String} representations for icons. These strings will get parsed by the corresponding
 * {@link IconDecoder}.
 * <p>
 * A String representation may be an arbitrary String, as long as it is an allowed String for URL path segments.
 * It should be safe to use <code>a-z A-Z 0-9 . - _ ~ ! $ ' ( ) * + , ; = : @</code>.
 * If the String contains parenthesis ("(" or ")"), it must make sure to close any opening one of them.
 * <p>
 * Implementations MUST be able to encode unstyled icons, i.e. icons that have no style set (null).
 * <p>
 * Implementations MUST provide a default constructor!
 *
 * @param <ICON>  The icon class this encoder can handle.
 * @param <STYLE> The style class these icons support.
 */
public interface IconEncoder<ICON extends Icon<ICON, STYLE>, STYLE> {

	/**
	 * Creates a string representation of the provided icon.
	 * <p>
	 * The string representation may be an arbitrary string, as long as it is an allowed string for URL path segments.
	 * It should be safe to use <code>a-z A-Z 0-9 . - _ ~ ! $ ' ( ) * + , ; = : @</code>.
	 * If the String contains parenthesis ("(" or ")"), it must make sure to close any opening one of them.
	 * <p>
	 * Note that this method MUST support encoding unstyled icons, i.e. icons that have no style set (null).
	 *
	 * @param icon    The icon to encode.
	 * @param context
	 * @return The encoded icon
	 */
	String encodeIcon(ICON icon, IconEncoderContext context);

}
