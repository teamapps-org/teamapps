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
package org.teamapps.dto.generate;

import com.google.common.base.CaseFormat;
import org.stringtemplate.v4.AttributeRenderer;

import java.util.Locale;

public class StringRenderer implements AttributeRenderer {

    public String toString(Object o, String formatString, Locale locale) {
        String s = (String) o;
        if (formatString == null) {
            return s;
        } else if (formatString.equals("upper")) {
            return s.toUpperCase(locale);
        } else if (formatString.equals("lower")) {
            return s.toLowerCase(locale);
        } else if (formatString.equals("cap")) {
            return s.length() > 0 ? Character.toUpperCase(s.charAt(0)) + s.substring(1) : s;
        }  else if (formatString.equals("javaConstant")) {
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, s);
        } else {
            throw new IllegalArgumentException("unknown string format: " + formatString);
        }
    }


}
