/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.common.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RgbaColor implements Color {

	private static final Pattern RGBA_PATTERN = Pattern.compile("rgba\\(\\s*([\\d.]+)\\s*,\\s*([\\d.]+)\\s*,\\s*([\\d.]+)\\s*,\\s*([\\d.]+%?)\\s*\\)");
	private static final Pattern RGB_PATTERN = Pattern.compile("rgb\\(\\s*([\\d.]+)\\s*,\\s*([\\d.]+)\\s*,\\s*([\\d.]+)\\s*\\)");
	private static final Pattern HSLA_PATTERN = Pattern.compile("hsla\\(\\s*([\\d.]+)\\s*,\\s*([\\d.]+%)\\s*,\\s*([\\d.]+%)\\s*,\\s*([\\d.]+%?)\\s*\\)");
	private static final Pattern HSL_PATTERN = Pattern.compile("hsl\\(\\s*([\\d.]+)\\s*,\\s*([\\d.]+%)\\s*,\\s*([\\d.]+%)\\s*\\)");

	/**
	 * 0-255
	 */
	private final int red;
	/**
	 * 0-255
	 */
	private final int green;
	/**
	 * 0-255
	 */
	private final int blue;
	/**
	 * 0-1
	 */
	private final float alpha;

	public RgbaColor withAlpha(float alpha) {
		return new RgbaColor(getRed(), getGreen(), getBlue(), alpha);
	}

	public RgbaColor(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 1;
	}

	public RgbaColor(int red, int green, int blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	@Override
	public String toHtmlColorString() {
		if (alpha == 1) {
			return "rgb(" + red + "," + green + "," + blue + ")";
		}
		return "rgba(" + red + "," + green + "," + blue + "," + alpha + ")";
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	public float getHue() {
		return rgbToHsl(red, green, blue)[0];
	}

	public float getSaturation() {
		return rgbToHsl(red, green, blue)[1];
	}

	public float getLuminance() {
		return rgbToHsl(red, green, blue)[2];
	}

	public float getAlpha() {
		return alpha;
	}

	public RgbaColor withBrightness(float brightnessZeroToOne) {
		float boundedBrightness = Math.max(0, Math.min(brightnessZeroToOne, 1));
		float[] hsb = java.awt.Color.RGBtoHSB(red, green, blue, null);
		return Color.fromHsba(hsb[0], hsb[1], boundedBrightness, alpha);
	}

	public RgbaColor withLuminance(float luminanceZeroToOne) {
		float[] hsl = rgbToHsl(red, green, blue);
		return fromHsla(hsl[0], hsl[1], luminanceZeroToOne, alpha);
	}

	public static float[] rgbToHsl(int red, int green, int blue) {
		float r = red / 255f;
		float g = green / 255f;
		float b = blue / 255f;

		float min = Math.min(r, Math.min(g, b));
		float max = Math.max(r, Math.max(g, b));

		float h;
		if (max == min) {
			h = 0;
		} else if (max == r) {
			h = (g - b) / (6 * (max - min));
		} else if (max == g) {
			h = ((b - r) / (6 * (max - min))) + 1 / 3f;
		} else /* max == b */ {
			h = ((r - g) / (6 * (max - min))) + 2 / 3f;
		}

		if (h < 0) {
			h += 1;
		} else if (h > 1) {
			h -= 1;
		}

		float l = (max + min) / 2;

		float s;
		if (max == min) {
			s = 0;
		} else if (l <= .5f) {
			s = (max - min) / (max + min);
		} else {
			s = (max - min) / (2 - max - min);
		}

		return new float[]{h, s, l};
	}

	/**
	 * @param h     0-1
	 * @param s     0-1
	 * @param l     0-1
	 * @param alpha 0-1
	 */
	public static RgbaColor fromHsla(float h, float s, float l, float alpha) {
		float q;
		if (l < 0.5) {
			q = l * (1 + s);
		} else {
			q = (l + s) - (s * l);
		}

		float p = 2 * l - q;

		float r = Math.min(Math.max(0, hueToRgbComponent(p, q, h + (1.0f / 3.0f))), 1.0f);
		float g = Math.min(Math.max(0, hueToRgbComponent(p, q, h)), 1.0f);
		float b = Math.min(Math.max(0, hueToRgbComponent(p, q, h + (2.0f / 3.0f))), 1.0f);

		return new RgbaColor(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255), alpha);
	}

	private static float hueToRgbComponent(float p, float q, float h) {
		if (h < 0) {
			h += 1;
		} else if (h > 1) {
			h -= 1;
		}
		if (6 * h < 1) {
			return p + ((q - p) * 6 * h);
		} else if (2 * h < 1) {
			return q;
		} else if (3 * h < 2) {
			return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
		} else {
			return p;
		}
	}


	@Override
	public String toString() {
		return "Color{red=" + red + ", green=" + green + ", blue=" + blue + ", alpha=" + alpha + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RgbaColor color = (RgbaColor) o;

		if (red != color.red) {
			return false;
		}
		if (green != color.green) {
			return false;
		}
		if (blue != color.blue) {
			return false;
		}
		return Float.compare(color.alpha, alpha) == 0;
	}

	@Override
	public int hashCode() {
		int result = red;
		result = 31 * result + green;
		result = 31 * result + blue;
		result = 31 * result + (alpha != +0.0f ? Float.floatToIntBits(alpha) : 0);
		return result;
	}

	public static RgbaColor fromAwtColor(java.awt.Color color) {
		return new RgbaColor(color.getRed(), color.getGreen(), color.getBlue(), ((float) color.getAlpha()) / 255);
	}

	public static RgbaColor fromAwtColor(java.awt.Color color, float alpha) {
		return new RgbaColor(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	public static RgbaColor fromHsba(float h, float s, float b, float alpha) {
		int rgb = java.awt.Color.HSBtoRGB(h, s, b);
		return fromRgbaValue((rgb << 8) | (((int) (alpha * 255)) & 0xff));
	}

	public static RgbaColor fromRgb(int red, int green, int blue) {
		return new RgbaColor(red, green, blue);
	}

	public static RgbaColor fromRgba(int red, int green, int blue, float alpha) {
		return new RgbaColor(red, green, blue, alpha);
	}

	public static RgbaColor fromRgbValue(int rgb) {
		int r = (rgb & 0x00ff0000) >> 16;
		int g = (rgb & 0x0000ff00) >> 8;
		int b = (rgb & 0x000000ff);
		return new RgbaColor(r, g, b);
	}

	public static RgbaColor fromRgbaValue(int rgba) {
		int r = (rgba >> 24) & 0xff;
		int g = (rgba >> 16) & 0xff;
		int b = (rgba >> 8) & 0xff;
		float a = (rgba & 0xff) / 255f;
		return new RgbaColor(r, g, b, a);
	}

	public static RgbaColor fromHex(String hex) {
		if (hex.startsWith("#")) {
			hex = hex.substring(1);
		}
		if (hex.length() == 3) {
			int r = Integer.parseInt(hex, 0, 1, 16);
			int g = Integer.parseInt(hex, 1, 2, 16);
			int b = Integer.parseInt(hex, 2, 3, 16);
			return new RgbaColor((r << 4) + r, (g << 4) + g, (b << 4) + b);
		} else {
			int r = Integer.parseInt(hex, 0, 2, 16);
			int g = Integer.parseInt(hex, 2, 4, 16);
			int b = Integer.parseInt(hex, 4, 6, 16);
			int a = hex.length() == 8 ? Integer.parseInt(hex, 6, 8, 16) : 255;
			return new RgbaColor(r, g, b, ((float) a) / 255);
		}

	}

	public static RgbaColor fromHtmlString(String value) {
		value = value.trim();
		if (value.startsWith("rgba")) {
			Matcher matcher = RgbaColor.RGBA_PATTERN.matcher(value);
			if (matcher.find()) {
				int r = (int) Float.parseFloat(matcher.group(1));
				int g = (int) Float.parseFloat(matcher.group(2));
				int b = (int) Float.parseFloat(matcher.group(3));
				float a = parseCssFloatingPointNumber(matcher.group(4));
				return new RgbaColor(r, g, b, a);
			}
		} else if (value.startsWith("rgb")) {
			Matcher matcher = RgbaColor.RGB_PATTERN.matcher(value);
			if (matcher.find()) {
				int r = (int) Float.parseFloat(matcher.group(1));
				int g = (int) Float.parseFloat(matcher.group(2));
				int b = (int) Float.parseFloat(matcher.group(3));
				return new RgbaColor(r, g, b);
			}
		} else if (value.startsWith("#")) {
			return fromHex(value);
		} else if (value.startsWith("hsla")) {
			Matcher matcher = RgbaColor.HSLA_PATTERN.matcher(value);
			if (matcher.find()) {
				float h = Float.parseFloat(matcher.group(1)) / 255f;
				float s = parseCssFloatingPointNumber(matcher.group(2));
				float l = parseCssFloatingPointNumber(matcher.group(3));
				float a = parseCssFloatingPointNumber(matcher.group(4));
				return RgbaColor.fromHsla(h, s, l, a);
			}
		} else if (value.startsWith("hsl")) {
			Matcher matcher = RgbaColor.HSL_PATTERN.matcher(value);
			if (matcher.find()) {
				float h = Float.parseFloat(matcher.group(1)) / 255f;
				float s = parseCssFloatingPointNumber(matcher.group(2));
				float l = parseCssFloatingPointNumber(matcher.group(3));
				return RgbaColor.fromHsla(h, s, l, 1);
			}
		}
		return CssStandardColors.CSS_STANDARD_COLORS_BY_NAME.get(value);
	}

	private static float parseCssFloatingPointNumber(String s) {
		float a;
		if (s.endsWith("%")) {
			a = Integer.parseInt(s.substring(0, s.length() - 1)) / 100f;
		} else {
			a = Float.parseFloat(s);
		}
		return a;
	}
}
