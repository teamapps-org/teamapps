/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.privilege;

import org.teamapps.privilege.preset.PrivilegeGroupPreset;
import org.teamapps.ux.session.SessionContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class SimplePrivilegeController implements PrivilegeController {

	private final Function<SessionContext, String> applicationRoleBySessionContextFunction;
	private Map<String, Set<String>> privilegeFqnSetByApplicationRoleName;

	public SimplePrivilegeController(Function<SessionContext, String> applicationRoleBySessionContextFunction) {
		this.applicationRoleBySessionContextFunction = applicationRoleBySessionContextFunction;
		privilegeFqnSetByApplicationRoleName = new HashMap<>();
	}

	@Override
	public void registerApplicationPrivileges(ApplicationPrivilegesInfo applicationPrivilegesInfo) {
		String applicationNamespace = applicationPrivilegesInfo.getApplicationNamespace();
		applicationPrivilegesInfo.getApplicationRolePresets().forEach(preset -> {
			String applicationRoleName = preset.getName();
			Set<String> privilegeFqnSet = privilegeFqnSetByApplicationRoleName.computeIfAbsent(applicationRoleName, s -> new HashSet<>());
			for (PrivilegeGroupPreset privilegeGroupPreset : preset.getPrivilegeGroupPresets()) {
				PrivilegeGroup privilegeGroup = privilegeGroupPreset.getPrivilegeGroup();
				for (Privilege activePrivilege : privilegeGroupPreset.getActivePrivileges()) {
					privilegeFqnSet.add(createPrivilegeFqn(applicationNamespace, privilegeGroup, activePrivilege));
				}
			}
		});
	}

	@Override
	public boolean isAllowed(SessionContext context, String applicationNamespace, PrivilegeGroup privilegeGroup, Privilege privilege) {
		String applicationRoleName = applicationRoleBySessionContextFunction.apply(context);
		if (applicationRoleName == null) {
			return false;
		}
		Set<String> privilegeFqnSet = privilegeFqnSetByApplicationRoleName.get(applicationRoleName);
		if (privilegeFqnSet.contains(createPrivilegeFqn(applicationNamespace, privilegeGroup, privilege))) {
			return true;
		} else {
			return false;
		}
	}

	private String createPrivilegeFqn(String applicationNamespace, PrivilegeGroup privilegeGroup, Privilege privilege) {
		return applicationNamespace + "." + privilegeGroup.getName() + "." + privilege.getName();
	}
}
