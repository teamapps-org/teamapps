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
package org.teamapps.ux.application.validation;

public class RecordValue {

    private final String propertyName;
    private final boolean required;
    private final Object value;
    private final boolean changed;

    public RecordValue(String propertyName, boolean required, Object value, boolean changed) {
        this.propertyName = propertyName;
        this.required = required;
        this.value = value;
        this.changed = changed;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isRequired() {
        return required;
    }

    public Object getValue() {
        return value;
    }

    public boolean isChanged() {
        return changed;
    }

    public String getStringValue() {
        if (value != null && value instanceof String) {
            return (String) value;
        }
        return null;
    }

    public int getIntValue() {
        if (value != null && value instanceof Integer) {
            return (Integer) value;
        }
        return 0;
    }

    public long getLongValue() {
        if (value != null && value instanceof Long) {
            return (Long) value;
        }
        return 0;
    }
}
