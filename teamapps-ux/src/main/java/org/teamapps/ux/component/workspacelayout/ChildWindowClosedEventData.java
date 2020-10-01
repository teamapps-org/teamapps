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
package org.teamapps.ux.component.workspacelayout;

import java.util.List;

public class ChildWindowClosedEventData {
	private final String windowId;
	private final WorkSpaceLayoutItem windowRootItem;

	public ChildWindowClosedEventData(String windowId, WorkSpaceLayoutItem windowRootItem) {
		this.windowId = windowId;
		this.windowRootItem = windowRootItem;
	}

	public String getWindowId() {
		return windowId;
	}

	public WorkSpaceLayoutItem getWindowRootItem() {
		return windowRootItem;
	}

	public List<WorkSpaceLayoutView> getViews() {
		return windowRootItem.getAllViews();
	}
}
