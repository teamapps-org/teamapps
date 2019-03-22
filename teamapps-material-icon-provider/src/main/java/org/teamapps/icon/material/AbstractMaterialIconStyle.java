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

import org.teamapps.icons.api.IconStyle;

public abstract class AbstractMaterialIconStyle implements IconStyle {

    private final String styleId;
    private final StyleType styleType;
    private String styleName;
    private boolean canBeUsedAsSubIcon;

    public AbstractMaterialIconStyle(String styleId, StyleType styleType) {
        this.styleId = styleId;
        this.styleType = styleType;
    }

    public AbstractMaterialIconStyle(String styleId, StyleType styleType, String styleName, boolean canBeUsedAsSubIcon) {
        this.styleId = styleId;
        this.styleType = styleType;
        this.styleName = styleName;
        this.canBeUsedAsSubIcon = canBeUsedAsSubIcon;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public void setCanBeUsedAsSubIcon(boolean canBeUsedAsSubIcon) {
        this.canBeUsedAsSubIcon = canBeUsedAsSubIcon;
    }

    public StyleType getStyleType() {
        return styleType;
    }

    @Override
    public String getStyleId() {
        return styleId;
    }

    @Override
    public String getStyleName() {
        return styleName;
    }

    @Override
    public boolean canBeUsedAsSubIcon() {
        return canBeUsedAsSubIcon;
    }

    public abstract String applyStyle(String svg);

    public String applyStyle(String svg, String styleTags) {
        int pos = svg.indexOf('>');
        return svg.substring(0, pos + 2) + styleTags + svg.substring(pos + 1, svg.length());
    }

    public String createStyleTags(String ... colors) {
        StringBuilder sb = new StringBuilder("<style>\n");
        for (int i = 1; i <= colors.length; i++) {
            sb.append(".teamapps-color-").append(i).append(" {").append("color:").append(colors[i - 1]).append("}");
        }
        sb.append("\n</style>");
        return sb.toString();
    }
}
