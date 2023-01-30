/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.dto.generate;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static File createDirectory(File parentDir) {
        boolean successfullyCreatedParentDirs = parentDir.mkdirs();
        if (!successfullyCreatedParentDirs) {
            throw new IllegalStateException("Could not create directories to " + parentDir.getAbsolutePath());
        }
        return parentDir;
    }

    public static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException(directory + " is not a directory!");
            }
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        return (directory.delete());
    }

    public File createTempDir() throws IOException {
        final File tempDir = File.createTempFile("temp", Long.toString(System.nanoTime()));
        if (!tempDir.delete() || !tempDir.mkdir()) {
            throw new IOException("Could not create temp dir: " + tempDir.getAbsolutePath());
        }
        return tempDir;
    }
}
