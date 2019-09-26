package org.teamapps.privilege;

public abstract class AbstractPrivilegeProviderFactory implements PrivilegeProviderFactory {

	public abstract PrivilegeController getPrivilegeController();

	@Override
	public PrivilegeProvider createPrivilegeProvider(ApplicationPrivilegesInfo privilegesInfo) {
		PrivilegeController privilegeController = getPrivilegeController();
		privilegeController.registerApplicationPrivileges(privilegesInfo);
		return new StandardPrivilegeProvider(privilegeController, privilegesInfo.getApplicationNamespace());
	}
}
