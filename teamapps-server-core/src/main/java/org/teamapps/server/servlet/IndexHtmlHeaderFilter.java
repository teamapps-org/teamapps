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
package org.teamapps.server.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IndexHtmlHeaderFilter extends HttpFilter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		if (servletResponse instanceof HttpServletResponse) {
			HttpServletResponse r = (HttpServletResponse) servletResponse;
			r.setHeader("cache-control", "max-age=0, no-cache, no-store, must-revalidate");
			r.setHeader("expires", "Thu, 01 Jan 1970 00:00:00 GMT");
			r.setHeader("pragma", "no-cache");
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}
}
