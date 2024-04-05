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
package org.teamapps.projector.components.trivial.tree.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListTreeModel<RECORD> extends AbstractTreeModel<RECORD> implements ComboBoxModel<RECORD> {

	private final List<RECORD> records = new ArrayList<>();
	private BiPredicate<RECORD, String> searchPredicate = (record, queryString) -> record.toString() != null && record.toString().toLowerCase().contains(queryString.toLowerCase());
	private Function<RECORD, TreeNodeInfo<RECORD>> treeNodeInfoFunction;

	public ListTreeModel(List<RECORD> records) {
		this(records, null);
	}

	public ListTreeModel(List<RECORD> records, BiPredicate<RECORD, String> searchPredicate) {
		this.records.addAll(records);
		if (searchPredicate != null) {
			this.searchPredicate = searchPredicate;
		}
	}

	public void setTreeNodeInfoFunction(Function<RECORD, TreeNodeInfo<RECORD>> treeNodeInfoFunction) {
		this.treeNodeInfoFunction = treeNodeInfoFunction;
	}

	@Override
	public TreeNodeInfo getTreeNodeInfo(RECORD record) {
		if (treeNodeInfoFunction != null) {
			return treeNodeInfoFunction.apply(record);
		} else {
			return super.getTreeNodeInfo(record);
		}
	}

	@Override
	public List<RECORD> getRecords() {
		return records;
	}

	@Override
	public List<RECORD> getRecords(String query) {
		if (StringUtils.isBlank(query)) {
			return records;
		} else {
			return records.stream()
					.filter(r -> searchPredicate.test(r, query))
					.collect(Collectors.toList());
		}
	}

	public void setRecords(List<RECORD> records) {
		this.records.clear();
		this.records.addAll(records);
		onAllNodesChanged.fire();
	}

	public BiPredicate<RECORD, String> getSearchPredicate() {
		return searchPredicate;
	}

	public void setSearchPredicate(BiPredicate<RECORD, String> searchPredicate) {
		this.searchPredicate = searchPredicate;
	}
}
