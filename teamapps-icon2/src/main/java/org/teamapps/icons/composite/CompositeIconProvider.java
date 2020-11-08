package org.teamapps.icons.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.icons.IconResource;
import org.teamapps.icons.spi.IconProvider;
import org.teamapps.icons.IconProviderContext;
import org.teamapps.icons.IconType;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class CompositeIconProvider implements IconProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final PngIconComposer pngIconComposer = new PngIconComposer();
	private final SvgIconComposer svgIconComposer = new SvgIconComposer();


	@Override
	public IconResource getIcon(String encodedIconString, int size, IconProviderContext context) {
		if (encodedIconString.charAt(0) != '0') {
			throw new IllegalArgumentException("No base icon in " + encodedIconString);
		}

		Map<Integer, String> subIconDescriptionsByPositionIndex = extractSubIconDescriptions(encodedIconString);

		IconResource baseIcon = context.getIcon(subIconDescriptionsByPositionIndex.get(0), size);
		IconResource bottomRight = null;
		IconResource bottomLeft = null;
		IconResource topLeft = null;
		IconResource topRight = null;
		if (subIconDescriptionsByPositionIndex.containsKey(1)) {
			bottomRight = context.getIcon(subIconDescriptionsByPositionIndex.get(1), size / 2);
		}
		if (subIconDescriptionsByPositionIndex.containsKey(2)) {
			bottomLeft = context.getIcon(subIconDescriptionsByPositionIndex.get(2), size / 2);
		}
		if (subIconDescriptionsByPositionIndex.containsKey(3)) {
			topLeft = context.getIcon(subIconDescriptionsByPositionIndex.get(3), size / 2);
		}
		if (subIconDescriptionsByPositionIndex.containsKey(4)) {
			topRight = context.getIcon(subIconDescriptionsByPositionIndex.get(4), size / 2);
		}

		boolean iconsCompatible = Stream.of(bottomRight, bottomLeft, topLeft, topRight)
				.filter(Objects::nonNull)
				.allMatch(iconResource -> iconResource.getIconType().equals(baseIcon.getIconType()));

		if (!iconsCompatible) {
			LOGGER.error("Sub-icons do not have the same type as base icon: " + baseIcon.getIconType());
			return baseIcon;
		}

		if (baseIcon.getIconType() == IconType.SVG) {
			byte[] composedIconBytes = svgIconComposer.compose(
					baseIcon.getBytes(),
					bottomRight != null ? bottomRight.getBytes() : null,
					bottomLeft != null ? bottomLeft.getBytes() : null,
					topLeft != null ? topLeft.getBytes() : null,
					topRight != null ? topRight.getBytes() : null
			);
			return new IconResource(composedIconBytes, IconType.SVG);
		} else if (baseIcon.getIconType() == IconType.PNG) {
			byte[] composedIconBytes = pngIconComposer.compose(
					size,
					baseIcon.getBytes(),
					bottomRight != null ? bottomRight.getBytes() : null,
					bottomLeft != null ? bottomLeft.getBytes() : null,
					topLeft != null ? topLeft.getBytes() : null,
					topRight != null ? topRight.getBytes() : null
			);
			return new IconResource(composedIconBytes, IconType.PNG);
		} else {
			return baseIcon;
		}
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

	public int findClosingParenthesisPosition(String text, int openingParenthesisPosition) {
		int openCounter = 1;
		int pos = openingParenthesisPosition;
		while (openCounter > 0 && pos < text.length()) {
			pos++;
			char c = text.charAt(pos);
			if (c == '(') {
				openCounter++;
			} else if (c == ')') {
				openCounter--;
			}
		}
		return pos == text.length() ? -1 : pos;
	}

}
