package org.teamapps.privilege;

import org.teamapps.ux.session.CurrentSessionContext;

public class StandardPrivilegeProvider implements PrivilegeProvider {

	private final PrivilegeController privilegeController;
	private final String applicationNamespace;

	public StandardPrivilegeProvider(PrivilegeController privilegeController, String applicationNamespace) {
		this.privilegeController = privilegeController;
		this.applicationNamespace = applicationNamespace;
	}

	@Override
	public boolean isAllowed(PrivilegeGroup privilegeGroup, Privilege privilege) {
		return privilegeController.isAllowed(CurrentSessionContext.get(), applicationNamespace, privilegeGroup, privilege);
	}
}
