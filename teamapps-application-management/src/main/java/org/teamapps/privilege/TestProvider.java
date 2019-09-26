package org.teamapps.privilege;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.teamapps.json.TeamAppsObjectMapperFactory;
import org.teamapps.privilege.preset.ApplicationRolePreset;
import org.teamapps.privilege.preset.PrivilegeGroupPreset;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

public class TestProvider {

	public static void main(String[] args) {
		SessionContext sessionContext = new SessionContext(null, null, null, null, null, TeamAppsObjectMapperFactory.create());
		sessionContext.runWithContext(() -> {
			ApplicationPrivilegesInfo privilegesInfo = new ApplicationPrivilegesInfo("org.test");
			privilegesInfo.addPrivilegeGroup(new PrivilegeGroup("test", new Privilege("read"), new Privilege("write")));
			privilegesInfo.addApplicationRolePreset(new ApplicationRolePreset("admin", new PrivilegeGroupPreset(new PrivilegeGroup("test", new Privilege("read"), new Privilege("write")))));
			SimplePrivilegeProviderFactory providerFactory = new SimplePrivilegeProviderFactory(context -> "admin");
			PrivilegeProvider privilegeProvider = providerFactory.createPrivilegeProvider(privilegesInfo);

			System.out.println("allowed:" + privilegeProvider.isAllowed(new PrivilegeGroup("test"), new Privilege("read")));
			System.out.println("allowed:" + privilegeProvider.isAllowed(new PrivilegeGroup("test"), new Privilege("read2")));
			System.out.println("allowed:" + privilegeProvider.isAllowed(new PrivilegeGroup("test"), new Privilege("write")));
		});
	}
}
