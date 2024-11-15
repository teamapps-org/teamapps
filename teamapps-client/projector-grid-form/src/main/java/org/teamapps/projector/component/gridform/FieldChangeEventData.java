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
package org.teamapps.projector.component.gridform;

import org.teamapps.projector.component.field.AbstractField;

public class FieldChangeEventData {

	private final String propertyName;
	private final AbstractField<?> field;
	private final Object value;

	public FieldChangeEventData(String propertyName, AbstractField<?> field, Object value) {
		this.propertyName = propertyName;
		this.field = field;
		this.value = value;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public AbstractField<?> getField() {
		return field;
	}

	public Object getValue() {
		return value;
	}
}