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
