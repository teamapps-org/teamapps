/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.icons.systemprovider;

import org.teamapps.icons.api.CustomIconStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class StylingIconProvider {

	private static final Color COLOR_1 = new Color(255, 30, 30);
	private static final Color COLOR_2 = new Color(30, 255, 30);
	private static final Color COLOR_3 = new Color(30, 30, 255);
	private static final Color COLOR_4 = new Color(255, 255, 30);
	private static final Color COLOR_5 = new Color(255, 30, 255);

	private static final Color[] SEARCH_COLORS = new Color[] {COLOR_1, COLOR_2, COLOR_3, COLOR_4, COLOR_5};
	private static final Color[] SEARCH_COLORS_WITH_BASE = new Color[] {Color.BLACK, Color.WHITE, COLOR_1, COLOR_2, COLOR_3, COLOR_4, COLOR_5};

	public static final Color WHITE = new Color(255, 255, 255, 1);
	public static final Color BLACK = new Color(0, 0, 0, 1);

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

	public byte[] styleIcon(CustomIconStyle customIconStyle, byte[] imageBytes) {
		return convertImage(imageBytes, customIconStyle.getSearchColors(), customIconStyle.getReplaceColors(), customIconStyle.getMatchFuzzinessThreshold());
	}

	public byte[] convertImage(byte[] imageBytes, Color[] search, Color[] replace, int colorMatchingThreshold) {
		try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
			//colorImage(image, search, replace, colorMatchingThreshold);
			recolorImage(image, search, replace, colorMatchingThreshold);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", bos);
			return bos.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void colorImage(BufferedImage image, Color[] search, Color[] replace, int threshold) {
		int width = image.getWidth();
		int height = image.getHeight();
		WritableRaster raster = image.getRaster();
		for (int xx = 0; xx < width; xx++) {
			for (int yy = 0; yy < height; yy++) {
				int[] col = raster.getPixel(xx, yy, (int[]) null);
				for (int i = 0; i < search.length; i++) {
					if (isMatch(col, search[i], threshold)) {
						replace(col, replace[i]);
						raster.setPixel(xx, yy, col);
					}
				}
			}
		}
	}

	private boolean isMatch(int[] col, Color color, int threshold) {
		int diffRed = Math.abs(col[0] - color.getRed());
		int diffGreen = Math.abs(col[1] - color.getGreen());
		int difBlue = Math.abs(col[2] - color.getBlue());
		int maxDiff = threshold;
		return diffRed < maxDiff && diffGreen < maxDiff && difBlue < maxDiff;
	}

	private void recolorImage(BufferedImage image, Color[] search, Color[] replace, int threshold) {
		int width = image.getWidth();
		int height = image.getHeight();
		WritableRaster raster = image.getRaster();
		for (int xx = 0; xx < width; xx++) {
			for (int yy = 0; yy < height; yy++) {
				int[] col = raster.getPixel(xx, yy, (int[]) null);
				int bestMatchIndex = -1;
				float bestMatch = 1000;
				for (int i = 0; i < replace.length; i++) {
					float matchDiff = calculateMatchDiff(col, search[i], threshold);
					if (matchDiff >= 0 && matchDiff < bestMatch) {
						bestMatchIndex = i;
						bestMatch = matchDiff;
					}
				}
				if (bestMatchIndex >= 0) {
					int[] convertedColor = convertColor(col, search[bestMatchIndex], replace[bestMatchIndex], threshold);
					if (convertedColor != null) {
						raster.setPixel(xx, yy, convertedColor);
					}
				}
			}
		}
	}

	private float calculateMatchDiff(int[] rasterColor, Color searchColor, int threshold) {
		float[] hsb = Color.RGBtoHSB(rasterColor[0], rasterColor[1], rasterColor[2], null);
		float hue = hsb[0];
		float saturation = hsb[1];
		float brightness = hsb[2];

		float[] hsbSearch = Color.RGBtoHSB(searchColor.getRed(), searchColor.getGreen(), searchColor.getBlue(), null);
		float hueSearch = hsbSearch[0];

		return Math.abs(hue - hueSearch);
	}

	private int[] convertColor(int[] rasterColor, Color searchColor, Color replaceColor, int threshold) {
		float[] hsb = Color.RGBtoHSB(rasterColor[0], rasterColor[1], rasterColor[2], null);
		float[] hsbSearch = Color.RGBtoHSB(searchColor.getRed(), searchColor.getGreen(), searchColor.getBlue(), null);

		float diffHue = hsbSearch[0] - hsb[0];
		float diffSaturation = hsbSearch[1] - hsb[1];
		float diffBrightness = hsbSearch[2] - hsb[2];


		float[] hsbReplace = Color.RGBtoHSB(replaceColor.getRed(), replaceColor.getGreen(), replaceColor.getBlue(), null);

		float hue = hsbReplace[0] - diffHue;

		float saturation = hsbReplace[1] - diffSaturation;
		if (saturation > 1) saturation = 1;
		if (saturation < 0) saturation = 0;
		float brightness = hsbReplace[2] - diffBrightness;
		if (brightness > 1) brightness = 1;
		if (brightness < 0) brightness = 0;

		Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
		rasterColor[0] = hsbColor.getRed();
		rasterColor[1] = hsbColor.getGreen();
		rasterColor[2] = hsbColor.getBlue();
		return rasterColor;
	}

	private boolean isMatchExact(int[] col, Color color) {
		return col[0] == color.getRed() && col[1] == color.getGreen() && col[2] == color.getBlue();
	}

	private void replace(int[] col, Color color) {
		col[0] = color.getRed();
		col[1] = color.getGreen();
		col[2] = color.getBlue();
	}



}
