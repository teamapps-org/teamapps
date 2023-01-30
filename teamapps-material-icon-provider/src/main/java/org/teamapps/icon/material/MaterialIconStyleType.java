/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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

public enum MaterialIconStyleType {

    PLAIN("plain", false),
    PLAIN_SHADOW("shadow", false),
    STICKER("sticker", false),
    OUTLINE("outline", false),
    OUTLINE_FILLED("outlinefilled", false),
    GRADIENT("gradient", true),
    GRADIENT_OUTLINE("gradientoutline", true),
    ;

    private final String packageName;
    private final boolean multiColor;

    MaterialIconStyleType(String packageName, boolean multiColor) {
        this.packageName = packageName;
        this.multiColor = multiColor;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isMultiColor() {
        return multiColor;
    }
}
