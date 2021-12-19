/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
package org.teamapps.ux.component.form.layoutpolicy;

import org.teamapps.dto.UiFormLayoutPolicy;
import org.teamapps.dto.UiFormSection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormLayoutPolicy {

	private int minWidth;  // in pixels
	private List<FormSection> sections = new ArrayList<>();

	public List<FormSection> getSections() {
		return sections;
	}

	public FormLayoutPolicy addSection(FormSection section) {
		sections.add(section);
		return this;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public FormLayoutPolicy setMinWidth(int minWidth) {
		this.minWidth = minWidth;
		return this;
	}

	public FormLayoutPolicy setSections(List<FormSection> sections) {
		this.sections = sections;
		return this;
	}

	public UiFormLayoutPolicy createUiLayoutPolicy() {
		List<UiFormSection> uiSections = sections.stream().map(section -> section != null ? section.createUiFormSection() : null).collect(Collectors.toList());
		return new UiFormLayoutPolicy(minWidth, uiSections);
	}
}
