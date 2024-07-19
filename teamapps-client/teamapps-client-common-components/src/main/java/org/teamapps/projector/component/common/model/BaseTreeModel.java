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
package org.teamapps.projector.component.common.model;

import org.teamapps.projector.component.common.tree.TreeNode;
import org.teamapps.projector.component.common.tree.TreeNodeInfo;
import org.teamapps.projector.component.common.tree.TreeNodeInfoExtractor;

import java.util.Collections;
import java.util.List;

public interface BaseTreeModel<RECORD> extends TreeNodeInfoExtractor<RECORD> {

	/**
	 * Get the child records of a node. This is currently only used for lazy parent nodes but might be used
	 * for other reasons later.
	 */
	default List<RECORD> getChildRecords(RECORD parentRecord) {
		return Collections.emptyList();
	}

	default TreeNodeInfo getTreeNodeInfo(RECORD record) {
		if (record instanceof TreeNode) {
			return (TreeNode) record;
		} else {
			return null;
		}
	}

}
