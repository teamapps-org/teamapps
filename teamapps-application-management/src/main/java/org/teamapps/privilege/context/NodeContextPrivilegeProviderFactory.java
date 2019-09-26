package org.teamapps.privilege.context;

import org.teamapps.privilege.ApplicationPrivilegesInfo;

public interface NodeContextPrivilegeProviderFactory<NODE> {

	NodeContextPrivilegeProvider<NODE> createPrivilegeProvider(ApplicationPrivilegesInfo privilegesInfo);
}
