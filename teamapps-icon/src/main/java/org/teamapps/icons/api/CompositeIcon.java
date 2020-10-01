/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.icons.api;

public class CompositeIcon implements Icon {

	public static final String COMPOSED_ICON_PREFIX = "x-multi-icon.";
	public static final String SUB_ICON_MARKER = ".x-sub-";
	public static final String BOTTOM_RIGHT_SUB_ICON = SUB_ICON_MARKER + "1.";
	public static final String BOTTOM_LEFT_SUB_ICON = SUB_ICON_MARKER + "2.";
	public static final String TOP_LEFT_SUB_ICON = SUB_ICON_MARKER + "3.";
	public static final String TOP_RIGHT_SUB_ICON = SUB_ICON_MARKER + "4.";

	private final Icon baseIcon;
	private	Icon bottomRightIcon;
	private	Icon bottomLeftIcon;
	private	Icon topLeftIcon;
	private	Icon topRightIcon;

	public static CompositeIcon of(Icon baseIcon, Icon bottomRightIcon) {
		return new CompositeIcon(baseIcon, bottomRightIcon);
	}

	public static CompositeIcon of(Icon baseIcon, Icon bottomRightIcon, Icon bottomLeftIcon, Icon topLeftIcon, Icon topRightIcon) {
		return new CompositeIcon(baseIcon, bottomRightIcon, bottomLeftIcon, topLeftIcon, topRightIcon);
	}

	public CompositeIcon(Icon baseIcon) {
		this.baseIcon = baseIcon;
	}

	public CompositeIcon(Icon baseIcon, Icon bottomRightIcon) {
		this.baseIcon = baseIcon;
		this.bottomRightIcon = bottomRightIcon;
	}

	public CompositeIcon(Icon baseIcon, Icon bottomRightIcon, Icon bottomLeftIcon, Icon topLeftIcon, Icon topRightIcon) {
		this.baseIcon = baseIcon;
		this.bottomRightIcon = bottomRightIcon;
		this.bottomLeftIcon = bottomLeftIcon;
		this.topLeftIcon = topLeftIcon;
		this.topRightIcon = topRightIcon;
	}

	@Override
	public String getQualifiedIconId(IconTheme theme, boolean baseStyle) {
		StringBuilder sb = new StringBuilder();
		sb.append(COMPOSED_ICON_PREFIX);
		sb.append(baseIcon.getQualifiedIconId(theme, baseStyle));
		if (bottomRightIcon != null) {
			sb.append(BOTTOM_RIGHT_SUB_ICON);
			sb.append(bottomRightIcon.getQualifiedIconId(theme, false));
		}
		if (bottomLeftIcon != null) {
			sb.append(BOTTOM_LEFT_SUB_ICON);
			sb.append(bottomLeftIcon.getQualifiedIconId(theme, false));
		}
		if (topLeftIcon != null) {
			sb.append(TOP_LEFT_SUB_ICON);
			sb.append(topLeftIcon.getQualifiedIconId(theme, false));
		}
		if (topRightIcon != null) {
			sb.append(TOP_RIGHT_SUB_ICON);
			sb.append(topRightIcon.getQualifiedIconId(theme, false));
		}

		return sb.toString();
	}
}
