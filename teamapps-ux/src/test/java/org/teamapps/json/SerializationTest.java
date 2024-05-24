/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.teamapps.dto.protocol.server.CMD;
import org.teamapps.dto.protocol.server.INIT_NOK;
import org.teamapps.dto.protocol.server.SessionClosingReason;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {

	private ObjectMapper teamAppsObjectMapper;

	@Before
	public void setUp() throws Exception {
		teamAppsObjectMapper = TeamAppsObjectMapperFactory.create();
		teamAppsObjectMapper.registerSubtypes(INIT_NOK.class);
	}

	@Test
	public void serialize() throws Exception {
		INIT_NOK init = new INIT_NOK(SessionClosingReason.SESSION_NOT_FOUND);
		assertThat(teamAppsObjectMapper.writeValueAsString(init)).isEqualTo("{\"_type\":\"INIT_NOK\",\"reason\":0}");
	}

	@Test
	public void serializeCommands() throws Exception {
		CMD cmd = new CMD("myLib", "myClientObjectId", "myCommand", new Object[]{"param1", 222}, false);
		assertThat(teamAppsObjectMapper.writeValueAsString(cmd)).isEqualTo("{\"_type\":\"CMD\",\"lid\":\"myLib\",\"oid\":\"myClientObjectId\",\"name\":\"myCommand\",\"params\":[\"param1\",222],\"r\":false,\"sn\":0}");
	}

}
