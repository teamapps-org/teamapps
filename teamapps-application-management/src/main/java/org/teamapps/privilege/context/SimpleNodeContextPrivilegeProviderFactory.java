package org.teamapps.privilege.context;

import org.teamapps.privilege.ApplicationPrivilegesInfo;

public class SimpleNodeContextPrivilegeProviderFactory<NODE> implements NodeContextPrivilegeProviderFactory<NODE> {

	private final SimpleNodeContextPrivilegeController<NODE> nodeContextPrivilegeController;

	public SimpleNodeContextPrivilegeProviderFactory(SimpleNodeContextPrivilegeController<NODE> nodeContextPrivilegeController) {
		this.nodeContextPrivilegeController = nodeContextPrivilegeController;
	}

	@Override
	public NodeContextPrivilegeProvider<NODE> createPrivilegeProvider(ApplicationPrivilegesInfo privilegesInfo) {
		nodeContextPrivilegeController.registerApplicationPrivileges(privilegesInfo);
		return new StandardNodeContextPrivilegeProvider<>(nodeContextPrivilegeController, privilegesInfo.getApplicationNamespace());
	}
}
