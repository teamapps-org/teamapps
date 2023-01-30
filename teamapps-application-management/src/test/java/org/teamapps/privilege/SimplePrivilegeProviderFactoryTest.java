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
package org.teamapps.privilege;

import org.junit.Test;
import org.mockito.Mockito;
import org.teamapps.privilege.preset.ApplicationRolePreset;
import org.teamapps.privilege.preset.PrivilegeGroupPreset;
import org.teamapps.testutil.UxTestUtil;
import org.teamapps.ux.session.SessionContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimplePrivilegeProviderFactoryTest {

	@Test
	public void getPrivilegeController() {
		UxTestUtil.runWithSessionContext(Mockito.mock(SessionContext.class), () -> {
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

}
