package org.teamapps.privilege.context;

import org.teamapps.privilege.Privilege;
import org.teamapps.privilege.PrivilegeGroup;
import org.teamapps.privilege.SimplePrivilegeController;
import org.teamapps.ux.session.SessionContext;

import java.util.Set;
import java.util.function.Function;

public class SimpleNodeContextPrivilegeController<NODE>  extends SimplePrivilegeController implements NodeContextPrivilegeController<NODE> {


	private final Function<SessionContext, Set<NODE>> nodeSetBySessionContextFunction;

	public SimpleNodeContextPrivilegeController(Function<SessionContext, String> applicationRoleBySessionContextFunction, Function<SessionContext, Set<NODE>> nodeSetBySessionContextFunction) {
		super(applicationRoleBySessionContextFunction);
		this.nodeSetBySessionContextFunction = nodeSetBySessionContextFunction;
	}


	@Override
	public boolean isAllowedInNode(NODE node, SessionContext context, String applicationNamespace, PrivilegeGroup privilegeGroup, Privilege privilege) {
		Set<NODE> nodeSet = nodeSetBySessionContextFunction.apply(context);
		if (nodeSet != null && nodeSet.contains(node)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Set<NODE> getAllowedNodes(SessionContext context, String applicationNamespace, PrivilegeGroup privilegeGroup, Privilege privilege) {
		return nodeSetBySessionContextFunction.apply(context);
	}


}
