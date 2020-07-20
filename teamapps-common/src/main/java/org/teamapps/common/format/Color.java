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

public class Color {

	public static final Color WHITE = new Color(255, 255, 255, 1);
	public static final Color BLACK = new Color(0, 0, 0, 1);
	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	public static final Color GRAY = new Color(128, 128, 128);
	public static final Color GRAY_STANDARD = new Color(119, 119, 119);
	public static final Color LIGHT_GRAY = new Color(211, 211, 211);
	public static final Color SILVER = new Color(192, 192, 192);
	public static final Color DIM_GRAY = new Color(105, 105, 105);
	public static final Color RED = new Color(255, 0, 0);
	public static final Color DARK_RED = new Color(139, 0, 0);
	public static final Color FIRE_BRICK = new Color(178, 34, 34);
	public static final Color CRIMSON = new Color(220, 20, 60);
	public static final Color ORANGE_RED = new Color(255, 69, 0);
	public static final Color CORAL = new Color(255, 127, 80);
	public static final Color DARK_ORANGE = new Color(255, 140, 0);
	public static final Color ORANGE = new Color(255, 165, 0);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color LIGHT_YELLOW = new Color(255, 255, 224);
	public static final Color KHAKI = new Color(240, 230, 140);
	public static final Color DARK_KHAKI = new Color(189, 183, 107);
	public static final Color GOLD = new Color(255, 215, 0);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color DARK_BLUE = new Color(0, 0, 139);
	public static final Color MIDNIGHT_BLUE = new Color(25, 25, 112);
	public static final Color LIGHT_BLUE = new Color(173, 216, 230);
	public static final Color SKY_BLUE = new Color(135, 206, 235);
	public static final Color DEEP_SKY_BLUE = new Color(0, 191, 255);
	public static final Color NAVY = new Color(0, 0, 128);
	public static final Color GREEN = new Color(0, 128, 0);
	public static final Color DARK_GREEN = new Color(0, 100, 0);
	public static final Color LIGHT_GREEN = new Color(144, 238, 144);
	public static final Color LIME_GREEN = new Color(50, 205, 50);
	public static final Color PURPLE = new Color(128, 0, 128);
	public static final Color BLUE_VIOLET = new Color(138, 43, 226);
	public static final Color INDIGO = new Color(75, 0, 130);
	public static final Color BROWN = new Color(165, 42, 42);
	public static final Color SADDLE_BROWN = new Color(139, 69, 19);
	public static final Color CHOCOLATE = new Color(210, 105, 30);
	public static final Color TEAL = new Color(0, 128, 128);

	public static final Color BOOTSTRAP_PRIMARY = new Color(51, 122, 183);
	public static final Color BOOTSTRAP_SUCCESS = new Color(92, 184, 92);
	public static final Color BOOTSTRAP_INFO = new Color(91, 192, 222);
	public static final Color BOOTSTRAP_WARNING = new Color(240, 173, 78);
	public static final Color BOOTSTRAP_DANGER = new Color(217, 83, 79);

	public static final Color MATERIAL_RED_50 = new Color(255, 235, 238);
	public static final Color MATERIAL_RED_100 = new Color(255, 205, 210);
	public static final Color MATERIAL_RED_200 = new Color(239, 154, 154);
	public static final Color MATERIAL_RED_300 = new Color(229, 115, 115);
	public static final Color MATERIAL_RED_400 = new Color(239, 83, 80);
	public static final Color MATERIAL_RED_500 = new Color(244, 67, 54);
	public static final Color MATERIAL_RED_600 = new Color(229, 57, 53);
	public static final Color MATERIAL_RED_700 = new Color(211, 47, 47);
	public static final Color MATERIAL_RED_800 = new Color(198, 40, 40);
	public static final Color MATERIAL_RED_900 = new Color(183, 28, 28);
	public static final Color MATERIAL_RED_A100 = new Color(255, 138, 128);
	public static final Color MATERIAL_RED_A200 = new Color(255, 82, 82);
	public static final Color MATERIAL_RED_A400 = new Color(255, 23, 68);
	public static final Color MATERIAL_RED_A700 = new Color(213, 0, 0);
	public static final Color MATERIAL_PINK_50 = new Color(252, 228, 236);
	public static final Color MATERIAL_PINK_100 = new Color(248, 187, 208);
	public static final Color MATERIAL_PINK_200 = new Color(244, 143, 177);
	public static final Color MATERIAL_PINK_300 = new Color(240, 98, 146);
	public static final Color MATERIAL_PINK_400 = new Color(236, 64, 122);
	public static final Color MATERIAL_PINK_500 = new Color(233, 30, 99);
	public static final Color MATERIAL_PINK_600 = new Color(216, 27, 96);
	public static final Color MATERIAL_PINK_700 = new Color(194, 24, 91);
	public static final Color MATERIAL_PINK_800 = new Color(173, 20, 87);
	public static final Color MATERIAL_PINK_900 = new Color(136, 14, 79);
	public static final Color MATERIAL_PINK_A100 = new Color(255, 128, 171);
	public static final Color MATERIAL_PINK_A200 = new Color(255, 64, 129);
	public static final Color MATERIAL_PINK_A400 = new Color(245, 0, 87);
	public static final Color MATERIAL_PINK_A700 = new Color(197, 17, 98);
	public static final Color MATERIAL_PURPLE_50 = new Color(243, 229, 245);
	public static final Color MATERIAL_PURPLE_100 = new Color(225, 190, 231);
	public static final Color MATERIAL_PURPLE_200 = new Color(206, 147, 216);
	public static final Color MATERIAL_PURPLE_300 = new Color(186, 104, 200);
	public static final Color MATERIAL_PURPLE_400 = new Color(171, 71, 188);
	public static final Color MATERIAL_PURPLE_500 = new Color(156, 39, 176);
	public static final Color MATERIAL_PURPLE_600 = new Color(142, 36, 170);
	public static final Color MATERIAL_PURPLE_700 = new Color(123, 31, 162);
	public static final Color MATERIAL_PURPLE_800 = new Color(106, 27, 154);
	public static final Color MATERIAL_PURPLE_900 = new Color(74, 20, 140);
	public static final Color MATERIAL_PURPLE_A100 = new Color(234, 128, 252);
	public static final Color MATERIAL_PURPLE_A200 = new Color(224, 64, 251);
	public static final Color MATERIAL_PURPLE_A400 = new Color(213, 0, 249);
	public static final Color MATERIAL_PURPLE_A700 = new Color(170, 0, 255);
	public static final Color MATERIAL_DEEP_PURPLE_50 = new Color(237, 231, 246);
	public static final Color MATERIAL_DEEP_PURPLE_100 = new Color(209, 196, 233);
	public static final Color MATERIAL_DEEP_PURPLE_200 = new Color(179, 157, 219);
	public static final Color MATERIAL_DEEP_PURPLE_300 = new Color(149, 117, 205);
	public static final Color MATERIAL_DEEP_PURPLE_400 = new Color(126, 87, 194);
	public static final Color MATERIAL_DEEP_PURPLE_500 = new Color(103, 58, 183);
	public static final Color MATERIAL_DEEP_PURPLE_600 = new Color(94, 53, 177);
	public static final Color MATERIAL_DEEP_PURPLE_700 = new Color(81, 45, 168);
	public static final Color MATERIAL_DEEP_PURPLE_800 = new Color(69, 39, 160);
	public static final Color MATERIAL_DEEP_PURPLE_900 = new Color(49, 27, 146);
	public static final Color MATERIAL_DEEP_PURPLE_A100 = new Color(179, 136, 255);
	public static final Color MATERIAL_DEEP_PURPLE_A200 = new Color(124, 77, 255);
	public static final Color MATERIAL_DEEP_PURPLE_A400 = new Color(101, 31, 255);
	public static final Color MATERIAL_DEEP_PURPLE_A700 = new Color(98, 0, 234);
	public static final Color MATERIAL_INDIGO_50 = new Color(232, 234, 246);
	public static final Color MATERIAL_INDIGO_100 = new Color(197, 202, 233);
	public static final Color MATERIAL_INDIGO_200 = new Color(159, 168, 218);
	public static final Color MATERIAL_INDIGO_300 = new Color(121, 134, 203);
	public static final Color MATERIAL_INDIGO_400 = new Color(92, 107, 192);
	public static final Color MATERIAL_INDIGO_500 = new Color(63, 81, 181);
	public static final Color MATERIAL_INDIGO_600 = new Color(57, 73, 171);
	public static final Color MATERIAL_INDIGO_700 = new Color(48, 63, 159);
	public static final Color MATERIAL_INDIGO_800 = new Color(40, 53, 147);
	public static final Color MATERIAL_INDIGO_900 = new Color(26, 35, 126);
	public static final Color MATERIAL_INDIGO_A100 = new Color(140, 158, 255);
	public static final Color MATERIAL_INDIGO_A200 = new Color(83, 109, 254);
	public static final Color MATERIAL_INDIGO_A400 = new Color(61, 90, 254);
	public static final Color MATERIAL_INDIGO_A700 = new Color(48, 79, 254);
	public static final Color MATERIAL_BLUE_50 = new Color(227, 242, 253);
	public static final Color MATERIAL_BLUE_100 = new Color(187, 222, 251);
	public static final Color MATERIAL_BLUE_200 = new Color(144, 202, 249);
	public static final Color MATERIAL_BLUE_300 = new Color(100, 181, 246);
	public static final Color MATERIAL_BLUE_400 = new Color(66, 165, 245);
	public static final Color MATERIAL_BLUE_500 = new Color(33, 150, 243);
	public static final Color MATERIAL_BLUE_600 = new Color(30, 136, 229);
	public static final Color MATERIAL_BLUE_700 = new Color(25, 118, 210);
	public static final Color MATERIAL_BLUE_800 = new Color(21, 101, 192);
	public static final Color MATERIAL_BLUE_900 = new Color(13, 71, 161);
	public static final Color MATERIAL_BLUE_A100 = new Color(130, 177, 255);
	public static final Color MATERIAL_BLUE_A200 = new Color(68, 138, 255);
	public static final Color MATERIAL_BLUE_A400 = new Color(41, 121, 255);
	public static final Color MATERIAL_BLUE_A700 = new Color(41, 98, 255);
	public static final Color MATERIAL_LIGHT_BLUE_50 = new Color(225, 245, 254);
	public static final Color MATERIAL_LIGHT_BLUE_100 = new Color(179, 229, 252);
	public static final Color MATERIAL_LIGHT_BLUE_200 = new Color(129, 212, 250);
	public static final Color MATERIAL_LIGHT_BLUE_300 = new Color(79, 195, 247);
	public static final Color MATERIAL_LIGHT_BLUE_400 = new Color(41, 182, 252);
	public static final Color MATERIAL_LIGHT_BLUE_500 = new Color(3, 169, 244);
	public static final Color MATERIAL_LIGHT_BLUE_600 = new Color(3, 155, 229);
	public static final Color MATERIAL_LIGHT_BLUE_700 = new Color(2, 136, 209);
	public static final Color MATERIAL_LIGHT_BLUE_800 = new Color(2, 119, 189);
	public static final Color MATERIAL_LIGHT_BLUE_900 = new Color(1, 87, 155);
	public static final Color MATERIAL_LIGHT_BLUE_A100 = new Color(128, 216, 255);
	public static final Color MATERIAL_LIGHT_BLUE_A200 = new Color(64, 196, 255);
	public static final Color MATERIAL_LIGHT_BLUE_A400 = new Color(0, 176, 255);
	public static final Color MATERIAL_LIGHT_BLUE_A700 = new Color(0, 145, 234);
	public static final Color MATERIAL_CYAN_50 = new Color(224, 247, 250);
	public static final Color MATERIAL_CYAN_100 = new Color(178, 235, 242);
	public static final Color MATERIAL_CYAN_200 = new Color(128, 222, 234);
	public static final Color MATERIAL_CYAN_300 = new Color(77, 208, 225);
	public static final Color MATERIAL_CYAN_400 = new Color(38, 198, 218);
	public static final Color MATERIAL_CYAN_500 = new Color(0, 188, 212);
	public static final Color MATERIAL_CYAN_600 = new Color(0, 172, 193);
	public static final Color MATERIAL_CYAN_700 = new Color(0, 151, 167);
	public static final Color MATERIAL_CYAN_800 = new Color(0, 131, 143);
	public static final Color MATERIAL_CYAN_900 = new Color(0, 96, 100);
	public static final Color MATERIAL_CYAN_A100 = new Color(132, 255, 255);
	public static final Color MATERIAL_CYAN_A200 = new Color(24, 255, 255);
	public static final Color MATERIAL_CYAN_A400 = new Color(0, 229, 255);
	public static final Color MATERIAL_CYAN_A700 = new Color(0, 184, 212);
	public static final Color MATERIAL_TEAL_50 = new Color(224, 242, 241);
	public static final Color MATERIAL_TEAL_100 = new Color(178, 223, 219);
	public static final Color MATERIAL_TEAL_200 = new Color(128, 203, 196);
	public static final Color MATERIAL_TEAL_300 = new Color(77, 182, 172);
	public static final Color MATERIAL_TEAL_400 = new Color(38, 166, 154);
	public static final Color MATERIAL_TEAL_500 = new Color(0, 150, 136);
	public static final Color MATERIAL_TEAL_600 = new Color(0, 137, 123);
	public static final Color MATERIAL_TEAL_700 = new Color(0, 121, 107);
	public static final Color MATERIAL_TEAL_800 = new Color(0, 105, 92);
	public static final Color MATERIAL_TEAL_900 = new Color(0, 77, 64);
	public static final Color MATERIAL_TEAL_A100 = new Color(167, 255, 235);
	public static final Color MATERIAL_TEAL_A200 = new Color(100, 255, 218);
	public static final Color MATERIAL_TEAL_A400 = new Color(29, 233, 182);
	public static final Color MATERIAL_TEAL_A700 = new Color(0, 191, 165);
	public static final Color MATERIAL_GREEN_50 = new Color(232, 245, 233);
	public static final Color MATERIAL_GREEN_100 = new Color(200, 230, 201);
	public static final Color MATERIAL_GREEN_200 = new Color(165, 214, 167);
	public static final Color MATERIAL_GREEN_300 = new Color(129, 199, 132);
	public static final Color MATERIAL_GREEN_400 = new Color(102, 187, 106);
	public static final Color MATERIAL_GREEN_500 = new Color(76, 175, 80);
	public static final Color MATERIAL_GREEN_600 = new Color(67, 160, 71);
	public static final Color MATERIAL_GREEN_700 = new Color(56, 142, 60);
	public static final Color MATERIAL_GREEN_800 = new Color(46, 125, 50);
	public static final Color MATERIAL_GREEN_900 = new Color(27, 94, 32);
	public static final Color MATERIAL_GREEN_A100 = new Color(185, 246, 202);
	public static final Color MATERIAL_GREEN_A200 = new Color(105, 240, 174);
	public static final Color MATERIAL_GREEN_A400 = new Color(0, 230, 118);
	public static final Color MATERIAL_GREEN_A700 = new Color(0, 200, 83);
	public static final Color MATERIAL_LIGHT_GREEN_50 = new Color(241, 248, 233);
	public static final Color MATERIAL_LIGHT_GREEN_100 = new Color(220, 237, 200);
	public static final Color MATERIAL_LIGHT_GREEN_200 = new Color(197, 225, 165);
	public static final Color MATERIAL_LIGHT_GREEN_300 = new Color(174, 213, 129);
	public static final Color MATERIAL_LIGHT_GREEN_400 = new Color(156, 204, 101);
	public static final Color MATERIAL_LIGHT_GREEN_500 = new Color(139, 195, 74);
	public static final Color MATERIAL_LIGHT_GREEN_600 = new Color(124, 179, 66);
	public static final Color MATERIAL_LIGHT_GREEN_700 = new Color(104, 159, 56);
	public static final Color MATERIAL_LIGHT_GREEN_800 = new Color(85, 139, 47);
	public static final Color MATERIAL_LIGHT_GREEN_900 = new Color(51, 105, 30);
	public static final Color MATERIAL_LIGHT_GREEN_A100 = new Color(204, 255, 144);
	public static final Color MATERIAL_LIGHT_GREEN_A200 = new Color(178, 255, 89);
	public static final Color MATERIAL_LIGHT_GREEN_A400 = new Color(118, 255, 3);
	public static final Color MATERIAL_LIGHT_GREEN_A700 = new Color(100, 221, 23);
	public static final Color MATERIAL_LIME_50 = new Color(249, 251, 231);
	public static final Color MATERIAL_LIME_100 = new Color(240, 244, 195);
	public static final Color MATERIAL_LIME_200 = new Color(230, 238, 156);
	public static final Color MATERIAL_LIME_300 = new Color(220, 231, 117);
	public static final Color MATERIAL_LIME_400 = new Color(212, 225, 87);
	public static final Color MATERIAL_LIME_500 = new Color(205, 220, 57);
	public static final Color MATERIAL_LIME_600 = new Color(192, 202, 51);
	public static final Color MATERIAL_LIME_700 = new Color(164, 180, 43);
	public static final Color MATERIAL_LIME_800 = new Color(158, 157, 36);
	public static final Color MATERIAL_LIME_900 = new Color(130, 119, 23);
	public static final Color MATERIAL_LIME_A100 = new Color(244, 255, 129);
	public static final Color MATERIAL_LIME_A200 = new Color(238, 255, 65);
	public static final Color MATERIAL_LIME_A400 = new Color(198, 255, 0);
	public static final Color MATERIAL_LIME_A700 = new Color(174, 234, 0);
	public static final Color MATERIAL_YELLOW_50 = new Color(255, 253, 231);
	public static final Color MATERIAL_YELLOW_100 = new Color(255, 249, 196);
	public static final Color MATERIAL_YELLOW_200 = new Color(255, 245, 144);
	public static final Color MATERIAL_YELLOW_300 = new Color(255, 241, 118);
	public static final Color MATERIAL_YELLOW_400 = new Color(255, 238, 88);
	public static final Color MATERIAL_YELLOW_500 = new Color(255, 235, 59);
	public static final Color MATERIAL_YELLOW_600 = new Color(253, 216, 53);
	public static final Color MATERIAL_YELLOW_700 = new Color(251, 192, 45);
	public static final Color MATERIAL_YELLOW_800 = new Color(249, 168, 37);
	public static final Color MATERIAL_YELLOW_900 = new Color(245, 127, 23);
	public static final Color MATERIAL_YELLOW_A100 = new Color(255, 255, 130);
	public static final Color MATERIAL_YELLOW_A200 = new Color(255, 255, 0);
	public static final Color MATERIAL_YELLOW_A400 = new Color(255, 234, 0);
	public static final Color MATERIAL_YELLOW_A700 = new Color(255, 214, 0);
	public static final Color MATERIAL_AMBER_50 = new Color(255, 248, 225);
	public static final Color MATERIAL_AMBER_100 = new Color(255, 236, 179);
	public static final Color MATERIAL_AMBER_200 = new Color(255, 224, 130);
	public static final Color MATERIAL_AMBER_300 = new Color(255, 213, 79);
	public static final Color MATERIAL_AMBER_400 = new Color(255, 202, 40);
	public static final Color MATERIAL_AMBER_500 = new Color(255, 193, 7);
	public static final Color MATERIAL_AMBER_600 = new Color(255, 179, 0);
	public static final Color MATERIAL_AMBER_700 = new Color(255, 160, 0);
	public static final Color MATERIAL_AMBER_800 = new Color(255, 143, 0);
	public static final Color MATERIAL_AMBER_900 = new Color(255, 111, 0);
	public static final Color MATERIAL_AMBER_A100 = new Color(255, 229, 127);
	public static final Color MATERIAL_AMBER_A200 = new Color(255, 215, 64);
	public static final Color MATERIAL_AMBER_A400 = new Color(255, 196, 0);
	public static final Color MATERIAL_AMBER_A700 = new Color(255, 171, 0);
	public static final Color MATERIAL_ORANGE_50 = new Color(255, 243, 224);
	public static final Color MATERIAL_ORANGE_100 = new Color(255, 224, 178);
	public static final Color MATERIAL_ORANGE_200 = new Color(255, 204, 128);
	public static final Color MATERIAL_ORANGE_300 = new Color(255, 183, 77);
	public static final Color MATERIAL_ORANGE_400 = new Color(255, 167, 38);
	public static final Color MATERIAL_ORANGE_500 = new Color(255, 152, 0);
	public static final Color MATERIAL_ORANGE_600 = new Color(251, 140, 0);
	public static final Color MATERIAL_ORANGE_700 = new Color(245, 124, 0);
	public static final Color MATERIAL_ORANGE_800 = new Color(239, 108, 0);
	public static final Color MATERIAL_ORANGE_900 = new Color(230, 81, 0);
	public static final Color MATERIAL_ORANGE_A100 = new Color(255, 209, 128);
	public static final Color MATERIAL_ORANGE_A200 = new Color(255, 171, 64);
	public static final Color MATERIAL_ORANGE_A400 = new Color(255, 145, 0);
	public static final Color MATERIAL_ORANGE_A700 = new Color(255, 109, 0);
	public static final Color MATERIAL_DEEP_ORANGE_50 = new Color(251, 233, 167);
	public static final Color MATERIAL_DEEP_ORANGE_100 = new Color(255, 204, 188);
	public static final Color MATERIAL_DEEP_ORANGE_200 = new Color(255, 171, 145);
	public static final Color MATERIAL_DEEP_ORANGE_300 = new Color(255, 138, 101);
	public static final Color MATERIAL_DEEP_ORANGE_400 = new Color(255, 112, 67);
	public static final Color MATERIAL_DEEP_ORANGE_500 = new Color(255, 87, 34);
	public static final Color MATERIAL_DEEP_ORANGE_600 = new Color(244, 81, 30);
	public static final Color MATERIAL_DEEP_ORANGE_700 = new Color(230, 74, 25);
	public static final Color MATERIAL_DEEP_ORANGE_800 = new Color(216, 67, 21);
	public static final Color MATERIAL_DEEP_ORANGE_900 = new Color(191, 54, 12);
	public static final Color MATERIAL_DEEP_ORANGE_A100 = new Color(255, 158, 128);
	public static final Color MATERIAL_DEEP_ORANGE_A200 = new Color(255, 110, 64);
	public static final Color MATERIAL_DEEP_ORANGE_A400 = new Color(255, 61, 0);
	public static final Color MATERIAL_DEEP_ORANGE_A700 = new Color(221, 38, 0);
	public static final Color MATERIAL_BROWN_50 = new Color(239, 235, 233);
	public static final Color MATERIAL_BROWN_100 = new Color(215, 204, 200);
	public static final Color MATERIAL_BROWN_200 = new Color(188, 170, 164);
	public static final Color MATERIAL_BROWN_300 = new Color(161, 136, 127);
	public static final Color MATERIAL_BROWN_400 = new Color(141, 110, 99);
	public static final Color MATERIAL_BROWN_500 = new Color(121, 85, 72);
	public static final Color MATERIAL_BROWN_600 = new Color(109, 76, 65);
	public static final Color MATERIAL_BROWN_700 = new Color(93, 64, 55);
	public static final Color MATERIAL_BROWN_800 = new Color(78, 52, 46);
	public static final Color MATERIAL_BROWN_900 = new Color(62, 39, 35);
	public static final Color MATERIAL_GREY_50 = new Color(250, 250, 250);
	public static final Color MATERIAL_GREY_100 = new Color(245, 245, 245);
	public static final Color MATERIAL_GREY_200 = new Color(238, 238, 238);
	public static final Color MATERIAL_GREY_300 = new Color(224, 224, 224);
	public static final Color MATERIAL_GREY_400 = new Color(189, 189, 189);
	public static final Color MATERIAL_GREY_500 = new Color(158, 158, 158);
	public static final Color MATERIAL_GREY_600 = new Color(117, 117, 117);
	public static final Color MATERIAL_GREY_700 = new Color(97, 97, 97);
	public static final Color MATERIAL_GREY_800 = new Color(66, 66, 66);
	public static final Color MATERIAL_GREY_900 = new Color(33, 33, 33);
	public static final Color MATERIAL_BLACK_1000 = new Color(0, 0, 0);
	public static final Color MATERIAL_WHITE_1000 = new Color(255, 255, 255);
	public static final Color MATERIAL_BLUE_GREY_50 = new Color(236, 239, 241);
	public static final Color MATERIAL_BLUE_GREY_100 = new Color(207, 216, 220);
	public static final Color MATERIAL_BLUE_GREY_200 = new Color(176, 187, 197);
	public static final Color MATERIAL_BLUE_GREY_300 = new Color(144, 164, 174);
	public static final Color MATERIAL_BLUE_GREY_400 = new Color(120, 144, 156);
	public static final Color MATERIAL_BLUE_GREY_500 = new Color(96, 125, 139);
	public static final Color MATERIAL_BLUE_GREY_600 = new Color(84, 110, 122);
	public static final Color MATERIAL_BLUE_GREY_700 = new Color(69, 90, 100);
	public static final Color MATERIAL_BLUE_GREY_800 = new Color(55, 71, 79);
	public static final Color MATERIAL_BLUE_GREY_900 = new Color(38, 50, 56);

	private final int red;
	private final int green;
	private final int blue;
	private final float alpha;

	public static Color from(java.awt.Color color) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), ((float) color.getAlpha()) / 255);
	}

	public static Color from(java.awt.Color color, float alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	public static Color fromHsba(float h, float s, float b, float alpha) {
		int rgb = java.awt.Color.HSBtoRGB(h, s, b);
		return fromRgbaValue((rgb << 8) | (((int) (alpha * 255)) & 0xff));
	}

	public static Color fromRgbValue(int rgb) {
		int r = (rgb & 0x00ff0000) >> 16;
		int g = (rgb & 0x0000ff00) >> 8;
		int b = (rgb & 0x000000ff);
		return new Color(r, g, b);
	}

	public static Color fromRgbaValue(int rgba) {
		int r = (rgba >> 24) & 0xff;
		int g = (rgba >> 16) & 0xff;
		int b = (rgba >> 8) & 0xff;
		float a = (rgba & 0xff) / 255f;
		return new Color(r, g, b, a);
	}

	public static Color fromHex(String hex) {
		if (hex.startsWith("#")) {
			hex = hex.substring(1);
		}
		int r = Integer.valueOf(hex.substring(0, 2), 16);
		int g = Integer.valueOf(hex.substring(2, 4), 16);
		int b = Integer.valueOf(hex.substring(4, 6), 16);
		int a = hex.length() == 8 ? Integer.valueOf(hex.substring(6, 8), 16) : 255;

		return new Color(r, g, b, ((float) a) / 255);
	}

	public static Color withAlpha(Color color, float alpha) {
		if (color == null) {
			return null;
		}
		return color.withAlpha(alpha);
	}

	public Color withAlpha(float alpha) {
		return new Color(getRed(), getGreen(), getBlue(), alpha);
	}

	public Color(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 1;
	}

	public Color(int red, int green, int blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

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

	public float getAlpha() {
		return alpha;
	}

	public Color withBrightness(float brightnessZeroToOne) {
		float boundedBrightness = Math.max(0, Math.min(brightnessZeroToOne, 1));
		float[] hsb = java.awt.Color.RGBtoHSB(red, green, blue, null);
		return fromHsba(hsb[0], hsb[1], boundedBrightness, alpha);
	}

	public Color withLuminance(float luminanceZeroToOne) {
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

	public static Color fromHsla(float h, float s, float l, float alpha) {
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

		return new Color(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255), alpha);
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

		Color color = (Color) o;

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
}
