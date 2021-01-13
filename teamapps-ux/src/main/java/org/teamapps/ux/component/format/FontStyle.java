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
package org.teamapps.ux.component.format;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiFontStyle;

public class FontStyle {

	protected float relativeFontSize = 1;
	protected Color fontColor;
	protected Color backgroundColor;
	protected boolean bold;
	protected boolean underline;
	protected boolean italic;

	public FontStyle() {
	}

	public FontStyle(float relativeFontSize) {
		this.relativeFontSize = relativeFontSize;
	}

	public FontStyle(float relativeFontSize, Color fontColor) {
		this.relativeFontSize = relativeFontSize;
		this.fontColor = fontColor;
	}

	public FontStyle(float relativeFontSize, Color fontColor, Color backgroundColor, boolean bold, boolean underline, boolean italic) {
		this.relativeFontSize = relativeFontSize;
		this.fontColor = fontColor;
		this.backgroundColor = backgroundColor;
		this.bold = bold;
		this.underline = underline;
		this.italic = italic;
	}

	public FontStyle setFontColor(final Color fontColor) {
		this.fontColor = fontColor;
		return this;
	}

	public FontStyle setBackgroundColor(final Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public FontStyle setBold(final boolean bold) {
		this.bold = bold;
		return this;
	}

	public FontStyle setUnderline(final boolean underline) {
		this.underline = underline;
		return this;
	}

	public FontStyle setItalic(final boolean italic) {
		this.italic = italic;
		return this;
	}

	public FontStyle setRelativeFontSize(final float relativeFontSize) {
		this.relativeFontSize = relativeFontSize;
		return this;
	}

	public Color getFontColor() {
		return fontColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isUnderline() {
		return underline;
	}

	public boolean isItalic() {
		return italic;
	}

	public float getRelativeFontSize() {
		return relativeFontSize;
	}

	public UiFontStyle createUiFontStyle() {
		UiFontStyle uiFontStyle = new UiFontStyle();
		uiFontStyle.setFontColor(fontColor != null ? fontColor.toHtmlColorString() : null);
		uiFontStyle.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		uiFontStyle.setBold(bold);
		uiFontStyle.setUnderline(underline);
		uiFontStyle.setItalic(italic);
		uiFontStyle.setRelativeFontSize(relativeFontSize);
		return uiFontStyle;
	}
}
