package org.teamapps.icons.spi;

import org.teamapps.icons.IconEncoderContext;
import org.teamapps.icons.api.Icon;

public interface IconEncoder<I extends Icon> {

	String encodeIcon(I icon, IconEncoderContext context);

}
