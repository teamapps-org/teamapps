/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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

public interface Color {

	String toHtmlColorString();

	
	// ======== static ========

	RgbaColor TRANSPARENT = new RgbaColor(0, 0, 0, 0);

	RgbaColor ALICE_BLUE = new RgbaColor(240, 248, 255);
	RgbaColor ANTIQUE_WHITE = new RgbaColor(250, 235, 215);
	RgbaColor AQUA = new RgbaColor(0, 255, 255);
	RgbaColor AQUA_MARINE = new RgbaColor(127, 255, 212);
	RgbaColor AZURE = new RgbaColor(240, 255, 255);
	RgbaColor BEIGE = new RgbaColor(245, 245, 220);
	RgbaColor BISQUE = new RgbaColor(255, 228, 196);
	RgbaColor BLACK = new RgbaColor(0, 0, 0);
	RgbaColor BLANCHED_ALMOND = new RgbaColor(255, 235, 205);
	RgbaColor BLUE = new RgbaColor(0, 0, 255);
	RgbaColor BLUE_VIOLET = new RgbaColor(138, 43, 226);
	RgbaColor BROWN = new RgbaColor(165, 42, 42);
	RgbaColor BURLY_WOOD = new RgbaColor(222, 184, 135);
	RgbaColor CADET_BLUE = new RgbaColor(95, 158, 160);
	RgbaColor CHARTREUSE = new RgbaColor(127, 255, 0);
	RgbaColor CHOCOLATE = new RgbaColor(210, 105, 30);
	RgbaColor CORAL = new RgbaColor(255, 127, 80);
	RgbaColor CORN_FLOWER_BLUE = new RgbaColor(100, 149, 237);
	RgbaColor CORN_SILK = new RgbaColor(255, 248, 220);
	RgbaColor CRIMSON = new RgbaColor(220, 20, 60);
	RgbaColor CYAN = new RgbaColor(0, 255, 255);
	RgbaColor DARK_BLUE = new RgbaColor(0, 0, 139);
	RgbaColor DARK_CYAN = new RgbaColor(0, 139, 139);
	RgbaColor DARK_GOLDEN_ROD = new RgbaColor(184, 134, 11);
	RgbaColor DARK_GRAY = new RgbaColor(169, 169, 169);
	RgbaColor DARK_GREEN = new RgbaColor(0, 100, 0);
	RgbaColor DARK_GREY = new RgbaColor(169, 169, 169);
	RgbaColor DARK_KHAKI = new RgbaColor(189, 183, 107);
	RgbaColor DARK_MAGENTA = new RgbaColor(139, 0, 139);
	RgbaColor DARK_OLIVE_GREEN = new RgbaColor(85, 107, 47);
	RgbaColor DARK_ORANGE = new RgbaColor(255, 140, 0);
	RgbaColor DARK_ORCHID = new RgbaColor(153, 50, 204);
	RgbaColor DARK_RED = new RgbaColor(139, 0, 0);
	RgbaColor DARK_SALMON = new RgbaColor(233, 150, 122);
	RgbaColor DARK_SEA_GREEN = new RgbaColor(143, 188, 143);
	RgbaColor DARK_SLATE_BLUE = new RgbaColor(72, 61, 139);
	RgbaColor DARK_SLATE_GRAY = new RgbaColor(47, 79, 79);
	RgbaColor DARK_SLATE_GREY = new RgbaColor(47, 79, 79);
	RgbaColor DARK_TURQUOISE = new RgbaColor(0, 206, 209);
	RgbaColor DARK_VIOLET = new RgbaColor(148, 0, 211);
	RgbaColor DEEP_PINK = new RgbaColor(255, 20, 147);
	RgbaColor DEEP_SKY_BLUE = new RgbaColor(0, 191, 255);
	RgbaColor DIMG_RAY = new RgbaColor(105, 105, 105);
	RgbaColor DIMG_REY = new RgbaColor(105, 105, 105);
	RgbaColor DODGER_BLUE = new RgbaColor(30, 144, 255);
	RgbaColor FIRE_BRICK = new RgbaColor(178, 34, 34);
	RgbaColor FLORAL_WHITE = new RgbaColor(255, 250, 240);
	RgbaColor FOREST_GREEN = new RgbaColor(34, 139, 34);
	RgbaColor FUCHSIA = new RgbaColor(255, 0, 255);
	RgbaColor GAINSBORO = new RgbaColor(220, 220, 220);
	RgbaColor GHOST_WHITE = new RgbaColor(248, 248, 255);
	RgbaColor GOLD = new RgbaColor(255, 215, 0);
	RgbaColor GOLDEN_ROD = new RgbaColor(218, 165, 32);
	RgbaColor GRAY = new RgbaColor(128, 128, 128);
	RgbaColor GREEN = new RgbaColor(0, 128, 0);
	RgbaColor GREEN_YELLOW = new RgbaColor(173, 255, 47);
	RgbaColor GREY = new RgbaColor(128, 128, 128);
	RgbaColor HONEY_DEW = new RgbaColor(240, 255, 240);
	RgbaColor HOT_PINK = new RgbaColor(255, 105, 180);
	RgbaColor INDIAN_RED = new RgbaColor(205, 92, 92);
	RgbaColor INDIGO = new RgbaColor(75, 0, 130);
	RgbaColor IVORY = new RgbaColor(255, 255, 240);
	RgbaColor KHAKI = new RgbaColor(240, 230, 140);
	RgbaColor LAVENDER = new RgbaColor(230, 230, 250);
	RgbaColor LAVENDER_BLUSH = new RgbaColor(255, 240, 245);
	RgbaColor LAWN_GREEN = new RgbaColor(124, 252, 0);
	RgbaColor LEMON_CHIFFON = new RgbaColor(255, 250, 205);
	RgbaColor LIGHT_BLUE = new RgbaColor(173, 216, 230);
	RgbaColor LIGHT_CORAL = new RgbaColor(240, 128, 128);
	RgbaColor LIGHT_CYAN = new RgbaColor(224, 255, 255);
	RgbaColor LIGHT_GOLDEN_ROD_YELLOW = new RgbaColor(250, 250, 210);
	RgbaColor LIGHT_GRAY = new RgbaColor(211, 211, 211);
	RgbaColor LIGHT_GREEN = new RgbaColor(144, 238, 144);
	RgbaColor LIGHT_GREY = new RgbaColor(211, 211, 211);
	RgbaColor LIGHT_PINK = new RgbaColor(255, 182, 193);
	RgbaColor LIGHT_SALMON = new RgbaColor(255, 160, 122);
	RgbaColor LIGHT_SEA_GREEN = new RgbaColor(32, 178, 170);
	RgbaColor LIGHT_SKY_BLUE = new RgbaColor(135, 206, 250);
	RgbaColor LIGHT_SLATE_GRAY = new RgbaColor(119, 136, 153);
	RgbaColor LIGHT_SLATE_GREY = new RgbaColor(119, 136, 153);
	RgbaColor LIGHT_STEEL_BLUE = new RgbaColor(176, 196, 222);
	RgbaColor LIGHT_YELLOW = new RgbaColor(255, 255, 224);
	RgbaColor LIME = new RgbaColor(0, 255, 0);
	RgbaColor LIME_GREEN = new RgbaColor(50, 205, 50);
	RgbaColor LINEN = new RgbaColor(250, 240, 230);
	RgbaColor MAGENTA = new RgbaColor(255, 0, 255);
	RgbaColor MAROON = new RgbaColor(128, 0, 0);
	RgbaColor MEDIUM_AQUA_MARINE = new RgbaColor(102, 205, 170);
	RgbaColor MEDIUM_BLUE = new RgbaColor(0, 0, 205);
	RgbaColor MEDIUM_ORCHID = new RgbaColor(186, 85, 211);
	RgbaColor MEDIUM_PURPLE = new RgbaColor(147, 112, 219);
	RgbaColor MEDIUM_SEAG_REEN = new RgbaColor(60, 179, 113);
	RgbaColor MEDIUM_SLATE_BLUE = new RgbaColor(123, 104, 238);
	RgbaColor MEDIUM_SPRING_GREEN = new RgbaColor(0, 250, 154);
	RgbaColor MEDIUM_TURQUOISE = new RgbaColor(72, 209, 204);
	RgbaColor MEDIUM_VIOLET_RED = new RgbaColor(199, 21, 133);
	RgbaColor MIDNIGHT_BLUE = new RgbaColor(25, 25, 112);
	RgbaColor MINT_CREAM = new RgbaColor(245, 255, 250);
	RgbaColor MISTY_ROSE = new RgbaColor(255, 228, 225);
	RgbaColor MOCCASIN = new RgbaColor(255, 228, 181);
	RgbaColor NAVAJO_WHITE = new RgbaColor(255, 222, 173);
	RgbaColor NAVY = new RgbaColor(0, 0, 128);
	RgbaColor OLD_LACE = new RgbaColor(253, 245, 230);
	RgbaColor OLIVE = new RgbaColor(128, 128, 0);
	RgbaColor OLIVE_DRAB = new RgbaColor(107, 142, 35);
	RgbaColor ORANGE = new RgbaColor(255, 165, 0);
	RgbaColor ORANGE_RED = new RgbaColor(255, 69, 0);
	RgbaColor ORCHID = new RgbaColor(218, 112, 214);
	RgbaColor PALE_GOLDEN_ROD = new RgbaColor(238, 232, 170);
	RgbaColor PALE_GREEN = new RgbaColor(152, 251, 152);
	RgbaColor PALE_TURQUOISE = new RgbaColor(175, 238, 238);
	RgbaColor PALE_VIOLET_RED = new RgbaColor(219, 112, 147);
	RgbaColor PAPAYA_WHIP = new RgbaColor(255, 239, 213);
	RgbaColor PEACH_PUFF = new RgbaColor(255, 218, 185);
	RgbaColor PERU = new RgbaColor(205, 133, 63);
	RgbaColor PINK = new RgbaColor(255, 192, 203);
	RgbaColor PLUM = new RgbaColor(221, 160, 221);
	RgbaColor POWDER_BLUE = new RgbaColor(176, 224, 230);
	RgbaColor PURPLE = new RgbaColor(128, 0, 128);
	RgbaColor RED = new RgbaColor(255, 0, 0);
	RgbaColor ROSY_BROWN = new RgbaColor(188, 143, 143);
	RgbaColor ROYAL_BLUE = new RgbaColor(65, 105, 225);
	RgbaColor SADDLE_BROWN = new RgbaColor(139, 69, 19);
	RgbaColor SALMON = new RgbaColor(250, 128, 114);
	RgbaColor SANDY_BROWN = new RgbaColor(244, 164, 96);
	RgbaColor SEA_GREEN = new RgbaColor(46, 139, 87);
	RgbaColor SEA_SHELL = new RgbaColor(255, 245, 238);
	RgbaColor SIENNA = new RgbaColor(160, 82, 45);
	RgbaColor SILVER = new RgbaColor(192, 192, 192);
	RgbaColor SKY_BLUE = new RgbaColor(135, 206, 235);
	RgbaColor SLATE_BLUE = new RgbaColor(106, 90, 205);
	RgbaColor SLATE_GRAY = new RgbaColor(112, 128, 144);
	RgbaColor SLATE_GREY = new RgbaColor(112, 128, 144);
	RgbaColor SNOW = new RgbaColor(255, 250, 250);
	RgbaColor SPRING_GREEN = new RgbaColor(0, 255, 127);
	RgbaColor STEEL_BLUE = new RgbaColor(70, 130, 180);
	RgbaColor TAN = new RgbaColor(210, 180, 140);
	RgbaColor TEAL = new RgbaColor(0, 128, 128);
	RgbaColor THISTLE = new RgbaColor(216, 191, 216);
	RgbaColor TOMATO = new RgbaColor(255, 99, 71);
	RgbaColor TURQUOISE = new RgbaColor(64, 224, 208);
	RgbaColor VIOLET = new RgbaColor(238, 130, 238);
	RgbaColor WHEAT = new RgbaColor(245, 222, 179);
	RgbaColor WHITE = new RgbaColor(255, 255, 255);
	RgbaColor WHITE_SMOKE = new RgbaColor(245, 245, 245);
	RgbaColor YELLOW = new RgbaColor(255, 255, 0);
	RgbaColor YELLOW_GREEN = new RgbaColor(154, 205, 50);

	RgbaColor GRAY_STANDARD = new RgbaColor(119, 119, 119);
	RgbaColor DIM_GRAY = new RgbaColor(105, 105, 105);

	CssStringColor PRIMARY = fromVariableName("--ta-color-primary");
	CssStringColor SUCCESS = fromVariableName("--ta-color-success");
	CssStringColor INFO = fromVariableName("--ta-color-info");
	CssStringColor WARNING = fromVariableName("--ta-color-warning");
	CssStringColor DANGER = fromVariableName("--ta-color-danger");

	RgbaColor MATERIAL_RED_50 = new RgbaColor(255, 235, 238);
	RgbaColor MATERIAL_RED_100 = new RgbaColor(255, 205, 210);
	RgbaColor MATERIAL_RED_200 = new RgbaColor(239, 154, 154);
	RgbaColor MATERIAL_RED_300 = new RgbaColor(229, 115, 115);
	RgbaColor MATERIAL_RED_400 = new RgbaColor(239, 83, 80);
	RgbaColor MATERIAL_RED_500 = new RgbaColor(244, 67, 54);
	RgbaColor MATERIAL_RED_600 = new RgbaColor(229, 57, 53);
	RgbaColor MATERIAL_RED_700 = new RgbaColor(211, 47, 47);
	RgbaColor MATERIAL_RED_800 = new RgbaColor(198, 40, 40);
	RgbaColor MATERIAL_RED_900 = new RgbaColor(183, 28, 28);
	RgbaColor MATERIAL_RED_A100 = new RgbaColor(255, 138, 128);
	RgbaColor MATERIAL_RED_A200 = new RgbaColor(255, 82, 82);
	RgbaColor MATERIAL_RED_A400 = new RgbaColor(255, 23, 68);
	RgbaColor MATERIAL_RED_A700 = new RgbaColor(213, 0, 0);
	RgbaColor MATERIAL_PINK_50 = new RgbaColor(252, 228, 236);
	RgbaColor MATERIAL_PINK_100 = new RgbaColor(248, 187, 208);
	RgbaColor MATERIAL_PINK_200 = new RgbaColor(244, 143, 177);
	RgbaColor MATERIAL_PINK_300 = new RgbaColor(240, 98, 146);
	RgbaColor MATERIAL_PINK_400 = new RgbaColor(236, 64, 122);
	RgbaColor MATERIAL_PINK_500 = new RgbaColor(233, 30, 99);
	RgbaColor MATERIAL_PINK_600 = new RgbaColor(216, 27, 96);
	RgbaColor MATERIAL_PINK_700 = new RgbaColor(194, 24, 91);
	RgbaColor MATERIAL_PINK_800 = new RgbaColor(173, 20, 87);
	RgbaColor MATERIAL_PINK_900 = new RgbaColor(136, 14, 79);
	RgbaColor MATERIAL_PINK_A100 = new RgbaColor(255, 128, 171);
	RgbaColor MATERIAL_PINK_A200 = new RgbaColor(255, 64, 129);
	RgbaColor MATERIAL_PINK_A400 = new RgbaColor(245, 0, 87);
	RgbaColor MATERIAL_PINK_A700 = new RgbaColor(197, 17, 98);
	RgbaColor MATERIAL_PURPLE_50 = new RgbaColor(243, 229, 245);
	RgbaColor MATERIAL_PURPLE_100 = new RgbaColor(225, 190, 231);
	RgbaColor MATERIAL_PURPLE_200 = new RgbaColor(206, 147, 216);
	RgbaColor MATERIAL_PURPLE_300 = new RgbaColor(186, 104, 200);
	RgbaColor MATERIAL_PURPLE_400 = new RgbaColor(171, 71, 188);
	RgbaColor MATERIAL_PURPLE_500 = new RgbaColor(156, 39, 176);
	RgbaColor MATERIAL_PURPLE_600 = new RgbaColor(142, 36, 170);
	RgbaColor MATERIAL_PURPLE_700 = new RgbaColor(123, 31, 162);
	RgbaColor MATERIAL_PURPLE_800 = new RgbaColor(106, 27, 154);
	RgbaColor MATERIAL_PURPLE_900 = new RgbaColor(74, 20, 140);
	RgbaColor MATERIAL_PURPLE_A100 = new RgbaColor(234, 128, 252);
	RgbaColor MATERIAL_PURPLE_A200 = new RgbaColor(224, 64, 251);
	RgbaColor MATERIAL_PURPLE_A400 = new RgbaColor(213, 0, 249);
	RgbaColor MATERIAL_PURPLE_A700 = new RgbaColor(170, 0, 255);
	RgbaColor MATERIAL_DEEP_PURPLE_50 = new RgbaColor(237, 231, 246);
	RgbaColor MATERIAL_DEEP_PURPLE_100 = new RgbaColor(209, 196, 233);
	RgbaColor MATERIAL_DEEP_PURPLE_200 = new RgbaColor(179, 157, 219);
	RgbaColor MATERIAL_DEEP_PURPLE_300 = new RgbaColor(149, 117, 205);
	RgbaColor MATERIAL_DEEP_PURPLE_400 = new RgbaColor(126, 87, 194);
	RgbaColor MATERIAL_DEEP_PURPLE_500 = new RgbaColor(103, 58, 183);
	RgbaColor MATERIAL_DEEP_PURPLE_600 = new RgbaColor(94, 53, 177);
	RgbaColor MATERIAL_DEEP_PURPLE_700 = new RgbaColor(81, 45, 168);
	RgbaColor MATERIAL_DEEP_PURPLE_800 = new RgbaColor(69, 39, 160);
	RgbaColor MATERIAL_DEEP_PURPLE_900 = new RgbaColor(49, 27, 146);
	RgbaColor MATERIAL_DEEP_PURPLE_A100 = new RgbaColor(179, 136, 255);
	RgbaColor MATERIAL_DEEP_PURPLE_A200 = new RgbaColor(124, 77, 255);
	RgbaColor MATERIAL_DEEP_PURPLE_A400 = new RgbaColor(101, 31, 255);
	RgbaColor MATERIAL_DEEP_PURPLE_A700 = new RgbaColor(98, 0, 234);
	RgbaColor MATERIAL_INDIGO_50 = new RgbaColor(232, 234, 246);
	RgbaColor MATERIAL_INDIGO_100 = new RgbaColor(197, 202, 233);
	RgbaColor MATERIAL_INDIGO_200 = new RgbaColor(159, 168, 218);
	RgbaColor MATERIAL_INDIGO_300 = new RgbaColor(121, 134, 203);
	RgbaColor MATERIAL_INDIGO_400 = new RgbaColor(92, 107, 192);
	RgbaColor MATERIAL_INDIGO_500 = new RgbaColor(63, 81, 181);
	RgbaColor MATERIAL_INDIGO_600 = new RgbaColor(57, 73, 171);
	RgbaColor MATERIAL_INDIGO_700 = new RgbaColor(48, 63, 159);
	RgbaColor MATERIAL_INDIGO_800 = new RgbaColor(40, 53, 147);
	RgbaColor MATERIAL_INDIGO_900 = new RgbaColor(26, 35, 126);
	RgbaColor MATERIAL_INDIGO_A100 = new RgbaColor(140, 158, 255);
	RgbaColor MATERIAL_INDIGO_A200 = new RgbaColor(83, 109, 254);
	RgbaColor MATERIAL_INDIGO_A400 = new RgbaColor(61, 90, 254);
	RgbaColor MATERIAL_INDIGO_A700 = new RgbaColor(48, 79, 254);
	RgbaColor MATERIAL_BLUE_50 = new RgbaColor(227, 242, 253);
	RgbaColor MATERIAL_BLUE_100 = new RgbaColor(187, 222, 251);
	RgbaColor MATERIAL_BLUE_200 = new RgbaColor(144, 202, 249);
	RgbaColor MATERIAL_BLUE_300 = new RgbaColor(100, 181, 246);
	RgbaColor MATERIAL_BLUE_400 = new RgbaColor(66, 165, 245);
	RgbaColor MATERIAL_BLUE_500 = new RgbaColor(33, 150, 243);
	RgbaColor MATERIAL_BLUE_600 = new RgbaColor(30, 136, 229);
	RgbaColor MATERIAL_BLUE_700 = new RgbaColor(25, 118, 210);
	RgbaColor MATERIAL_BLUE_800 = new RgbaColor(21, 101, 192);
	RgbaColor MATERIAL_BLUE_900 = new RgbaColor(13, 71, 161);
	RgbaColor MATERIAL_BLUE_A100 = new RgbaColor(130, 177, 255);
	RgbaColor MATERIAL_BLUE_A200 = new RgbaColor(68, 138, 255);
	RgbaColor MATERIAL_BLUE_A400 = new RgbaColor(41, 121, 255);
	RgbaColor MATERIAL_BLUE_A700 = new RgbaColor(41, 98, 255);
	RgbaColor MATERIAL_LIGHT_BLUE_50 = new RgbaColor(225, 245, 254);
	RgbaColor MATERIAL_LIGHT_BLUE_100 = new RgbaColor(179, 229, 252);
	RgbaColor MATERIAL_LIGHT_BLUE_200 = new RgbaColor(129, 212, 250);
	RgbaColor MATERIAL_LIGHT_BLUE_300 = new RgbaColor(79, 195, 247);
	RgbaColor MATERIAL_LIGHT_BLUE_400 = new RgbaColor(41, 182, 252);
	RgbaColor MATERIAL_LIGHT_BLUE_500 = new RgbaColor(3, 169, 244);
	RgbaColor MATERIAL_LIGHT_BLUE_600 = new RgbaColor(3, 155, 229);
	RgbaColor MATERIAL_LIGHT_BLUE_700 = new RgbaColor(2, 136, 209);
	RgbaColor MATERIAL_LIGHT_BLUE_800 = new RgbaColor(2, 119, 189);
	RgbaColor MATERIAL_LIGHT_BLUE_900 = new RgbaColor(1, 87, 155);
	RgbaColor MATERIAL_LIGHT_BLUE_A100 = new RgbaColor(128, 216, 255);
	RgbaColor MATERIAL_LIGHT_BLUE_A200 = new RgbaColor(64, 196, 255);
	RgbaColor MATERIAL_LIGHT_BLUE_A400 = new RgbaColor(0, 176, 255);
	RgbaColor MATERIAL_LIGHT_BLUE_A700 = new RgbaColor(0, 145, 234);
	RgbaColor MATERIAL_CYAN_50 = new RgbaColor(224, 247, 250);
	RgbaColor MATERIAL_CYAN_100 = new RgbaColor(178, 235, 242);
	RgbaColor MATERIAL_CYAN_200 = new RgbaColor(128, 222, 234);
	RgbaColor MATERIAL_CYAN_300 = new RgbaColor(77, 208, 225);
	RgbaColor MATERIAL_CYAN_400 = new RgbaColor(38, 198, 218);
	RgbaColor MATERIAL_CYAN_500 = new RgbaColor(0, 188, 212);
	RgbaColor MATERIAL_CYAN_600 = new RgbaColor(0, 172, 193);
	RgbaColor MATERIAL_CYAN_700 = new RgbaColor(0, 151, 167);
	RgbaColor MATERIAL_CYAN_800 = new RgbaColor(0, 131, 143);
	RgbaColor MATERIAL_CYAN_900 = new RgbaColor(0, 96, 100);
	RgbaColor MATERIAL_CYAN_A100 = new RgbaColor(132, 255, 255);
	RgbaColor MATERIAL_CYAN_A200 = new RgbaColor(24, 255, 255);
	RgbaColor MATERIAL_CYAN_A400 = new RgbaColor(0, 229, 255);
	RgbaColor MATERIAL_CYAN_A700 = new RgbaColor(0, 184, 212);
	RgbaColor MATERIAL_TEAL_50 = new RgbaColor(224, 242, 241);
	RgbaColor MATERIAL_TEAL_100 = new RgbaColor(178, 223, 219);
	RgbaColor MATERIAL_TEAL_200 = new RgbaColor(128, 203, 196);
	RgbaColor MATERIAL_TEAL_300 = new RgbaColor(77, 182, 172);
	RgbaColor MATERIAL_TEAL_400 = new RgbaColor(38, 166, 154);
	RgbaColor MATERIAL_TEAL_500 = new RgbaColor(0, 150, 136);
	RgbaColor MATERIAL_TEAL_600 = new RgbaColor(0, 137, 123);
	RgbaColor MATERIAL_TEAL_700 = new RgbaColor(0, 121, 107);
	RgbaColor MATERIAL_TEAL_800 = new RgbaColor(0, 105, 92);
	RgbaColor MATERIAL_TEAL_900 = new RgbaColor(0, 77, 64);
	RgbaColor MATERIAL_TEAL_A100 = new RgbaColor(167, 255, 235);
	RgbaColor MATERIAL_TEAL_A200 = new RgbaColor(100, 255, 218);
	RgbaColor MATERIAL_TEAL_A400 = new RgbaColor(29, 233, 182);
	RgbaColor MATERIAL_TEAL_A700 = new RgbaColor(0, 191, 165);
	RgbaColor MATERIAL_GREEN_50 = new RgbaColor(232, 245, 233);
	RgbaColor MATERIAL_GREEN_100 = new RgbaColor(200, 230, 201);
	RgbaColor MATERIAL_GREEN_200 = new RgbaColor(165, 214, 167);
	RgbaColor MATERIAL_GREEN_300 = new RgbaColor(129, 199, 132);
	RgbaColor MATERIAL_GREEN_400 = new RgbaColor(102, 187, 106);
	RgbaColor MATERIAL_GREEN_500 = new RgbaColor(76, 175, 80);
	RgbaColor MATERIAL_GREEN_600 = new RgbaColor(67, 160, 71);
	RgbaColor MATERIAL_GREEN_700 = new RgbaColor(56, 142, 60);
	RgbaColor MATERIAL_GREEN_800 = new RgbaColor(46, 125, 50);
	RgbaColor MATERIAL_GREEN_900 = new RgbaColor(27, 94, 32);
	RgbaColor MATERIAL_GREEN_A100 = new RgbaColor(185, 246, 202);
	RgbaColor MATERIAL_GREEN_A200 = new RgbaColor(105, 240, 174);
	RgbaColor MATERIAL_GREEN_A400 = new RgbaColor(0, 230, 118);
	RgbaColor MATERIAL_GREEN_A700 = new RgbaColor(0, 200, 83);
	RgbaColor MATERIAL_LIGHT_GREEN_50 = new RgbaColor(241, 248, 233);
	RgbaColor MATERIAL_LIGHT_GREEN_100 = new RgbaColor(220, 237, 200);
	RgbaColor MATERIAL_LIGHT_GREEN_200 = new RgbaColor(197, 225, 165);
	RgbaColor MATERIAL_LIGHT_GREEN_300 = new RgbaColor(174, 213, 129);
	RgbaColor MATERIAL_LIGHT_GREEN_400 = new RgbaColor(156, 204, 101);
	RgbaColor MATERIAL_LIGHT_GREEN_500 = new RgbaColor(139, 195, 74);
	RgbaColor MATERIAL_LIGHT_GREEN_600 = new RgbaColor(124, 179, 66);
	RgbaColor MATERIAL_LIGHT_GREEN_700 = new RgbaColor(104, 159, 56);
	RgbaColor MATERIAL_LIGHT_GREEN_800 = new RgbaColor(85, 139, 47);
	RgbaColor MATERIAL_LIGHT_GREEN_900 = new RgbaColor(51, 105, 30);
	RgbaColor MATERIAL_LIGHT_GREEN_A100 = new RgbaColor(204, 255, 144);
	RgbaColor MATERIAL_LIGHT_GREEN_A200 = new RgbaColor(178, 255, 89);
	RgbaColor MATERIAL_LIGHT_GREEN_A400 = new RgbaColor(118, 255, 3);
	RgbaColor MATERIAL_LIGHT_GREEN_A700 = new RgbaColor(100, 221, 23);
	RgbaColor MATERIAL_LIME_50 = new RgbaColor(249, 251, 231);
	RgbaColor MATERIAL_LIME_100 = new RgbaColor(240, 244, 195);
	RgbaColor MATERIAL_LIME_200 = new RgbaColor(230, 238, 156);
	RgbaColor MATERIAL_LIME_300 = new RgbaColor(220, 231, 117);
	RgbaColor MATERIAL_LIME_400 = new RgbaColor(212, 225, 87);
	RgbaColor MATERIAL_LIME_500 = new RgbaColor(205, 220, 57);
	RgbaColor MATERIAL_LIME_600 = new RgbaColor(192, 202, 51);
	RgbaColor MATERIAL_LIME_700 = new RgbaColor(164, 180, 43);
	RgbaColor MATERIAL_LIME_800 = new RgbaColor(158, 157, 36);
	RgbaColor MATERIAL_LIME_900 = new RgbaColor(130, 119, 23);
	RgbaColor MATERIAL_LIME_A100 = new RgbaColor(244, 255, 129);
	RgbaColor MATERIAL_LIME_A200 = new RgbaColor(238, 255, 65);
	RgbaColor MATERIAL_LIME_A400 = new RgbaColor(198, 255, 0);
	RgbaColor MATERIAL_LIME_A700 = new RgbaColor(174, 234, 0);
	RgbaColor MATERIAL_YELLOW_50 = new RgbaColor(255, 253, 231);
	RgbaColor MATERIAL_YELLOW_100 = new RgbaColor(255, 249, 196);
	RgbaColor MATERIAL_YELLOW_200 = new RgbaColor(255, 245, 144);
	RgbaColor MATERIAL_YELLOW_300 = new RgbaColor(255, 241, 118);
	RgbaColor MATERIAL_YELLOW_400 = new RgbaColor(255, 238, 88);
	RgbaColor MATERIAL_YELLOW_500 = new RgbaColor(255, 235, 59);
	RgbaColor MATERIAL_YELLOW_600 = new RgbaColor(253, 216, 53);
	RgbaColor MATERIAL_YELLOW_700 = new RgbaColor(251, 192, 45);
	RgbaColor MATERIAL_YELLOW_800 = new RgbaColor(249, 168, 37);
	RgbaColor MATERIAL_YELLOW_900 = new RgbaColor(245, 127, 23);
	RgbaColor MATERIAL_YELLOW_A100 = new RgbaColor(255, 255, 130);
	RgbaColor MATERIAL_YELLOW_A200 = new RgbaColor(255, 255, 0);
	RgbaColor MATERIAL_YELLOW_A400 = new RgbaColor(255, 234, 0);
	RgbaColor MATERIAL_YELLOW_A700 = new RgbaColor(255, 214, 0);
	RgbaColor MATERIAL_AMBER_50 = new RgbaColor(255, 248, 225);
	RgbaColor MATERIAL_AMBER_100 = new RgbaColor(255, 236, 179);
	RgbaColor MATERIAL_AMBER_200 = new RgbaColor(255, 224, 130);
	RgbaColor MATERIAL_AMBER_300 = new RgbaColor(255, 213, 79);
	RgbaColor MATERIAL_AMBER_400 = new RgbaColor(255, 202, 40);
	RgbaColor MATERIAL_AMBER_500 = new RgbaColor(255, 193, 7);
	RgbaColor MATERIAL_AMBER_600 = new RgbaColor(255, 179, 0);
	RgbaColor MATERIAL_AMBER_700 = new RgbaColor(255, 160, 0);
	RgbaColor MATERIAL_AMBER_800 = new RgbaColor(255, 143, 0);
	RgbaColor MATERIAL_AMBER_900 = new RgbaColor(255, 111, 0);
	RgbaColor MATERIAL_AMBER_A100 = new RgbaColor(255, 229, 127);
	RgbaColor MATERIAL_AMBER_A200 = new RgbaColor(255, 215, 64);
	RgbaColor MATERIAL_AMBER_A400 = new RgbaColor(255, 196, 0);
	RgbaColor MATERIAL_AMBER_A700 = new RgbaColor(255, 171, 0);
	RgbaColor MATERIAL_ORANGE_50 = new RgbaColor(255, 243, 224);
	RgbaColor MATERIAL_ORANGE_100 = new RgbaColor(255, 224, 178);
	RgbaColor MATERIAL_ORANGE_200 = new RgbaColor(255, 204, 128);
	RgbaColor MATERIAL_ORANGE_300 = new RgbaColor(255, 183, 77);
	RgbaColor MATERIAL_ORANGE_400 = new RgbaColor(255, 167, 38);
	RgbaColor MATERIAL_ORANGE_500 = new RgbaColor(255, 152, 0);
	RgbaColor MATERIAL_ORANGE_600 = new RgbaColor(251, 140, 0);
	RgbaColor MATERIAL_ORANGE_700 = new RgbaColor(245, 124, 0);
	RgbaColor MATERIAL_ORANGE_800 = new RgbaColor(239, 108, 0);
	RgbaColor MATERIAL_ORANGE_900 = new RgbaColor(230, 81, 0);
	RgbaColor MATERIAL_ORANGE_A100 = new RgbaColor(255, 209, 128);
	RgbaColor MATERIAL_ORANGE_A200 = new RgbaColor(255, 171, 64);
	RgbaColor MATERIAL_ORANGE_A400 = new RgbaColor(255, 145, 0);
	RgbaColor MATERIAL_ORANGE_A700 = new RgbaColor(255, 109, 0);
	RgbaColor MATERIAL_DEEP_ORANGE_50 = new RgbaColor(251, 233, 167);
	RgbaColor MATERIAL_DEEP_ORANGE_100 = new RgbaColor(255, 204, 188);
	RgbaColor MATERIAL_DEEP_ORANGE_200 = new RgbaColor(255, 171, 145);
	RgbaColor MATERIAL_DEEP_ORANGE_300 = new RgbaColor(255, 138, 101);
	RgbaColor MATERIAL_DEEP_ORANGE_400 = new RgbaColor(255, 112, 67);
	RgbaColor MATERIAL_DEEP_ORANGE_500 = new RgbaColor(255, 87, 34);
	RgbaColor MATERIAL_DEEP_ORANGE_600 = new RgbaColor(244, 81, 30);
	RgbaColor MATERIAL_DEEP_ORANGE_700 = new RgbaColor(230, 74, 25);
	RgbaColor MATERIAL_DEEP_ORANGE_800 = new RgbaColor(216, 67, 21);
	RgbaColor MATERIAL_DEEP_ORANGE_900 = new RgbaColor(191, 54, 12);
	RgbaColor MATERIAL_DEEP_ORANGE_A100 = new RgbaColor(255, 158, 128);
	RgbaColor MATERIAL_DEEP_ORANGE_A200 = new RgbaColor(255, 110, 64);
	RgbaColor MATERIAL_DEEP_ORANGE_A400 = new RgbaColor(255, 61, 0);
	RgbaColor MATERIAL_DEEP_ORANGE_A700 = new RgbaColor(221, 38, 0);
	RgbaColor MATERIAL_BROWN_50 = new RgbaColor(239, 235, 233);
	RgbaColor MATERIAL_BROWN_100 = new RgbaColor(215, 204, 200);
	RgbaColor MATERIAL_BROWN_200 = new RgbaColor(188, 170, 164);
	RgbaColor MATERIAL_BROWN_300 = new RgbaColor(161, 136, 127);
	RgbaColor MATERIAL_BROWN_400 = new RgbaColor(141, 110, 99);
	RgbaColor MATERIAL_BROWN_500 = new RgbaColor(121, 85, 72);
	RgbaColor MATERIAL_BROWN_600 = new RgbaColor(109, 76, 65);
	RgbaColor MATERIAL_BROWN_700 = new RgbaColor(93, 64, 55);
	RgbaColor MATERIAL_BROWN_800 = new RgbaColor(78, 52, 46);
	RgbaColor MATERIAL_BROWN_900 = new RgbaColor(62, 39, 35);
	RgbaColor MATERIAL_GREY_50 = new RgbaColor(250, 250, 250);
	RgbaColor MATERIAL_GREY_100 = new RgbaColor(245, 245, 245);
	RgbaColor MATERIAL_GREY_200 = new RgbaColor(238, 238, 238);
	RgbaColor MATERIAL_GREY_300 = new RgbaColor(224, 224, 224);
	RgbaColor MATERIAL_GREY_400 = new RgbaColor(189, 189, 189);
	RgbaColor MATERIAL_GREY_500 = new RgbaColor(158, 158, 158);
	RgbaColor MATERIAL_GREY_600 = new RgbaColor(117, 117, 117);
	RgbaColor MATERIAL_GREY_700 = new RgbaColor(97, 97, 97);
	RgbaColor MATERIAL_GREY_800 = new RgbaColor(66, 66, 66);
	RgbaColor MATERIAL_GREY_900 = new RgbaColor(33, 33, 33);
	RgbaColor MATERIAL_BLACK_1000 = new RgbaColor(0, 0, 0);
	RgbaColor MATERIAL_WHITE_1000 = new RgbaColor(255, 255, 255);
	RgbaColor MATERIAL_BLUE_GREY_50 = new RgbaColor(236, 239, 241);
	RgbaColor MATERIAL_BLUE_GREY_100 = new RgbaColor(207, 216, 220);
	RgbaColor MATERIAL_BLUE_GREY_200 = new RgbaColor(176, 187, 197);
	RgbaColor MATERIAL_BLUE_GREY_300 = new RgbaColor(144, 164, 174);
	RgbaColor MATERIAL_BLUE_GREY_400 = new RgbaColor(120, 144, 156);
	RgbaColor MATERIAL_BLUE_GREY_500 = new RgbaColor(96, 125, 139);
	RgbaColor MATERIAL_BLUE_GREY_600 = new RgbaColor(84, 110, 122);
	RgbaColor MATERIAL_BLUE_GREY_700 = new RgbaColor(69, 90, 100);
	RgbaColor MATERIAL_BLUE_GREY_800 = new RgbaColor(55, 71, 79);
	RgbaColor MATERIAL_BLUE_GREY_900 = new RgbaColor(38, 50, 56);

	static CssStringColor fromVariableName(String variableName) {
		return CssStringColor.fromVariableName(variableName);
	}

	static RgbaColor fromAwtColor(java.awt.Color color) {
		return RgbaColor.fromAwtColor(color);
	}

	static RgbaColor fromAwtColor(java.awt.Color color, float alpha) {
		return RgbaColor.fromAwtColor(color, alpha);
	}

	static RgbaColor fromHsba(float h, float s, float b, float alpha) {
		return RgbaColor.fromHsba(h, s, b, alpha);
	}

	static RgbaColor fromRgb(int red, int green, int blue) {
		return new RgbaColor(red, green, blue);
	}

	static RgbaColor fromRgba(int red, int green, int blue, float alpha) {
		return new RgbaColor(red, green, blue, alpha);
	}

	static RgbaColor fromRgbValue(int rgb) {
		return RgbaColor.fromRgbValue(rgb);
	}

	static RgbaColor fromRgbaValue(int rgba) {
		return RgbaColor.fromRgbaValue(rgba);
	}

	static RgbaColor fromHex(String hex) {
		return RgbaColor.fromHex(hex);
	}

	static Color fromHtmlString(String value) {
		if (value.trim().startsWith("var(")) {
			return new CssStringColor(value);
		} else {
			return RgbaColor.fromHtmlString(value);
		}
	}

}
