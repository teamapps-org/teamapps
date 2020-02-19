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
package org.teamapps.client;

import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class ClientCodeExtractor {

	private final static Logger LOGGER = LoggerFactory.getLogger(ClientCodeExtractor.class);


	private static final String TEAMAPPS_CLIENT_FILE_NAME = "teamapps-client.zip";
	private static final String TEAMAPPS_CLIENT_CHECKSUM_FILE_NAME = "teamapps-client.zip.MD5";

	public static void initializeWebserverDirectory(File webAppDirectory) throws IOException {
		File currentlyDeployedChecksumFile = new File(webAppDirectory, TEAMAPPS_CLIENT_CHECKSUM_FILE_NAME);
		String currentlyDeployedArtifactChecksum = currentlyDeployedChecksumFile.exists() ? IOUtils.toString(currentlyDeployedChecksumFile.toURI()).trim() : null;

		String[] currentlyDeployedFiles = webAppDirectory.list((dir, name) -> !name.startsWith("."));
		if (currentlyDeployedArtifactChecksum == null && currentlyDeployedFiles != null && currentlyDeployedFiles.length > 0) {
			LOGGER.warn("Checksum file not present (" + TEAMAPPS_CLIENT_CHECKSUM_FILE_NAME + ") but directory not empty!. Will NOT refresh the webapp directory since this is probably a development "
					+ "environment. If not, consider cleaning up the directory!");
			return;
		}

		URL checksumResourceUrl = ClientCodeExtractor.class.getResource("/" + TEAMAPPS_CLIENT_CHECKSUM_FILE_NAME);
		String artifactChecksum = checksumResourceUrl != null ? IOUtils.toString(checksumResourceUrl).trim() : null;

		LOGGER.info("Checksum of currently deployed artifact (" + webAppDirectory.getAbsolutePath() + "): " + currentlyDeployedArtifactChecksum);
		LOGGER.info("Checksum of executed artifact: " + artifactChecksum);

		if (!Objects.equals(currentlyDeployedArtifactChecksum, artifactChecksum)) {
			overwriteWebappDirectory(webAppDirectory, currentlyDeployedChecksumFile, checksumResourceUrl);
		} else {
			LOGGER.info("Checksum has not changed. Nothing to do.");
		}
	}

	private static void overwriteWebappDirectory(File webAppDirectory, File currentlyDeployedChecksumFile, URL checksumResourceUrl) throws IOException {
		if (webAppDirectory.exists()) {
			boolean deleted = webAppDirectory.delete();
			if (!deleted) {
				LOGGER.error("Could not delete webapp directory!");
			}
		}
		webAppDirectory.mkdirs();

		File tempFile = File.createTempFile("teamapps-client", "zip");
		try (InputStream in = ClientCodeExtractor.class.getResourceAsStream("/" + TEAMAPPS_CLIENT_FILE_NAME);
		     FileOutputStream out = new FileOutputStream(tempFile);) {
			LOGGER.info("Extracting " + TEAMAPPS_CLIENT_FILE_NAME + " from classpath to temp file: " + tempFile.getAbsolutePath());
			IOUtils.copy(in, out);
		}

		LOGGER.info("Unzipping " + tempFile.getAbsolutePath() + " to " + webAppDirectory.getAbsolutePath());
		unzipFile(tempFile, webAppDirectory);
		try (InputStream in = checksumResourceUrl.openStream();
		     FileOutputStream out = new FileOutputStream(currentlyDeployedChecksumFile)) {
			LOGGER.info("Writing checksum to " + currentlyDeployedChecksumFile.getAbsolutePath());
			IOUtils.copy(in, out);
		}
	}

	private static boolean unzipFile(File file, File destinationDir) {
		try {
			if (file == null || !file.exists() || destinationDir == null || !destinationDir.isDirectory()) {
				return false;
			}
			ZipFile zipFile = new ZipFile(file);
			zipFile.extractAll(destinationDir.getAbsolutePath());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
