/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.dto.generate;

import org.stringtemplate.v4.STGroupFile;
import org.teamapps.dto.TeamAppsDtoParser;
import org.teamapps.dto.generate.adapter.*;

public class StGroupFactory {
    public static STGroupFile createStGroup(
            String templateFileResourcePath,
            TeamAppsDtoModel model
    ) {
        STGroupFile stGroup = new STGroupFile(StGroupFactory.class.getResource(templateFileResourcePath), "UTF-8", '<', '>');
        stGroup.registerRenderer(String.class, new StringRenderer());

        stGroup.registerModelAdaptor(Object.class, new PojoModelAdaptor());
        stGroup.registerModelAdaptor(TeamAppsDtoParser.ClassDeclarationContext.class, new ClassDeclarationContextModelAdaptor(model));
        stGroup.registerModelAdaptor(TeamAppsDtoParser.InterfaceDeclarationContext.class, new InterfaceDeclarationContextModelAdaptor(model));
        stGroup.registerModelAdaptor(TeamAppsDtoParser.EnumDeclarationContext.class, new EnumDeclarationContextModelAdapter());
        stGroup.registerModelAdaptor(TeamAppsDtoParser.EventDeclarationContext.class, new EventDeclarationContextModelAdaptor(model));
        stGroup.registerModelAdaptor(TeamAppsDtoParser.CommandDeclarationContext.class, new CommandDeclarationContextModelAdaptor(model));
        stGroup.registerModelAdaptor(TeamAppsDtoParser.QueryDeclarationContext.class, new QueryDeclarationContextModelAdaptor(model));
        stGroup.registerModelAdaptor(TeamAppsDtoParser.PropertyDeclarationContext.class, new PropertyDeclarationContextModelAdaptor());
        stGroup.registerModelAdaptor(TeamAppsDtoParser.TypeContext.class, new TypeContextModelAdaptor(model));
        return stGroup;
    }
}
