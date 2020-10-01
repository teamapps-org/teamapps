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
package org.teamapps.ux.model;

import org.teamapps.event.Event;

import java.util.List;

public abstract class AbstractTreeModel<RECORD> implements TreeModel<RECORD> {

	public final Event<Void> onAllNodesChanged = new Event<>();
	public final Event<TreeModelChangedEventData<RECORD>> onChanged = new Event<>();

	@Override
	public Event<Void> onAllNodesChanged() {
		return onAllNodesChanged;
	}

	@Override
	public Event<TreeModelChangedEventData<RECORD>> onChanged() {
		return onChanged;
	}
}
