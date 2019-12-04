package org.teamapps.privilege;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.teamapps.icons.api.IconTheme;
import org.teamapps.privilege.preset.ApplicationRolePreset;
import org.teamapps.privilege.preset.PrivilegeGroupPreset;
import org.teamapps.server.UxServerContext;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.uisession.UiCommandExecutor;
import org.teamapps.ux.session.ClientInfo;
import org.teamapps.ux.session.SessionContext;

import java.util.Collections;

import static org.junit.Assert.*;

public class SimplePrivilegeProviderFactoryTest {

	@Test
	public void getPrivilegeController() {
		createDummySessionContext().runWithContext(() -> {
			ApplicationPrivilegesInfo privilegesInfo = new ApplicationPrivilegesInfo("org.test");
			privilegesInfo.addPrivilegeGroup(new PrivilegeGroup("test", new Privilege("read"), new Privilege("write")));
			privilegesInfo.addApplicationRolePreset(new ApplicationRolePreset("admin", new PrivilegeGroupPreset(new PrivilegeGroup("test"), new Privilege("read"), new Privilege("write"))));
			SimplePrivilegeProviderFactory providerFactory = new SimplePrivilegeProviderFactory(context -> "admin");
			PrivilegeProvider privilegeProvider = providerFactory.createPrivilegeProvider(privilegesInfo);

			assertTrue(privilegeProvider.isAllowed(new PrivilegeGroup("test"), new Privilege("read")));
			assertTrue(privilegeProvider.isAllowed(new PrivilegeGroup("test"), new Privilege("write")));
			assertFalse(privilegeProvider.isAllowed(new PrivilegeGroup("test"), new Privilege("read2")));
			assertFalse(privilegeProvider.isAllowed(new PrivilegeGroup("other"), new Privilege("write")));
		});
	}

	public static SessionContext createDummySessionContext() {
		return new SessionContext(
				new QualifiedUiSessionId("httpSessionId", "uiSessionId"),
				new ClientInfo("ip", 1024, 768, 1000, 700, "en", false, "Europe/Berlin", 120, Collections.emptyList(), "userAgentString", "", Collections.emptyMap()),
				Mockito.mock(UiCommandExecutor.class),
				Mockito.mock(UxServerContext.class),
				Mockito.mock(IconTheme.class),
				Mockito.mock(ObjectMapper.class)
		);
	}
}