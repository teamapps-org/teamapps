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
package org.teamapps.ux.component.template;

import org.teamapps.dto.UiTemplate;
import org.teamapps.dto.UiTemplateReference;

import java.util.List;

public class TemplateReference implements Template {

	private final String templateId;
	private final Template template;

	public TemplateReference(Template template, String templateId) {
		this.template = template;
		this.templateId = templateId;
	}

	public String getTemplateId() {
		return templateId;
	}

	public Template getTemplate() {
		Template template = this.template;
		while (template instanceof TemplateReference) {
			template = ((TemplateReference) template).getTemplate();
		}
		return template;
	}

	@Override
	public List<String> getDataKeys() {
		return template.getDataKeys();
	}

	@Override
	public UiTemplate createUiTemplate() {
		return new UiTemplateReference(templateId);
	}
}
