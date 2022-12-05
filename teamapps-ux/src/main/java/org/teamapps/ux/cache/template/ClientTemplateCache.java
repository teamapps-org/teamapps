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
package org.teamapps.ux.cache.template;

import org.teamapps.ux.component.field.combobox.TemplateDecider;
import org.teamapps.ux.component.template.Template;

public class ClientTemplateCache<RECORD> {

	private final Listener listener;

	private Template defaultTemplate;
	private TemplateDecider<RECORD> templateDecider = record -> defaultTemplate;

	public ClientTemplateCache(Template defaultTemplate, Listener listener) {
		this.defaultTemplate = defaultTemplate;
		this.listener = listener;
	}

	public ClientTemplateCache(Listener listener) {
		this.listener = listener;
	}

	public interface Listener {
		void onNewTemplate(int id, Template templat);
	}

	public Template getDefaultTemplate() {
		return defaultTemplate;
	}

	public void setDefaultTemplate(Template defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

	public TemplateDecider<RECORD> getTemplateDecider() {
		return templateDecider;
	}

	public void setTemplateDecider(TemplateDecider<RECORD> templateDecider) {
		this.templateDecider = templateDecider;
	}

	public static class TemplateWithClientId {
		private Template template;
		private Integer clientId;

		public TemplateWithClientId(Template template, Integer clientId) {
			this.template = template;
			this.clientId = clientId;
		}

		public Template getTemplate() {
			return template;
		}

		public void setTemplate(Template template) {
			this.template = template;
		}

		public Integer getClientId() {
			return clientId;
		}

		public void setClientId(Integer clientId) {
			this.clientId = clientId;
		}
	}
}
