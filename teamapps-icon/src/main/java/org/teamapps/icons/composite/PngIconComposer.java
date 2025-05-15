/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.icons.composite;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PngIconComposer {

	public byte[] compose(int size, byte[] baseIcon, byte[] bottomRight, byte[] bottomLeft, byte[] topLeft, byte[] topRight) {
		try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(baseIcon));
			if (bottomRight != null) {
				BufferedImage subIcon = ImageIO.read(new ByteArrayInputStream(bottomRight));
				image = Thumbnails.of(image).size(size, size).watermark(Positions.BOTTOM_RIGHT, subIcon, 1.0f).asBufferedImage();
			}
			if (bottomLeft != null) {
				BufferedImage subIcon = ImageIO.read(new ByteArrayInputStream(bottomLeft));
				image = Thumbnails.of(image).size(size, size).watermark(Positions.BOTTOM_LEFT, subIcon, 1.0f).asBufferedImage();
			}
			if (topLeft != null) {
				BufferedImage subIcon = ImageIO.read(new ByteArrayInputStream(topLeft));
				image = Thumbnails.of(image).size(size, size).watermark(Positions.TOP_LEFT, subIcon, 1.0f).asBufferedImage();
			}
			if (topRight != null) {
				BufferedImage subIcon = ImageIO.read(new ByteArrayInputStream(topRight));
				image = Thumbnails.of(image).size(size, size).watermark(Positions.TOP_RIGHT, subIcon, 1.0f).asBufferedImage();
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Thumbnails.of(image).size(size, size).outputFormat("PNG").toOutputStream(bos);
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
