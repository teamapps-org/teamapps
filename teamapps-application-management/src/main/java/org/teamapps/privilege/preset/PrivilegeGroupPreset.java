package org.teamapps.privilege.preset;

import org.teamapps.privilege.Privilege;
import org.teamapps.privilege.PrivilegeGroup;

import java.util.Arrays;
import java.util.List;

public class PrivilegeGroupPreset {

	private final PrivilegeGroup privilegeGroup;
	private final List<Privilege> activePrivileges;

	public PrivilegeGroupPreset(PrivilegeGroup privilegeGroup, Privilege... activePrivileges) {
		this(privilegeGroup, Arrays.asList(activePrivileges));
	}

	public PrivilegeGroupPreset(PrivilegeGroup privilegeGroup, List<Privilege> activePrivileges) {
		this.privilegeGroup = privilegeGroup;
		this.activePrivileges = activePrivileges;
	}

	public PrivilegeGroup getPrivilegeGroup() {
		return privilegeGroup;
	}

	public List<Privilege> getActivePrivileges() {
		return activePrivileges;
	}
}
