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

import static org.teamapps.icons.api.CompositeIcon.*;

public class StyledCompositeIcon implements Icon {

	private final StyledIcon baseIcon;
	private	StyledIcon bottomRightIcon;
	private	StyledIcon bottomLeftIcon;
	private	StyledIcon topLeftIcon;
	private	StyledIcon topRightIcon;

	public static boolean isComposedIcon(String icon) {
		return icon != null && icon.startsWith(COMPOSED_ICON_PREFIX);
	}

	public static StyledCompositeIcon parse(String icon) {
		if (icon == null) {
			return null;
		}
		if (!icon.startsWith(COMPOSED_ICON_PREFIX)) {
			StyledIcon baseIcon = StyledIcon.parseIcon(icon);
			return new StyledCompositeIcon(baseIcon);
		}
		icon = icon.substring(COMPOSED_ICON_PREFIX.length());

		int pos = icon.indexOf(SUB_ICON_MARKER);
		if (pos < 0) {
			StyledIcon baseIcon = StyledIcon.parseIcon(icon);
			return new StyledCompositeIcon(baseIcon);
		}
		String baseIconId = icon.substring(0, pos);
		StyledIcon baseIcon = StyledIcon.parseIcon(baseIconId);
		StyledCompositeIcon composedIcon = new StyledCompositeIcon(baseIcon);
		while (pos >= 0) {
			int index = Integer.parseInt(icon.substring(pos + SUB_ICON_MARKER.length(), pos + SUB_ICON_MARKER.length() + 1));
			int subIconStartPos = pos + SUB_ICON_MARKER.length() + 2;
			pos = icon.indexOf(SUB_ICON_MARKER, subIconStartPos);
			String subIcon = null;
			if (pos < 0) {
				subIcon = icon.substring(subIconStartPos);
			} else {
				subIcon = icon.substring(subIconStartPos, pos);
			}
			composedIcon.setSubIconByIndex(subIcon, index);
		}
		return composedIcon;
	}

	private void setSubIconByIndex(String subIconId, int index) {
		StyledIcon subIcon = StyledIcon.parseIcon(subIconId);
		switch (index) {
			case 1:
				bottomRightIcon = subIcon;
				break;
			case 2:
				bottomLeftIcon = subIcon;
				break;
			case 3:
				topLeftIcon = subIcon;
				break;
			case 4:
				topRightIcon = subIcon;
				break;
		}
	}

	public StyledCompositeIcon(StyledIcon baseIcon) {
		this.baseIcon = baseIcon;
	}

	public StyledCompositeIcon(StyledIcon baseIcon, StyledIcon bottomRightIcon, StyledIcon bottomLeftIcon, StyledIcon topLeftIcon, StyledIcon topRightIcon) {
		this.baseIcon = baseIcon;
		this.bottomRightIcon = bottomRightIcon;
		this.bottomLeftIcon = bottomLeftIcon;
		this.topLeftIcon = topLeftIcon;
		this.topRightIcon = topRightIcon;
	}

	public StyledIcon getBaseIcon() {
		return baseIcon;
	}

	public StyledIcon getBottomLeftIcon() {
		return bottomLeftIcon;
	}

	public StyledIcon getBottomRightIcon() {
		return bottomRightIcon;
	}

	public StyledIcon getTopLeftIcon() {
		return topLeftIcon;
	}

	public StyledIcon getTopRightIcon() {
		return topRightIcon;
	}

	@Override
	public String getQualifiedIconId(IconTheme theme, boolean baseStyle) {
		StringBuilder sb = new StringBuilder();
		sb.append(COMPOSED_ICON_PREFIX);
		sb.append(baseIcon.getQualifiedIconId());
		if (bottomRightIcon != null) {
			sb.append(BOTTOM_RIGHT_SUB_ICON);
			sb.append(bottomRightIcon.getQualifiedIconId());
		}
		if (bottomLeftIcon != null) {
			sb.append(BOTTOM_LEFT_SUB_ICON);
			sb.append(bottomLeftIcon.getQualifiedIconId());
		}
		if (topLeftIcon != null) {
			sb.append(TOP_LEFT_SUB_ICON);
			sb.append(topLeftIcon.getQualifiedIconId());
		}
		if (topRightIcon != null) {
			sb.append(TOP_RIGHT_SUB_ICON);
			sb.append(topRightIcon.getQualifiedIconId());
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return "StyledComposedIcon{" +
				"baseIcon=" + baseIcon +
				", bottomLeftIcon=" + bottomLeftIcon +
				", bottomRightIcon=" + bottomRightIcon +
				", topLeftIcon=" + topLeftIcon +
				", topRightIcon=" + topRightIcon +
				'}';
	}


}
