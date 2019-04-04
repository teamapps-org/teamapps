package org.teamapps.icons.systemprovider;

import java.nio.charset.StandardCharsets;

public class CompositeSvgProvider {

	public byte[] createComposedIcon(int size, byte[] baseIcon, byte[] bottomRight, byte[] bottomLeft, byte[] topLeft, byte[] topRight) {
		String svg = createSvg(baseIcon);
		svg = svg.substring(0, svg.lastIndexOf("</svg>"));

		if (bottomRight != null) {
			String svgBottomRight = createSvg(bottomRight).replaceAll("teamapps-color", "teamapps-color-bottom-right");
			svg += "\n" + "<g style=\"transform:translate(50%, 50%) scale(0.5)\">" + svgBottomRight + "</g>\n";
		}

		if (bottomLeft != null) {
			String svgBottomLeft = createSvg(bottomLeft).replaceAll("teamapps-color", "teamapps-color-bottom-left");
			svg += "\n" + "<g style=\"transform:translate(0, 50%) scale(0.5)\">" + svgBottomLeft + "</g>\n";
		}

		if (topLeft != null) {
			String svgBottomRight = createSvg(topLeft).replaceAll("teamapps-color", "teamapps-color-top-left");
			svg += "\n" + "<g style=\"transform:translate(0, 0) scale(0.5)\">" + svgBottomRight + "</g>\n";
		}

		if (topRight != null) {
			String svgTopRight = createSvg(topRight).replaceAll("teamapps-color", "teamapps-color-top-right");
			svg += "\n" + "<g style=\"transform:translate(50%, 0) scale(0.5)\">" + svgTopRight + "</g>\n";
		}

		return (svg + "</svg>").getBytes(StandardCharsets.UTF_8);
	}

	private String createSvg(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}

}
