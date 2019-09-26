package org.teamapps.privilege;

import org.teamapps.privilege.preset.ApplicationRolePreset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationPrivilegesInfo {

	private final String applicationNamespace;
	private final List<PrivilegeGroup> privilegeGroups;
	private final List<ApplicationRolePreset> applicationRolePresets;

	public ApplicationPrivilegesInfo(String applicationNamespace) {
		this.applicationNamespace = applicationNamespace;
		this.privilegeGroups = new ArrayList<>();
		this.applicationRolePresets = new ArrayList<>();
	}

	public ApplicationPrivilegesInfo(String applicationNamespace, PrivilegeGroup... privilegeGroups) {
		this(applicationNamespace, Arrays.asList(privilegeGroups), new ArrayList<>());
	}

	public ApplicationPrivilegesInfo(String applicationNamespace, List<PrivilegeGroup> privilegeGroups, List<ApplicationRolePreset> applicationRolePresets) {
		this.applicationNamespace = applicationNamespace;
		this.privilegeGroups = privilegeGroups;
		this.applicationRolePresets = applicationRolePresets;
	}

	public void addPrivilegeGroup(PrivilegeGroup privilegeGroup) {
		privilegeGroups.add(privilegeGroup);
	}

	public void addApplicationRolePreset(ApplicationRolePreset applicationRolePreset) {
		applicationRolePresets.add(applicationRolePreset);
	}

	public String getApplicationNamespace() {
		return applicationNamespace;
	}

	public List<PrivilegeGroup> getPrivilegeGroups() {
		return privilegeGroups;
	}

	public List<ApplicationRolePreset> getApplicationRolePresets() {
		return applicationRolePresets;
	}
}
