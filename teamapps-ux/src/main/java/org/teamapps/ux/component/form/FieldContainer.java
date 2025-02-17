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
package org.teamapps.ux.component.form;

import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;

import java.util.Map;

/**
 * Can be added to a form. Contains fields that will be treated by the form as it teats its on fields.
 * In particular
 * <ul>
 *     <li>{@link AbstractForm#validate()} will validate the {@link FieldContainer}'s fields</li>
 *     <li>{@link AbstractForm#applyRecordValuesToFields(Object)} and {@link AbstractForm#applyFieldValuesToRecord(Object)} will respect the {@link FieldContainer}'s fields</li>
 *     <li>{@link AbstractForm#getFields()} and {@link AbstractForm#getFieldsMap()} will contain the {@link FieldContainer}'s fields</li>
 * </ul>
 */
public interface FieldContainer {

	Component getMainComponent();

	/**
	 * Must not change between invocations!
	 */
	Map<String, AbstractField<?>> getFields();

}
