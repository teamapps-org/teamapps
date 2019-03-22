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
package org.teamapps.icon.material;

import org.apache.commons.io.IOUtils;
import org.teamapps.common.format.Color;
import org.teamapps.icons.api.IconStyle;
import org.teamapps.icons.provider.SvgIconProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MaterialIconProvider implements SvgIconProvider<IconStyle> {

    public static final String LIBRARY_ID = "material";
    private static List<AbstractMaterialIconStyle> baseStyles = new ArrayList<>();

    //GRADIENT styles:
    public static final AbstractMaterialIconStyle GRADIENT_1 = addStyle(new GradientStyle("gradient_1", Color.MATERIAL_YELLOW_300.toHtmlColorString(), Color.MATERIAL_DEEP_ORANGE_600.toHtmlColorString(), Color.MATERIAL_RED_900.toHtmlColorString()));

    //GRADIENT OUTLINE styles:
    public static final AbstractMaterialIconStyle GRADIENT_OUTLINE_1 = addStyle(new GradientOutlineStyle("gradient_outline_1", Color.MATERIAL_YELLOW_300.toHtmlColorString(), Color.MATERIAL_DEEP_ORANGE_600.toHtmlColorString(), Color.MATERIAL_RED_900.toHtmlColorString()));

    //PLAIN styles:
    public static final AbstractMaterialIconStyle PLAIN_BOOTSTRAP_PRIMARY = addStyle(new PlainStyle("plain_bootstrap_primary", Color.BOOTSTRAP_PRIMARY.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_BOOTSTRAP_SUCCESS = addStyle(new PlainStyle("plain_bootstrap_success", Color.BOOTSTRAP_SUCCESS.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_BOOTSTRAP_INFO = addStyle(new PlainStyle("plain_bootstrap_info", Color.BOOTSTRAP_INFO.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_BOOTSTRAP_WARNING = addStyle(new PlainStyle("plain_bootstrap_warning", Color.BOOTSTRAP_WARNING.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_BOOTSTRAP_DANGER = addStyle(new PlainStyle("plain_bootstrap_danger", Color.BOOTSTRAP_DANGER.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_50 = addStyle(new PlainStyle("plain_material_red_50", Color.MATERIAL_RED_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_100 = addStyle(new PlainStyle("plain_material_red_100", Color.MATERIAL_RED_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_200 = addStyle(new PlainStyle("plain_material_red_200", Color.MATERIAL_RED_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_300 = addStyle(new PlainStyle("plain_material_red_300", Color.MATERIAL_RED_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_400 = addStyle(new PlainStyle("plain_material_red_400", Color.MATERIAL_RED_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_500 = addStyle(new PlainStyle("plain_material_red_500", Color.MATERIAL_RED_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_600 = addStyle(new PlainStyle("plain_material_red_600", Color.MATERIAL_RED_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_700 = addStyle(new PlainStyle("plain_material_red_700", Color.MATERIAL_RED_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_800 = addStyle(new PlainStyle("plain_material_red_800", Color.MATERIAL_RED_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_900 = addStyle(new PlainStyle("plain_material_red_900", Color.MATERIAL_RED_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_A100 = addStyle(new PlainStyle("plain_material_red_a100", Color.MATERIAL_RED_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_A200 = addStyle(new PlainStyle("plain_material_red_a200", Color.MATERIAL_RED_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_A400 = addStyle(new PlainStyle("plain_material_red_a400", Color.MATERIAL_RED_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_RED_A700 = addStyle(new PlainStyle("plain_material_red_a700", Color.MATERIAL_RED_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_50 = addStyle(new PlainStyle("plain_material_pink_50", Color.MATERIAL_PINK_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_100 = addStyle(new PlainStyle("plain_material_pink_100", Color.MATERIAL_PINK_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_200 = addStyle(new PlainStyle("plain_material_pink_200", Color.MATERIAL_PINK_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_300 = addStyle(new PlainStyle("plain_material_pink_300", Color.MATERIAL_PINK_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_400 = addStyle(new PlainStyle("plain_material_pink_400", Color.MATERIAL_PINK_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_500 = addStyle(new PlainStyle("plain_material_pink_500", Color.MATERIAL_PINK_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_600 = addStyle(new PlainStyle("plain_material_pink_600", Color.MATERIAL_PINK_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_700 = addStyle(new PlainStyle("plain_material_pink_700", Color.MATERIAL_PINK_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_800 = addStyle(new PlainStyle("plain_material_pink_800", Color.MATERIAL_PINK_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_900 = addStyle(new PlainStyle("plain_material_pink_900", Color.MATERIAL_PINK_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_A100 = addStyle(new PlainStyle("plain_material_pink_a100", Color.MATERIAL_PINK_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_A200 = addStyle(new PlainStyle("plain_material_pink_a200", Color.MATERIAL_PINK_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_A400 = addStyle(new PlainStyle("plain_material_pink_a400", Color.MATERIAL_PINK_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PINK_A700 = addStyle(new PlainStyle("plain_material_pink_a700", Color.MATERIAL_PINK_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_50 = addStyle(new PlainStyle("plain_material_purple_50", Color.MATERIAL_PURPLE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_100 = addStyle(new PlainStyle("plain_material_purple_100", Color.MATERIAL_PURPLE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_200 = addStyle(new PlainStyle("plain_material_purple_200", Color.MATERIAL_PURPLE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_300 = addStyle(new PlainStyle("plain_material_purple_300", Color.MATERIAL_PURPLE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_400 = addStyle(new PlainStyle("plain_material_purple_400", Color.MATERIAL_PURPLE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_500 = addStyle(new PlainStyle("plain_material_purple_500", Color.MATERIAL_PURPLE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_600 = addStyle(new PlainStyle("plain_material_purple_600", Color.MATERIAL_PURPLE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_700 = addStyle(new PlainStyle("plain_material_purple_700", Color.MATERIAL_PURPLE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_800 = addStyle(new PlainStyle("plain_material_purple_800", Color.MATERIAL_PURPLE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_900 = addStyle(new PlainStyle("plain_material_purple_900", Color.MATERIAL_PURPLE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_A100 = addStyle(new PlainStyle("plain_material_purple_a100", Color.MATERIAL_PURPLE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_A200 = addStyle(new PlainStyle("plain_material_purple_a200", Color.MATERIAL_PURPLE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_A400 = addStyle(new PlainStyle("plain_material_purple_a400", Color.MATERIAL_PURPLE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_PURPLE_A700 = addStyle(new PlainStyle("plain_material_purple_a700", Color.MATERIAL_PURPLE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_50 = addStyle(new PlainStyle("plain_material_deep_purple_50", Color.MATERIAL_DEEP_PURPLE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_100 = addStyle(new PlainStyle("plain_material_deep_purple_100", Color.MATERIAL_DEEP_PURPLE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_200 = addStyle(new PlainStyle("plain_material_deep_purple_200", Color.MATERIAL_DEEP_PURPLE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_300 = addStyle(new PlainStyle("plain_material_deep_purple_300", Color.MATERIAL_DEEP_PURPLE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_400 = addStyle(new PlainStyle("plain_material_deep_purple_400", Color.MATERIAL_DEEP_PURPLE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_500 = addStyle(new PlainStyle("plain_material_deep_purple_500", Color.MATERIAL_DEEP_PURPLE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_600 = addStyle(new PlainStyle("plain_material_deep_purple_600", Color.MATERIAL_DEEP_PURPLE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_700 = addStyle(new PlainStyle("plain_material_deep_purple_700", Color.MATERIAL_DEEP_PURPLE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_800 = addStyle(new PlainStyle("plain_material_deep_purple_800", Color.MATERIAL_DEEP_PURPLE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_900 = addStyle(new PlainStyle("plain_material_deep_purple_900", Color.MATERIAL_DEEP_PURPLE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_A100 = addStyle(new PlainStyle("plain_material_deep_purple_a100", Color.MATERIAL_DEEP_PURPLE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_A200 = addStyle(new PlainStyle("plain_material_deep_purple_a200", Color.MATERIAL_DEEP_PURPLE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_A400 = addStyle(new PlainStyle("plain_material_deep_purple_a400", Color.MATERIAL_DEEP_PURPLE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_PURPLE_A700 = addStyle(new PlainStyle("plain_material_deep_purple_a700", Color.MATERIAL_DEEP_PURPLE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_50 = addStyle(new PlainStyle("plain_material_indigo_50", Color.MATERIAL_INDIGO_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_100 = addStyle(new PlainStyle("plain_material_indigo_100", Color.MATERIAL_INDIGO_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_200 = addStyle(new PlainStyle("plain_material_indigo_200", Color.MATERIAL_INDIGO_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_300 = addStyle(new PlainStyle("plain_material_indigo_300", Color.MATERIAL_INDIGO_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_400 = addStyle(new PlainStyle("plain_material_indigo_400", Color.MATERIAL_INDIGO_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_500 = addStyle(new PlainStyle("plain_material_indigo_500", Color.MATERIAL_INDIGO_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_600 = addStyle(new PlainStyle("plain_material_indigo_600", Color.MATERIAL_INDIGO_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_700 = addStyle(new PlainStyle("plain_material_indigo_700", Color.MATERIAL_INDIGO_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_800 = addStyle(new PlainStyle("plain_material_indigo_800", Color.MATERIAL_INDIGO_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_900 = addStyle(new PlainStyle("plain_material_indigo_900", Color.MATERIAL_INDIGO_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_A100 = addStyle(new PlainStyle("plain_material_indigo_a100", Color.MATERIAL_INDIGO_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_A200 = addStyle(new PlainStyle("plain_material_indigo_a200", Color.MATERIAL_INDIGO_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_A400 = addStyle(new PlainStyle("plain_material_indigo_a400", Color.MATERIAL_INDIGO_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_INDIGO_A700 = addStyle(new PlainStyle("plain_material_indigo_a700", Color.MATERIAL_INDIGO_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_50 = addStyle(new PlainStyle("plain_material_blue_50", Color.MATERIAL_BLUE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_100 = addStyle(new PlainStyle("plain_material_blue_100", Color.MATERIAL_BLUE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_200 = addStyle(new PlainStyle("plain_material_blue_200", Color.MATERIAL_BLUE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_300 = addStyle(new PlainStyle("plain_material_blue_300", Color.MATERIAL_BLUE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_400 = addStyle(new PlainStyle("plain_material_blue_400", Color.MATERIAL_BLUE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_500 = addStyle(new PlainStyle("plain_material_blue_500", Color.MATERIAL_BLUE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_600 = addStyle(new PlainStyle("plain_material_blue_600", Color.MATERIAL_BLUE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_700 = addStyle(new PlainStyle("plain_material_blue_700", Color.MATERIAL_BLUE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_800 = addStyle(new PlainStyle("plain_material_blue_800", Color.MATERIAL_BLUE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_900 = addStyle(new PlainStyle("plain_material_blue_900", Color.MATERIAL_BLUE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_A100 = addStyle(new PlainStyle("plain_material_blue_a100", Color.MATERIAL_BLUE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_A200 = addStyle(new PlainStyle("plain_material_blue_a200", Color.MATERIAL_BLUE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_A400 = addStyle(new PlainStyle("plain_material_blue_a400", Color.MATERIAL_BLUE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_A700 = addStyle(new PlainStyle("plain_material_blue_a700", Color.MATERIAL_BLUE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_50 = addStyle(new PlainStyle("plain_material_light_blue_50", Color.MATERIAL_LIGHT_BLUE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_100 = addStyle(new PlainStyle("plain_material_light_blue_100", Color.MATERIAL_LIGHT_BLUE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_200 = addStyle(new PlainStyle("plain_material_light_blue_200", Color.MATERIAL_LIGHT_BLUE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_300 = addStyle(new PlainStyle("plain_material_light_blue_300", Color.MATERIAL_LIGHT_BLUE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_400 = addStyle(new PlainStyle("plain_material_light_blue_400", Color.MATERIAL_LIGHT_BLUE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_500 = addStyle(new PlainStyle("plain_material_light_blue_500", Color.MATERIAL_LIGHT_BLUE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_600 = addStyle(new PlainStyle("plain_material_light_blue_600", Color.MATERIAL_LIGHT_BLUE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_700 = addStyle(new PlainStyle("plain_material_light_blue_700", Color.MATERIAL_LIGHT_BLUE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_800 = addStyle(new PlainStyle("plain_material_light_blue_800", Color.MATERIAL_LIGHT_BLUE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_900 = addStyle(new PlainStyle("plain_material_light_blue_900", Color.MATERIAL_LIGHT_BLUE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_A100 = addStyle(new PlainStyle("plain_material_light_blue_a100", Color.MATERIAL_LIGHT_BLUE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_A200 = addStyle(new PlainStyle("plain_material_light_blue_a200", Color.MATERIAL_LIGHT_BLUE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_A400 = addStyle(new PlainStyle("plain_material_light_blue_a400", Color.MATERIAL_LIGHT_BLUE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_BLUE_A700 = addStyle(new PlainStyle("plain_material_light_blue_a700", Color.MATERIAL_LIGHT_BLUE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_50 = addStyle(new PlainStyle("plain_material_cyan_50", Color.MATERIAL_CYAN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_100 = addStyle(new PlainStyle("plain_material_cyan_100", Color.MATERIAL_CYAN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_200 = addStyle(new PlainStyle("plain_material_cyan_200", Color.MATERIAL_CYAN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_300 = addStyle(new PlainStyle("plain_material_cyan_300", Color.MATERIAL_CYAN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_400 = addStyle(new PlainStyle("plain_material_cyan_400", Color.MATERIAL_CYAN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_500 = addStyle(new PlainStyle("plain_material_cyan_500", Color.MATERIAL_CYAN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_600 = addStyle(new PlainStyle("plain_material_cyan_600", Color.MATERIAL_CYAN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_700 = addStyle(new PlainStyle("plain_material_cyan_700", Color.MATERIAL_CYAN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_800 = addStyle(new PlainStyle("plain_material_cyan_800", Color.MATERIAL_CYAN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_900 = addStyle(new PlainStyle("plain_material_cyan_900", Color.MATERIAL_CYAN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_A100 = addStyle(new PlainStyle("plain_material_cyan_a100", Color.MATERIAL_CYAN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_A200 = addStyle(new PlainStyle("plain_material_cyan_a200", Color.MATERIAL_CYAN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_A400 = addStyle(new PlainStyle("plain_material_cyan_a400", Color.MATERIAL_CYAN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_CYAN_A700 = addStyle(new PlainStyle("plain_material_cyan_a700", Color.MATERIAL_CYAN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_50 = addStyle(new PlainStyle("plain_material_teal_50", Color.MATERIAL_TEAL_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_100 = addStyle(new PlainStyle("plain_material_teal_100", Color.MATERIAL_TEAL_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_200 = addStyle(new PlainStyle("plain_material_teal_200", Color.MATERIAL_TEAL_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_300 = addStyle(new PlainStyle("plain_material_teal_300", Color.MATERIAL_TEAL_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_400 = addStyle(new PlainStyle("plain_material_teal_400", Color.MATERIAL_TEAL_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_500 = addStyle(new PlainStyle("plain_material_teal_500", Color.MATERIAL_TEAL_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_600 = addStyle(new PlainStyle("plain_material_teal_600", Color.MATERIAL_TEAL_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_700 = addStyle(new PlainStyle("plain_material_teal_700", Color.MATERIAL_TEAL_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_800 = addStyle(new PlainStyle("plain_material_teal_800", Color.MATERIAL_TEAL_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_900 = addStyle(new PlainStyle("plain_material_teal_900", Color.MATERIAL_TEAL_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_A100 = addStyle(new PlainStyle("plain_material_teal_a100", Color.MATERIAL_TEAL_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_A200 = addStyle(new PlainStyle("plain_material_teal_a200", Color.MATERIAL_TEAL_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_A400 = addStyle(new PlainStyle("plain_material_teal_a400", Color.MATERIAL_TEAL_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_TEAL_A700 = addStyle(new PlainStyle("plain_material_teal_a700", Color.MATERIAL_TEAL_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_50 = addStyle(new PlainStyle("plain_material_green_50", Color.MATERIAL_GREEN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_100 = addStyle(new PlainStyle("plain_material_green_100", Color.MATERIAL_GREEN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_200 = addStyle(new PlainStyle("plain_material_green_200", Color.MATERIAL_GREEN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_300 = addStyle(new PlainStyle("plain_material_green_300", Color.MATERIAL_GREEN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_400 = addStyle(new PlainStyle("plain_material_green_400", Color.MATERIAL_GREEN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_500 = addStyle(new PlainStyle("plain_material_green_500", Color.MATERIAL_GREEN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_600 = addStyle(new PlainStyle("plain_material_green_600", Color.MATERIAL_GREEN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_700 = addStyle(new PlainStyle("plain_material_green_700", Color.MATERIAL_GREEN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_800 = addStyle(new PlainStyle("plain_material_green_800", Color.MATERIAL_GREEN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_900 = addStyle(new PlainStyle("plain_material_green_900", Color.MATERIAL_GREEN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_A100 = addStyle(new PlainStyle("plain_material_green_a100", Color.MATERIAL_GREEN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_A200 = addStyle(new PlainStyle("plain_material_green_a200", Color.MATERIAL_GREEN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_A400 = addStyle(new PlainStyle("plain_material_green_a400", Color.MATERIAL_GREEN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREEN_A700 = addStyle(new PlainStyle("plain_material_green_a700", Color.MATERIAL_GREEN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_50 = addStyle(new PlainStyle("plain_material_light_green_50", Color.MATERIAL_LIGHT_GREEN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_100 = addStyle(new PlainStyle("plain_material_light_green_100", Color.MATERIAL_LIGHT_GREEN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_200 = addStyle(new PlainStyle("plain_material_light_green_200", Color.MATERIAL_LIGHT_GREEN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_300 = addStyle(new PlainStyle("plain_material_light_green_300", Color.MATERIAL_LIGHT_GREEN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_400 = addStyle(new PlainStyle("plain_material_light_green_400", Color.MATERIAL_LIGHT_GREEN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_500 = addStyle(new PlainStyle("plain_material_light_green_500", Color.MATERIAL_LIGHT_GREEN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_600 = addStyle(new PlainStyle("plain_material_light_green_600", Color.MATERIAL_LIGHT_GREEN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_700 = addStyle(new PlainStyle("plain_material_light_green_700", Color.MATERIAL_LIGHT_GREEN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_800 = addStyle(new PlainStyle("plain_material_light_green_800", Color.MATERIAL_LIGHT_GREEN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_900 = addStyle(new PlainStyle("plain_material_light_green_900", Color.MATERIAL_LIGHT_GREEN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_A100 = addStyle(new PlainStyle("plain_material_light_green_a100", Color.MATERIAL_LIGHT_GREEN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_A200 = addStyle(new PlainStyle("plain_material_light_green_a200", Color.MATERIAL_LIGHT_GREEN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_A400 = addStyle(new PlainStyle("plain_material_light_green_a400", Color.MATERIAL_LIGHT_GREEN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIGHT_GREEN_A700 = addStyle(new PlainStyle("plain_material_light_green_a700", Color.MATERIAL_LIGHT_GREEN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_50 = addStyle(new PlainStyle("plain_material_lime_50", Color.MATERIAL_LIME_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_100 = addStyle(new PlainStyle("plain_material_lime_100", Color.MATERIAL_LIME_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_200 = addStyle(new PlainStyle("plain_material_lime_200", Color.MATERIAL_LIME_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_300 = addStyle(new PlainStyle("plain_material_lime_300", Color.MATERIAL_LIME_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_400 = addStyle(new PlainStyle("plain_material_lime_400", Color.MATERIAL_LIME_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_500 = addStyle(new PlainStyle("plain_material_lime_500", Color.MATERIAL_LIME_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_600 = addStyle(new PlainStyle("plain_material_lime_600", Color.MATERIAL_LIME_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_700 = addStyle(new PlainStyle("plain_material_lime_700", Color.MATERIAL_LIME_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_800 = addStyle(new PlainStyle("plain_material_lime_800", Color.MATERIAL_LIME_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_900 = addStyle(new PlainStyle("plain_material_lime_900", Color.MATERIAL_LIME_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_A100 = addStyle(new PlainStyle("plain_material_lime_a100", Color.MATERIAL_LIME_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_A200 = addStyle(new PlainStyle("plain_material_lime_a200", Color.MATERIAL_LIME_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_A400 = addStyle(new PlainStyle("plain_material_lime_a400", Color.MATERIAL_LIME_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_LIME_A700 = addStyle(new PlainStyle("plain_material_lime_a700", Color.MATERIAL_LIME_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_50 = addStyle(new PlainStyle("plain_material_yellow_50", Color.MATERIAL_YELLOW_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_100 = addStyle(new PlainStyle("plain_material_yellow_100", Color.MATERIAL_YELLOW_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_200 = addStyle(new PlainStyle("plain_material_yellow_200", Color.MATERIAL_YELLOW_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_300 = addStyle(new PlainStyle("plain_material_yellow_300", Color.MATERIAL_YELLOW_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_400 = addStyle(new PlainStyle("plain_material_yellow_400", Color.MATERIAL_YELLOW_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_500 = addStyle(new PlainStyle("plain_material_yellow_500", Color.MATERIAL_YELLOW_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_600 = addStyle(new PlainStyle("plain_material_yellow_600", Color.MATERIAL_YELLOW_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_700 = addStyle(new PlainStyle("plain_material_yellow_700", Color.MATERIAL_YELLOW_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_800 = addStyle(new PlainStyle("plain_material_yellow_800", Color.MATERIAL_YELLOW_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_900 = addStyle(new PlainStyle("plain_material_yellow_900", Color.MATERIAL_YELLOW_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_A100 = addStyle(new PlainStyle("plain_material_yellow_a100", Color.MATERIAL_YELLOW_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_A200 = addStyle(new PlainStyle("plain_material_yellow_a200", Color.MATERIAL_YELLOW_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_A400 = addStyle(new PlainStyle("plain_material_yellow_a400", Color.MATERIAL_YELLOW_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_YELLOW_A700 = addStyle(new PlainStyle("plain_material_yellow_a700", Color.MATERIAL_YELLOW_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_50 = addStyle(new PlainStyle("plain_material_amber_50", Color.MATERIAL_AMBER_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_100 = addStyle(new PlainStyle("plain_material_amber_100", Color.MATERIAL_AMBER_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_200 = addStyle(new PlainStyle("plain_material_amber_200", Color.MATERIAL_AMBER_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_300 = addStyle(new PlainStyle("plain_material_amber_300", Color.MATERIAL_AMBER_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_400 = addStyle(new PlainStyle("plain_material_amber_400", Color.MATERIAL_AMBER_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_500 = addStyle(new PlainStyle("plain_material_amber_500", Color.MATERIAL_AMBER_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_600 = addStyle(new PlainStyle("plain_material_amber_600", Color.MATERIAL_AMBER_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_700 = addStyle(new PlainStyle("plain_material_amber_700", Color.MATERIAL_AMBER_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_800 = addStyle(new PlainStyle("plain_material_amber_800", Color.MATERIAL_AMBER_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_900 = addStyle(new PlainStyle("plain_material_amber_900", Color.MATERIAL_AMBER_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_A100 = addStyle(new PlainStyle("plain_material_amber_a100", Color.MATERIAL_AMBER_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_A200 = addStyle(new PlainStyle("plain_material_amber_a200", Color.MATERIAL_AMBER_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_A400 = addStyle(new PlainStyle("plain_material_amber_a400", Color.MATERIAL_AMBER_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_AMBER_A700 = addStyle(new PlainStyle("plain_material_amber_a700", Color.MATERIAL_AMBER_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_50 = addStyle(new PlainStyle("plain_material_orange_50", Color.MATERIAL_ORANGE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_100 = addStyle(new PlainStyle("plain_material_orange_100", Color.MATERIAL_ORANGE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_200 = addStyle(new PlainStyle("plain_material_orange_200", Color.MATERIAL_ORANGE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_300 = addStyle(new PlainStyle("plain_material_orange_300", Color.MATERIAL_ORANGE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_400 = addStyle(new PlainStyle("plain_material_orange_400", Color.MATERIAL_ORANGE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_500 = addStyle(new PlainStyle("plain_material_orange_500", Color.MATERIAL_ORANGE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_600 = addStyle(new PlainStyle("plain_material_orange_600", Color.MATERIAL_ORANGE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_700 = addStyle(new PlainStyle("plain_material_orange_700", Color.MATERIAL_ORANGE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_800 = addStyle(new PlainStyle("plain_material_orange_800", Color.MATERIAL_ORANGE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_900 = addStyle(new PlainStyle("plain_material_orange_900", Color.MATERIAL_ORANGE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_A100 = addStyle(new PlainStyle("plain_material_orange_a100", Color.MATERIAL_ORANGE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_A200 = addStyle(new PlainStyle("plain_material_orange_a200", Color.MATERIAL_ORANGE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_A400 = addStyle(new PlainStyle("plain_material_orange_a400", Color.MATERIAL_ORANGE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_ORANGE_A700 = addStyle(new PlainStyle("plain_material_orange_a700", Color.MATERIAL_ORANGE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_50 = addStyle(new PlainStyle("plain_material_deep_orange_50", Color.MATERIAL_DEEP_ORANGE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_100 = addStyle(new PlainStyle("plain_material_deep_orange_100", Color.MATERIAL_DEEP_ORANGE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_200 = addStyle(new PlainStyle("plain_material_deep_orange_200", Color.MATERIAL_DEEP_ORANGE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_300 = addStyle(new PlainStyle("plain_material_deep_orange_300", Color.MATERIAL_DEEP_ORANGE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_400 = addStyle(new PlainStyle("plain_material_deep_orange_400", Color.MATERIAL_DEEP_ORANGE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_500 = addStyle(new PlainStyle("plain_material_deep_orange_500", Color.MATERIAL_DEEP_ORANGE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_600 = addStyle(new PlainStyle("plain_material_deep_orange_600", Color.MATERIAL_DEEP_ORANGE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_700 = addStyle(new PlainStyle("plain_material_deep_orange_700", Color.MATERIAL_DEEP_ORANGE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_800 = addStyle(new PlainStyle("plain_material_deep_orange_800", Color.MATERIAL_DEEP_ORANGE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_900 = addStyle(new PlainStyle("plain_material_deep_orange_900", Color.MATERIAL_DEEP_ORANGE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_A100 = addStyle(new PlainStyle("plain_material_deep_orange_a100", Color.MATERIAL_DEEP_ORANGE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_A200 = addStyle(new PlainStyle("plain_material_deep_orange_a200", Color.MATERIAL_DEEP_ORANGE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_A400 = addStyle(new PlainStyle("plain_material_deep_orange_a400", Color.MATERIAL_DEEP_ORANGE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_DEEP_ORANGE_A700 = addStyle(new PlainStyle("plain_material_deep_orange_a700", Color.MATERIAL_DEEP_ORANGE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BROWN_50 = addStyle(new PlainStyle("plain_material_brown_50", Color.MATERIAL_BROWN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BROWN_100 = addStyle(new PlainStyle("plain_material_brown_100", Color.MATERIAL_BROWN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BROWN_200 = addStyle(new PlainStyle("plain_material_brown_200", Color.MATERIAL_BROWN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BROWN_300 = addStyle(new PlainStyle("plain_material_brown_300", Color.MATERIAL_BROWN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BROWN_400 = addStyle(new PlainStyle("plain_material_brown_400", Color.MATERIAL_BROWN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BROWN_500 = addStyle(new PlainStyle("plain_material_brown_500", Color.MATERIAL_BROWN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BROWN_600 = addStyle(new PlainStyle("plain_material_brown_600", Color.MATERIAL_BROWN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BROWN_700 = addStyle(new PlainStyle("plain_material_brown_700", Color.MATERIAL_BROWN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BROWN_800 = addStyle(new PlainStyle("plain_material_brown_800", Color.MATERIAL_BROWN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BROWN_900 = addStyle(new PlainStyle("plain_material_brown_900", Color.MATERIAL_BROWN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREY_50 = addStyle(new PlainStyle("plain_material_grey_50", Color.MATERIAL_GREY_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREY_100 = addStyle(new PlainStyle("plain_material_grey_100", Color.MATERIAL_GREY_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREY_200 = addStyle(new PlainStyle("plain_material_grey_200", Color.MATERIAL_GREY_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREY_300 = addStyle(new PlainStyle("plain_material_grey_300", Color.MATERIAL_GREY_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREY_400 = addStyle(new PlainStyle("plain_material_grey_400", Color.MATERIAL_GREY_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREY_500 = addStyle(new PlainStyle("plain_material_grey_500", Color.MATERIAL_GREY_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREY_600 = addStyle(new PlainStyle("plain_material_grey_600", Color.MATERIAL_GREY_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREY_700 = addStyle(new PlainStyle("plain_material_grey_700", Color.MATERIAL_GREY_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREY_800 = addStyle(new PlainStyle("plain_material_grey_800", Color.MATERIAL_GREY_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_GREY_900 = addStyle(new PlainStyle("plain_material_grey_900", Color.MATERIAL_GREY_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLACK_1000 = addStyle(new PlainStyle("plain_material_black_1000", Color.MATERIAL_BLACK_1000.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_WHITE_1000 = addStyle(new PlainStyle("plain_material_white_1000", Color.MATERIAL_WHITE_1000.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_GREY_50 = addStyle(new PlainStyle("plain_material_blue_grey_50", Color.MATERIAL_BLUE_GREY_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_GREY_100 = addStyle(new PlainStyle("plain_material_blue_grey_100", Color.MATERIAL_BLUE_GREY_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_GREY_200 = addStyle(new PlainStyle("plain_material_blue_grey_200", Color.MATERIAL_BLUE_GREY_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_GREY_300 = addStyle(new PlainStyle("plain_material_blue_grey_300", Color.MATERIAL_BLUE_GREY_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_GREY_400 = addStyle(new PlainStyle("plain_material_blue_grey_400", Color.MATERIAL_BLUE_GREY_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_GREY_500 = addStyle(new PlainStyle("plain_material_blue_grey_500", Color.MATERIAL_BLUE_GREY_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_GREY_600 = addStyle(new PlainStyle("plain_material_blue_grey_600", Color.MATERIAL_BLUE_GREY_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_GREY_700 = addStyle(new PlainStyle("plain_material_blue_grey_700", Color.MATERIAL_BLUE_GREY_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_GREY_800 = addStyle(new PlainStyle("plain_material_blue_grey_800", Color.MATERIAL_BLUE_GREY_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_MATERIAL_BLUE_GREY_900 = addStyle(new PlainStyle("plain_material_blue_grey_900", Color.MATERIAL_BLUE_GREY_900.toHtmlColorString()));

    //PLAIN_SHADOW styles:
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_BOOTSTRAP_PRIMARY = addStyle(new PlainShadowStyle("plain_shadow_bootstrap_primary", Color.BOOTSTRAP_PRIMARY.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_BOOTSTRAP_SUCCESS = addStyle(new PlainShadowStyle("plain_shadow_bootstrap_success", Color.BOOTSTRAP_SUCCESS.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_BOOTSTRAP_INFO = addStyle(new PlainShadowStyle("plain_shadow_bootstrap_info", Color.BOOTSTRAP_INFO.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_BOOTSTRAP_WARNING = addStyle(new PlainShadowStyle("plain_shadow_bootstrap_warning", Color.BOOTSTRAP_WARNING.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_BOOTSTRAP_DANGER = addStyle(new PlainShadowStyle("plain_shadow_bootstrap_danger", Color.BOOTSTRAP_DANGER.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_50 = addStyle(new PlainShadowStyle("plain_shadow_material_red_50", Color.MATERIAL_RED_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_100 = addStyle(new PlainShadowStyle("plain_shadow_material_red_100", Color.MATERIAL_RED_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_200 = addStyle(new PlainShadowStyle("plain_shadow_material_red_200", Color.MATERIAL_RED_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_300 = addStyle(new PlainShadowStyle("plain_shadow_material_red_300", Color.MATERIAL_RED_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_400 = addStyle(new PlainShadowStyle("plain_shadow_material_red_400", Color.MATERIAL_RED_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_500 = addStyle(new PlainShadowStyle("plain_shadow_material_red_500", Color.MATERIAL_RED_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_600 = addStyle(new PlainShadowStyle("plain_shadow_material_red_600", Color.MATERIAL_RED_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_700 = addStyle(new PlainShadowStyle("plain_shadow_material_red_700", Color.MATERIAL_RED_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_800 = addStyle(new PlainShadowStyle("plain_shadow_material_red_800", Color.MATERIAL_RED_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_900 = addStyle(new PlainShadowStyle("plain_shadow_material_red_900", Color.MATERIAL_RED_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_red_a100", Color.MATERIAL_RED_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_red_a200", Color.MATERIAL_RED_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_red_a400", Color.MATERIAL_RED_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_RED_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_red_a700", Color.MATERIAL_RED_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_50 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_50", Color.MATERIAL_PINK_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_100 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_100", Color.MATERIAL_PINK_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_200 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_200", Color.MATERIAL_PINK_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_300 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_300", Color.MATERIAL_PINK_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_400 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_400", Color.MATERIAL_PINK_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_500 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_500", Color.MATERIAL_PINK_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_600 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_600", Color.MATERIAL_PINK_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_700 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_700", Color.MATERIAL_PINK_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_800 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_800", Color.MATERIAL_PINK_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_900 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_900", Color.MATERIAL_PINK_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_a100", Color.MATERIAL_PINK_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_a200", Color.MATERIAL_PINK_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_a400", Color.MATERIAL_PINK_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PINK_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_pink_a700", Color.MATERIAL_PINK_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_50 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_50", Color.MATERIAL_PURPLE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_100 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_100", Color.MATERIAL_PURPLE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_200 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_200", Color.MATERIAL_PURPLE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_300 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_300", Color.MATERIAL_PURPLE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_400 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_400", Color.MATERIAL_PURPLE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_500 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_500", Color.MATERIAL_PURPLE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_600 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_600", Color.MATERIAL_PURPLE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_700 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_700", Color.MATERIAL_PURPLE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_800 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_800", Color.MATERIAL_PURPLE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_900 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_900", Color.MATERIAL_PURPLE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_a100", Color.MATERIAL_PURPLE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_a200", Color.MATERIAL_PURPLE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_a400", Color.MATERIAL_PURPLE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_PURPLE_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_purple_a700", Color.MATERIAL_PURPLE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_50 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_50", Color.MATERIAL_DEEP_PURPLE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_100 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_100", Color.MATERIAL_DEEP_PURPLE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_200 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_200", Color.MATERIAL_DEEP_PURPLE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_300 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_300", Color.MATERIAL_DEEP_PURPLE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_400 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_400", Color.MATERIAL_DEEP_PURPLE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_500 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_500", Color.MATERIAL_DEEP_PURPLE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_600 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_600", Color.MATERIAL_DEEP_PURPLE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_700 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_700", Color.MATERIAL_DEEP_PURPLE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_800 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_800", Color.MATERIAL_DEEP_PURPLE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_900 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_900", Color.MATERIAL_DEEP_PURPLE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_a100", Color.MATERIAL_DEEP_PURPLE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_a200", Color.MATERIAL_DEEP_PURPLE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_a400", Color.MATERIAL_DEEP_PURPLE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_PURPLE_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_purple_a700", Color.MATERIAL_DEEP_PURPLE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_50 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_50", Color.MATERIAL_INDIGO_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_100 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_100", Color.MATERIAL_INDIGO_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_200 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_200", Color.MATERIAL_INDIGO_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_300 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_300", Color.MATERIAL_INDIGO_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_400 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_400", Color.MATERIAL_INDIGO_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_500 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_500", Color.MATERIAL_INDIGO_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_600 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_600", Color.MATERIAL_INDIGO_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_700 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_700", Color.MATERIAL_INDIGO_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_800 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_800", Color.MATERIAL_INDIGO_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_900 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_900", Color.MATERIAL_INDIGO_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_a100", Color.MATERIAL_INDIGO_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_a200", Color.MATERIAL_INDIGO_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_a400", Color.MATERIAL_INDIGO_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_INDIGO_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_indigo_a700", Color.MATERIAL_INDIGO_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_50 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_50", Color.MATERIAL_BLUE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_100 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_100", Color.MATERIAL_BLUE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_200 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_200", Color.MATERIAL_BLUE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_300 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_300", Color.MATERIAL_BLUE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_400 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_400", Color.MATERIAL_BLUE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_500 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_500", Color.MATERIAL_BLUE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_600 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_600", Color.MATERIAL_BLUE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_700 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_700", Color.MATERIAL_BLUE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_800 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_800", Color.MATERIAL_BLUE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_900 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_900", Color.MATERIAL_BLUE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_a100", Color.MATERIAL_BLUE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_a200", Color.MATERIAL_BLUE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_a400", Color.MATERIAL_BLUE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_a700", Color.MATERIAL_BLUE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_50 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_50", Color.MATERIAL_LIGHT_BLUE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_100 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_100", Color.MATERIAL_LIGHT_BLUE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_200 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_200", Color.MATERIAL_LIGHT_BLUE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_300 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_300", Color.MATERIAL_LIGHT_BLUE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_400 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_400", Color.MATERIAL_LIGHT_BLUE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_500 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_500", Color.MATERIAL_LIGHT_BLUE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_600 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_600", Color.MATERIAL_LIGHT_BLUE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_700 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_700", Color.MATERIAL_LIGHT_BLUE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_800 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_800", Color.MATERIAL_LIGHT_BLUE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_900 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_900", Color.MATERIAL_LIGHT_BLUE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_a100", Color.MATERIAL_LIGHT_BLUE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_a200", Color.MATERIAL_LIGHT_BLUE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_a400", Color.MATERIAL_LIGHT_BLUE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_BLUE_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_light_blue_a700", Color.MATERIAL_LIGHT_BLUE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_50 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_50", Color.MATERIAL_CYAN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_100 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_100", Color.MATERIAL_CYAN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_200 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_200", Color.MATERIAL_CYAN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_300 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_300", Color.MATERIAL_CYAN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_400 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_400", Color.MATERIAL_CYAN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_500 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_500", Color.MATERIAL_CYAN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_600 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_600", Color.MATERIAL_CYAN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_700 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_700", Color.MATERIAL_CYAN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_800 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_800", Color.MATERIAL_CYAN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_900 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_900", Color.MATERIAL_CYAN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_a100", Color.MATERIAL_CYAN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_a200", Color.MATERIAL_CYAN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_a400", Color.MATERIAL_CYAN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_CYAN_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_cyan_a700", Color.MATERIAL_CYAN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_50 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_50", Color.MATERIAL_TEAL_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_100 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_100", Color.MATERIAL_TEAL_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_200 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_200", Color.MATERIAL_TEAL_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_300 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_300", Color.MATERIAL_TEAL_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_400 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_400", Color.MATERIAL_TEAL_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_500 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_500", Color.MATERIAL_TEAL_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_600 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_600", Color.MATERIAL_TEAL_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_700 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_700", Color.MATERIAL_TEAL_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_800 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_800", Color.MATERIAL_TEAL_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_900 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_900", Color.MATERIAL_TEAL_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_a100", Color.MATERIAL_TEAL_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_a200", Color.MATERIAL_TEAL_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_a400", Color.MATERIAL_TEAL_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_TEAL_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_teal_a700", Color.MATERIAL_TEAL_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_50 = addStyle(new PlainShadowStyle("plain_shadow_material_green_50", Color.MATERIAL_GREEN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_100 = addStyle(new PlainShadowStyle("plain_shadow_material_green_100", Color.MATERIAL_GREEN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_200 = addStyle(new PlainShadowStyle("plain_shadow_material_green_200", Color.MATERIAL_GREEN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_300 = addStyle(new PlainShadowStyle("plain_shadow_material_green_300", Color.MATERIAL_GREEN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_400 = addStyle(new PlainShadowStyle("plain_shadow_material_green_400", Color.MATERIAL_GREEN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_500 = addStyle(new PlainShadowStyle("plain_shadow_material_green_500", Color.MATERIAL_GREEN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_600 = addStyle(new PlainShadowStyle("plain_shadow_material_green_600", Color.MATERIAL_GREEN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_700 = addStyle(new PlainShadowStyle("plain_shadow_material_green_700", Color.MATERIAL_GREEN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_800 = addStyle(new PlainShadowStyle("plain_shadow_material_green_800", Color.MATERIAL_GREEN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_900 = addStyle(new PlainShadowStyle("plain_shadow_material_green_900", Color.MATERIAL_GREEN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_green_a100", Color.MATERIAL_GREEN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_green_a200", Color.MATERIAL_GREEN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_green_a400", Color.MATERIAL_GREEN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREEN_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_green_a700", Color.MATERIAL_GREEN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_50 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_50", Color.MATERIAL_LIGHT_GREEN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_100 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_100", Color.MATERIAL_LIGHT_GREEN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_200 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_200", Color.MATERIAL_LIGHT_GREEN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_300 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_300", Color.MATERIAL_LIGHT_GREEN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_400 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_400", Color.MATERIAL_LIGHT_GREEN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_500 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_500", Color.MATERIAL_LIGHT_GREEN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_600 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_600", Color.MATERIAL_LIGHT_GREEN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_700 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_700", Color.MATERIAL_LIGHT_GREEN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_800 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_800", Color.MATERIAL_LIGHT_GREEN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_900 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_900", Color.MATERIAL_LIGHT_GREEN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_a100", Color.MATERIAL_LIGHT_GREEN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_a200", Color.MATERIAL_LIGHT_GREEN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_a400", Color.MATERIAL_LIGHT_GREEN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIGHT_GREEN_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_light_green_a700", Color.MATERIAL_LIGHT_GREEN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_50 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_50", Color.MATERIAL_LIME_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_100 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_100", Color.MATERIAL_LIME_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_200 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_200", Color.MATERIAL_LIME_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_300 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_300", Color.MATERIAL_LIME_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_400 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_400", Color.MATERIAL_LIME_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_500 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_500", Color.MATERIAL_LIME_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_600 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_600", Color.MATERIAL_LIME_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_700 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_700", Color.MATERIAL_LIME_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_800 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_800", Color.MATERIAL_LIME_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_900 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_900", Color.MATERIAL_LIME_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_a100", Color.MATERIAL_LIME_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_a200", Color.MATERIAL_LIME_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_a400", Color.MATERIAL_LIME_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_LIME_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_lime_a700", Color.MATERIAL_LIME_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_50 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_50", Color.MATERIAL_YELLOW_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_100 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_100", Color.MATERIAL_YELLOW_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_200 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_200", Color.MATERIAL_YELLOW_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_300 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_300", Color.MATERIAL_YELLOW_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_400 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_400", Color.MATERIAL_YELLOW_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_500 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_500", Color.MATERIAL_YELLOW_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_600 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_600", Color.MATERIAL_YELLOW_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_700 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_700", Color.MATERIAL_YELLOW_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_800 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_800", Color.MATERIAL_YELLOW_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_900 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_900", Color.MATERIAL_YELLOW_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_a100", Color.MATERIAL_YELLOW_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_a200", Color.MATERIAL_YELLOW_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_a400", Color.MATERIAL_YELLOW_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_YELLOW_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_yellow_a700", Color.MATERIAL_YELLOW_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_50 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_50", Color.MATERIAL_AMBER_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_100 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_100", Color.MATERIAL_AMBER_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_200 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_200", Color.MATERIAL_AMBER_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_300 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_300", Color.MATERIAL_AMBER_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_400 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_400", Color.MATERIAL_AMBER_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_500 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_500", Color.MATERIAL_AMBER_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_600 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_600", Color.MATERIAL_AMBER_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_700 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_700", Color.MATERIAL_AMBER_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_800 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_800", Color.MATERIAL_AMBER_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_900 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_900", Color.MATERIAL_AMBER_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_a100", Color.MATERIAL_AMBER_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_a200", Color.MATERIAL_AMBER_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_a400", Color.MATERIAL_AMBER_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_AMBER_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_amber_a700", Color.MATERIAL_AMBER_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_50 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_50", Color.MATERIAL_ORANGE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_100 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_100", Color.MATERIAL_ORANGE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_200 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_200", Color.MATERIAL_ORANGE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_300 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_300", Color.MATERIAL_ORANGE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_400 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_400", Color.MATERIAL_ORANGE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_500 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_500", Color.MATERIAL_ORANGE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_600 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_600", Color.MATERIAL_ORANGE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_700 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_700", Color.MATERIAL_ORANGE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_800 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_800", Color.MATERIAL_ORANGE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_900 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_900", Color.MATERIAL_ORANGE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_a100", Color.MATERIAL_ORANGE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_a200", Color.MATERIAL_ORANGE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_a400", Color.MATERIAL_ORANGE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_ORANGE_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_orange_a700", Color.MATERIAL_ORANGE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_50 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_50", Color.MATERIAL_DEEP_ORANGE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_100 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_100", Color.MATERIAL_DEEP_ORANGE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_200 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_200", Color.MATERIAL_DEEP_ORANGE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_300 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_300", Color.MATERIAL_DEEP_ORANGE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_400 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_400", Color.MATERIAL_DEEP_ORANGE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_500 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_500", Color.MATERIAL_DEEP_ORANGE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_600 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_600", Color.MATERIAL_DEEP_ORANGE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_700 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_700", Color.MATERIAL_DEEP_ORANGE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_800 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_800", Color.MATERIAL_DEEP_ORANGE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_900 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_900", Color.MATERIAL_DEEP_ORANGE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_A100 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_a100", Color.MATERIAL_DEEP_ORANGE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_A200 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_a200", Color.MATERIAL_DEEP_ORANGE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_A400 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_a400", Color.MATERIAL_DEEP_ORANGE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_DEEP_ORANGE_A700 = addStyle(new PlainShadowStyle("plain_shadow_material_deep_orange_a700", Color.MATERIAL_DEEP_ORANGE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BROWN_50 = addStyle(new PlainShadowStyle("plain_shadow_material_brown_50", Color.MATERIAL_BROWN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BROWN_100 = addStyle(new PlainShadowStyle("plain_shadow_material_brown_100", Color.MATERIAL_BROWN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BROWN_200 = addStyle(new PlainShadowStyle("plain_shadow_material_brown_200", Color.MATERIAL_BROWN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BROWN_300 = addStyle(new PlainShadowStyle("plain_shadow_material_brown_300", Color.MATERIAL_BROWN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BROWN_400 = addStyle(new PlainShadowStyle("plain_shadow_material_brown_400", Color.MATERIAL_BROWN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BROWN_500 = addStyle(new PlainShadowStyle("plain_shadow_material_brown_500", Color.MATERIAL_BROWN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BROWN_600 = addStyle(new PlainShadowStyle("plain_shadow_material_brown_600", Color.MATERIAL_BROWN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BROWN_700 = addStyle(new PlainShadowStyle("plain_shadow_material_brown_700", Color.MATERIAL_BROWN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BROWN_800 = addStyle(new PlainShadowStyle("plain_shadow_material_brown_800", Color.MATERIAL_BROWN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BROWN_900 = addStyle(new PlainShadowStyle("plain_shadow_material_brown_900", Color.MATERIAL_BROWN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREY_50 = addStyle(new PlainShadowStyle("plain_shadow_material_grey_50", Color.MATERIAL_GREY_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREY_100 = addStyle(new PlainShadowStyle("plain_shadow_material_grey_100", Color.MATERIAL_GREY_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREY_200 = addStyle(new PlainShadowStyle("plain_shadow_material_grey_200", Color.MATERIAL_GREY_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREY_300 = addStyle(new PlainShadowStyle("plain_shadow_material_grey_300", Color.MATERIAL_GREY_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREY_400 = addStyle(new PlainShadowStyle("plain_shadow_material_grey_400", Color.MATERIAL_GREY_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREY_500 = addStyle(new PlainShadowStyle("plain_shadow_material_grey_500", Color.MATERIAL_GREY_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREY_600 = addStyle(new PlainShadowStyle("plain_shadow_material_grey_600", Color.MATERIAL_GREY_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREY_700 = addStyle(new PlainShadowStyle("plain_shadow_material_grey_700", Color.MATERIAL_GREY_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREY_800 = addStyle(new PlainShadowStyle("plain_shadow_material_grey_800", Color.MATERIAL_GREY_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_GREY_900 = addStyle(new PlainShadowStyle("plain_shadow_material_grey_900", Color.MATERIAL_GREY_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLACK_1000 = addStyle(new PlainShadowStyle("plain_shadow_material_black_1000", Color.MATERIAL_BLACK_1000.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_WHITE_1000 = addStyle(new PlainShadowStyle("plain_shadow_material_white_1000", Color.MATERIAL_WHITE_1000.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_GREY_50 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_grey_50", Color.MATERIAL_BLUE_GREY_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_GREY_100 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_grey_100", Color.MATERIAL_BLUE_GREY_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_GREY_200 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_grey_200", Color.MATERIAL_BLUE_GREY_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_GREY_300 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_grey_300", Color.MATERIAL_BLUE_GREY_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_GREY_400 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_grey_400", Color.MATERIAL_BLUE_GREY_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_GREY_500 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_grey_500", Color.MATERIAL_BLUE_GREY_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_GREY_600 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_grey_600", Color.MATERIAL_BLUE_GREY_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_GREY_700 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_grey_700", Color.MATERIAL_BLUE_GREY_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_GREY_800 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_grey_800", Color.MATERIAL_BLUE_GREY_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle PLAIN_SHADOW_MATERIAL_BLUE_GREY_900 = addStyle(new PlainShadowStyle("plain_shadow_material_blue_grey_900", Color.MATERIAL_BLUE_GREY_900.toHtmlColorString()));

    //OUTLINE styles:
    public static final AbstractMaterialIconStyle OUTLINE_BOOTSTRAP_PRIMARY = addStyle(new OutlineStyle("outline_bootstrap_primary", Color.BOOTSTRAP_PRIMARY.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_BOOTSTRAP_SUCCESS = addStyle(new OutlineStyle("outline_bootstrap_success", Color.BOOTSTRAP_SUCCESS.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_BOOTSTRAP_INFO = addStyle(new OutlineStyle("outline_bootstrap_info", Color.BOOTSTRAP_INFO.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_BOOTSTRAP_WARNING = addStyle(new OutlineStyle("outline_bootstrap_warning", Color.BOOTSTRAP_WARNING.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_BOOTSTRAP_DANGER = addStyle(new OutlineStyle("outline_bootstrap_danger", Color.BOOTSTRAP_DANGER.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_50 = addStyle(new OutlineStyle("outline_material_red_50", Color.MATERIAL_RED_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_100 = addStyle(new OutlineStyle("outline_material_red_100", Color.MATERIAL_RED_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_200 = addStyle(new OutlineStyle("outline_material_red_200", Color.MATERIAL_RED_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_300 = addStyle(new OutlineStyle("outline_material_red_300", Color.MATERIAL_RED_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_400 = addStyle(new OutlineStyle("outline_material_red_400", Color.MATERIAL_RED_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_500 = addStyle(new OutlineStyle("outline_material_red_500", Color.MATERIAL_RED_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_600 = addStyle(new OutlineStyle("outline_material_red_600", Color.MATERIAL_RED_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_700 = addStyle(new OutlineStyle("outline_material_red_700", Color.MATERIAL_RED_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_800 = addStyle(new OutlineStyle("outline_material_red_800", Color.MATERIAL_RED_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_900 = addStyle(new OutlineStyle("outline_material_red_900", Color.MATERIAL_RED_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_A100 = addStyle(new OutlineStyle("outline_material_red_a100", Color.MATERIAL_RED_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_A200 = addStyle(new OutlineStyle("outline_material_red_a200", Color.MATERIAL_RED_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_A400 = addStyle(new OutlineStyle("outline_material_red_a400", Color.MATERIAL_RED_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_RED_A700 = addStyle(new OutlineStyle("outline_material_red_a700", Color.MATERIAL_RED_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_50 = addStyle(new OutlineStyle("outline_material_pink_50", Color.MATERIAL_PINK_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_100 = addStyle(new OutlineStyle("outline_material_pink_100", Color.MATERIAL_PINK_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_200 = addStyle(new OutlineStyle("outline_material_pink_200", Color.MATERIAL_PINK_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_300 = addStyle(new OutlineStyle("outline_material_pink_300", Color.MATERIAL_PINK_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_400 = addStyle(new OutlineStyle("outline_material_pink_400", Color.MATERIAL_PINK_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_500 = addStyle(new OutlineStyle("outline_material_pink_500", Color.MATERIAL_PINK_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_600 = addStyle(new OutlineStyle("outline_material_pink_600", Color.MATERIAL_PINK_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_700 = addStyle(new OutlineStyle("outline_material_pink_700", Color.MATERIAL_PINK_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_800 = addStyle(new OutlineStyle("outline_material_pink_800", Color.MATERIAL_PINK_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_900 = addStyle(new OutlineStyle("outline_material_pink_900", Color.MATERIAL_PINK_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_A100 = addStyle(new OutlineStyle("outline_material_pink_a100", Color.MATERIAL_PINK_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_A200 = addStyle(new OutlineStyle("outline_material_pink_a200", Color.MATERIAL_PINK_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_A400 = addStyle(new OutlineStyle("outline_material_pink_a400", Color.MATERIAL_PINK_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PINK_A700 = addStyle(new OutlineStyle("outline_material_pink_a700", Color.MATERIAL_PINK_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_50 = addStyle(new OutlineStyle("outline_material_purple_50", Color.MATERIAL_PURPLE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_100 = addStyle(new OutlineStyle("outline_material_purple_100", Color.MATERIAL_PURPLE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_200 = addStyle(new OutlineStyle("outline_material_purple_200", Color.MATERIAL_PURPLE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_300 = addStyle(new OutlineStyle("outline_material_purple_300", Color.MATERIAL_PURPLE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_400 = addStyle(new OutlineStyle("outline_material_purple_400", Color.MATERIAL_PURPLE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_500 = addStyle(new OutlineStyle("outline_material_purple_500", Color.MATERIAL_PURPLE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_600 = addStyle(new OutlineStyle("outline_material_purple_600", Color.MATERIAL_PURPLE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_700 = addStyle(new OutlineStyle("outline_material_purple_700", Color.MATERIAL_PURPLE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_800 = addStyle(new OutlineStyle("outline_material_purple_800", Color.MATERIAL_PURPLE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_900 = addStyle(new OutlineStyle("outline_material_purple_900", Color.MATERIAL_PURPLE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_A100 = addStyle(new OutlineStyle("outline_material_purple_a100", Color.MATERIAL_PURPLE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_A200 = addStyle(new OutlineStyle("outline_material_purple_a200", Color.MATERIAL_PURPLE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_A400 = addStyle(new OutlineStyle("outline_material_purple_a400", Color.MATERIAL_PURPLE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_PURPLE_A700 = addStyle(new OutlineStyle("outline_material_purple_a700", Color.MATERIAL_PURPLE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_50 = addStyle(new OutlineStyle("outline_material_deep_purple_50", Color.MATERIAL_DEEP_PURPLE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_100 = addStyle(new OutlineStyle("outline_material_deep_purple_100", Color.MATERIAL_DEEP_PURPLE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_200 = addStyle(new OutlineStyle("outline_material_deep_purple_200", Color.MATERIAL_DEEP_PURPLE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_300 = addStyle(new OutlineStyle("outline_material_deep_purple_300", Color.MATERIAL_DEEP_PURPLE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_400 = addStyle(new OutlineStyle("outline_material_deep_purple_400", Color.MATERIAL_DEEP_PURPLE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_500 = addStyle(new OutlineStyle("outline_material_deep_purple_500", Color.MATERIAL_DEEP_PURPLE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_600 = addStyle(new OutlineStyle("outline_material_deep_purple_600", Color.MATERIAL_DEEP_PURPLE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_700 = addStyle(new OutlineStyle("outline_material_deep_purple_700", Color.MATERIAL_DEEP_PURPLE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_800 = addStyle(new OutlineStyle("outline_material_deep_purple_800", Color.MATERIAL_DEEP_PURPLE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_900 = addStyle(new OutlineStyle("outline_material_deep_purple_900", Color.MATERIAL_DEEP_PURPLE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_A100 = addStyle(new OutlineStyle("outline_material_deep_purple_a100", Color.MATERIAL_DEEP_PURPLE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_A200 = addStyle(new OutlineStyle("outline_material_deep_purple_a200", Color.MATERIAL_DEEP_PURPLE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_A400 = addStyle(new OutlineStyle("outline_material_deep_purple_a400", Color.MATERIAL_DEEP_PURPLE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_PURPLE_A700 = addStyle(new OutlineStyle("outline_material_deep_purple_a700", Color.MATERIAL_DEEP_PURPLE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_50 = addStyle(new OutlineStyle("outline_material_indigo_50", Color.MATERIAL_INDIGO_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_100 = addStyle(new OutlineStyle("outline_material_indigo_100", Color.MATERIAL_INDIGO_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_200 = addStyle(new OutlineStyle("outline_material_indigo_200", Color.MATERIAL_INDIGO_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_300 = addStyle(new OutlineStyle("outline_material_indigo_300", Color.MATERIAL_INDIGO_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_400 = addStyle(new OutlineStyle("outline_material_indigo_400", Color.MATERIAL_INDIGO_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_500 = addStyle(new OutlineStyle("outline_material_indigo_500", Color.MATERIAL_INDIGO_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_600 = addStyle(new OutlineStyle("outline_material_indigo_600", Color.MATERIAL_INDIGO_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_700 = addStyle(new OutlineStyle("outline_material_indigo_700", Color.MATERIAL_INDIGO_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_800 = addStyle(new OutlineStyle("outline_material_indigo_800", Color.MATERIAL_INDIGO_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_900 = addStyle(new OutlineStyle("outline_material_indigo_900", Color.MATERIAL_INDIGO_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_A100 = addStyle(new OutlineStyle("outline_material_indigo_a100", Color.MATERIAL_INDIGO_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_A200 = addStyle(new OutlineStyle("outline_material_indigo_a200", Color.MATERIAL_INDIGO_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_A400 = addStyle(new OutlineStyle("outline_material_indigo_a400", Color.MATERIAL_INDIGO_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_INDIGO_A700 = addStyle(new OutlineStyle("outline_material_indigo_a700", Color.MATERIAL_INDIGO_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_50 = addStyle(new OutlineStyle("outline_material_blue_50", Color.MATERIAL_BLUE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_100 = addStyle(new OutlineStyle("outline_material_blue_100", Color.MATERIAL_BLUE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_200 = addStyle(new OutlineStyle("outline_material_blue_200", Color.MATERIAL_BLUE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_300 = addStyle(new OutlineStyle("outline_material_blue_300", Color.MATERIAL_BLUE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_400 = addStyle(new OutlineStyle("outline_material_blue_400", Color.MATERIAL_BLUE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_500 = addStyle(new OutlineStyle("outline_material_blue_500", Color.MATERIAL_BLUE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_600 = addStyle(new OutlineStyle("outline_material_blue_600", Color.MATERIAL_BLUE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_700 = addStyle(new OutlineStyle("outline_material_blue_700", Color.MATERIAL_BLUE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_800 = addStyle(new OutlineStyle("outline_material_blue_800", Color.MATERIAL_BLUE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_900 = addStyle(new OutlineStyle("outline_material_blue_900", Color.MATERIAL_BLUE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_A100 = addStyle(new OutlineStyle("outline_material_blue_a100", Color.MATERIAL_BLUE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_A200 = addStyle(new OutlineStyle("outline_material_blue_a200", Color.MATERIAL_BLUE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_A400 = addStyle(new OutlineStyle("outline_material_blue_a400", Color.MATERIAL_BLUE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_A700 = addStyle(new OutlineStyle("outline_material_blue_a700", Color.MATERIAL_BLUE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_50 = addStyle(new OutlineStyle("outline_material_light_blue_50", Color.MATERIAL_LIGHT_BLUE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_100 = addStyle(new OutlineStyle("outline_material_light_blue_100", Color.MATERIAL_LIGHT_BLUE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_200 = addStyle(new OutlineStyle("outline_material_light_blue_200", Color.MATERIAL_LIGHT_BLUE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_300 = addStyle(new OutlineStyle("outline_material_light_blue_300", Color.MATERIAL_LIGHT_BLUE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_400 = addStyle(new OutlineStyle("outline_material_light_blue_400", Color.MATERIAL_LIGHT_BLUE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_500 = addStyle(new OutlineStyle("outline_material_light_blue_500", Color.MATERIAL_LIGHT_BLUE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_600 = addStyle(new OutlineStyle("outline_material_light_blue_600", Color.MATERIAL_LIGHT_BLUE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_700 = addStyle(new OutlineStyle("outline_material_light_blue_700", Color.MATERIAL_LIGHT_BLUE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_800 = addStyle(new OutlineStyle("outline_material_light_blue_800", Color.MATERIAL_LIGHT_BLUE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_900 = addStyle(new OutlineStyle("outline_material_light_blue_900", Color.MATERIAL_LIGHT_BLUE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_A100 = addStyle(new OutlineStyle("outline_material_light_blue_a100", Color.MATERIAL_LIGHT_BLUE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_A200 = addStyle(new OutlineStyle("outline_material_light_blue_a200", Color.MATERIAL_LIGHT_BLUE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_A400 = addStyle(new OutlineStyle("outline_material_light_blue_a400", Color.MATERIAL_LIGHT_BLUE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_BLUE_A700 = addStyle(new OutlineStyle("outline_material_light_blue_a700", Color.MATERIAL_LIGHT_BLUE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_50 = addStyle(new OutlineStyle("outline_material_cyan_50", Color.MATERIAL_CYAN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_100 = addStyle(new OutlineStyle("outline_material_cyan_100", Color.MATERIAL_CYAN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_200 = addStyle(new OutlineStyle("outline_material_cyan_200", Color.MATERIAL_CYAN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_300 = addStyle(new OutlineStyle("outline_material_cyan_300", Color.MATERIAL_CYAN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_400 = addStyle(new OutlineStyle("outline_material_cyan_400", Color.MATERIAL_CYAN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_500 = addStyle(new OutlineStyle("outline_material_cyan_500", Color.MATERIAL_CYAN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_600 = addStyle(new OutlineStyle("outline_material_cyan_600", Color.MATERIAL_CYAN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_700 = addStyle(new OutlineStyle("outline_material_cyan_700", Color.MATERIAL_CYAN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_800 = addStyle(new OutlineStyle("outline_material_cyan_800", Color.MATERIAL_CYAN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_900 = addStyle(new OutlineStyle("outline_material_cyan_900", Color.MATERIAL_CYAN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_A100 = addStyle(new OutlineStyle("outline_material_cyan_a100", Color.MATERIAL_CYAN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_A200 = addStyle(new OutlineStyle("outline_material_cyan_a200", Color.MATERIAL_CYAN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_A400 = addStyle(new OutlineStyle("outline_material_cyan_a400", Color.MATERIAL_CYAN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_CYAN_A700 = addStyle(new OutlineStyle("outline_material_cyan_a700", Color.MATERIAL_CYAN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_50 = addStyle(new OutlineStyle("outline_material_teal_50", Color.MATERIAL_TEAL_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_100 = addStyle(new OutlineStyle("outline_material_teal_100", Color.MATERIAL_TEAL_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_200 = addStyle(new OutlineStyle("outline_material_teal_200", Color.MATERIAL_TEAL_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_300 = addStyle(new OutlineStyle("outline_material_teal_300", Color.MATERIAL_TEAL_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_400 = addStyle(new OutlineStyle("outline_material_teal_400", Color.MATERIAL_TEAL_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_500 = addStyle(new OutlineStyle("outline_material_teal_500", Color.MATERIAL_TEAL_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_600 = addStyle(new OutlineStyle("outline_material_teal_600", Color.MATERIAL_TEAL_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_700 = addStyle(new OutlineStyle("outline_material_teal_700", Color.MATERIAL_TEAL_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_800 = addStyle(new OutlineStyle("outline_material_teal_800", Color.MATERIAL_TEAL_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_900 = addStyle(new OutlineStyle("outline_material_teal_900", Color.MATERIAL_TEAL_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_A100 = addStyle(new OutlineStyle("outline_material_teal_a100", Color.MATERIAL_TEAL_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_A200 = addStyle(new OutlineStyle("outline_material_teal_a200", Color.MATERIAL_TEAL_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_A400 = addStyle(new OutlineStyle("outline_material_teal_a400", Color.MATERIAL_TEAL_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_TEAL_A700 = addStyle(new OutlineStyle("outline_material_teal_a700", Color.MATERIAL_TEAL_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_50 = addStyle(new OutlineStyle("outline_material_green_50", Color.MATERIAL_GREEN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_100 = addStyle(new OutlineStyle("outline_material_green_100", Color.MATERIAL_GREEN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_200 = addStyle(new OutlineStyle("outline_material_green_200", Color.MATERIAL_GREEN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_300 = addStyle(new OutlineStyle("outline_material_green_300", Color.MATERIAL_GREEN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_400 = addStyle(new OutlineStyle("outline_material_green_400", Color.MATERIAL_GREEN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_500 = addStyle(new OutlineStyle("outline_material_green_500", Color.MATERIAL_GREEN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_600 = addStyle(new OutlineStyle("outline_material_green_600", Color.MATERIAL_GREEN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_700 = addStyle(new OutlineStyle("outline_material_green_700", Color.MATERIAL_GREEN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_800 = addStyle(new OutlineStyle("outline_material_green_800", Color.MATERIAL_GREEN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_900 = addStyle(new OutlineStyle("outline_material_green_900", Color.MATERIAL_GREEN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_A100 = addStyle(new OutlineStyle("outline_material_green_a100", Color.MATERIAL_GREEN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_A200 = addStyle(new OutlineStyle("outline_material_green_a200", Color.MATERIAL_GREEN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_A400 = addStyle(new OutlineStyle("outline_material_green_a400", Color.MATERIAL_GREEN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREEN_A700 = addStyle(new OutlineStyle("outline_material_green_a700", Color.MATERIAL_GREEN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_50 = addStyle(new OutlineStyle("outline_material_light_green_50", Color.MATERIAL_LIGHT_GREEN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_100 = addStyle(new OutlineStyle("outline_material_light_green_100", Color.MATERIAL_LIGHT_GREEN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_200 = addStyle(new OutlineStyle("outline_material_light_green_200", Color.MATERIAL_LIGHT_GREEN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_300 = addStyle(new OutlineStyle("outline_material_light_green_300", Color.MATERIAL_LIGHT_GREEN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_400 = addStyle(new OutlineStyle("outline_material_light_green_400", Color.MATERIAL_LIGHT_GREEN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_500 = addStyle(new OutlineStyle("outline_material_light_green_500", Color.MATERIAL_LIGHT_GREEN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_600 = addStyle(new OutlineStyle("outline_material_light_green_600", Color.MATERIAL_LIGHT_GREEN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_700 = addStyle(new OutlineStyle("outline_material_light_green_700", Color.MATERIAL_LIGHT_GREEN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_800 = addStyle(new OutlineStyle("outline_material_light_green_800", Color.MATERIAL_LIGHT_GREEN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_900 = addStyle(new OutlineStyle("outline_material_light_green_900", Color.MATERIAL_LIGHT_GREEN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_A100 = addStyle(new OutlineStyle("outline_material_light_green_a100", Color.MATERIAL_LIGHT_GREEN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_A200 = addStyle(new OutlineStyle("outline_material_light_green_a200", Color.MATERIAL_LIGHT_GREEN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_A400 = addStyle(new OutlineStyle("outline_material_light_green_a400", Color.MATERIAL_LIGHT_GREEN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIGHT_GREEN_A700 = addStyle(new OutlineStyle("outline_material_light_green_a700", Color.MATERIAL_LIGHT_GREEN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_50 = addStyle(new OutlineStyle("outline_material_lime_50", Color.MATERIAL_LIME_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_100 = addStyle(new OutlineStyle("outline_material_lime_100", Color.MATERIAL_LIME_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_200 = addStyle(new OutlineStyle("outline_material_lime_200", Color.MATERIAL_LIME_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_300 = addStyle(new OutlineStyle("outline_material_lime_300", Color.MATERIAL_LIME_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_400 = addStyle(new OutlineStyle("outline_material_lime_400", Color.MATERIAL_LIME_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_500 = addStyle(new OutlineStyle("outline_material_lime_500", Color.MATERIAL_LIME_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_600 = addStyle(new OutlineStyle("outline_material_lime_600", Color.MATERIAL_LIME_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_700 = addStyle(new OutlineStyle("outline_material_lime_700", Color.MATERIAL_LIME_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_800 = addStyle(new OutlineStyle("outline_material_lime_800", Color.MATERIAL_LIME_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_900 = addStyle(new OutlineStyle("outline_material_lime_900", Color.MATERIAL_LIME_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_A100 = addStyle(new OutlineStyle("outline_material_lime_a100", Color.MATERIAL_LIME_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_A200 = addStyle(new OutlineStyle("outline_material_lime_a200", Color.MATERIAL_LIME_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_A400 = addStyle(new OutlineStyle("outline_material_lime_a400", Color.MATERIAL_LIME_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_LIME_A700 = addStyle(new OutlineStyle("outline_material_lime_a700", Color.MATERIAL_LIME_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_50 = addStyle(new OutlineStyle("outline_material_yellow_50", Color.MATERIAL_YELLOW_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_100 = addStyle(new OutlineStyle("outline_material_yellow_100", Color.MATERIAL_YELLOW_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_200 = addStyle(new OutlineStyle("outline_material_yellow_200", Color.MATERIAL_YELLOW_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_300 = addStyle(new OutlineStyle("outline_material_yellow_300", Color.MATERIAL_YELLOW_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_400 = addStyle(new OutlineStyle("outline_material_yellow_400", Color.MATERIAL_YELLOW_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_500 = addStyle(new OutlineStyle("outline_material_yellow_500", Color.MATERIAL_YELLOW_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_600 = addStyle(new OutlineStyle("outline_material_yellow_600", Color.MATERIAL_YELLOW_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_700 = addStyle(new OutlineStyle("outline_material_yellow_700", Color.MATERIAL_YELLOW_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_800 = addStyle(new OutlineStyle("outline_material_yellow_800", Color.MATERIAL_YELLOW_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_900 = addStyle(new OutlineStyle("outline_material_yellow_900", Color.MATERIAL_YELLOW_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_A100 = addStyle(new OutlineStyle("outline_material_yellow_a100", Color.MATERIAL_YELLOW_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_A200 = addStyle(new OutlineStyle("outline_material_yellow_a200", Color.MATERIAL_YELLOW_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_A400 = addStyle(new OutlineStyle("outline_material_yellow_a400", Color.MATERIAL_YELLOW_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_YELLOW_A700 = addStyle(new OutlineStyle("outline_material_yellow_a700", Color.MATERIAL_YELLOW_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_50 = addStyle(new OutlineStyle("outline_material_amber_50", Color.MATERIAL_AMBER_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_100 = addStyle(new OutlineStyle("outline_material_amber_100", Color.MATERIAL_AMBER_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_200 = addStyle(new OutlineStyle("outline_material_amber_200", Color.MATERIAL_AMBER_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_300 = addStyle(new OutlineStyle("outline_material_amber_300", Color.MATERIAL_AMBER_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_400 = addStyle(new OutlineStyle("outline_material_amber_400", Color.MATERIAL_AMBER_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_500 = addStyle(new OutlineStyle("outline_material_amber_500", Color.MATERIAL_AMBER_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_600 = addStyle(new OutlineStyle("outline_material_amber_600", Color.MATERIAL_AMBER_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_700 = addStyle(new OutlineStyle("outline_material_amber_700", Color.MATERIAL_AMBER_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_800 = addStyle(new OutlineStyle("outline_material_amber_800", Color.MATERIAL_AMBER_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_900 = addStyle(new OutlineStyle("outline_material_amber_900", Color.MATERIAL_AMBER_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_A100 = addStyle(new OutlineStyle("outline_material_amber_a100", Color.MATERIAL_AMBER_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_A200 = addStyle(new OutlineStyle("outline_material_amber_a200", Color.MATERIAL_AMBER_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_A400 = addStyle(new OutlineStyle("outline_material_amber_a400", Color.MATERIAL_AMBER_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_AMBER_A700 = addStyle(new OutlineStyle("outline_material_amber_a700", Color.MATERIAL_AMBER_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_50 = addStyle(new OutlineStyle("outline_material_orange_50", Color.MATERIAL_ORANGE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_100 = addStyle(new OutlineStyle("outline_material_orange_100", Color.MATERIAL_ORANGE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_200 = addStyle(new OutlineStyle("outline_material_orange_200", Color.MATERIAL_ORANGE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_300 = addStyle(new OutlineStyle("outline_material_orange_300", Color.MATERIAL_ORANGE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_400 = addStyle(new OutlineStyle("outline_material_orange_400", Color.MATERIAL_ORANGE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_500 = addStyle(new OutlineStyle("outline_material_orange_500", Color.MATERIAL_ORANGE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_600 = addStyle(new OutlineStyle("outline_material_orange_600", Color.MATERIAL_ORANGE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_700 = addStyle(new OutlineStyle("outline_material_orange_700", Color.MATERIAL_ORANGE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_800 = addStyle(new OutlineStyle("outline_material_orange_800", Color.MATERIAL_ORANGE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_900 = addStyle(new OutlineStyle("outline_material_orange_900", Color.MATERIAL_ORANGE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_A100 = addStyle(new OutlineStyle("outline_material_orange_a100", Color.MATERIAL_ORANGE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_A200 = addStyle(new OutlineStyle("outline_material_orange_a200", Color.MATERIAL_ORANGE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_A400 = addStyle(new OutlineStyle("outline_material_orange_a400", Color.MATERIAL_ORANGE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_ORANGE_A700 = addStyle(new OutlineStyle("outline_material_orange_a700", Color.MATERIAL_ORANGE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_50 = addStyle(new OutlineStyle("outline_material_deep_orange_50", Color.MATERIAL_DEEP_ORANGE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_100 = addStyle(new OutlineStyle("outline_material_deep_orange_100", Color.MATERIAL_DEEP_ORANGE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_200 = addStyle(new OutlineStyle("outline_material_deep_orange_200", Color.MATERIAL_DEEP_ORANGE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_300 = addStyle(new OutlineStyle("outline_material_deep_orange_300", Color.MATERIAL_DEEP_ORANGE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_400 = addStyle(new OutlineStyle("outline_material_deep_orange_400", Color.MATERIAL_DEEP_ORANGE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_500 = addStyle(new OutlineStyle("outline_material_deep_orange_500", Color.MATERIAL_DEEP_ORANGE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_600 = addStyle(new OutlineStyle("outline_material_deep_orange_600", Color.MATERIAL_DEEP_ORANGE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_700 = addStyle(new OutlineStyle("outline_material_deep_orange_700", Color.MATERIAL_DEEP_ORANGE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_800 = addStyle(new OutlineStyle("outline_material_deep_orange_800", Color.MATERIAL_DEEP_ORANGE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_900 = addStyle(new OutlineStyle("outline_material_deep_orange_900", Color.MATERIAL_DEEP_ORANGE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_A100 = addStyle(new OutlineStyle("outline_material_deep_orange_a100", Color.MATERIAL_DEEP_ORANGE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_A200 = addStyle(new OutlineStyle("outline_material_deep_orange_a200", Color.MATERIAL_DEEP_ORANGE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_A400 = addStyle(new OutlineStyle("outline_material_deep_orange_a400", Color.MATERIAL_DEEP_ORANGE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_DEEP_ORANGE_A700 = addStyle(new OutlineStyle("outline_material_deep_orange_a700", Color.MATERIAL_DEEP_ORANGE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BROWN_50 = addStyle(new OutlineStyle("outline_material_brown_50", Color.MATERIAL_BROWN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BROWN_100 = addStyle(new OutlineStyle("outline_material_brown_100", Color.MATERIAL_BROWN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BROWN_200 = addStyle(new OutlineStyle("outline_material_brown_200", Color.MATERIAL_BROWN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BROWN_300 = addStyle(new OutlineStyle("outline_material_brown_300", Color.MATERIAL_BROWN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BROWN_400 = addStyle(new OutlineStyle("outline_material_brown_400", Color.MATERIAL_BROWN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BROWN_500 = addStyle(new OutlineStyle("outline_material_brown_500", Color.MATERIAL_BROWN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BROWN_600 = addStyle(new OutlineStyle("outline_material_brown_600", Color.MATERIAL_BROWN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BROWN_700 = addStyle(new OutlineStyle("outline_material_brown_700", Color.MATERIAL_BROWN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BROWN_800 = addStyle(new OutlineStyle("outline_material_brown_800", Color.MATERIAL_BROWN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BROWN_900 = addStyle(new OutlineStyle("outline_material_brown_900", Color.MATERIAL_BROWN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREY_50 = addStyle(new OutlineStyle("outline_material_grey_50", Color.MATERIAL_GREY_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREY_100 = addStyle(new OutlineStyle("outline_material_grey_100", Color.MATERIAL_GREY_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREY_200 = addStyle(new OutlineStyle("outline_material_grey_200", Color.MATERIAL_GREY_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREY_300 = addStyle(new OutlineStyle("outline_material_grey_300", Color.MATERIAL_GREY_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREY_400 = addStyle(new OutlineStyle("outline_material_grey_400", Color.MATERIAL_GREY_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREY_500 = addStyle(new OutlineStyle("outline_material_grey_500", Color.MATERIAL_GREY_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREY_600 = addStyle(new OutlineStyle("outline_material_grey_600", Color.MATERIAL_GREY_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREY_700 = addStyle(new OutlineStyle("outline_material_grey_700", Color.MATERIAL_GREY_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREY_800 = addStyle(new OutlineStyle("outline_material_grey_800", Color.MATERIAL_GREY_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_GREY_900 = addStyle(new OutlineStyle("outline_material_grey_900", Color.MATERIAL_GREY_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLACK_1000 = addStyle(new OutlineStyle("outline_material_black_1000", Color.MATERIAL_BLACK_1000.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_WHITE_1000 = addStyle(new OutlineStyle("outline_material_white_1000", Color.MATERIAL_WHITE_1000.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_GREY_50 = addStyle(new OutlineStyle("outline_material_blue_grey_50", Color.MATERIAL_BLUE_GREY_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_GREY_100 = addStyle(new OutlineStyle("outline_material_blue_grey_100", Color.MATERIAL_BLUE_GREY_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_GREY_200 = addStyle(new OutlineStyle("outline_material_blue_grey_200", Color.MATERIAL_BLUE_GREY_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_GREY_300 = addStyle(new OutlineStyle("outline_material_blue_grey_300", Color.MATERIAL_BLUE_GREY_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_GREY_400 = addStyle(new OutlineStyle("outline_material_blue_grey_400", Color.MATERIAL_BLUE_GREY_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_GREY_500 = addStyle(new OutlineStyle("outline_material_blue_grey_500", Color.MATERIAL_BLUE_GREY_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_GREY_600 = addStyle(new OutlineStyle("outline_material_blue_grey_600", Color.MATERIAL_BLUE_GREY_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_GREY_700 = addStyle(new OutlineStyle("outline_material_blue_grey_700", Color.MATERIAL_BLUE_GREY_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_GREY_800 = addStyle(new OutlineStyle("outline_material_blue_grey_800", Color.MATERIAL_BLUE_GREY_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_MATERIAL_BLUE_GREY_900 = addStyle(new OutlineStyle("outline_material_blue_grey_900", Color.MATERIAL_BLUE_GREY_900.toHtmlColorString()));

    //OUTLINE_FILLED styles:
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_BOOTSTRAP_PRIMARY = addStyle(new OutlineFilledStyle("outline_filled_bootstrap_primary", Color.BOOTSTRAP_PRIMARY.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_BOOTSTRAP_SUCCESS = addStyle(new OutlineFilledStyle("outline_filled_bootstrap_success", Color.BOOTSTRAP_SUCCESS.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_BOOTSTRAP_INFO = addStyle(new OutlineFilledStyle("outline_filled_bootstrap_info", Color.BOOTSTRAP_INFO.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_BOOTSTRAP_WARNING = addStyle(new OutlineFilledStyle("outline_filled_bootstrap_warning", Color.BOOTSTRAP_WARNING.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_BOOTSTRAP_DANGER = addStyle(new OutlineFilledStyle("outline_filled_bootstrap_danger", Color.BOOTSTRAP_DANGER.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_50 = addStyle(new OutlineFilledStyle("outline_filled_material_red_50", Color.MATERIAL_RED_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_100 = addStyle(new OutlineFilledStyle("outline_filled_material_red_100", Color.MATERIAL_RED_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_200 = addStyle(new OutlineFilledStyle("outline_filled_material_red_200", Color.MATERIAL_RED_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_300 = addStyle(new OutlineFilledStyle("outline_filled_material_red_300", Color.MATERIAL_RED_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_400 = addStyle(new OutlineFilledStyle("outline_filled_material_red_400", Color.MATERIAL_RED_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_500 = addStyle(new OutlineFilledStyle("outline_filled_material_red_500", Color.MATERIAL_RED_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_600 = addStyle(new OutlineFilledStyle("outline_filled_material_red_600", Color.MATERIAL_RED_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_700 = addStyle(new OutlineFilledStyle("outline_filled_material_red_700", Color.MATERIAL_RED_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_800 = addStyle(new OutlineFilledStyle("outline_filled_material_red_800", Color.MATERIAL_RED_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_900 = addStyle(new OutlineFilledStyle("outline_filled_material_red_900", Color.MATERIAL_RED_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_red_a100", Color.MATERIAL_RED_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_red_a200", Color.MATERIAL_RED_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_red_a400", Color.MATERIAL_RED_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_RED_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_red_a700", Color.MATERIAL_RED_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_50 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_50", Color.MATERIAL_PINK_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_100 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_100", Color.MATERIAL_PINK_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_200 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_200", Color.MATERIAL_PINK_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_300 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_300", Color.MATERIAL_PINK_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_400 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_400", Color.MATERIAL_PINK_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_500 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_500", Color.MATERIAL_PINK_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_600 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_600", Color.MATERIAL_PINK_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_700 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_700", Color.MATERIAL_PINK_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_800 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_800", Color.MATERIAL_PINK_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_900 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_900", Color.MATERIAL_PINK_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_a100", Color.MATERIAL_PINK_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_a200", Color.MATERIAL_PINK_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_a400", Color.MATERIAL_PINK_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PINK_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_pink_a700", Color.MATERIAL_PINK_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_50 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_50", Color.MATERIAL_PURPLE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_100 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_100", Color.MATERIAL_PURPLE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_200 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_200", Color.MATERIAL_PURPLE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_300 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_300", Color.MATERIAL_PURPLE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_400 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_400", Color.MATERIAL_PURPLE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_500 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_500", Color.MATERIAL_PURPLE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_600 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_600", Color.MATERIAL_PURPLE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_700 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_700", Color.MATERIAL_PURPLE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_800 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_800", Color.MATERIAL_PURPLE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_900 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_900", Color.MATERIAL_PURPLE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_a100", Color.MATERIAL_PURPLE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_a200", Color.MATERIAL_PURPLE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_a400", Color.MATERIAL_PURPLE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_PURPLE_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_purple_a700", Color.MATERIAL_PURPLE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_50 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_50", Color.MATERIAL_DEEP_PURPLE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_100 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_100", Color.MATERIAL_DEEP_PURPLE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_200 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_200", Color.MATERIAL_DEEP_PURPLE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_300 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_300", Color.MATERIAL_DEEP_PURPLE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_400 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_400", Color.MATERIAL_DEEP_PURPLE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_500 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_500", Color.MATERIAL_DEEP_PURPLE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_600 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_600", Color.MATERIAL_DEEP_PURPLE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_700 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_700", Color.MATERIAL_DEEP_PURPLE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_800 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_800", Color.MATERIAL_DEEP_PURPLE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_900 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_900", Color.MATERIAL_DEEP_PURPLE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_a100", Color.MATERIAL_DEEP_PURPLE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_a200", Color.MATERIAL_DEEP_PURPLE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_a400", Color.MATERIAL_DEEP_PURPLE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_PURPLE_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_purple_a700", Color.MATERIAL_DEEP_PURPLE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_50 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_50", Color.MATERIAL_INDIGO_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_100 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_100", Color.MATERIAL_INDIGO_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_200 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_200", Color.MATERIAL_INDIGO_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_300 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_300", Color.MATERIAL_INDIGO_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_400 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_400", Color.MATERIAL_INDIGO_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_500 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_500", Color.MATERIAL_INDIGO_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_600 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_600", Color.MATERIAL_INDIGO_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_700 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_700", Color.MATERIAL_INDIGO_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_800 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_800", Color.MATERIAL_INDIGO_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_900 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_900", Color.MATERIAL_INDIGO_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_a100", Color.MATERIAL_INDIGO_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_a200", Color.MATERIAL_INDIGO_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_a400", Color.MATERIAL_INDIGO_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_INDIGO_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_indigo_a700", Color.MATERIAL_INDIGO_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_50 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_50", Color.MATERIAL_BLUE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_100 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_100", Color.MATERIAL_BLUE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_200 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_200", Color.MATERIAL_BLUE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_300 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_300", Color.MATERIAL_BLUE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_400 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_400", Color.MATERIAL_BLUE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_500 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_500", Color.MATERIAL_BLUE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_600 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_600", Color.MATERIAL_BLUE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_700 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_700", Color.MATERIAL_BLUE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_800 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_800", Color.MATERIAL_BLUE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_900 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_900", Color.MATERIAL_BLUE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_a100", Color.MATERIAL_BLUE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_a200", Color.MATERIAL_BLUE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_a400", Color.MATERIAL_BLUE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_a700", Color.MATERIAL_BLUE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_50 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_50", Color.MATERIAL_LIGHT_BLUE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_100 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_100", Color.MATERIAL_LIGHT_BLUE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_200 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_200", Color.MATERIAL_LIGHT_BLUE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_300 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_300", Color.MATERIAL_LIGHT_BLUE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_400 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_400", Color.MATERIAL_LIGHT_BLUE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_500 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_500", Color.MATERIAL_LIGHT_BLUE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_600 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_600", Color.MATERIAL_LIGHT_BLUE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_700 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_700", Color.MATERIAL_LIGHT_BLUE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_800 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_800", Color.MATERIAL_LIGHT_BLUE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_900 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_900", Color.MATERIAL_LIGHT_BLUE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_a100", Color.MATERIAL_LIGHT_BLUE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_a200", Color.MATERIAL_LIGHT_BLUE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_a400", Color.MATERIAL_LIGHT_BLUE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_BLUE_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_light_blue_a700", Color.MATERIAL_LIGHT_BLUE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_50 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_50", Color.MATERIAL_CYAN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_100 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_100", Color.MATERIAL_CYAN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_200 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_200", Color.MATERIAL_CYAN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_300 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_300", Color.MATERIAL_CYAN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_400 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_400", Color.MATERIAL_CYAN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_500 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_500", Color.MATERIAL_CYAN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_600 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_600", Color.MATERIAL_CYAN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_700 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_700", Color.MATERIAL_CYAN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_800 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_800", Color.MATERIAL_CYAN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_900 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_900", Color.MATERIAL_CYAN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_a100", Color.MATERIAL_CYAN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_a200", Color.MATERIAL_CYAN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_a400", Color.MATERIAL_CYAN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_CYAN_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_cyan_a700", Color.MATERIAL_CYAN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_50 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_50", Color.MATERIAL_TEAL_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_100 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_100", Color.MATERIAL_TEAL_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_200 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_200", Color.MATERIAL_TEAL_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_300 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_300", Color.MATERIAL_TEAL_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_400 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_400", Color.MATERIAL_TEAL_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_500 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_500", Color.MATERIAL_TEAL_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_600 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_600", Color.MATERIAL_TEAL_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_700 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_700", Color.MATERIAL_TEAL_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_800 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_800", Color.MATERIAL_TEAL_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_900 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_900", Color.MATERIAL_TEAL_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_a100", Color.MATERIAL_TEAL_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_a200", Color.MATERIAL_TEAL_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_a400", Color.MATERIAL_TEAL_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_TEAL_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_teal_a700", Color.MATERIAL_TEAL_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_50 = addStyle(new OutlineFilledStyle("outline_filled_material_green_50", Color.MATERIAL_GREEN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_100 = addStyle(new OutlineFilledStyle("outline_filled_material_green_100", Color.MATERIAL_GREEN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_200 = addStyle(new OutlineFilledStyle("outline_filled_material_green_200", Color.MATERIAL_GREEN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_300 = addStyle(new OutlineFilledStyle("outline_filled_material_green_300", Color.MATERIAL_GREEN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_400 = addStyle(new OutlineFilledStyle("outline_filled_material_green_400", Color.MATERIAL_GREEN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_500 = addStyle(new OutlineFilledStyle("outline_filled_material_green_500", Color.MATERIAL_GREEN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_600 = addStyle(new OutlineFilledStyle("outline_filled_material_green_600", Color.MATERIAL_GREEN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_700 = addStyle(new OutlineFilledStyle("outline_filled_material_green_700", Color.MATERIAL_GREEN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_800 = addStyle(new OutlineFilledStyle("outline_filled_material_green_800", Color.MATERIAL_GREEN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_900 = addStyle(new OutlineFilledStyle("outline_filled_material_green_900", Color.MATERIAL_GREEN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_green_a100", Color.MATERIAL_GREEN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_green_a200", Color.MATERIAL_GREEN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_green_a400", Color.MATERIAL_GREEN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREEN_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_green_a700", Color.MATERIAL_GREEN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_50 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_50", Color.MATERIAL_LIGHT_GREEN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_100 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_100", Color.MATERIAL_LIGHT_GREEN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_200 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_200", Color.MATERIAL_LIGHT_GREEN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_300 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_300", Color.MATERIAL_LIGHT_GREEN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_400 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_400", Color.MATERIAL_LIGHT_GREEN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_500 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_500", Color.MATERIAL_LIGHT_GREEN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_600 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_600", Color.MATERIAL_LIGHT_GREEN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_700 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_700", Color.MATERIAL_LIGHT_GREEN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_800 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_800", Color.MATERIAL_LIGHT_GREEN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_900 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_900", Color.MATERIAL_LIGHT_GREEN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_a100", Color.MATERIAL_LIGHT_GREEN_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_a200", Color.MATERIAL_LIGHT_GREEN_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_a400", Color.MATERIAL_LIGHT_GREEN_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIGHT_GREEN_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_light_green_a700", Color.MATERIAL_LIGHT_GREEN_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_50 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_50", Color.MATERIAL_LIME_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_100 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_100", Color.MATERIAL_LIME_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_200 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_200", Color.MATERIAL_LIME_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_300 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_300", Color.MATERIAL_LIME_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_400 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_400", Color.MATERIAL_LIME_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_500 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_500", Color.MATERIAL_LIME_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_600 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_600", Color.MATERIAL_LIME_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_700 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_700", Color.MATERIAL_LIME_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_800 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_800", Color.MATERIAL_LIME_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_900 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_900", Color.MATERIAL_LIME_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_a100", Color.MATERIAL_LIME_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_a200", Color.MATERIAL_LIME_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_a400", Color.MATERIAL_LIME_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_LIME_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_lime_a700", Color.MATERIAL_LIME_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_50 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_50", Color.MATERIAL_YELLOW_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_100 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_100", Color.MATERIAL_YELLOW_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_200 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_200", Color.MATERIAL_YELLOW_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_300 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_300", Color.MATERIAL_YELLOW_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_400 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_400", Color.MATERIAL_YELLOW_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_500 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_500", Color.MATERIAL_YELLOW_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_600 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_600", Color.MATERIAL_YELLOW_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_700 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_700", Color.MATERIAL_YELLOW_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_800 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_800", Color.MATERIAL_YELLOW_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_900 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_900", Color.MATERIAL_YELLOW_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_a100", Color.MATERIAL_YELLOW_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_a200", Color.MATERIAL_YELLOW_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_a400", Color.MATERIAL_YELLOW_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_YELLOW_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_yellow_a700", Color.MATERIAL_YELLOW_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_50 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_50", Color.MATERIAL_AMBER_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_100 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_100", Color.MATERIAL_AMBER_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_200 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_200", Color.MATERIAL_AMBER_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_300 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_300", Color.MATERIAL_AMBER_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_400 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_400", Color.MATERIAL_AMBER_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_500 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_500", Color.MATERIAL_AMBER_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_600 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_600", Color.MATERIAL_AMBER_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_700 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_700", Color.MATERIAL_AMBER_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_800 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_800", Color.MATERIAL_AMBER_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_900 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_900", Color.MATERIAL_AMBER_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_a100", Color.MATERIAL_AMBER_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_a200", Color.MATERIAL_AMBER_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_a400", Color.MATERIAL_AMBER_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_AMBER_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_amber_a700", Color.MATERIAL_AMBER_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_50 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_50", Color.MATERIAL_ORANGE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_100 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_100", Color.MATERIAL_ORANGE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_200 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_200", Color.MATERIAL_ORANGE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_300 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_300", Color.MATERIAL_ORANGE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_400 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_400", Color.MATERIAL_ORANGE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_500 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_500", Color.MATERIAL_ORANGE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_600 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_600", Color.MATERIAL_ORANGE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_700 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_700", Color.MATERIAL_ORANGE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_800 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_800", Color.MATERIAL_ORANGE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_900 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_900", Color.MATERIAL_ORANGE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_a100", Color.MATERIAL_ORANGE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_a200", Color.MATERIAL_ORANGE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_a400", Color.MATERIAL_ORANGE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_ORANGE_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_orange_a700", Color.MATERIAL_ORANGE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_50 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_50", Color.MATERIAL_DEEP_ORANGE_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_100 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_100", Color.MATERIAL_DEEP_ORANGE_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_200 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_200", Color.MATERIAL_DEEP_ORANGE_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_300 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_300", Color.MATERIAL_DEEP_ORANGE_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_400 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_400", Color.MATERIAL_DEEP_ORANGE_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_500 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_500", Color.MATERIAL_DEEP_ORANGE_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_600 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_600", Color.MATERIAL_DEEP_ORANGE_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_700 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_700", Color.MATERIAL_DEEP_ORANGE_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_800 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_800", Color.MATERIAL_DEEP_ORANGE_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_900 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_900", Color.MATERIAL_DEEP_ORANGE_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_A100 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_a100", Color.MATERIAL_DEEP_ORANGE_A100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_A200 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_a200", Color.MATERIAL_DEEP_ORANGE_A200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_A400 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_a400", Color.MATERIAL_DEEP_ORANGE_A400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_DEEP_ORANGE_A700 = addStyle(new OutlineFilledStyle("outline_filled_material_deep_orange_a700", Color.MATERIAL_DEEP_ORANGE_A700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BROWN_50 = addStyle(new OutlineFilledStyle("outline_filled_material_brown_50", Color.MATERIAL_BROWN_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BROWN_100 = addStyle(new OutlineFilledStyle("outline_filled_material_brown_100", Color.MATERIAL_BROWN_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BROWN_200 = addStyle(new OutlineFilledStyle("outline_filled_material_brown_200", Color.MATERIAL_BROWN_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BROWN_300 = addStyle(new OutlineFilledStyle("outline_filled_material_brown_300", Color.MATERIAL_BROWN_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BROWN_400 = addStyle(new OutlineFilledStyle("outline_filled_material_brown_400", Color.MATERIAL_BROWN_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BROWN_500 = addStyle(new OutlineFilledStyle("outline_filled_material_brown_500", Color.MATERIAL_BROWN_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BROWN_600 = addStyle(new OutlineFilledStyle("outline_filled_material_brown_600", Color.MATERIAL_BROWN_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BROWN_700 = addStyle(new OutlineFilledStyle("outline_filled_material_brown_700", Color.MATERIAL_BROWN_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BROWN_800 = addStyle(new OutlineFilledStyle("outline_filled_material_brown_800", Color.MATERIAL_BROWN_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BROWN_900 = addStyle(new OutlineFilledStyle("outline_filled_material_brown_900", Color.MATERIAL_BROWN_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREY_50 = addStyle(new OutlineFilledStyle("outline_filled_material_grey_50", Color.MATERIAL_GREY_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREY_100 = addStyle(new OutlineFilledStyle("outline_filled_material_grey_100", Color.MATERIAL_GREY_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREY_200 = addStyle(new OutlineFilledStyle("outline_filled_material_grey_200", Color.MATERIAL_GREY_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREY_300 = addStyle(new OutlineFilledStyle("outline_filled_material_grey_300", Color.MATERIAL_GREY_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREY_400 = addStyle(new OutlineFilledStyle("outline_filled_material_grey_400", Color.MATERIAL_GREY_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREY_500 = addStyle(new OutlineFilledStyle("outline_filled_material_grey_500", Color.MATERIAL_GREY_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREY_600 = addStyle(new OutlineFilledStyle("outline_filled_material_grey_600", Color.MATERIAL_GREY_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREY_700 = addStyle(new OutlineFilledStyle("outline_filled_material_grey_700", Color.MATERIAL_GREY_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREY_800 = addStyle(new OutlineFilledStyle("outline_filled_material_grey_800", Color.MATERIAL_GREY_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_GREY_900 = addStyle(new OutlineFilledStyle("outline_filled_material_grey_900", Color.MATERIAL_GREY_900.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLACK_1000 = addStyle(new OutlineFilledStyle("outline_filled_material_black_1000", Color.MATERIAL_BLACK_1000.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_WHITE_1000 = addStyle(new OutlineFilledStyle("outline_filled_material_white_1000", Color.MATERIAL_WHITE_1000.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_GREY_50 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_grey_50", Color.MATERIAL_BLUE_GREY_50.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_GREY_100 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_grey_100", Color.MATERIAL_BLUE_GREY_100.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_GREY_200 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_grey_200", Color.MATERIAL_BLUE_GREY_200.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_GREY_300 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_grey_300", Color.MATERIAL_BLUE_GREY_300.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_GREY_400 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_grey_400", Color.MATERIAL_BLUE_GREY_400.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_GREY_500 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_grey_500", Color.MATERIAL_BLUE_GREY_500.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_GREY_600 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_grey_600", Color.MATERIAL_BLUE_GREY_600.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_GREY_700 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_grey_700", Color.MATERIAL_BLUE_GREY_700.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_GREY_800 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_grey_800", Color.MATERIAL_BLUE_GREY_800.toHtmlColorString()));
    public static final AbstractMaterialIconStyle OUTLINE_FILLED_MATERIAL_BLUE_GREY_900 = addStyle(new OutlineFilledStyle("outline_filled_material_blue_grey_900", Color.MATERIAL_BLUE_GREY_900.toHtmlColorString()));



    private static AbstractMaterialIconStyle addStyle(AbstractMaterialIconStyle style) {
        baseStyles.add(style);
        return style;
    }

    private Map<String, AbstractMaterialIconStyle> styleById = new HashMap<>();
    private Map<String, String> svgByStyleAndName = new HashMap<>();

    public MaterialIconProvider() {
        baseStyles.forEach(this::addIconStyle);
    }

    public void addIconStyle(AbstractMaterialIconStyle iconStyle) {
        styleById.put(iconStyle.getStyleId(), iconStyle);
    }

    public void addPlainStyle(String styleId, String color) {
        addIconStyle(new PlainStyle(styleId, color));
    }

    public void addPlainShadowStyle(String styleId, String color) {
        addIconStyle(new PlainShadowStyle(styleId, color));
    }

    public void addOutlineStyle(String styleId, String color) {
        addIconStyle(new OutlineStyle(styleId, color));
    }

    public void addOutlineFilledStyle(String styleId, String color) {
        addIconStyle(new OutlineFilledStyle(styleId, color));
    }

    public void addGradientStyle(String styleId, String color1, String color2, String color3) {
        addIconStyle(new GradientStyle(styleId, color1, color2, color3));
    }

    @Override
    public String getInnerSvg(IconStyle style, String iconName) {
        String svg = getSVG(style.getStyleId(), iconName);
        if (svg != null) {
            //todo remove svg tags
        }
        return null;
    }

    @Override
    public byte[] getIcon(String styleId, int size, String iconName) {
        String svg = getSVG(styleId, iconName);
        if (svg != null) {
            return svg.getBytes(StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }

    private String getSVG(String styleId, String iconName) {
        if (!iconName.endsWith(".svg")) {
            iconName += ".svg";
        }
        String key = styleId + ":" + iconName;
        String svg = svgByStyleAndName.get(key);
        if (svg != null) {
            return svg;
        }
        AbstractMaterialIconStyle iconStyle = styleById.get(styleId);
        if (iconStyle == null) {
            return null;
        }


        InputStream inputStream = getClass().getResourceAsStream("/org/teamapps/icon/material/" + iconStyle.getStyleType().getPackageName() + "/" + iconName);
        if (inputStream == null) {
            return null;
        }
        try {
            svg = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            svg = svg.replace("<desc>add icon - Licensed under Apache License v2.0 (http://www.apache.org/licenses/LICENSE-2.0) - Created with Iconfu.com - Derivative work of Material icons (Copyright Google Inc.)</desc>", "");
            svg = iconStyle.applyStyle(svg);
            svgByStyleAndName.put(key, svg);
            return svg;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String getIconLibraryId() {
        return LIBRARY_ID;
    }

    @Override
    public Set<Integer> getAvailableIconSizes() {
        return null;
    }

    @Override
    public Set<IconStyle> getAvailableIconStyles() {
        return new HashSet<>(styleById.values());
    }

    @Override
    public IconStyle getDefaultDesktopStyle() {
        return PLAIN_MATERIAL_BLUE_A700;
    }

    @Override
    public IconStyle getDefaultMobileStyle() {
        return PLAIN_MATERIAL_BLUE_A700;
    }

    @Override
    public IconStyle getDefaultSubIconStyle() {
        return PLAIN_MATERIAL_BLUE_A700;
    }

    public static List<AbstractMaterialIconStyle> getBaseStyles() {
        return baseStyles;
    }

}
