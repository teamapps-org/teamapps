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
package org.teamapps.client;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClientCodeExtractor {

	private final static Logger LOGGER = LoggerFactory.getLogger(ClientCodeExtractor.class);


	private static final String TEAMAPPS_CLIENT_FILE_NAME = "teamapps-client.zip";
	private static final String TEAMAPPS_CLIENT_CHECKSUM_FILE_NAME = "teamapps-client.zip.MD5";
	public static final String TEAMAPPS_CLIENT_CHECKSUM_RESOURCE_NAME = "/" + TEAMAPPS_CLIENT_CHECKSUM_FILE_NAME;

	public static void initializeWebserverDirectory(File webAppDirectory) throws IOException {
		File currentlyDeployedChecksumFile = new File(webAppDirectory, TEAMAPPS_CLIENT_CHECKSUM_FILE_NAME);
		String currentlyDeployedArtifactChecksum = currentlyDeployedChecksumFile.exists() ? IOUtils.toString(new FileInputStream(currentlyDeployedChecksumFile), StandardCharsets.UTF_8).trim() : null;

		String[] currentlyDeployedFiles = webAppDirectory.list((dir, name) -> !name.startsWith("."));
		if (currentlyDeployedArtifactChecksum == null && currentlyDeployedFiles != null && currentlyDeployedFiles.length > 0) {
			LOGGER.warn("Checksum file not present (" + TEAMAPPS_CLIENT_CHECKSUM_FILE_NAME + ") but directory not empty!. Will NOT refresh the webapp directory since this is probably a development "
					+ "environment. If not, consider cleaning up the directory!");
			return;
		}

		URL checksumResourceUrl = ClientCodeExtractor.class.getResource("/" + TEAMAPPS_CLIENT_CHECKSUM_FILE_NAME);
		String artifactChecksum = checksumResourceUrl != null ? IOUtils.toString(checksumResourceUrl, StandardCharsets.UTF_8).trim() : null;

		LOGGER.info("Checksum of currently deployed artifact (" + webAppDirectory.getAbsolutePath() + "): " + currentlyDeployedArtifactChecksum);
		LOGGER.info("Checksum of executed artifact: " + artifactChecksum);

		if (!Objects.equals(currentlyDeployedArtifactChecksum, artifactChecksum)) {
			overwriteWebappDirectory(webAppDirectory, currentlyDeployedChecksumFile);
		} else {
			LOGGER.info("Checksum has not changed. Nothing to do.");
		}
	}

	private static void overwriteWebappDirectory(File webAppDirectory, File currentlyDeployedChecksumFile) throws IOException {
		if (webAppDirectory.exists()) {
			FileUtils.deleteDirectory(webAppDirectory);
		}
		webAppDirectory.mkdirs();

		File tempFile = File.createTempFile("teamapps-client", "zip");
		try (InputStream in = ClientCodeExtractor.class.getResourceAsStream("/" + TEAMAPPS_CLIENT_FILE_NAME);
		     FileOutputStream out = new FileOutputStream(tempFile)) {
			LOGGER.info("Extracting " + TEAMAPPS_CLIENT_FILE_NAME + " from classpath to temp file: " + tempFile.getAbsolutePath());
			IOUtils.copy(in, out);
		}

		LOGGER.info("Unzipping " + tempFile.getAbsolutePath() + " to " + webAppDirectory.getAbsolutePath());
		unzipFile(tempFile, webAppDirectory);
		String newChecksum = readResourceAsStringOrNull(TEAMAPPS_CLIENT_CHECKSUM_RESOURCE_NAME);
		try (FileOutputStream out = new FileOutputStream(currentlyDeployedChecksumFile)) {
			LOGGER.info("Writing checksum to " + currentlyDeployedChecksumFile.getAbsolutePath());
			IOUtils.write(newChecksum, out, StandardCharsets.UTF_8);
		}
	}

	private static void unzipFile(File zipFile, File destDir) throws IOException {
		Objects.requireNonNull(zipFile);
		Objects.requireNonNull(destDir);
		createDirIfNotExists(destDir);

		byte[] buffer = new byte[1024];
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				while (zipEntry != null) {
					File destFile = new File(destDir, zipEntry.getName());
					String destDirPath = destDir.getCanonicalPath();
					String destFilePath = destFile.getCanonicalPath();
					if (!destFilePath.startsWith(destDirPath + File.separator)) {
						// Prevent zip slip! See https://snyk.io/research/zip-slip-vulnerability
						throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
					}
					if (zipEntry.isDirectory()) {
						createDirIfNotExists(destFile);
					} else {
						File parent = destFile.getParentFile();
						createDirIfNotExists(parent);
						FileOutputStream fos = new FileOutputStream(destFile);
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						fos.close();
					}
					zipEntry = zis.getNextEntry();
				}
			}
			zis.closeEntry();
		}

		Files.setLastModifiedTime(destDir.toPath().resolve("index.html"), FileTime.from(Instant.now()));
	}

	private static void createDirIfNotExists(File dir) throws IOException {
		if (!dir.isDirectory() && !dir.mkdirs()) {
			throw new IOException("Failed to create directory " + dir);
		}
	}

	static String readResourceAsStringOrNull(String resourceName) {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(resourceName)) {
			if (is != null) {
				return IOUtils.toString(is, StandardCharsets.UTF_8);
			} else {
				return null;
			}
		} catch (IOException e) {
			return null;
		}
	}

}
