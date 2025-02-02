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
package org.teamapps.projector.component.trivial.tree.model;

import org.teamapps.projector.event.ProjectorEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface TreeModel<RECORD> extends BaseTreeModel<RECORD> {

	ProjectorEvent<Void> onAllNodesChanged();

	ProjectorEvent<TreeModelChangedEventData<RECORD>> onChanged();

	List<RECORD> getRecords();


	// CONVENIENCE:

	default void updateNodes(List<RECORD> records) {
		onChanged().fire(new TreeModelChangedEventData<>(Collections.emptyList(), records));
	}

	default void updateNodes(RECORD... records) {
		updateNodes(Arrays.asList(records));
	}

}
