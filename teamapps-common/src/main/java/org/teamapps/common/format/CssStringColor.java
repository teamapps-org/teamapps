package org.teamapps.common.format;

public class CssStringColor implements Color {

	private final String colorString;

	public CssStringColor(String colorString) {
		this.colorString = colorString;
	}

	public String getColorString() {
		return colorString;
	}

	@Override
	public String toHtmlColorString() {
		return colorString;
	}

	public static CssStringColor fromVariableName(String variableName) {
		if (variableName.startsWith("--")) {
			variableName = variableName.substring(2);
		}
		return new CssStringColor("var(--" + variableName + ")");
	}
}
