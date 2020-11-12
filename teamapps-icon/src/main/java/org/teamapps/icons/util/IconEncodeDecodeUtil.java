package org.teamapps.icons.util;

public class IconEncodeDecodeUtil {

	public static int findClosingParenthesisPosition(String text, int openingParenthesisPosition) {
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
