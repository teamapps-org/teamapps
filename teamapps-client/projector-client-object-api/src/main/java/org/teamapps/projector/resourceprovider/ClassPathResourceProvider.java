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
package org.teamapps.projector.resourceprovider;

import com.google.common.annotations.VisibleForTesting;
import org.teamapps.projector.resource.ClassPathResource;
import org.teamapps.projector.resource.Resource;

import java.util.function.Function;

public class ClassPathResourceProvider implements ResourceProvider {

	private final String basePackage;
	private final ClassLoader classLoader = getClass().getClassLoader(); // TODO
	private final Function<String, String> javaResourceNameToMimeTypeFunction;
	private final DirectoryResolutionStrategy directoryResolutionStrategy;

	public ClassPathResourceProvider(String basePackage) {
		this(basePackage, s -> null, DirectoryResolutionStrategy.empty());
	}

	public ClassPathResourceProvider(String basePackage, DirectoryResolutionStrategy directoryResolutionStrategy) {
		this(basePackage, s -> null, directoryResolutionStrategy);
	}

	public ClassPathResourceProvider(String basePackage, Function<String, String> javaResourceNameToMimeTypeFunction) {
		this(basePackage, javaResourceNameToMimeTypeFunction, DirectoryResolutionStrategy.empty());
	}

	public ClassPathResourceProvider(String basePackage, Function<String, String> javaResourceNameToMimeTypeFunction, DirectoryResolutionStrategy directoryResolutionStrategy) {
		this.basePackage = normalizeClassPathResourcePath(basePackage);
		this.javaResourceNameToMimeTypeFunction = javaResourceNameToMimeTypeFunction;
		this.directoryResolutionStrategy = directoryResolutionStrategy;
	}

	@Override
	public Resource getResource(String servletPath, String relativeResourcePath, String httpSessionId) {
		boolean isDirectory = relativeResourcePath.endsWith("/");
		if (isDirectory) {
			return directoryResolutionStrategy.resolveDirectory(relativeResourcePath).stream()
					.filter(path -> classLoader.getResource(getJavaResourceName(relativeResourcePath)) != null)
					.findFirst()
					.map(path -> new ClassPathResource(getJavaResourceName(path)))
					.orElse(null);
		} else {
			return new ClassPathResource(getJavaResourceName(relativeResourcePath),
					javaResourceNameToMimeTypeFunction.apply(relativeResourcePath));
		}
	}

	private String getJavaResourceName(String resource) {
		return basePackage + resource;
	}

	@VisibleForTesting
	static String normalizeClassPathResourcePath(String basePackage) {
		if (!basePackage.contains("/") && basePackage.contains(".")) { // package name
			basePackage = basePackage.replaceAll("\\.", "/");
		}
		if (basePackage.startsWith("/")) {
			basePackage = basePackage.substring(1);
		}
		if (basePackage.endsWith("/")) {
			basePackage = basePackage.substring(0, basePackage.length() - 1);
		}
		return basePackage;
	}
	
}
