package org.teamapps.privilege;

public interface PrivilegeProviderFactory {

	PrivilegeProvider createPrivilegeProvider(ApplicationPrivilegesInfo privilegesInfo);
}
