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

import com.google.common.annotations.VisibleForTesting;

import java.util.function.Function;

public class ClassPathResourceProvider implements ResourceProvider {

	private final String basePackage;
	private final Function<String, String> javaResourceNameToMimeTypeFunction;

	public ClassPathResourceProvider(String basePackage) {
		this(basePackage, s -> null);
	}

	public ClassPathResourceProvider(String basePackage, Function<String, String> javaResourceNameToMimeTypeFunction) {
		this.basePackage = normalizeClassPathResourcePath(basePackage);
		this.javaResourceNameToMimeTypeFunction = javaResourceNameToMimeTypeFunction;
	}

	@Override
	public Resource getResource(String servletPath, String relativeResourcePath, String httpSessionId) {
		return new ClassPathResource(getJavaResourceName(relativeResourcePath),
				javaResourceNameToMimeTypeFunction.apply(relativeResourcePath));
	}

	private String getJavaResourceName(String resource) {
		return basePackage + resource;
	}

	@VisibleForTesting
	static String normalizeClassPathResourcePath(String basePackage) {
		if (!basePackage.contains("/") && basePackage.contains(".")) {
			basePackage = basePackage.replaceAll("\\.", "/");
		}
		if (!basePackage.startsWith("/")) {
			basePackage = "/" + basePackage;
		}
		if (basePackage.endsWith("/")) {
			basePackage = basePackage.substring(0, basePackage.length() - 1);
		}
		return basePackage;
	}
	
}
