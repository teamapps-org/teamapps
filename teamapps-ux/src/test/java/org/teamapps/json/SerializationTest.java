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
import org.teamapps.dto.DtoClientObjectReference;
import org.teamapps.dto.DtoMultiLineTextField;
import org.teamapps.dto.DtoObject;
import org.teamapps.dto.DtoWorkSpaceLayout;
import org.teamapps.dto.protocol.DtoINIT;
import org.teamapps.dto.protocol.DtoINIT_NOK;
import org.teamapps.dto.protocol.DtoSessionClosingReason;

public class SerializationTest {

	private ObjectMapper teamAppsObjectMapper;

	@Before
	public void setUp() throws Exception {
		teamAppsObjectMapper = TeamAppsObjectMapperFactory.create();
		teamAppsObjectMapper.registerSubtypes(DtoINIT_NOK.class);
	}

	@Test
	public void serializeViaJsonSimple() throws Exception {
		DtoINIT init = new DtoINIT("sessionId", null, 123);
		System.out.println(teamAppsObjectMapper.writeValueAsString(init));
	}

	@Test
	public void serializeEnums() throws Exception {
		DtoINIT_NOK init = new DtoINIT_NOK(DtoSessionClosingReason.SESSION_NOT_FOUND);
		System.out.println(teamAppsObjectMapper.writeValueAsString(init));
	}

	@Test
	public void serializeCommands() throws Exception {
		DtoWorkSpaceLayout.RefreshViewComponentCommand o = new DtoWorkSpaceLayout.RefreshViewComponentCommand("viewName", new DtoClientObjectReference("asdf"));
		System.out.println(teamAppsObjectMapper.writeValueAsString(o));
	}

	@Test
	public void deserializeViaJackson() throws Exception {
		DtoObject uiObject = teamAppsObjectMapper.readValue("{\"_type\": \"INIT_NOK\", \"reason\":0}", DtoObject.class);
		System.out.println(uiObject);
	}

	@Test
	public void blah() throws Exception {
		DtoMultiLineTextField.AppendCommand o = new DtoMultiLineTextField.AppendCommand("string", true);
		System.out.println(teamAppsObjectMapper.writeValueAsString(o));
	}
}
