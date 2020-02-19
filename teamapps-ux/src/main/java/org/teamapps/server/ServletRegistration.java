/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.server;

import javax.servlet.Servlet;
import java.util.Collection;
import java.util.Collections;

public class ServletRegistration {

	private final Servlet servlet;
	private final Collection<String> mappings;

	public ServletRegistration(Servlet servlet, String mapping) {
		this.servlet = servlet;
		this.mappings = Collections.singleton(mapping);
	}

	public ServletRegistration(Servlet servlet, Collection<String> mappings) {
		this.servlet = servlet;
		this.mappings = mappings;
	}

	public Servlet getServlet() {
		return servlet;
	}

	public Collection<String> getMappings() {
		return mappings;
	}
}
