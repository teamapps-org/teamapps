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
