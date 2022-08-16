/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.dsl.generate.adapter;

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;
import org.teamapps.dsl.TeamAppsDtoParser;

public class PropertyDeclarationContextModelAdaptor extends PojoModelAdaptor {

    @Override
    public Object getProperty(Interpreter interpreter, ST self, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
        TeamAppsDtoParser.PropertyDeclarationContext propertyContext = (TeamAppsDtoParser.PropertyDeclarationContext) o;

        if ("declaringType".equals(propertyName)) {
            return propertyContext.parent;
        } else {
            return super.getProperty(interpreter, self, o, property, propertyName);
        }
    }
}
