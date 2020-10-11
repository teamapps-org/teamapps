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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


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
	public void test() throws Exception {
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
