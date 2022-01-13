/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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

import java.util.Objects;

public class ApplicationGroup implements Comparable<ApplicationGroup> {

	public static ApplicationGroup EMPTY_INSTANCE = new ApplicationGroup(null, null);

	private final Icon icon;
	private final String title;
	private int displayRow;

	public ApplicationGroup(Icon icon, String title) {
		this.icon = icon;
		this.title = title;
	}

	public ApplicationGroup(Icon icon, String title, int displayRow) {
		this.icon = icon;
		this.title = title;
		this.displayRow = displayRow;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

	public int getDisplayRow() {
		return displayRow;
	}

	public void setDisplayRow(int displayRow) {
		this.displayRow = displayRow;
	}

	@Override
	public int compareTo(ApplicationGroup o) {
		return Integer.compare(displayRow, o.getDisplayRow());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ApplicationGroup that = (ApplicationGroup) o;
		return getDisplayRow() == that.getDisplayRow() &&
				Objects.equals(getTitle(), that.getTitle());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTitle(), getDisplayRow());
	}
}
