package org.teamapps.app.background;

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
			context.setBackgroundColor(backgroundColor, animationDuration);
		} else if (backgroundImagePath != null) {
			context.setBackgroundImage(uuid, animationDuration);
		} else if (backgroundImageFile != null) {
			context.setBackgroundImage(uuid, animationDuration);
		}
	}
}
