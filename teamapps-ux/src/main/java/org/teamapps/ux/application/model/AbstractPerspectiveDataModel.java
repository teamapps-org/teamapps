/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.application.model;

import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.data.value.Sorting;
import org.teamapps.ux.component.calendar.AbstractCalendarEvent;
import org.teamapps.ux.component.calendar.AbstractCalendarModel;
import org.teamapps.ux.component.calendar.CalendarEvent;
import org.teamapps.ux.component.calendar.CalendarModel;
import org.teamapps.ux.component.infiniteitemview.AbstractInfiniteItemViewModel;
import org.teamapps.ux.component.infiniteitemview.InfiniteItemViewModel;
import org.teamapps.ux.component.table.AbstractTableModel;
import org.teamapps.ux.component.table.TableModel;
import org.teamapps.ux.component.timegraph.Interval;
import org.teamapps.ux.component.timegraph.TimeGraphModel;
import org.teamapps.ux.component.timegraph.partitioning.AbstractRawTimedDataModel;
import org.teamapps.ux.component.timegraph.partitioning.PartitioningTimeGraphModel;
import org.teamapps.ux.component.tree.TreeNodeInfo;
import org.teamapps.ux.component.tree.TreeNodeInfoExtractor;
import org.teamapps.ux.model.AbstractTreeModel;
import org.teamapps.ux.model.TreeModel;
import org.teamapps.ux.session.CurrentSessionContext;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractPerspectiveDataModel<RECORD> implements PerspectiveDataModel<RECORD> {

	private final PropertyProvider<RECORD> propertyProvider;
	private AbstractTableModel<RECORD> tableModel;
	private AbstractInfiniteItemViewModel<RECORD> infiniteItemViewModel;
	private CalendarModel<CalendarEvent> calendarModel;
	private Function<RECORD, AbstractCalendarEvent> calendarEventProvider;
	private TimeGraphModel timeGraphModel;
	private TreeModel<RECORD> treeModel;

	public AbstractPerspectiveDataModel(PropertyProvider<RECORD> propertyProvider) {
		this.propertyProvider = propertyProvider;
		init();
	}

	public abstract int getRecordCount();

	public abstract List<RECORD> getEntities(int startIndex, int length, Instant start, Instant end, Sorting sorting);

	private void init() {
		tableModel = new AbstractTableModel<>() {
			@Override
			public int getCount() {
				return getRecordCount();
			}

			@Override
			public List<RECORD> getRecords(int startIndex, int length, Sorting sorting) {
				return getEntities(startIndex, length, null, null, sorting);
			}
		};

		infiniteItemViewModel = new AbstractInfiniteItemViewModel<>() {
			@Override
			public int getCount() {
				return getRecordCount();
			}

			@Override
			public List<RECORD> getRecords(int startIndex, int length) {
				return getEntities(startIndex, length, null, null, null);
			}
		};

		calendarModel = new AbstractCalendarModel<>() {
			@Override
			public List<CalendarEvent> getEventsForInterval(Instant start, Instant end) {
				if (calendarEventProvider == null) {
					return Collections.emptyList();
				}
				List<RECORD> entities = getEntities(0, Integer.MAX_VALUE, start, end, null);
				return entities.stream().map(RECORD -> calendarEventProvider.apply(RECORD)).collect(Collectors.toList());
			}
		};

		timeGraphModel = new PartitioningTimeGraphModel(CurrentSessionContext.get().getTimeZone(), new AbstractRawTimedDataModel() {
			@Override
			public long[] getRawEventTimes(String dataSeriesId, Interval neededIntervalX) {
				return getTimeGraphData(dataSeriesId, neededIntervalX);
			}

			@Override
			public Interval getDomainX(Collection<String> dataSeriesId) {
				return getTimeGraphDomain(dataSeriesId);
			}
		});


		treeModel = new AbstractTreeModel<>() {
			@Override
			public List<RECORD> getRecords(String query) {
				return getEntities(0, Integer.MAX_VALUE, null, null, null);
			}
		};
	}

	protected void handleDataUpdated() {
		tableModel.onAllDataChanged.fire(null);
		infiniteItemViewModel.onAllDataChanged.fire(null);
		calendarModel.onCalendarDataChanged().fire(null);
		timeGraphModel.onDataChanged().fire(null);
		//todo: tree expects the list of nodes
		treeModel.onAllNodesChanged().fire(null);
	}

	private long[] getTimeGraphData(String lineId, Interval interval) {
		long startTime = interval.getMin();
		long endTime = interval.getMax();

		List<RECORD> entities = getEntities(0, Integer.MAX_VALUE, null, null, null);
		long[] timeStamps = new long[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			Instant instant = propertyProvider.getInstantValue(entities.get(i), lineId);
			if (instant != null) {
				long time = instant.toEpochMilli();
				if (startTime <= time && endTime >= time) {
					timeStamps[i] = time;
				}
			}
		}
		return Arrays.stream(timeStamps)
				.filter(value -> value > 0)
				.toArray();
	}

	private Interval getTimeGraphDomain(Collection<String> lineIds) {
		List<RECORD> entities = getEntities(0, Integer.MAX_VALUE, null, null, null);
		long minValue = Long.MAX_VALUE;
		long maxValue = Long.MIN_VALUE;
		for (String lineId : lineIds) {
			for (int i = 0; i < entities.size(); i++) {
				Instant instant = propertyProvider.getInstantValue(entities.get(i), lineId);
				if (instant != null) {
					long time = instant.toEpochMilli();
					minValue = Math.min(minValue, time);
					maxValue = Math.max(maxValue, time);
				}
			}
		}
		return new Interval(minValue, maxValue);
	}

	public PropertyProvider<RECORD> getPropertyProvider() {
		return propertyProvider;
	}

	@Override
	public TableModel<RECORD> getTableModel() {
		return tableModel;
	}

	@Override
	public InfiniteItemViewModel<RECORD> getInfiniteItemViewModel() {
		return infiniteItemViewModel;
	}

	@Override
	public TimeGraphModel getTimeGraphModel() {
		return timeGraphModel;
	}

	@Override
	public TreeModel<RECORD> getTreeModel() {
		return treeModel;
	}

	public CalendarModel<CalendarEvent> getCalendarModel() {
		return calendarModel;
	}

	@Override
	public TreeNodeInfoExtractor<RECORD> getTreeNodeParentExtractor(String parentPropertyName) {
		if (parentPropertyName == null) {
			return null;
		}
		return record -> new TreeNodeInfo() {
			@Override
			public Object getParent() {
				return propertyProvider.getValue(record, parentPropertyName);
			}

			@Override
			public boolean isLazyChildren() {
				return false;
			}

			@Override
			public boolean isExpanded() {
				return false;
			}
		};
	}

	@Override
	public CalendarModel<CalendarEvent> getCalendarModel(Function<RECORD, AbstractCalendarEvent> eventProvider, String calendarFieldName) {
		if (eventProvider != null) {
			this.calendarEventProvider = eventProvider;
		}
		return calendarModel;
	}
}
