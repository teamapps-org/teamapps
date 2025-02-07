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
package org.teamapps.projector.component.field;

import org.teamapps.projector.i18n.ProjectorTranslationKeys;
import org.teamapps.projector.session.CurrentSessionContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FieldUtil {

    private FieldUtil() {
        // private
    }

    public static boolean validateAll(Field<?>... fields) {
        return validateAll(Arrays.stream(fields));
    }

    public static boolean validateAll(List<Field<?>> fields) {
        return validateAll(fields.stream());
    }

    public static boolean validateAll(Stream<Field<?>> stream) {
        List<FieldMessage> errorMessage = stream
                .flatMap(field -> field.validate().stream())
                .filter(message -> message.getSeverity() == FieldMessageSeverity.ERROR)
                .collect(Collectors.toList()); // to List because we want ALL validators to run!
        return errorMessage.size() == 0;
    }

    /**
     * @deprecated really needed anywhere? what for?
     */
    @Deprecated
    public static boolean validateAllAsRequired(AbstractField<?>... fields) {
        FieldMessage errorMessage = new FieldMessage(FieldMessageSeverity.ERROR, CurrentSessionContext.get().getLocalized(ProjectorTranslationKeys.REQUIRED_FIELD.getKey()));
        boolean isNotEmpty = true;
        for (AbstractField<?> field : fields) {
            if (field.isEmpty()) {
                field.removeCustomFieldMessage(errorMessage);
                field.addCustomFieldMessage(errorMessage);
                isNotEmpty = false;
            }
        }
        return isNotEmpty;
    }

}
