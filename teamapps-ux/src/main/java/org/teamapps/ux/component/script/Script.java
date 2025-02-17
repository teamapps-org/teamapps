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
package org.teamapps.ux.component.script;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiScript;
import org.teamapps.ux.component.AbstractComponent;

import java.util.List;

public class Script extends AbstractComponent {

	private final String script;

	public Script(String script) {
		this.script = script;
	}

	@Override
	public UiComponent createUiComponent() {
		UiScript uiScript = new UiScript(script);
		mapAbstractUiComponentProperties(uiScript);
		return uiScript;
	}

	public void callFunction(String name, Object... parameters) {
		queueCommandIfRendered(() -> new UiScript.CallFunctionCommand(getId(), name, List.of(parameters)));
	}
}
