package org.teamapps.ux.component.infiniteitemview;

import java.util.Objects;

public class ItemRange {

	private final int start;
	private final int length;

	public static ItemRange startEnd(int start, int endExclusive) {
		return new ItemRange(start, endExclusive - start);
	}

	public static ItemRange startLength(int start, int length) {
		return new ItemRange(start, length);
	}

	private ItemRange(int start, int length) {
		this.start = start;
		this.length = length;
	}

	public int getStart() {
		return start;
	}

	public int getLength() {
		return length;
	}

	public int getEnd() {
		return start + length;
	}

	public boolean overlaps(ItemRange other) {
		return length > 0 && other.length > 0 && start < other.getEnd() && getEnd() > other.start;
	}

	@Override
	public String toString() {
		return "ItemRange{" +
				"start=" + start +
				", end=" + getEnd() +
				", length=" + length +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ItemRange itemRange = (ItemRange) o;
		return start == itemRange.start &&
				length == itemRange.length;
	}

	@Override
	public int hashCode() {
		return Objects.hash(start, length);
	}
}
