package org.teamapps.privilege;

public interface PrivilegeProvider {

	boolean isAllowed(PrivilegeGroup privilegeGroup, Privilege privilege);

}
