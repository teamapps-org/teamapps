package org.teamapps.privilege;

import org.teamapps.ux.session.SessionContext;

import java.util.function.Function;

public class SimplePrivilegeProviderFactory extends AbstractPrivilegeProviderFactory{


	private final SimplePrivilegeController privilegeController;

	public SimplePrivilegeProviderFactory(Function<SessionContext, String> applicationRoleBySessionContextFunction) {
		privilegeController = new SimplePrivilegeController(applicationRoleBySessionContextFunction);
	}

	@Override
	public PrivilegeController getPrivilegeController() {
		return privilegeController;
	}
}
