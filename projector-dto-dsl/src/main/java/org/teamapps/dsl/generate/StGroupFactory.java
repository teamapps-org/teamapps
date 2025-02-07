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
package org.teamapps.dsl.generate;

import org.stringtemplate.v4.STGroupFile;
import org.teamapps.dsl.generate.adapter.OptionalModelAdaptor;
import org.teamapps.dsl.generate.adapter.PojoModelAdaptor;

import java.util.Optional;

public class StGroupFactory {
    public static STGroupFile createStGroup(
            String templateFileResourcePath,
            IntermediateDtoModel model
    ) {
        STGroupFile stGroup = new STGroupFile(StGroupFactory.class.getResource(templateFileResourcePath), "UTF-8", '<', '>');
        stGroup.registerRenderer(String.class, new StringRenderer());
        stGroup.registerModelAdaptor(Object.class, new PojoModelAdaptor<>());
        stGroup.registerModelAdaptor(Optional.class, new OptionalModelAdaptor());
        return stGroup;
    }
}
