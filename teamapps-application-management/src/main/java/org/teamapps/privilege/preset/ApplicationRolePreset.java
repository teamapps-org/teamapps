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
package org.teamapps.privilege.preset;

import org.teamapps.icons.Icon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationRolePreset {

	private final String name;
	private final List<PrivilegeGroupPreset> privilegeGroupPresets;
	private Icon icon;
	private String caption;
	private String description;

	public ApplicationRolePreset(String name) {
		this.name = name;
		this.privilegeGroupPresets = new ArrayList<>();
	}

	public ApplicationRolePreset(String name, PrivilegeGroupPreset... privilegeGroupPresets) {
		this(name, Arrays.asList(privilegeGroupPresets));
	}

	public ApplicationRolePreset(String name, List<PrivilegeGroupPreset> privilegeGroupPresets) {
		this.name = name;
		this.privilegeGroupPresets = privilegeGroupPresets;
	}

	public ApplicationRolePreset addPreset(PrivilegeGroupPreset privilegeGroupPreset) {
		privilegeGroupPresets.add(privilegeGroupPreset);
		return this;
	}

	public String getName() {
		return name;
	}

	public List<PrivilegeGroupPreset> getPrivilegeGroupPresets() {
		return privilegeGroupPresets;
	}

	public Icon getIcon() {
		return icon;
	}

	public ApplicationRolePreset setIcon(Icon icon) {
		this.icon = icon;
		return this;
	}

	public String getCaption() {
		return caption;
	}

	public ApplicationRolePreset setCaption(String caption) {
		this.caption = caption;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ApplicationRolePreset setDescription(String description) {
		this.description = description;
		return this;
	}
}
