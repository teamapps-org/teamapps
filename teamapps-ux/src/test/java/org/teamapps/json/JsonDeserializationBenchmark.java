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
package org.teamapps.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Test;
import org.teamapps.dto.UiCommand;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class JsonDeserializationBenchmark {

	private final ObjectMapper teamAppsObjectMapper = TeamAppsObjectMapperFactory.create();

	@Test
	public void testLowLevelJsonParsingPerformance() throws Exception {
		String json = readResourceToString("sample.json");

		long startTime;
		JsonNode[] jsonNodes = new JsonNode[1024];
		UiCommand[] uiCommands = new UiCommand[1024];

		for (int j = 0; j < 5; j++) {
			startTime = System.currentTimeMillis();
			for (int i = 0; i < 1_000; i++) {
				jsonNodes[i % 1024] = teamAppsObjectMapper.readTree(json);
			}
			System.out.println("Jackson TreeNode: " + (System.currentTimeMillis() - startTime + "ms"));

			startTime = System.currentTimeMillis();
			for (int i = 0; i < 1_000; i++) {
				uiCommands[i % 1024] = teamAppsObjectMapper.readValue(json, UiCommand.class);
			}
			System.out.println("Jackson data binding (directly to Java objects!): " + (System.currentTimeMillis() - startTime + "ms"));


		}
		System.out.println(jsonNodes[0]);
		System.out.println(uiCommands[0]);
	}

	public static String readResourceToString(String resourceName) {
		URL url = Resources.getResource(resourceName);
		try {
			return Resources.toString(url, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
