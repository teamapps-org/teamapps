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

public abstract class MultiColorStyle extends AbstractMaterialIconStyle {

    private final String color1;
    private final String color2;
    private final String color3;

    public MultiColorStyle(String styleId, StyleType styleType, String color1, String color2, String color3) {
        super(styleId, styleType);
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
    }

    public String getColor1() {
        return color1;
    }

    public String getColor2() {
        return color2;
    }

    public String getColor3() {
        return color3;
    }

    @Override
    public String applyStyle(String svg) {
        String styleTags = createStyleTags(color1, color2, color3);
        return applyStyle(svg, styleTags);
    }

}
