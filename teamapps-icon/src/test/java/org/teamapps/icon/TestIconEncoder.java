package org.teamapps.icon;

import org.teamapps.icons.spi.IconEncoder;
import org.teamapps.icons.IconEncoderContext;

public class TestIconEncoder implements IconEncoder<TestIcon, Void> {

	@Override
	public String encodeIcon(TestIcon icon, IconEncoderContext context) {
		return icon.name();
	}

}
