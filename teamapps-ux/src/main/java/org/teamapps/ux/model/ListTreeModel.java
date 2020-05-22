/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.ux.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class ListTreeModel<RECORD> extends AbstractTreeModel<RECORD> {

	private List<RECORD> records = new ArrayList<>();
	private BiPredicate<RECORD, String> searchPredicate = (record, queryString) -> record.toString() != null && record.toString().toLowerCase().contains(queryString.toLowerCase());

	public ListTreeModel(List<RECORD> records) {
		this(records, null);
	}

	public ListTreeModel(List<RECORD> records, BiPredicate<RECORD, String> searchPredicate) {
		this.records.addAll(records);
		if (searchPredicate != null) {
			this.searchPredicate = searchPredicate;
		}
	}

	@Override
	public List<RECORD> getRecords(String query) {
		if (query == null || query.isEmpty()) {
			return records;
		} else {
			return records.stream()
					.filter(r -> searchPredicate.test(r, query))
					.collect(Collectors.toList());
		}
	}

	public List<RECORD> getRecords() {
		return records;
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
