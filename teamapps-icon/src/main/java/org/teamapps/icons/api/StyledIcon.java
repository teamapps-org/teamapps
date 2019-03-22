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
package org.teamapps.icons.api;

public class StyledIcon implements SimpleIcon {

	private String iconLibraryId;
	private String styleId;
	private String iconName;

	public static StyledIcon parseIcon(String styledIconId) {
		String[] parts = styledIconId.split(SEPARATOR_PATTERN);
		String iconLibraryId = parts[0];
		String styleId = parts[1];
		int pos = iconLibraryId.length() + SEPARATOR.length() + styleId.length() + SEPARATOR.length();
		String iconName = styledIconId.substring(pos);
		return new StyledIcon(iconLibraryId, styleId, iconName);
	}

	public StyledIcon(String iconLibraryId, String styleId, String iconName) {
		this.iconLibraryId = iconLibraryId;
		this.styleId = styleId;
		this.iconName = iconName;
	}

	@Override
	public String getIconName() {
		return iconName;
	}

	@Override
	public String getIconLibraryId() {
		return iconLibraryId;
	}

	public String getStyleId() {
		return styleId;
	}

	public String getQualifiedIconId(IconTheme theme, boolean baseStyle) {
		return getQualifiedIconId();
	}

	public String getQualifiedIconId() {
		return getIconLibraryId() + SEPARATOR +
				styleId + SEPARATOR +
				getIconName();
	}

	@Override
	public String toString() {
		return "StyledIcon{" +
				"iconLibraryId='" + iconLibraryId + '\'' +
				", styleId='" + styleId + '\'' +
				", iconName='" + iconName + '\'' +
				'}';
	}

}
