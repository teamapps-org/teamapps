package org.teamapps.ux.component.infiniteitemview;

import org.teamapps.event.Event;

import java.util.List;

public interface InfiniteListModel<RECORD> {
	int getCount();

	List<RECORD> getRecords(int startIndex, int length);

	Event<Void> onAllDataChanged();

	Event<ItemRangeChangeEvent<RECORD>> onRecordsAdded();

	Event<ItemRangeChangeEvent<RECORD>> onRecordsChanged();

	Event<ItemRangeChangeEvent<RECORD>> onRecordsDeleted();
}
