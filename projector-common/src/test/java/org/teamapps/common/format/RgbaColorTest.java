/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class RgbaColorTest {

	@Test
	public void testRgbToHsl() throws Exception {
		assertThat(RgbaColor.rgbToHsl(255, 255, 255)).containsExactly(0, 0, 1);
		assertThat(RgbaColor.rgbToHsl(255, 0, 0)).containsExactly(0, 1, .5f);
		assertThat(RgbaColor.rgbToHsl(0, 255, 0)).containsExactly(1 / 3f, 1, .5f);
		assertThat(RgbaColor.rgbToHsl(0, 0, 255)).containsExactly(2 / 3f, 1, .5f);
		assertThat(RgbaColor.rgbToHsl(0, 0, 0)).containsExactly(0, 0, 0);
	}

	@Test
	public void testFromHsla() throws Exception {
		testColorToAndFromHsl(RgbaColor.MATERIAL_GREEN_300);
		testColorToAndFromHsl(RgbaColor.MATERIAL_RED_500);
		testColorToAndFromHsl(RgbaColor.MATERIAL_BLUE_300);
		testColorToAndFromHsl(RgbaColor.MATERIAL_PURPLE_300);
		testColorToAndFromHsl(RgbaColor.MATERIAL_ORANGE_300);
		testColorToAndFromHsl(RgbaColor.MATERIAL_PINK_500);
		testColorToAndFromHsl(RgbaColor.MATERIAL_YELLOW_400);
	}

	private void testColorToAndFromHsl(RgbaColor c) {
		float[] hsl = RgbaColor.rgbToHsl(c.getRed(), c.getGreen(), c.getBlue());
		assertEquals(c, RgbaColor.fromHsla(hsl[0], hsl[1], hsl[2], 1));
	}

	@Test
	public void fromHtmlString_rgba() throws Exception {
		assertRgba(RgbaColor.fromHtmlString("rgba(1, 2, 3, 0)"), 1, 2, 3, 0);
		assertRgba(RgbaColor.fromHtmlString("rgba( 255  , 254  ,  253 ,  0.1 )"), 255, 254, 253, 0.1f);
		assertRgba(RgbaColor.fromHtmlString("rgba(0, 0, 0, .99)"), 0, 0, 0, .99f);
		assertRgba(RgbaColor.fromHtmlString("rgba(0, 0, 0, 50%)"), 0, 0, 0, .5f);
	}

	@Test
	public void fromHtmlString_rgb() throws Exception {
		assertRgba(RgbaColor.fromHtmlString("rgb(1, 2, 3)"), 1, 2, 3, 1);
		assertRgba(RgbaColor.fromHtmlString("rgb(  255 ,  254  , 253  )"), 255, 254, 253, 1);
		assertRgba(RgbaColor.fromHtmlString("rgb(0, 0, 0)"), 0, 0, 0, 1);
	}

	@Test
	public void fromHtmlString_hex() throws Exception {
		assertRgba(RgbaColor.fromHtmlString("#010203"), 1, 2, 3, 1);
		assertRgba(RgbaColor.fromHtmlString(" #fffefd "), 0xff, 0xfe, 0xfd, 1);
		assertRgba(RgbaColor.fromHtmlString("#000000"), 0, 0, 0, 1);
		assertRgba(RgbaColor.fromHtmlString("#123"), 0x11, 0x22, 0x33, 1);
	}

	@Test
	public void fromHtmlString_hsla() throws Exception {
		assertHsla(RgbaColor.fromHtmlString("hsla(0, 50%, 30%, .1)"), 0, .5f, .3f, .1f);
		assertHsla(RgbaColor.fromHtmlString("hsla(  75 ,  50%  , 10%  , 20% )"), 75 / 255f, .5f, .1f, .2f);
	}

	@Test
	public void fromHtmlString_hsl() throws Exception {
		assertHsla(RgbaColor.fromHtmlString("hsl(0, 50%, 30%)"), 0, .5f, .3f, 1);
		assertHsla(RgbaColor.fromHtmlString("hsl(  75 ,  50%  , 10%  )"), 75 / 255f, .5f, .1f, 1);
	}

	@Test
	public void fromHtmlString_standardColor() throws Exception {
		assertRgba(RgbaColor.fromHtmlString("floralwhite"), 255, 250, 240, 1);
	}

	@Test
	public void testToHtmlHexColorString() {
		assertThat(Color.fromRgb(0xFF, 0x0, 0x0).toHtmlHexColorString()).isEqualTo("#FF0000");
		assertThat(Color.fromRgb(0x0, 0xFF, 0x0).toHtmlHexColorString()).isEqualTo("#00FF00");
		assertThat(Color.fromRgb(0x0, 0x0, 0xFF).toHtmlHexColorString()).isEqualTo("#0000FF");
		assertThat(Color.fromRgb(0x1, 0x1, 0x1).toHtmlHexColorString()).isEqualTo("#010101");

		assertThat(Color.fromRgba(0xFF, 0x0, 0x0, 0.75f).toHtmlHexColorString()).isEqualTo("#FF0000BF");
		assertThat(Color.fromRgba(0x0, 0xFF, 0x0, 0.75f).toHtmlHexColorString()).isEqualTo("#00FF00BF");
		assertThat(Color.fromRgba(0x0, 0x0, 0xFF, 0.75f).toHtmlHexColorString()).isEqualTo("#0000FFBF");
		assertThat(Color.fromRgba(0x1, 0x1, 0x1, 0.75f).toHtmlHexColorString()).isEqualTo("#010101BF");
		assertThat(Color.fromRgba(0xFF, 0x0, 0x0, 0.05f).toHtmlHexColorString()).isEqualTo("#FF00000C");
		assertThat(Color.fromRgba(0x0, 0x0, 0xFF, 0.05f).toHtmlHexColorString()).isEqualTo("#0000FF0C");

		assertThat(Color.fromRgba(0xFF, 0x0, 0x0, 1).toHtmlHexColorString()).isEqualTo("#FF0000");
		assertThat(Color.fromRgba(0x0, 0xFF, 0x0, 1).toHtmlHexColorString()).isEqualTo("#00FF00");
		assertThat(Color.fromRgba(0x0, 0x0, 0xFF, 1).toHtmlHexColorString()).isEqualTo("#0000FF");
		assertThat(Color.fromRgba(0x1, 0x1, 0x1, 1).toHtmlHexColorString()).isEqualTo("#010101");

		assertThat(Color.fromRgba(0xFF, 0x0, 0x0, 0).toHtmlHexColorString()).isEqualTo("#FF000000");
		assertThat(Color.fromRgba(0x0, 0xFF, 0x0, 0).toHtmlHexColorString()).isEqualTo("#00FF0000");
		assertThat(Color.fromRgba(0x0, 0x0, 0xFF, 0).toHtmlHexColorString()).isEqualTo("#0000FF00");
		assertThat(Color.fromRgba(0x1, 0x1, 0x1, 0).toHtmlHexColorString()).isEqualTo("#01010100");
	}

	private void assertRgba(RgbaColor color, int r, int g, int b, float a) {
		assertNotNull(color);
		assertEquals(r, color.getRed());
		assertEquals(g, color.getGreen());
		assertEquals(b, color.getBlue());
		assertEquals(a, color.getAlpha(), .0001);
	}

	private void assertHsla(RgbaColor color, float h, float s, float l, float a) {
		assertNotNull(color);
		assertEquals(h, color.getHue(), .01);
		assertEquals(s, color.getSaturation(), .01);
		assertEquals(l, color.getLuminance(), .01);
		assertEquals(a, color.getAlpha(), .01);
	}

}
