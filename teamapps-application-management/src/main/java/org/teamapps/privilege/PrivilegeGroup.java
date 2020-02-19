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
package org.teamapps.privilege;

import org.teamapps.icons.api.Icon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrivilegeGroup {

	private final String name;
	private Icon icon;
	private String caption;
	private String description;
	private final List<Privilege> privileges = new ArrayList<>();

	public PrivilegeGroup(String name) {
		this.name = name;
	}

	public PrivilegeGroup(String name, Privilege... privileges) {
		this.name = name;
		addPrivileges(privileges);
	}

	public PrivilegeGroup addPrivilege(Privilege privilege) {
		privileges.add(privilege);
		return this;
	}
	public PrivilegeGroup addPrivileges(Privilege... privileges) {
		this.privileges.addAll(Arrays.asList(privileges));
		return this;
	}

	public String getName() {
		return name;
	}

	public Icon getIcon() {
		return icon;
	}

	public PrivilegeGroup setIcon(Icon icon) {
		this.icon = icon;
		return this;
	}

	public String getCaption() {
		return caption;
	}

	public PrivilegeGroup setCaption(String caption) {
		this.caption = caption;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public PrivilegeGroup setDescription(String description) {
		this.description = description;
		return this;
	}

	public List<Privilege> getPrivileges() {
		return privileges;
	}
}
