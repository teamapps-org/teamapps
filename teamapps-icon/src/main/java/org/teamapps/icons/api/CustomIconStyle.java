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

import java.awt.*;

public class CustomIconStyle implements IconStyle {

	private final String styleId;
	private final String styleName;
	private final String baseStyleId;
	private final boolean canBeUseAsSubIcon;
	private final Color[] searchColors;
	private final Color[] replaceColors;
	private int matchFuzzinessThreshold = 25;

	public CustomIconStyle(String styleId, String styleName, String baseStyleId, boolean canBeUseAsSubIcon, Color searchColor, Color replaceColor) {
		this.styleId = styleId;
		this.styleName = styleName;
		this.baseStyleId = baseStyleId;
		this.canBeUseAsSubIcon = canBeUseAsSubIcon;
		this.searchColors = new Color[] {searchColor};
		this.replaceColors = new Color[] {replaceColor};
	}

	public CustomIconStyle(String styleId, String styleName, String baseStyleId, boolean canBeUseAsSubIcon, Color[] searchColors, Color[] replaceColors) {
		this.styleId = styleId;
		this.styleName = styleName;
		this.baseStyleId = baseStyleId;
		this.canBeUseAsSubIcon = canBeUseAsSubIcon;
		this.searchColors = searchColors;
		this.replaceColors = replaceColors;
	}

	public CustomIconStyle(String styleId, String styleName, String baseStyleId, boolean canBeUseAsSubIcon, Color[] searchColors, Color[] replaceColors, int matchFuzzinessThreshold) {
		this.styleId = styleId;
		this.styleName = styleName;
		this.baseStyleId = baseStyleId;
		this.canBeUseAsSubIcon = canBeUseAsSubIcon;
		this.searchColors = searchColors;
		this.replaceColors = replaceColors;
		this.matchFuzzinessThreshold = matchFuzzinessThreshold;
	}


	public String getStyleId() {
		return styleId;
	}

	@Override
	public String getStyleName() {
		return styleName;
	}

	@Override
	public boolean canBeUsedAsSubIcon() {
		return canBeUseAsSubIcon;
	}

	public String getBaseStyleId() {
		return baseStyleId;
	}

	public Color[] getSearchColors() {
		return searchColors;
	}

	public Color[] getReplaceColors() {
		return replaceColors;
	}

	public int getMatchFuzzinessThreshold() {
		return matchFuzzinessThreshold;
	}

	public void setMatchFuzzinessThreshold(int matchFuzzinessThreshold) {
		this.matchFuzzinessThreshold = matchFuzzinessThreshold;
	}
}
