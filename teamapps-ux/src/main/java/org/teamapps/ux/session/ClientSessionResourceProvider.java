/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import org.teamapps.ux.resource.FileResource;
import org.teamapps.ux.resource.Resource;
import org.teamapps.uisession.QualifiedUiSessionId;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ClientSessionResourceProvider {

	public static final String BASE_PATH = "/files/";
	private QualifiedUiSessionId sessionId;
	private AtomicInteger idGenerator = new AtomicInteger();
	private Map<Integer, Resource> binaryResourceById = new HashMap<>();
	private Map<File, String> fileLinkByFile = new HashMap<>();
	private Map<String, String> resourceLinkByUniqueIdentifier = new HashMap<>();


	public ClientSessionResourceProvider(QualifiedUiSessionId sessionId) {
		this.sessionId = sessionId;
	}

	public Resource getBinaryResource(int resourceId) {
		return binaryResourceById.get(resourceId);
	}

	public String createFileLink(File file) {
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

	public String createResourceLink(Supplier<InputStream> inputStreamSupplier, long length, String resourceName, String uniqueIdentifier) {
		if (inputStreamSupplier == null) {
			return null;
		}
		if (uniqueIdentifier != null && resourceLinkByUniqueIdentifier.containsKey(uniqueIdentifier)) {
			return resourceLinkByUniqueIdentifier.get(uniqueIdentifier);
		}

		Resource resource =new Resource() {
			@Override
			public InputStream getInputStream() {
				return inputStreamSupplier.get();
			}

			@Override
			public long getLength() {
				return length;
			}

			@Override
			public Date getLastModified() {
				return new Date();
			}

			@Override
			public Date getExpires() {
				return new Date(System.currentTimeMillis() + 600000000L);
			}

			@Override
			public String getName() {
				return resourceName;
			}

			@Override
			public String getMimeType() {
				return null;
			}
		};
		int id = createId();
		binaryResourceById.put(id, resource);
		String resourceLink = createLink(id);
		if (uniqueIdentifier != null) {
			resourceLinkByUniqueIdentifier.put(uniqueIdentifier, resourceLink);
		}
		return resourceLink;
	}

	private int createId() {
		return idGenerator.incrementAndGet();
	}

	private String createLink(int id) {
		return BASE_PATH + sessionId.getUiSessionId() + "/res" + id;
	}
}
