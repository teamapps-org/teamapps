package org.teamapps.privilege.context;

import org.teamapps.privilege.Privilege;
import org.teamapps.privilege.PrivilegeGroup;
import org.teamapps.privilege.PrivilegeProvider;

import java.util.Set;

public interface NodeContextPrivilegeProvider<NODE> extends PrivilegeProvider {

	boolean isAllowedInNode(NODE node, PrivilegeGroup privilegeGroup, Privilege privilege);

	Set<NODE> getAllowedNodes(PrivilegeGroup privilegeGroup, Privilege privilege);

}
