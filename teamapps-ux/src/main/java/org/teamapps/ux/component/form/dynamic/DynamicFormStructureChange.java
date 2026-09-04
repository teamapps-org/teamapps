/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
package org.teamapps.ux.component.form.dynamic;

/** Describes a structural form mutation after the active layout policies were refreshed. */
public record DynamicFormStructureChange(Type type, String elementId, int activeFields,
										 int retainedFields, int retainedComponents) {

	public enum Type {
		SECTION_ADDED,
		SECTION_REMOVED,
		REPEATER_ADDED,
		REPEATER_REMOVED,
		FIELD_ADDED,
		FIELD_REMOVED,
		COMPONENT_ADDED,
		COMPONENT_REMOVED,
		ROW_ADDED,
		ROW_REMOVED,
		LAYOUT_CHANGED
	}
}
