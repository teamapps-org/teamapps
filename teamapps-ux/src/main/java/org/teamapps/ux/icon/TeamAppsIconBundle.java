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
package org.teamapps.ux.icon;

import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;

public enum TeamAppsIconBundle implements IconBundleEntry {

	ADD(MaterialIcon.ADD),
	SAVE(MaterialIcon.SAVE),
	DELETE(MaterialIcon.DELETE),
	UNDO(MaterialIcon.UNDO),
	CANCEL(MaterialIcon.CANCEL),

	FILTER(MaterialIcon.FILTER),
	SEARCH(MaterialIcon.SEARCH),
	SELECTION(MaterialIcon.SELECT_ALL),
	REMOVE(MaterialIcon.REMOVE),

	BACK(MaterialIcon.NAVIGATE_BEFORE),
	PREVIOUS(MaterialIcon.NAVIGATE_BEFORE),
	NEXT(MaterialIcon.NAVIGATE_NEXT),

	YEAR(MaterialIcon.EVENT_NOTE),
	MONTH(MaterialIcon.DATE_RANGE),
	WEEK(MaterialIcon.VIEW_WEEK),
	DAY(MaterialIcon.VIEW_DAY),

	APPLICATION_LAUNCHER(MaterialIcon.VIEW_MODULE),
	TREE(MaterialIcon.TOC),
	VIEWS(MaterialIcon.VIEW_CAROUSEL),
	TOOLBAR(MaterialIcon.SUBTITLES),

	UPLOAD(MaterialIcon.BACKUP),

	REFERENCE(MaterialIcon.LINK),
	MULTI_REFERENCE(MaterialIcon.LINEAR_SCALE),
	ENUM(MaterialIcon.LIST),

	;

	public static IconBundle createBundle() {
		return IconBundle.create(values());
	}

	private final Icon icon;

	TeamAppsIconBundle(Icon icon) {
		this.icon = icon;
	}

	@Override
	public String getKey() {
		return "teamApps_" + name();
	}

	@Override
	public Icon getIcon() {
		return icon;
	}


}
