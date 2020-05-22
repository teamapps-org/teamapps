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
package org.teamapps.ux.application.assembler;

import org.teamapps.icons.api.Icon;

public class AdditionalNavigationButton {

	private final Icon icon;
	private final String caption;
	private final Runnable handler;

	public AdditionalNavigationButton(Icon icon, String caption, Runnable handler) {
		this.icon = icon;
		this.caption = caption;
		this.handler = handler;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getCaption() {
		return caption;
	}

	public Runnable getHandler() {
		return handler;
	}
}
