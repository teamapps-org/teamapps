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
package org.teamapps.projector.component.infinitescroll.table;

import org.teamapps.projector.component.infinitescroll.infiniteitemview.RecordsAddedEvent;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListTableModel<RECORD> extends AbstractTableModel<RECORD> {

	private List<RECORD> list = new ArrayList<>();
	private Predicate<RECORD> filter = record -> true;
	private PropertyExtractor<RECORD> sortingPropertyExtractor = new BeanPropertyExtractor<>();

	public ListTableModel() {
	}

	public ListTableModel(List<RECORD> list) {
		this.list.addAll(list);
	}

	public void setList(List<RECORD> list) {
		if (list == null) {
			list = Collections.emptyList();
		}
		this.list = new ArrayList<>(list);
		onAllDataChanged.fire(null);
	}

	public List<RECORD> getList() {
		return Collections.unmodifiableList(list);
	}

	public void addRecord(RECORD record) {
		list.add(record);
		onRecordAdded.fire(new RecordsAddedEvent<>(list.size() - 1, List.of(record)));
	}

	public void addRecords(List<RECORD> records) {
		list.addAll(records);
		onAllDataChanged.fire(null);
	}

	@Override
	public int getCount() {
		if (filter == null) {
			return list.size();
		} else {
			return (int) list.stream()
					.filter(filter)
					.count();
		}
	}

	@Override
	public List<RECORD> getRecords(int startIndex, int length) {
		if (filter == null && sorting == null) {
			return list.subList(startIndex, Math.min(list.size(), startIndex + length));
		} else {
			Stream<RECORD> stream = list.stream();
			if (filter != null) {
				stream = stream.filter(filter);
			}
			if (sorting != null && sortingPropertyExtractor != null) {
				stream = stream.sorted(valueExtractingComparator());
			}
			return stream
					.skip(startIndex)
					.limit(length)
					.collect(Collectors.toList());
		}
	}

	private Comparator<RECORD> valueExtractingComparator() {
		Comparator<RECORD> comparator = (o1, o2) -> {
			Object v1 = sortingPropertyExtractor.getValue(o1, sorting.getFieldName());
			Object v2 = sortingPropertyExtractor.getValue(o2, sorting.getFieldName());
			if (v1 == null && v2 == null) {
				return 0;
			} else if (v1 == null) {
				return 1;
			} else if (v2 == null) {
				return -1;
			} else { // both are not null
				if (!(v1 instanceof Comparable) && !(v2 instanceof Comparable)) {
					return 0;
				} else if (!(v1 instanceof Comparable)) {
					return 1;
				} else if (!(v2 instanceof Comparable)) {
					return -1;
				} else {
					return ((Comparable) v1).compareTo(v2);
				}
			}
		};
		if (sorting.getSortDirection() == SortDirection.DESC) {
			comparator = comparator.reversed();
		}
		return comparator;
	}

	public List<RECORD> getAllRecords() {
		return new ArrayList<>(list);
	}

	public Predicate<RECORD> getFilter() {
		return filter;
	}

	public void setFilter(Predicate<RECORD> filter) {
		this.filter = filter;
		onAllDataChanged.fire(null);
	}

	public PropertyExtractor<RECORD> getSortingPropertyExtractor() {
		return sortingPropertyExtractor;
	}

	public void setSortingPropertyExtractor(PropertyExtractor<RECORD> sortingPropertyExtractor) {
		this.sortingPropertyExtractor = sortingPropertyExtractor;
	}
}
