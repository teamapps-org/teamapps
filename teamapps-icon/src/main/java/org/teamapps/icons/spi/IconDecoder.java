package org.teamapps.icons.spi;

import org.teamapps.icons.Icon;
import org.teamapps.icons.IconDecoderContext;

/**
 * Decodes icons from an encoded icon {@link String}s such as they are produced by the {@link IconEncoder}.
 * <p>
 * Implementations MUST provide a default constructor!
 *
 * @param <ICON>  The icon class this provider will return.
 * @param <STYLE> The style class these icons support.
 */
public interface IconDecoder<ICON extends Icon<ICON, STYLE>, STYLE> {

	/**
	 * Decodes an icon from an encoded icon {@link String}.
	 * <p>
	 * Implementations MUST return an icon, even if the requested size is not available.
	 * In this case, a larger or smaller icon should be returned.
	 * The resizing will be done elsewhere.
	 * <p>
	 * Implementations MUST support unstyled icons.
	 *
	 * @param encodedIconString The encoded icon String as produced by the corresponding {@link IconEncoder}.
	 * @return The icon.
	 */
	Icon<ICON, STYLE> decodeIcon(String encodedIconString, IconDecoderContext context);

}
