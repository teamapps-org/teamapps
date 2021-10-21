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

import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.ux.resource.FileResource;
import org.teamapps.ux.resource.Resource;
import org.teamapps.ux.resource.ResourceWrapper;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionContextResourceManager {

	public static final String BASE_PATH = "/files/";
	public static final String RESOURCE_LINK_ID_PREFIX = "res-";

	private static final Map<Class<? extends Resource>, Boolean> IMPLEMENTS_EQUAL_BY_RESOURCE_CLASS = new ConcurrentHashMap<>();

	private final QualifiedUiSessionId sessionId;
	private final AtomicInteger linkIdGenerator = new AtomicInteger();
	private final Map<Integer, Resource> resourceByLinkId = new ConcurrentHashMap<>();
	private final Map<Resource, String> resourceLinkByResource = new ConcurrentHashMap<>();

	public SessionContextResourceManager(QualifiedUiSessionId sessionId) {
		this.sessionId = sessionId;
	}

	public Resource getBinaryResource(int resourceId) {
		return resourceByLinkId.get(resourceId);
	}

	public String createFileLink(File file) {
		if (file == null) {
			return null;
		}
		return createResourceLink(new FileResource(file), null);
	}

	public String createResourceLink(Resource resource, String uniqueIdentifier) {
		if (resource == null) {
			return null;
		}
		String existingLink = resourceLinkByResource.get(resource);
		if (existingLink != null) {
			return existingLink;
		}
		if (uniqueIdentifier != null) {
			return createResourceLink(new UniqueIdentifierResourceWrapper(resource, uniqueIdentifier), null /*!!*/);
		}

		int linkId = createLinkId();
		String resourceLink = createLink(linkId);
		resourceByLinkId.put(linkId, resource);
		resourceLinkByResource.put(resource, resourceLink);
		return resourceLink;
	}

	private int createLinkId() {
		return linkIdGenerator.incrementAndGet();
	}

	private String createLink(int id) {
		return BASE_PATH + sessionId.getUiSessionId() + "/" + RESOURCE_LINK_ID_PREFIX + id;
	}

	private static class UniqueIdentifierResourceWrapper extends ResourceWrapper {
		private final String uniqueIdentifier;

		public UniqueIdentifierResourceWrapper(Resource resource, String uniqueIdentifier) {
			super(resource);
			this.uniqueIdentifier = uniqueIdentifier;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			UniqueIdentifierResourceWrapper that = (UniqueIdentifierResourceWrapper) o;
			return Objects.equals(uniqueIdentifier, that.uniqueIdentifier);
		}

		@Override
		public int hashCode() {
			return Objects.hash(uniqueIdentifier);
		}
	}
}
