/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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
