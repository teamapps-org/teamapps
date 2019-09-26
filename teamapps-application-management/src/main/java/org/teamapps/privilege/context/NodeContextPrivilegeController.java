package org.teamapps.privilege.context;

import org.teamapps.privilege.Privilege;
import org.teamapps.privilege.PrivilegeController;
import org.teamapps.privilege.PrivilegeGroup;
import org.teamapps.ux.session.SessionContext;

import java.util.Set;

public interface NodeContextPrivilegeController<NODE> extends PrivilegeController {

	boolean isAllowedInNode(NODE node, SessionContext context, String applicationNamespace, PrivilegeGroup privilegeGroup, Privilege privilege);

	Set<NODE> getAllowedNodes(SessionContext context, String applicationNamespace, PrivilegeGroup privilegeGroup, Privilege privilege);

}
