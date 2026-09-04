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
/**
 * Responsive forms with runtime-mutable sections, fields and repeatable rows.
 * <p>
 * {@link org.teamapps.ux.component.form.dynamic.DynamicForm} uses the existing
 * {@code UiGridForm} protocol. Its no-argument constructor provides the hybrid
 * layout preset; {@link org.teamapps.ux.component.form.dynamic.DynamicFormLayouts}
 * provides additional presets and custom breakpoints.
 * </p>
 * <pre>{@code
 * DynamicForm<Person> form = new DynamicForm<>();
 * DynamicFormSection section = form.addSection("person", "Person");
 * section.addField("firstName", "First name", new TextField());
 * section.addField("notes", "Notes", new MultiLineTextField()).columnSpan(2);
 * }</pre>
 * <p>
 * Structural removals hide, disable and omit components from future policies.
 * The unchanged client retains those tombstones until the session ends; use
 * the retained-count accessors when forms have unbounded mutation rates.
 * </p>
 */
package org.teamapps.ux.component.form.dynamic;
