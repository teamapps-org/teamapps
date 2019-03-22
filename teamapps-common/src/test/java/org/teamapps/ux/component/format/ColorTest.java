/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.format;

import org.junit.Assert;
import org.junit.Test;
import org.teamapps.common.format.Color;

import static org.assertj.core.api.Assertions.assertThat;


public class ColorTest {

	@Test
	public void testRgbToHsl() throws Exception {
		assertThat(Color.rgbToHsl(255, 255, 255)).containsExactly(0, 0, 1);
		assertThat(Color.rgbToHsl(255, 0, 0)).containsExactly(0, 1, .5f);
		assertThat(Color.rgbToHsl(0, 255, 0)).containsExactly(1/3f, 1, .5f);
		assertThat(Color.rgbToHsl(0, 0, 255)).containsExactly(2/3f, 1, .5f);
		assertThat(Color.rgbToHsl(0, 0, 0)).containsExactly(0, 0, 0);
	}

	@Test
	public void test() throws Exception {
		testColorToAndFromHsl(Color.MATERIAL_GREEN_300);
		testColorToAndFromHsl(Color.MATERIAL_RED_500);
		testColorToAndFromHsl(Color.MATERIAL_BLUE_300);
		testColorToAndFromHsl(Color.MATERIAL_PURPLE_300);
		testColorToAndFromHsl(Color.MATERIAL_ORANGE_300);
		testColorToAndFromHsl(Color.MATERIAL_PINK_500);
		testColorToAndFromHsl(Color.MATERIAL_YELLOW_400);
	}

	private void testColorToAndFromHsl(Color c) {
		float[] hsl = Color.rgbToHsl(c.getRed(), c.getGreen(), c.getBlue());
		Assert.assertEquals(c, Color.fromHsla(hsl[0], hsl[1], hsl[2], 1));
	}
}
