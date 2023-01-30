/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.app.multi;

import org.teamapps.icons.Icon;
import org.teamapps.theme.Theme;

public class ApplicationLaunchInfo {


	public static ApplicationLaunchInfo createNotAccessibleInfo() {
		return new ApplicationLaunchInfo();
	}

	public static ApplicationLaunchInfo create(Icon icon, String title, String description) {
		return new ApplicationLaunchInfo(ApplicationGroup.EMPTY_INSTANCE, icon, title, description, null, true, false, false);
	}

	public static ApplicationLaunchInfo create(ApplicationGroup applicationGroup, Icon icon, String title, String description) {
		return new ApplicationLaunchInfo(applicationGroup, icon, title, description, null, true, false, false);
	}

	public static ApplicationLaunchInfo create(ApplicationGroup applicationGroup, Icon icon, String title, String description, Theme theme, boolean closable, boolean preload, boolean display) {
		return new ApplicationLaunchInfo(applicationGroup, icon, title, description, theme, closable, preload, display);
	}

	private boolean accessible = true;
	private ApplicationGroup applicationGroup;
	private Icon icon;
	private String title;
	private String description;
	private Theme theme;
	private boolean closable;
	private boolean preload;
	private boolean display;

	private ApplicationLaunchInfo() {
		this.accessible = false;
	}

	public ApplicationLaunchInfo(ApplicationGroup applicationGroup, Icon icon, String title, String description, Theme theme, boolean closable, boolean preload, boolean display) {
		this.applicationGroup = applicationGroup != null ? applicationGroup : ApplicationGroup.EMPTY_INSTANCE;
		this.icon = icon;
		this.title = title;
		this.description = description;
		this.theme = theme;
		this.closable = closable;
		this.preload = preload;
		this.display = display;
	}

	public boolean isAccessible() {
		return accessible;
	}

	public ApplicationGroup getApplicationGroup() {
		return applicationGroup;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Theme getTheme() {
		return theme;
	}

	public boolean isClosable() {
		return closable;
	}

	public boolean isPreload() {
		return preload;
	}

	public boolean isDisplay() {
		return display;
	}
}
