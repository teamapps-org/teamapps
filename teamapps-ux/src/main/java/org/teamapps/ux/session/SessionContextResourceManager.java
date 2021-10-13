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
package org.teamapps.ux.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.ux.resource.FileResource;
import org.teamapps.ux.resource.Resource;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionContextResourceManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static final String BASE_PATH = "/files/";

	private final SessionContext sessionContext;
	private final AtomicInteger idGenerator = new AtomicInteger();
	private final Map<Integer, Resource> binaryResourceById = new HashMap<>();
	private final Map<File, String> fileLinkByFile = new HashMap<>();
	private final Map<String, String> resourceLinkByUniqueIdentifier = new HashMap<>();

	public SessionContextResourceManager(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	public Resource getBinaryResource(int resourceId) {
		return binaryResourceById.get(resourceId);
	}

	public String createFileLink(File file) {
		checkThread();
		if (file == null) {
			return null;
		}
		if (fileLinkByFile.containsKey(file)) {
			return fileLinkByFile.get(file);
		}
		FileResource resource = new FileResource(file);
		int id = createId();
		binaryResourceById.put(id, resource);
		String fileLink = createLink(id);
		fileLinkByFile.put(file, fileLink);
		return fileLink;
	}

	public String createResourceLink(Resource resource, String uniqueIdentifier) {
		checkThread();
		if (resource == null) {
			return null;
		}
		if (uniqueIdentifier != null && resourceLinkByUniqueIdentifier.containsKey(uniqueIdentifier)) {
			return resourceLinkByUniqueIdentifier.get(uniqueIdentifier);
		}

		int id = createId();
		binaryResourceById.put(id, resource);
		String resourceLink = createLink(id);
		if (uniqueIdentifier != null) {
			resourceLinkByUniqueIdentifier.put(uniqueIdentifier, resourceLink);
		}
		return resourceLink;
	}

	private void checkThread() {
		if (SessionContext.current() != sessionContext) {
			LOGGER.error("createFileLink called from wrong thread!", new RuntimeException());
		}
	}

	private int createId() {
		return idGenerator.incrementAndGet();
	}

	private String createLink(int id) {
		return BASE_PATH + sessionContext.getSessionId().getUiSessionId() + "/res" + id;
	}
}
