/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.server.jetty.embedded;

import org.teamapps.common.format.Color;
import org.teamapps.icons.Icon;

public class User {
	private Icon<?, ?> icon;
	private String firstName;
	private Color color;

	User(Icon<?, ?> icon, String firstName, Color color) {
		this.icon = icon;
		this.firstName = firstName;
		this.color = color;
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public String getFirstName() {
		return firstName;
	}

	public Color getColor() {
		return color;
	}

	public void setIcon(Icon<?, ?> icon) {
		this.icon = icon;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
