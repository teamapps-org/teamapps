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
package org.teamapps.ux.resource;

import org.junit.Assert;
import org.junit.Test;
import org.teamapps.projector.resource.FileResource;

import java.io.File;

public class FileResourceTest {

	@Test
	public void testMimeTypes() throws Exception {
		Assert.assertEquals("text/plain", new FileResource(new File("myFile.txt")).getMimeType());
		Assert.assertEquals("video/mp4", new FileResource(new File("myFile.mp4")).getMimeType());
		Assert.assertEquals("audio/mp4", new FileResource(new File("myFile.m4a")).getMimeType());
		Assert.assertEquals("application/octet-stream", new FileResource(new File("myFile.xyz")).getMimeType());
	}
}
