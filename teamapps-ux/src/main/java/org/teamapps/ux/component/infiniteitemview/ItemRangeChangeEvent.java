package org.teamapps.ux.component.infiniteitemview;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemRangeChangeEvent<RECORD> {

	private final ItemRange itemRange;
	private final List<RECORD> records;

	/**
	 * Use this constructor if the model does not have the changed items at hand.
	 * InfiniteItemViews might not actually need to react on the change, anyway.
	 */
	public ItemRangeChangeEvent(ItemRange itemRange) {
		this.itemRange = itemRange;
		this.records = null;
	}

	/**
	 * Use this constructor if the model has the changed items at hand.
	 */
	public ItemRangeChangeEvent(int startIndex, List<RECORD> records) {
		Objects.requireNonNull(records);
		this.itemRange = ItemRange.startLength(startIndex, records.size());
		this.records = records;
	}

	public ItemRange getItemRange() {
		return itemRange;
	}

	public Optional<List<RECORD>> getRecords() {
		return Optional.ofNullable(records);
	}

	public int getStart() {
		return getItemRange().getStart();
	}

	public int getLength() {
		return getItemRange().getLength();
	}

	public int getEnd() {
		return getItemRange().getEnd();
	}
}
