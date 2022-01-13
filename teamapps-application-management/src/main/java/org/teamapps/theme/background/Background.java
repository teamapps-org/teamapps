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
package org.teamapps.theme.background;

import org.teamapps.common.format.Color;
import org.teamapps.ux.session.SessionContext;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Background {

	public static Background createDefaultBackground() {
		return createImageBackground("/resources/backgrounds/default-bl.jpg", 0);
	}

	public static Background createDefaultLoginBackground() {
		return createImageBackground("/resources/backgrounds/login3.jpg", "/resources/backgrounds/login3-bl.jpg", 0);
	}

	public static Background createColorBackground(Color color, int animationDuration) {
		return new Background(color, null,  null, null,null, animationDuration);
	}

	public static Background createImageBackground(String backgroundImagePath, int animationDuration) {
		return new Background(null, backgroundImagePath, null, null, null, animationDuration);
	}

	public static Background createImageBackground(String backgroundImagePath, String blurredBackgroundImagePath, int animationDuration) {
		return new Background(null, backgroundImagePath, blurredBackgroundImagePath, null, null, animationDuration);
	}

	public static Background createImageBackground(File backgroundImageFile, int animationDuration) {
		return new Background(null, null, null, backgroundImageFile, null, animationDuration);
	}

	public static Background createImageBackground(File backgroundImageFile, File blurredBackgroundImageFile, int animationDuration) {
		return new Background(null, null, null,backgroundImageFile, blurredBackgroundImageFile, animationDuration);
	}

	public static Background createImageBackgroundWithBlurred(File backgroundImageFile, int animationDuration) throws IOException {
		BlurImage blurImage = new BlurImage();
		File blurredBackgroundImageFile = File.createTempFile("temp", ".png");
		blurImage.createBlurredImage(backgroundImageFile, blurredBackgroundImageFile);
		return new Background(null, null, null,backgroundImageFile, blurredBackgroundImageFile, animationDuration);
	}

	private Color backgroundColor;
	private String backgroundImagePath;
	private String blurredBackgroundImagePath;
	private File backgroundImageFile;
	private File blurredBackgroundImageFile;
	private int animationDuration;
	private boolean blurred;
	private String uuid = UUID.randomUUID().toString();

	private Background(Color backgroundColor, String backgroundImagePath, String blurredBackgroundImagePath, File backgroundImageFile, File blurredBackgroundImageFile, int animationDuration) {
		this.backgroundColor = backgroundColor;
		this.backgroundImagePath = backgroundImagePath;
		this.blurredBackgroundImagePath = blurredBackgroundImagePath;
		this.backgroundImageFile = backgroundImageFile;
		this.blurredBackgroundImageFile = blurredBackgroundImageFile;
		this.animationDuration = animationDuration;
		if (blurredBackgroundImagePath != null || blurredBackgroundImageFile != null) {
			blurred = true;
		}
	}

	public void registerAndApply(SessionContext context) {
		registerBackground( context);
		applyBackground(context);
	}

	public void registerBackground(SessionContext context) {
		if (backgroundImagePath != null) {
			if (blurred) {
				context.registerBackgroundImage(uuid, backgroundImagePath, blurredBackgroundImagePath);
			} else {
				context.registerBackgroundImage(uuid, backgroundImagePath, backgroundImagePath);
			}
		} else if (backgroundImageFile != null) {
			if (blurred) {
				String imageLink = context.createFileLink(backgroundImageFile);
				String blurredImageLink = context.createFileLink(blurredBackgroundImageFile);
				context.registerBackgroundImage(uuid, imageLink, blurredImageLink);
			} else {
				String imageLink = context.createFileLink(backgroundImageFile);
				context.registerBackgroundImage(uuid, imageLink, imageLink);
			}
		}
	}

	public void applyBackground(SessionContext context) {
		if (backgroundColor != null) {
			context.setBackgroundImage(null, 0);
			context.setBackgroundColor(backgroundColor, animationDuration);
		} else if (backgroundImagePath != null) {
			context.setBackgroundImage(uuid, animationDuration);
		} else if (backgroundImageFile != null) {
			context.setBackgroundImage(uuid, animationDuration);
		}
	}
}
