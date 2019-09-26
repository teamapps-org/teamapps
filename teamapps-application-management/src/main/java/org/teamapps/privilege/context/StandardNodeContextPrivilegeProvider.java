package org.teamapps.privilege.context;

import org.teamapps.privilege.Privilege;
import org.teamapps.privilege.PrivilegeGroup;
import org.teamapps.ux.session.CurrentSessionContext;

import java.util.Set;

public class StandardNodeContextPrivilegeProvider<NODE> implements NodeContextPrivilegeProvider<NODE> {

	private final NodeContextPrivilegeController privilegeController;
	private final String applicationNamespace;

	public StandardNodeContextPrivilegeProvider(NodeContextPrivilegeController privilegeController, String applicationNamespace) {
		this.privilegeController = privilegeController;
		this.applicationNamespace = applicationNamespace;
	}

	@Override
	public boolean isAllowedInNode(NODE node, PrivilegeGroup privilegeGroup, Privilege privilege) {
		return privilegeController.isAllowedInNode(node, CurrentSessionContext.get(), applicationNamespace, privilegeGroup, privilege);
	}

	@Override
	public Set<NODE> getAllowedNodes(PrivilegeGroup privilegeGroup, Privilege privilege) {
		return privilegeController.getAllowedNodes(CurrentSessionContext.get(), applicationNamespace, privilegeGroup,privilege);
	}

	@Override
	public boolean isAllowed(PrivilegeGroup privilegeGroup, Privilege privilege) {
		return privilegeController.isAllowed(CurrentSessionContext.get(), applicationNamespace, privilegeGroup, privilege);
	}
}
