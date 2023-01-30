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
package org.teamapps.dto.generate.adapter;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

import java.lang.reflect.Method;

public class PojoModelAdaptor implements ModelAdaptor {

    @Override
    public Object getProperty(Interpreter interpreter, ST self, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
        Method m = null;
        try {
            String mn = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            m = o.getClass().getMethod(mn);
        } catch (Exception e) {
        }
        if (propertyName.equals("fullText")) {
            ParserRuleContext parserRuleContext = (ParserRuleContext) o;
            return parserRuleContext.getStart().getInputStream().getText(Interval.of(parserRuleContext.getStart().getStartIndex(), parserRuleContext.getStop().getStopIndex()));
        }
        if (m == null) {
            try {
                m = o.getClass().getDeclaredMethod(propertyName);
            } catch (Exception e) {
            }
        }

        if (m != null) {
            try {
                return m.invoke(o);
            } catch (Exception e) {
                e.printStackTrace();
                throw new STNoSuchPropertyException(e, o, propertyName);
            }
        } else {
            throw new STNoSuchPropertyException(null, o, propertyName);
        }
    }
}
