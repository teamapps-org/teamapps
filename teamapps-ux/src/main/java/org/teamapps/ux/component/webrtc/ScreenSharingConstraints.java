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
package org.teamapps.ux.component.webrtc;

import org.teamapps.dto.UiScreenSharingConstraints;

public class ScreenSharingConstraints {

	private int maxWidth = Integer.MAX_VALUE;
	private int maxHeight = Integer.MAX_VALUE;

	public ScreenSharingConstraints() {
	}

	public ScreenSharingConstraints(int maxWidth, int maxHeight) {
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	public UiScreenSharingConstraints createUiScreenSharingConstraints() {
		UiScreenSharingConstraints ui = new UiScreenSharingConstraints();
		ui.setMaxWidth(maxWidth);
		ui.setMaxHeight(maxHeight);
		return ui;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ScreenSharingConstraints that = (ScreenSharingConstraints) o;

		if (maxWidth != that.maxWidth) {
			return false;
		}
		return maxHeight == that.maxHeight;
	}

	@Override
	public int hashCode() {
		int result = maxWidth;
		result = 31 * result + maxHeight;
		return result;
	}
}
