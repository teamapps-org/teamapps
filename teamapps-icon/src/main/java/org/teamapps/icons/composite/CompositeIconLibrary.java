package org.teamapps.icons.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.icons.*;
import org.teamapps.icons.spi.IconLibrary;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.teamapps.icons.util.IconEncodeDecodeUtil.findClosingParenthesisPosition;

public class CompositeIconLibrary implements IconLibrary<CompositeIcon> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final PngIconComposer pngIconComposer = new PngIconComposer();
	private final SvgIconComposer svgIconComposer = new SvgIconComposer();

	@Override
	public String getName() {
		return "composite";
	}

	@Override
	public Class<CompositeIcon> getIconClass() {
		return CompositeIcon.class;
	}

	@Override
	public IconStyle<CompositeIcon> getDefaultStyle() {
		return null;
	}

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

	@Override
	public CompositeIcon decodeIcon(String encodedIconString, IconDecoderContext context) {
		Map<Integer, String> subIconDescriptionsByPositionIndex = extractSubIconDescriptions(encodedIconString);

		Icon baseIcon = context.decodeIcon(subIconDescriptionsByPositionIndex.get(0));
		Icon bottomRight = subIconDescriptionsByPositionIndex.containsKey(1) ? context.decodeIcon(subIconDescriptionsByPositionIndex.get(1)) : null;
		Icon bottomLeft = subIconDescriptionsByPositionIndex.containsKey(2) ? context.decodeIcon(subIconDescriptionsByPositionIndex.get(2)) : null;
		Icon topLeft = subIconDescriptionsByPositionIndex.containsKey(3) ? context.decodeIcon(subIconDescriptionsByPositionIndex.get(3)) : null;
		Icon topRight = subIconDescriptionsByPositionIndex.containsKey(4) ? context.decodeIcon(subIconDescriptionsByPositionIndex.get(4)) : null;

		return CompositeIcon.of(baseIcon, bottomRight, bottomLeft, topLeft, topRight);
	}

	private Map<Integer, String> extractSubIconDescriptions(String encodedIconString) {
		Map<Integer, String> subIconDescriptions = new HashMap<>();
		int pos = 0;
		while (pos < encodedIconString.length()) {
			int iconPositionIndex = encodedIconString.charAt(pos) - 48;
			pos += 2;
			int endPos = findClosingParenthesisPosition(encodedIconString, pos);
			String encodedSubIconString = encodedIconString.substring(pos, endPos);
			pos = endPos + 1;
			subIconDescriptions.put(iconPositionIndex, encodedSubIconString);
		}
		return subIconDescriptions;
	}

	@Override
	public IconResource loadIcon(CompositeIcon icon, int size, IconLoaderContext context) {
		IconResource baseIconResource = context.loadIcon(icon.getBaseIcon(), size);
		if (baseIconResource == null) {
			return null;
		}

		IconResource bottomRightResource = icon.getBottomRightIcon() != null? context.loadIcon(icon.getBottomRightIcon(), size / 2) : null;
		IconResource bottomLeftResource = icon.getBottomLeftIcon() != null? context.loadIcon(icon.getBottomLeftIcon(), size / 2) : null;
		IconResource topLeftResource = icon.getTopLeftIcon() != null? context.loadIcon(icon.getTopLeftIcon(), size / 2) : null;
		IconResource topRightResource = icon.getTopRightIcon() != null? context.loadIcon(icon.getTopRightIcon(), size / 2) : null;

		boolean iconsCompatible = Stream.of(bottomRightResource, bottomLeftResource, topLeftResource, topRightResource)
				.filter(Objects::nonNull)
				.allMatch(iconResource -> iconResource.getIconType().equals(baseIconResource.getIconType()));

		if (!iconsCompatible) {
			LOGGER.error("Sub-icons do not have the same type as base icon: " + baseIconResource.getIconType());
			return baseIconResource;
		}

		if (baseIconResource.getIconType() == IconType.SVG) {
			byte[] composedIconBytes = svgIconComposer.compose(
					baseIconResource.getBytes(),
					bottomRightResource != null ? bottomRightResource.getBytes() : null,
					bottomLeftResource != null ? bottomLeftResource.getBytes() : null,
					topLeftResource != null ? topLeftResource.getBytes() : null,
					topRightResource != null ? topRightResource.getBytes() : null
			);
			return new IconResource(composedIconBytes, IconType.SVG);
		} else if (baseIconResource.getIconType() == IconType.PNG) {
			byte[] composedIconBytes = pngIconComposer.compose(
					size,
					baseIconResource.getBytes(),
					bottomRightResource != null ? bottomRightResource.getBytes() : null,
					bottomLeftResource != null ? bottomLeftResource.getBytes() : null,
					topLeftResource != null ? topLeftResource.getBytes() : null,
					topRightResource != null ? topRightResource.getBytes() : null
			);
			return new IconResource(composedIconBytes, IconType.PNG);
		} else {
			return baseIconResource;
		}
	}
}
