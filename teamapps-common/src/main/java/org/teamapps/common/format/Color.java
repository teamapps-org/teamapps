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
package org.teamapps.common.format;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public class Color {

	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	public static final Color ALICE_BLUE = new Color(240,248,255);
	public static final Color ANTIQUE_WHITE = new Color(250,235,215);
	public static final Color AQUA = new Color(0,255,255);
	public static final Color AQUA_MARINE = new Color(127,255,212);
	public static final Color AZURE = new Color(240,255,255);
	public static final Color BEIGE = new Color(245,245,220);
	public static final Color BISQUE = new Color(255,228,196);
	public static final Color BLACK = new Color(0,0,0);
	public static final Color BLANCHED_ALMOND = new Color(255,235,205);
	public static final Color BLUE = new Color(0,0,255);
	public static final Color BLUE_VIOLET = new Color(138,43,226);
	public static final Color BROWN = new Color(165,42,42);
	public static final Color BURLY_WOOD = new Color(222,184,135);
	public static final Color CADET_BLUE = new Color(95,158,160);
	public static final Color CHARTREUSE = new Color(127,255,0);
	public static final Color CHOCOLATE = new Color(210,105,30);
	public static final Color CORAL = new Color(255,127,80);
	public static final Color CORN_FLOWER_BLUE = new Color(100,149,237);
	public static final Color CORN_SILK = new Color(255,248,220);
	public static final Color CRIMSON = new Color(220,20,60);
	public static final Color CYAN = new Color(0,255,255);
	public static final Color DARK_BLUE = new Color(0,0,139);
	public static final Color DARK_CYAN = new Color(0,139,139);
	public static final Color DARK_GOLDEN_ROD = new Color(184,134,11);
	public static final Color DARK_GRAY = new Color(169,169,169);
	public static final Color DARK_GREEN = new Color(0,100,0);
	public static final Color DARK_GREY = new Color(169,169,169);
	public static final Color DARK_KHAKI = new Color(189,183,107);
	public static final Color DARK_MAGENTA = new Color(139,0,139);
	public static final Color DARK_OLIVE_GREEN = new Color(85,107,47);
	public static final Color DARK_ORANGE = new Color(255,140,0);
	public static final Color DARK_ORCHID = new Color(153,50,204);
	public static final Color DARK_RED = new Color(139,0,0);
	public static final Color DARK_SALMON = new Color(233,150,122);
	public static final Color DARK_SEA_GREEN = new Color(143,188,143);
	public static final Color DARK_SLATE_BLUE = new Color(72,61,139);
	public static final Color DARK_SLATE_GRAY = new Color(47,79,79);
	public static final Color DARK_SLATE_GREY = new Color(47,79,79);
	public static final Color DARK_TURQUOISE = new Color(0,206,209);
	public static final Color DARK_VIOLET = new Color(148,0,211);
	public static final Color DEEP_PINK = new Color(255,20,147);
	public static final Color DEEP_SKY_BLUE = new Color(0,191,255);
	public static final Color DIMG_RAY = new Color(105,105,105);
	public static final Color DIMG_REY = new Color(105,105,105);
	public static final Color DODGER_BLUE = new Color(30,144,255);
	public static final Color FIRE_BRICK = new Color(178,34,34);
	public static final Color FLORAL_WHITE = new Color(255,250,240);
	public static final Color FOREST_GREEN = new Color(34,139,34);
	public static final Color FUCHSIA = new Color(255,0,255);
	public static final Color GAINSBORO = new Color(220,220,220);
	public static final Color GHOST_WHITE = new Color(248,248,255);
	public static final Color GOLD = new Color(255,215,0);
	public static final Color GOLDEN_ROD = new Color(218,165,32);
	public static final Color GRAY = new Color(128,128,128);
	public static final Color GREEN = new Color(0,128,0);
	public static final Color GREEN_YELLOW = new Color(173,255,47);
	public static final Color GREY = new Color(128,128,128);
	public static final Color HONEY_DEW = new Color(240,255,240);
	public static final Color HOT_PINK = new Color(255,105,180);
	public static final Color INDIAN_RED = new Color(205,92,92);
	public static final Color INDIGO = new Color(75,0,130);
	public static final Color IVORY = new Color(255,255,240);
	public static final Color KHAKI = new Color(240,230,140);
	public static final Color LAVENDER = new Color(230,230,250);
	public static final Color LAVENDER_BLUSH = new Color(255,240,245);
	public static final Color LAWN_GREEN = new Color(124,252,0);
	public static final Color LEMON_CHIFFON = new Color(255,250,205);
	public static final Color LIGHT_BLUE = new Color(173,216,230);
	public static final Color LIGHT_CORAL = new Color(240,128,128);
	public static final Color LIGHT_CYAN = new Color(224,255,255);
	public static final Color LIGHT_GOLDEN_ROD_YELLOW = new Color(250,250,210);
	public static final Color LIGHT_GRAY = new Color(211,211,211);
	public static final Color LIGHT_GREEN = new Color(144,238,144);
	public static final Color LIGHT_GREY = new Color(211,211,211);
	public static final Color LIGHT_PINK = new Color(255,182,193);
	public static final Color LIGHT_SALMON = new Color(255,160,122);
	public static final Color LIGHT_SEA_GREEN = new Color(32,178,170);
	public static final Color LIGHT_SKY_BLUE = new Color(135,206,250);
	public static final Color LIGHT_SLATE_GRAY = new Color(119,136,153);
	public static final Color LIGHT_SLATE_GREY = new Color(119,136,153);
	public static final Color LIGHT_STEEL_BLUE = new Color(176,196,222);
	public static final Color LIGHT_YELLOW = new Color(255,255,224);
	public static final Color LIME = new Color(0,255,0);
	public static final Color LIME_GREEN = new Color(50,205,50);
	public static final Color LINEN = new Color(250,240,230);
	public static final Color MAGENTA = new Color(255,0,255);
	public static final Color MAROON = new Color(128,0,0);
	public static final Color MEDIUM_AQUA_MARINE = new Color(102,205,170);
	public static final Color MEDIUM_BLUE = new Color(0,0,205);
	public static final Color MEDIUM_ORCHID = new Color(186,85,211);
	public static final Color MEDIUM_PURPLE = new Color(147,112,219);
	public static final Color MEDIUM_SEAG_REEN = new Color(60,179,113);
	public static final Color MEDIUM_SLATE_BLUE = new Color(123,104,238);
	public static final Color MEDIUM_SPRING_GREEN = new Color(0,250,154);
	public static final Color MEDIUM_TURQUOISE = new Color(72,209,204);
	public static final Color MEDIUM_VIOLET_RED = new Color(199,21,133);
	public static final Color MIDNIGHT_BLUE = new Color(25,25,112);
	public static final Color MINT_CREAM = new Color(245,255,250);
	public static final Color MISTY_ROSE = new Color(255,228,225);
	public static final Color MOCCASIN = new Color(255,228,181);
	public static final Color NAVAJO_WHITE = new Color(255,222,173);
	public static final Color NAVY = new Color(0,0,128);
	public static final Color OLD_LACE = new Color(253,245,230);
	public static final Color OLIVE = new Color(128,128,0);
	public static final Color OLIVE_DRAB = new Color(107,142,35);
	public static final Color ORANGE = new Color(255,165,0);
	public static final Color ORANGE_RED = new Color(255,69,0);
	public static final Color ORCHID = new Color(218,112,214);
	public static final Color PALE_GOLDEN_ROD = new Color(238,232,170);
	public static final Color PALE_GREEN = new Color(152,251,152);
	public static final Color PALE_TURQUOISE = new Color(175,238,238);
	public static final Color PALE_VIOLET_RED = new Color(219,112,147);
	public static final Color PAPAYA_WHIP = new Color(255,239,213);
	public static final Color PEACH_PUFF = new Color(255,218,185);
	public static final Color PERU = new Color(205,133,63);
	public static final Color PINK = new Color(255,192,203);
	public static final Color PLUM = new Color(221,160,221);
	public static final Color POWDER_BLUE = new Color(176,224,230);
	public static final Color PURPLE = new Color(128,0,128);
	public static final Color RED = new Color(255,0,0);
	public static final Color ROSY_BROWN = new Color(188,143,143);
	public static final Color ROYAL_BLUE = new Color(65,105,225);
	public static final Color SADDLE_BROWN = new Color(139,69,19);
	public static final Color SALMON = new Color(250,128,114);
	public static final Color SANDY_BROWN = new Color(244,164,96);
	public static final Color SEA_GREEN = new Color(46,139,87);
	public static final Color SEA_SHELL = new Color(255,245,238);
	public static final Color SIENNA = new Color(160,82,45);
	public static final Color SILVER = new Color(192,192,192);
	public static final Color SKY_BLUE = new Color(135,206,235);
	public static final Color SLATE_BLUE = new Color(106,90,205);
	public static final Color SLATE_GRAY = new Color(112,128,144);
	public static final Color SLATE_GREY = new Color(112,128,144);
	public static final Color SNOW = new Color(255,250,250);
	public static final Color SPRING_GREEN = new Color(0,255,127);
	public static final Color STEEL_BLUE = new Color(70,130,180);
	public static final Color TAN = new Color(210,180,140);
	public static final Color TEAL = new Color(0,128,128);
	public static final Color THISTLE = new Color(216,191,216);
	public static final Color TOMATO = new Color(255,99,71);
	public static final Color TURQUOISE = new Color(64,224,208);
	public static final Color VIOLET = new Color(238,130,238);
	public static final Color WHEAT = new Color(245,222,179);
	public static final Color WHITE = new Color(255,255,255);
	public static final Color WHITE_SMOKE = new Color(245,245,245);
	public static final Color YELLOW = new Color(255,255,0);
	public static final Color YELLOW_GREEN = new Color(154,205,50);

	public static final Map<String, Color> cssStandardColorsByName = Map.ofEntries(
			entry("aliceblue", ALICE_BLUE),
			entry("antiquewhite", ANTIQUE_WHITE),
			entry("aqua", AQUA),
			entry("aquamarine", AQUA_MARINE),
			entry("azure", AZURE),
			entry("beige", BEIGE),
			entry("bisque", BISQUE),
			entry("black", BLACK),
			entry("blanchedalmond", BLANCHED_ALMOND),
			entry("blue", BLUE),
			entry("blueviolet", BLUE_VIOLET),
			entry("brown", BROWN),
			entry("burlywood", BURLY_WOOD),
			entry("cadetblue", CADET_BLUE),
			entry("chartreuse", CHARTREUSE),
			entry("chocolate", CHOCOLATE),
			entry("coral", CORAL),
			entry("cornflowerblue", CORN_FLOWER_BLUE),
			entry("cornsilk", CORN_SILK),
			entry("crimson", CRIMSON),
			entry("cyan", CYAN),
			entry("darkblue", DARK_BLUE),
			entry("darkcyan", DARK_CYAN),
			entry("darkgoldenrod", DARK_GOLDEN_ROD),
			entry("darkgray", DARK_GRAY),
			entry("darkgreen", DARK_GREEN),
			entry("darkgrey", DARK_GREY),
			entry("darkkhaki", DARK_KHAKI),
			entry("darkmagenta", DARK_MAGENTA),
			entry("darkolivegreen", DARK_OLIVE_GREEN),
			entry("darkorange", DARK_ORANGE),
			entry("darkorchid", DARK_ORCHID),
			entry("darkred", DARK_RED),
			entry("darksalmon", DARK_SALMON),
			entry("darkseagreen", DARK_SEA_GREEN),
			entry("darkslateblue", DARK_SLATE_BLUE),
			entry("darkslategray", DARK_SLATE_GRAY),
			entry("darkslategrey", DARK_SLATE_GREY),
			entry("darkturquoise", DARK_TURQUOISE),
			entry("darkviolet", DARK_VIOLET),
			entry("deeppink", DEEP_PINK),
			entry("deepskyblue", DEEP_SKY_BLUE),
			entry("dimgray", DIMG_RAY),
			entry("dimgrey", DIMG_REY),
			entry("dodgerblue", DODGER_BLUE),
			entry("firebrick", FIRE_BRICK),
			entry("floralwhite", FLORAL_WHITE),
			entry("forestgreen", FOREST_GREEN),
			entry("fuchsia", FUCHSIA),
			entry("gainsboro", GAINSBORO),
			entry("ghostwhite", GHOST_WHITE),
			entry("gold", GOLD),
			entry("goldenrod", GOLDEN_ROD),
			entry("gray", GRAY),
			entry("green", GREEN),
			entry("greenyellow", GREEN_YELLOW),
			entry("grey", GREY),
			entry("honeydew", HONEY_DEW),
			entry("hotpink", HOT_PINK),
			entry("indianred", INDIAN_RED),
			entry("indigo", INDIGO),
			entry("ivory", IVORY),
			entry("khaki", KHAKI),
			entry("lavender", LAVENDER),
			entry("lavenderblush", LAVENDER_BLUSH),
			entry("lawngreen", LAWN_GREEN),
			entry("lemonchiffon", LEMON_CHIFFON),
			entry("lightblue", LIGHT_BLUE),
			entry("lightcoral", LIGHT_CORAL),
			entry("lightcyan", LIGHT_CYAN),
			entry("lightgoldenrodyellow", LIGHT_GOLDEN_ROD_YELLOW),
			entry("lightgray", LIGHT_GRAY),
			entry("lightgreen", LIGHT_GREEN),
			entry("lightgrey", LIGHT_GREY),
			entry("lightpink", LIGHT_PINK),
			entry("lightsalmon", LIGHT_SALMON),
			entry("lightseagreen", LIGHT_SEA_GREEN),
			entry("lightskyblue", LIGHT_SKY_BLUE),
			entry("lightslategray", LIGHT_SLATE_GRAY),
			entry("lightslategrey", LIGHT_SLATE_GREY),
			entry("lightsteelblue", LIGHT_STEEL_BLUE),
			entry("lightyellow", LIGHT_YELLOW),
			entry("lime", LIME),
			entry("limegreen", LIME_GREEN),
			entry("linen", LINEN),
			entry("magenta", MAGENTA),
			entry("maroon", MAROON),
			entry("mediumaquamarine", MEDIUM_AQUA_MARINE),
			entry("mediumblue", MEDIUM_BLUE),
			entry("mediumorchid", MEDIUM_ORCHID),
			entry("mediumpurple", MEDIUM_PURPLE),
			entry("mediumseagreen", MEDIUM_SEAG_REEN),
			entry("mediumslateblue", MEDIUM_SLATE_BLUE),
			entry("mediumspringgreen", MEDIUM_SPRING_GREEN),
			entry("mediumturquoise", MEDIUM_TURQUOISE),
			entry("mediumvioletred", MEDIUM_VIOLET_RED),
			entry("midnightblue", MIDNIGHT_BLUE),
			entry("mintcream", MINT_CREAM),
			entry("mistyrose", MISTY_ROSE),
			entry("moccasin", MOCCASIN),
			entry("navajowhite", NAVAJO_WHITE),
			entry("navy", NAVY),
			entry("oldlace", OLD_LACE),
			entry("olive", OLIVE),
			entry("olivedrab", OLIVE_DRAB),
			entry("orange", ORANGE),
			entry("orangered", ORANGE_RED),
			entry("orchid", ORCHID),
			entry("palegoldenrod", PALE_GOLDEN_ROD),
			entry("palegreen", PALE_GREEN),
			entry("paleturquoise", PALE_TURQUOISE),
			entry("palevioletred", PALE_VIOLET_RED),
			entry("papayawhip", PAPAYA_WHIP),
			entry("peachpuff", PEACH_PUFF),
			entry("peru", PERU),
			entry("pink", PINK),
			entry("plum", PLUM),
			entry("powderblue", POWDER_BLUE),
			entry("purple", PURPLE),
			entry("red", RED),
			entry("rosybrown", ROSY_BROWN),
			entry("royalblue", ROYAL_BLUE),
			entry("saddlebrown", SADDLE_BROWN),
			entry("salmon", SALMON),
			entry("sandybrown", SANDY_BROWN),
			entry("seagreen", SEA_GREEN),
			entry("seashell", SEA_SHELL),
			entry("sienna", SIENNA),
			entry("silver", SILVER),
			entry("skyblue", SKY_BLUE),
			entry("slateblue", SLATE_BLUE),
			entry("slategray", SLATE_GRAY),
			entry("slategrey", SLATE_GREY),
			entry("snow", SNOW),
			entry("springgreen", SPRING_GREEN),
			entry("steelblue", STEEL_BLUE),
			entry("tan", TAN),
			entry("teal", TEAL),
			entry("thistle", THISTLE),
			entry("tomato", TOMATO),
			entry("turquoise", TURQUOISE),
			entry("violet", VIOLET),
			entry("wheat", WHEAT),
			entry("white", WHITE),
			entry("whitesmoke", WHITE_SMOKE),
			entry("yellow", YELLOW),
			entry("yellowgreen", YELLOW_GREEN)
	);

	public static final Color GRAY_STANDARD = new Color(119, 119, 119);
	public static final Color DIM_GRAY = new Color(105, 105, 105);

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
		if (hex.length() == 3) {
			int r = Integer.parseInt(hex, 0, 1, 16);
			int g = Integer.parseInt(hex, 1, 2, 16);
			int b = Integer.parseInt(hex, 2, 3, 16);
			return new Color((r << 4) + r, (g << 4) + g, (b << 4) + b);
		} else {
			int r = Integer.parseInt(hex, 0, 2, 16);
			int g = Integer.parseInt(hex, 2, 4, 16);
			int b = Integer.parseInt(hex, 4, 6, 16);
			int a = hex.length() == 8 ? Integer.parseInt(hex, 6, 8, 16) : 255;
			return new Color(r, g, b, ((float) a) / 255);
		}

	}

	public static Color withAlpha(Color color, float alpha) {
		if (color == null) {
			return null;
		}
		return color.withAlpha(alpha);
	}

	private static final Pattern RGBA_PATTERN = Pattern.compile("rgba\\(\\s*([\\d.]+)\\s*,\\s*([\\d.]+)\\s*,\\s*([\\d.]+)\\s*,\\s*([\\d.]+%?)\\s*\\)");
	private static final Pattern RGB_PATTERN = Pattern.compile("rgb\\(\\s*([\\d.]+)\\s*,\\s*([\\d.]+)\\s*,\\s*([\\d.]+)\\s*\\)");
	private static final Pattern HSLA_PATTERN = Pattern.compile("hsla\\(\\s*([\\d.]+)\\s*,\\s*([\\d.]+%)\\s*,\\s*([\\d.]+%)\\s*,\\s*([\\d.]+%?)\\s*\\)");
	private static final Pattern HSL_PATTERN = Pattern.compile("hsl\\(\\s*([\\d.]+)\\s*,\\s*([\\d.]+%)\\s*,\\s*([\\d.]+%)\\s*\\)");

	public static Color fromHtmlString(String value) {
		value = value.trim();
		if (value.startsWith("rgba")) {
			Matcher matcher = RGBA_PATTERN.matcher(value);
			if (matcher.find()) {
				int r = (int) Float.parseFloat(matcher.group(1));
				int g = (int) Float.parseFloat(matcher.group(2));
				int b = (int) Float.parseFloat(matcher.group(3));
				float a = parseCssFloatingPointNumber(matcher.group(4));
				return new Color(r, g, b, a);
			}
		} else if (value.startsWith("rgb")) {
			Matcher matcher = RGB_PATTERN.matcher(value);
			if (matcher.find()) {
				int r = (int) Float.parseFloat(matcher.group(1));
				int g = (int) Float.parseFloat(matcher.group(2));
				int b = (int) Float.parseFloat(matcher.group(3));
				return new Color(r, g, b);
			}
		} else if (value.startsWith("#")) {
			return Color.fromHex(value);
		} else if (value.startsWith("hsla")) {
			Matcher matcher = HSLA_PATTERN.matcher(value);
			if (matcher.find()) {
				float h = Float.parseFloat(matcher.group(1)) / 255f;
				float s = parseCssFloatingPointNumber(matcher.group(2));
				float l = parseCssFloatingPointNumber(matcher.group(3));
				float a = parseCssFloatingPointNumber(matcher.group(4));
				return Color.fromHsla(h, s, l, a);
			}
		} else if (value.startsWith("hsl")) {
			Matcher matcher = HSL_PATTERN.matcher(value);
			if (matcher.find()) {
				float h = Float.parseFloat(matcher.group(1)) / 255f;
				float s = parseCssFloatingPointNumber(matcher.group(2));
				float l = parseCssFloatingPointNumber(matcher.group(3));
				return Color.fromHsla(h, s, l, 1);
			}
		}
		return cssStandardColorsByName.get(value);
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

	/**
	 * @param h 0-1
	 * @param s 0-1
	 * @param l 0-1
	 * @param alpha 0-1
	 * @return
	 */
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
