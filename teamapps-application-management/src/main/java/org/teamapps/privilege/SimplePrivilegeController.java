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
