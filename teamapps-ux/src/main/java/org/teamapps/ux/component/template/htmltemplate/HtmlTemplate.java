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
package org.teamapps.ux.component.template.htmltemplate;

import org.teamapps.dto.UiHtmlTemplate;
import org.teamapps.dto.UiTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HtmlTemplate implements Template {

	private static final Pattern PLACE_HOLDER_REGEX = Pattern.compile("\\{\\{(\\w+)\\}\\}");

	private final String html;
	private final List<String> propertyNames;

	public HtmlTemplate(String htmlTemplateString) {
		this.html = htmlTemplateString;
		this.propertyNames = PLACE_HOLDER_REGEX.matcher(html).results()
				.map(matchResult -> matchResult.group(1))
				.collect(Collectors.toList());
	}

	@Override
	public UiTemplate createUiTemplate() {
		return new UiHtmlTemplate(html);
	}

	@Override
	public List<String> getPropertyNames() {
		return propertyNames;
	}
	
}
