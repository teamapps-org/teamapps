package org.teamapps.icons.composite;

import org.teamapps.icons.spi.IconEncoder;
import org.teamapps.icons.IconEncoderContext;

public class CompositeIconEncoder implements IconEncoder<CompositeIcon, Void> {

	@Override
	public String encodeIcon(CompositeIcon icon, IconEncoderContext context) {
		StringBuilder sb = new StringBuilder()
				.append("0(")
				.append(context.encodeIcon(icon.getBaseIcon()))
				.append(")");
		if (icon.getBottomRightIcon() != null) {
			sb.append("1(");
			sb.append(context.encodeIcon(icon.getBottomRightIcon()));
			sb.append(")");
		}
		if (icon.getBottomLeftIcon() != null) {
			sb.append("2(");
			sb.append(context.encodeIcon(icon.getBottomLeftIcon()));
			sb.append(")");
		}
		if (icon.getTopLeftIcon() != null) {
			sb.append("3(");
			sb.append(context.encodeIcon(icon.getTopLeftIcon()));
			sb.append(")");
		}
		if (icon.getTopRightIcon() != null) {
			sb.append("4(");
			sb.append(context.encodeIcon(icon.getTopRightIcon()));
			sb.append(")");
		}
		return sb.toString();
	}
}
