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
package org.teamapps.ux.component.table;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiSelectionFrame;

public class SelectionFrame {

	private Color color = new Color(102, 174, 232);
	private int borderWidth = 2;
	private int animationDuration = 100; // ms
	private int glowingWidth = 5;
	private int shadowWidth = 0;
	private boolean fullRow = false;

	public UiSelectionFrame createUiSelectionFrame() {
		UiSelectionFrame uiSelectionFrame = new UiSelectionFrame();
		uiSelectionFrame.setColor(color != null ? color.toHtmlColorString() : null);
		uiSelectionFrame.setBorderWidth(borderWidth);
		uiSelectionFrame.setAnimationDuration(animationDuration);
		uiSelectionFrame.setGlowingWidth(glowingWidth);
		uiSelectionFrame.setShadowWidth(shadowWidth);
		uiSelectionFrame.setFullRow(fullRow);
		return uiSelectionFrame;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public int getAnimationDuration() {
		return animationDuration;
	}

	public void setAnimationDuration(int animationDuration) {
		this.animationDuration = animationDuration;
	}

	public int getGlowingWidth() {
		return glowingWidth;
	}

	public void setGlowingWidth(int glowingWidth) {
		this.glowingWidth = glowingWidth;
	}

	public int getShadowWidth() {
		return shadowWidth;
	}

	public void setShadowWidth(int shadowWidth) {
		this.shadowWidth = shadowWidth;
	}

	public boolean isFullRow() {
		return fullRow;
	}

	public void setFullRow(boolean fullRow) {
		this.fullRow = fullRow;
	}
}
