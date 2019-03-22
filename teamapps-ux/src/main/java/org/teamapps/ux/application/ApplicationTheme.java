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
package org.teamapps.ux.application;

import org.apache.commons.io.IOUtils;
import org.teamapps.ux.session.StylingTheme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public interface ApplicationTheme {

    static ApplicationTheme createTheme(String applicationBackgroundImage) {
        return createTheme(applicationBackgroundImage, null, null, null, null, null);
    }

    static ApplicationTheme createTheme(String applicationBackgroundImage, StylingTheme applicationTheme) {
        return createTheme(applicationBackgroundImage, null, null, applicationTheme, null, null);
    }

    static ApplicationTheme createTheme(File applicationBackgroundImageFile) {
        return createTheme(null, applicationBackgroundImageFile, null, null, null, null);
    }

    static ApplicationTheme createTheme(File applicationBackgroundImageFile, StylingTheme applicationTheme) {
        return createTheme(null, applicationBackgroundImageFile, null, applicationTheme, null, null);
    }

    static ApplicationTheme createTheme(URL applicationBackgroundImageURL) {
        return createTheme(null, null, applicationBackgroundImageURL, null, null, null);
    }

    static ApplicationTheme createTheme(URL applicationBackgroundImageURL, StylingTheme applicationTheme) {
        return createTheme(null, null, applicationBackgroundImageURL, applicationTheme, null, null);
    }

    static ApplicationTheme createTheme(String applicationBackgroundImage, StylingTheme applicationTheme, String customIconStyleId, String customIconLibraryId) {
        return createTheme(applicationBackgroundImage, null, null, applicationTheme, customIconStyleId, customIconLibraryId);
    }

    private static File loadFile(URL url) {
        try {
            File tempFile = File.createTempFile("background", "jpg");
            IOUtils.copy(url.openStream(), new FileOutputStream(tempFile));
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ApplicationTheme createTheme(String applicationBackgroundImage, File applicationBackgroundImageFile, URL applicationBackgroundImageURL, StylingTheme applicationTheme, String customIconStyleId, String customIconLibraryId) {
        if (applicationBackgroundImageURL != null && applicationBackgroundImageFile == null) {
            applicationBackgroundImageFile = loadFile(applicationBackgroundImageURL);
        }
        final File backgroundFile = applicationBackgroundImageFile;
        return new ApplicationTheme() {


            @Override
            public String getApplicationBackgroundImage() {
                return applicationBackgroundImage;
            }

            @Override
            public File geApplicationBackgroundImageFile() {
                return backgroundFile;
            }

            @Override
            public StylingTheme getApplicationTheme() {
                if (applicationTheme == null) {
                    return StylingTheme.DEFAULT;
                }
                return applicationTheme;
            }

            @Override
            public String getCustomIconStyleId() {
                return customIconStyleId;
            }

            @Override
            public String getCustomIconLibraryId() {
                return customIconLibraryId;
            }
        };
    }

    String getApplicationBackgroundImage();

    File geApplicationBackgroundImageFile();

    StylingTheme getApplicationTheme();

    String getCustomIconStyleId();

    String getCustomIconLibraryId();

}
