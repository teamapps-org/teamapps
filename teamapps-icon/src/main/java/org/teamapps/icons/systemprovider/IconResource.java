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
package org.teamapps.icons.systemprovider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

public class IconResource  {

    private static final Date LAST_MODIFIED = new Date();

    private final byte[] bytes;
    private final String mimeType;

    public IconResource(byte[] bytes, String mimeType) {
        this.bytes = bytes;
        this.mimeType = mimeType;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public long getLength() {
        return bytes.length;
    }

    public Date getLastModified() {
        return LAST_MODIFIED;
    }

    public Date getExpires() {
        return new Date(System.currentTimeMillis() + 86_400_000L * 60);
    }

    public String getMimeType() {
        return mimeType;
    }
}
