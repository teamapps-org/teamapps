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

import org.teamapps.ux.component.field.FieldMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidationResult {

    private boolean success;
    private List<ValidationMessage> validationMessages = new ArrayList<>();


    public static ValidationResult success() {
        return new ValidationResult(true);
    }

    public static ValidationResult failure(ValidationMessage message) {
        return new ValidationResult(false).addMessage(message);
    }

    public ValidationResult() {
    }

    public ValidationResult(boolean success) {
        this.success = success;
    }

    public ValidationResult addMessage(ValidationMessage message) {
        validationMessages.add(message);
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setValidationMessages(List<ValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
    }

    public Map<String, List<FieldMessage>> getFieldMessagesMap() {
        return validationMessages.stream()
                .collect(Collectors.groupingBy(message -> message.getPropertyName(),
                        Collectors.mapping(message -> message.getFieldMessage(), Collectors.toList())));
    }
}
