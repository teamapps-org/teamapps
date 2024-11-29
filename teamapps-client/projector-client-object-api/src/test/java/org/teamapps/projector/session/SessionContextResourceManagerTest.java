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
package org.teamapps.projector.session;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.teamapps.projector.resource.FileResource;

import java.io.File;

public class SessionContextResourceManagerTest {

	@Test
	public void shouldCache() {
		SessionContextResourceManager manager = new SessionContextResourceManager("ui1");

		String resourceLink1 = manager.createResourceLink(new FileResource(new File("asdf.b")), null);
		String resourceLink2 = manager.createResourceLink(new FileResource(new File("asdf.b")), null);
		String resourceLink3 = manager.createResourceLink(new FileResource(new File("222.b")), null);

		Assertions.assertThat(resourceLink1).isEqualTo(resourceLink2);
		Assertions.assertThat(resourceLink3).isNotEqualTo(resourceLink2);
	}

	@Test
	public void shouldCacheUsingUniqueIdentifier() {
		SessionContextResourceManager manager = new SessionContextResourceManager("ui1");

		String resourceLink1 = manager.createResourceLink(new FileResource(new File("asdf.b")), "uid1");
		String resourceLink2 = manager.createResourceLink(new FileResource(new File("999.b")), "uid1");
		String resourceLink3 = manager.createResourceLink(new FileResource(new File("asdf.b")), "uuid2");

		Assertions.assertThat(resourceLink1).isEqualTo(resourceLink2);
		Assertions.assertThat(resourceLink3).isNotEqualTo(resourceLink1);
	}

}
