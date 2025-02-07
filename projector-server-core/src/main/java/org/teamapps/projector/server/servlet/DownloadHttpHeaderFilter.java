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
package org.teamapps.projector.server.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

public class DownloadHttpHeaderFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		String downloadFileName = req.getParameter("teamapps-download-filename");

		if (downloadFileName != null) {
			HttpServletResponse response = (HttpServletResponse) res;
			response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName + "\"");
			chain.doFilter(req, new HttpServletResponseWrapper(response) {
				@Override
				public void setHeader(String name, String value) {
					if (!name.toLowerCase().equals("content-disposition")) {
						super.setHeader(name, value);
					}
				}

				@Override
				public void addHeader(String name, String value) {
					if (!name.toLowerCase().equals("content-disposition")) {
						super.addHeader(name, value);
					}
				}
			});
		} else {
			chain.doFilter(req, res);
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
