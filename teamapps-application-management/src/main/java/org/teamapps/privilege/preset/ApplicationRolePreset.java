package org.teamapps.privilege.preset;

import org.teamapps.icons.api.Icon;

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
