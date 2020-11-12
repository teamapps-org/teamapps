package org.teamapps.icons.composite;

import org.teamapps.icons.Icon;
import org.teamapps.icons.IconDecoderContext;
import org.teamapps.icons.spi.IconDecoder;

import java.util.HashMap;
import java.util.Map;

import static org.teamapps.icons.util.IconEncodeDecodeUtil.findClosingParenthesisPosition;

public class CompositeIconDecoder implements IconDecoder<CompositeIcon, Void> {

	@Override
	public CompositeIcon decodeIcon(String encodedIconString, IconDecoderContext context) {
		Map<Integer, String> subIconDescriptionsByPositionIndex = extractSubIconDescriptions(encodedIconString);

		Icon<?, ?> baseIcon = context.decodeIcon(subIconDescriptionsByPositionIndex.get(0));
		Icon<?, ?> bottomRight = subIconDescriptionsByPositionIndex.containsKey(1) ? context.decodeIcon(subIconDescriptionsByPositionIndex.get(1)) : null;
		Icon<?, ?> bottomLeft = subIconDescriptionsByPositionIndex.containsKey(2) ? context.decodeIcon(subIconDescriptionsByPositionIndex.get(2)) : null;
		Icon<?, ?> topLeft = subIconDescriptionsByPositionIndex.containsKey(3) ? context.decodeIcon(subIconDescriptionsByPositionIndex.get(3)) : null;
		Icon<?, ?> topRight = subIconDescriptionsByPositionIndex.containsKey(4) ? context.decodeIcon(subIconDescriptionsByPositionIndex.get(4)) : null;
		
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

}
