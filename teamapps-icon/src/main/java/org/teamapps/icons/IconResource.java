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
package org.teamapps.icons;

public class IconResource {

    private final byte[] bytes;
    private final IconType iconType;
    private final int size;

    public IconResource(byte[] bytes, IconType iconType) {
        this(bytes, iconType, -1);
    }

    public IconResource(byte[] bytes, IconType iconType, int size) {
        this.bytes = bytes;
        this.iconType = iconType;
        this.size = size;

        if (iconType.isRasterImage() && size <= 0) {
            throw new IllegalArgumentException("iconSize is required for non-scalable icons.");
        }
    }

    public byte[] getBytes() {
        return bytes;
    }

    public long getLength() {
        return bytes.length;
    }

    public IconType getIconType() {
        return iconType;
    }

    public String getMimeType() {
        return iconType.getMimeType();
    }

    public int getSize() {
        return size;
    }
}
