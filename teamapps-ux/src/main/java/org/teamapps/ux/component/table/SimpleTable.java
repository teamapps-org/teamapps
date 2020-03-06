package org.teamapps.ux.component.table;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class SimpleTable<RECORD> extends Table<RECORD> {

	private ListTableModel<RECORD> model;

	public SimpleTable() {
		this(Collections.emptyList());
	}

	public SimpleTable(List<RECORD> records) {
		model = new ListTableModel<>(records);
	}

	public void setRecords(List<RECORD> records) {
		model.setList(records);
	}

	public List<RECORD> getRecords() {
		return model.getAllRecords();
	}

	public void addRecord(RECORD record) {
		model.addRecord(record);
	}

	public void addRecords(List<RECORD> records) {
		model.addRecords(records);
	}

	public void setFilter(Predicate<RECORD> filter) {
		model.setFilter(filter);
	}
}
