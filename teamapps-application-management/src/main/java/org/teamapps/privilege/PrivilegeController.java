package org.teamapps.privilege;

import org.teamapps.ux.session.SessionContext;

public interface PrivilegeController {

	void registerApplicationPrivileges(ApplicationPrivilegesInfo applicationPrivilegesInfo);

	boolean isAllowed(SessionContext context, String applicationNamespace, PrivilegeGroup privilegeGroup, Privilege privilege);
}
