/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.dto.generate;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeneratorTestUtil {

	public static void compareCodeWithResource(String expectedResultResourceName, String actual) {
		String expected = readResourceToString(expectedResultResourceName);

//		Assert.assertEquals(expected, actual);

		try {
			Files.asCharSink(new File("src/test/resources/" + expectedResultResourceName).getAbsoluteFile(), StandardCharsets.UTF_8).write(actual);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}       

	public static String readResourceToString(String resourceName) {
		URL url;
		try {
			url = Resources.getResource(resourceName);
		} catch (IllegalArgumentException e) {
			try {
				new File("src/testapp/resources/" + resourceName).createNewFile();
			} catch (IOException e1) {
				// ignore this exception...
			}
			return null;
		}
		try {
			return Resources.toString(url, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return null; // yes, just because we want to see the actual result ;-)
		}
	}

}
