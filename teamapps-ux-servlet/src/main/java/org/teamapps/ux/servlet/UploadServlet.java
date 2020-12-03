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
package org.teamapps.ux.servlet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@MultipartConfig(location="/tmp", fileSizeThreshold=1000_000, maxFileSize=-1L, maxRequestSize=-1L)
public class UploadServlet extends HttpServlet {

	private static final Logger LOGGER  = LoggerFactory.getLogger(UploadServlet.class);

	private final File uploadDirectory;

	private final BiConsumer<File, String> uploadListener;

	public UploadServlet(File uploadDirectory, BiConsumer<File, String> uploadListener) {
		this.uploadDirectory = uploadDirectory;
		this.uploadListener = uploadListener;
	}

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Collection<Part> parts = request.getParts();
		ArrayList<String> uuids = new ArrayList<>();

		try {
			for (Part file : parts) {
				String uuidString = UUID.randomUUID().toString();
				File tempFile = new File(uploadDirectory, uuidString);

				try (InputStream in = file.getInputStream();
				     OutputStream out = new FileOutputStream(tempFile)) {
					IOUtils.copy(in, out);
				}

				uuids.add(uuidString);

				uploadListener.accept(tempFile, uuidString);
			}
		} catch (Exception e) {
			LOGGER.warn("Error while uploading files" + e);
			response.setStatus(500);
			return;
		}

		response.setStatus(200);
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setContentType("application/json");
		response.getWriter().println("[" + uuids.stream()
				.map(uuid -> "\"" + uuid + "\"")
				.collect(Collectors.joining(",")) + "]");
	}
}
