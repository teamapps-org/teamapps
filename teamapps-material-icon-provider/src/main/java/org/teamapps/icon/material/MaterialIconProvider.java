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


    private Map<String, AbstractMaterialIconStyle> styleById = new HashMap<>();
    private Map<String, String> svgByStyleAndName = new HashMap<>();

    public MaterialIconProvider() {
        MaterialIconStyles.getBaseStyles().forEach(this::addIconStyle);
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
            iconStyle = MaterialIconStyles.PLAIN_GREY_700;
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
        return MaterialIconStyles.PLAIN_SHADOW_BLUE_700;
    }

    @Override
    public IconStyle getDefaultMobileStyle() {
        return MaterialIconStyles.PLAIN_SHADOW_BLUE_700;
    }

    @Override
    public IconStyle getDefaultSubIconStyle() {
        return MaterialIconStyles.PLAIN_SHADOW_BLUE_700;
    }

}
