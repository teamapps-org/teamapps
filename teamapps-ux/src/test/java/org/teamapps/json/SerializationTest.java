/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import org.junit.Test;
import org.teamapps.dto.INIT;
import org.teamapps.dto.INIT_NOK;
import org.teamapps.dto.UiClientObjectReference;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiMultiLineTextField;
import org.teamapps.dto.UiObject;
import org.teamapps.dto.UiSessionClosingReason;
import org.teamapps.dto.UiWorkSpaceLayout;

public class SerializationTest {

	private ObjectMapper teamAppsObjectMapper = TeamAppsObjectMapperFactory.create();

	@Test
	public void serializeViaJsonSimple() throws Exception {
		INIT init = new INIT("sessionId", null, 123);
		System.out.println(teamAppsObjectMapper.writeValueAsString(init));
	}

	@Test
	public void serializeEnums() throws Exception {
		INIT_NOK init = new INIT_NOK(UiSessionClosingReason.SESSION_NOT_FOUND);
		System.out.println(teamAppsObjectMapper.writeValueAsString(init));
	}

	@Test
	public void serializeCommands() throws Exception {
		UiWorkSpaceLayout.RefreshViewComponentCommand o = new UiWorkSpaceLayout.RefreshViewComponentCommand("componentId", "viewName", new UiClientObjectReference("asdf"));
		System.out.println(teamAppsObjectMapper.writeValueAsString(o));
	}

	@Test
	public void deserializeViaJackson() throws Exception {
		UiObject uiObject = teamAppsObjectMapper.readValue("{\"_type\": \"INIT_NOK\", \"reason\":0}", UiObject.class);
		System.out.println(uiObject);
	}

	@Test
	public void deserializeEvents() throws Exception {
		UiEvent uiObject = teamAppsObjectMapper.readValue("{\"componentId\":\"componentId\",\"_type\":\"UiGridForm.sectionCollapsedStateChanged\"}", UiEvent.class);
		System.out.println(uiObject);
	}

	@Test
	public void blah() throws Exception {
		UiMultiLineTextField.AppendCommand o = new UiMultiLineTextField.AppendCommand("id", "string", true);
		System.out.println(teamAppsObjectMapper.writeValueAsString(o));
	}
}
