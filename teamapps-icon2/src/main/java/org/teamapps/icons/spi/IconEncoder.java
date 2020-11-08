package org.teamapps.icons.spi;

import org.teamapps.icons.IconEncoderContext;
import org.teamapps.icons.api.Icon;

public interface IconEncoder<ICON extends Icon<ICON, STYLE>, STYLE> {

	IconEncoder<ICON, STYLE> withDefaultStyle(STYLE style);

	String encodeIcon(ICON icon, IconEncoderContext context);

}
