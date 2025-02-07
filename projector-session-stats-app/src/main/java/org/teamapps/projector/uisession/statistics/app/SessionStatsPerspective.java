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
package org.teamapps.projector.uisession.statistics.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.composite.CompositeIcon;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.field.*;
import org.teamapps.projector.component.core.flexcontainer.FlexSizeUnit;
import org.teamapps.projector.component.core.flexcontainer.FlexSizingPolicy;
import org.teamapps.projector.component.core.flexcontainer.VerticalLayout;
import org.teamapps.projector.component.core.toolbar.ToolbarButton;
import org.teamapps.projector.component.core.toolbar.ToolbarButtonGroup;
import org.teamapps.projector.component.infinitescroll.table.AbstractTableModel;
import org.teamapps.projector.component.infinitescroll.table.SortDirection;
import org.teamapps.projector.component.infinitescroll.table.Table;
import org.teamapps.projector.component.trivial.datetime.InstantDateTimeField;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.projector.session.uisession.UiSessionState;
import org.teamapps.projector.session.uisession.stats.CountStats;
import org.teamapps.projector.session.uisession.stats.SumStats;
import org.teamapps.projector.session.uisession.stats.UiSessionStatistics;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SessionStatsPerspective {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Table<SessionStatsTableRecord> table;
	private final VerticalLayout detailVerticalLayout;
	private final ToolbarButtonGroup detailsToolbarButtonGroup;
	private CountStatsTableModel commandStatsTableModel;
	private CountStatsTableModel eventStatsTableModel;
	private CountStatsTableModel commandResultStatsTableModel;
	private CountStatsTableModel queryStatsTableModel;
	private CountStatsTableModel queryResultStatsTableModel;

	public SessionStatsPerspective(SessionStatsSharedBaseTableModel baseTableModel) {
		table = createMasterTable(baseTableModel);
		detailVerticalLayout = createDetailVerticalLayout();
		detailsToolbarButtonGroup = createDetailsToolbarButtonGroup();
	}

	private Table<SessionStatsTableRecord> createMasterTable(SessionStatsSharedBaseTableModel baseTableModel) {
		Table<SessionStatsTableRecord> table = Table.<SessionStatsTableRecord>builder().build();
		table.addColumn("startTime", "Start Time", new InstantDateTimeField()).setValueExtractor(record -> Instant.ofEpochMilli(record.getStatistics().getStartTime()));
		table.addColumn("endTime", "End Time", new InstantDateTimeField()).setValueExtractor(record -> record.getStatistics().getEndTime() > 0 ? Instant.ofEpochMilli(record.getStatistics().getEndTime()) : null);
		table.addColumn("sessionId", "ID", new TextField()).setDefaultWidth(50)
				.setValueExtractor(record -> record.getStatistics().getSessionId().toString());
		table.addColumn("name", "Name", new TextField()).setValueExtractor(record -> record.getStatistics().getName());
		table.addColumn("state", "State", new TemplateField<>(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE)).setDefaultWidth(120)
				.setValueExtractor(record -> {
					Icon icon;
					UiSessionState state = record.getStatistics().getState();
					if (state == UiSessionState.ACTIVE) {
						icon = MaterialIcon.CHECK;
					} else if (state == UiSessionState.NEARLY_INACTIVE) {
						icon = CompositeIcon.of(MaterialIcon.CHECK, MaterialIcon.SCHEDULE);
					}  else if (state == UiSessionState.INACTIVE) {
						icon = MaterialIcon.SCHEDULE;
					} else if (state == UiSessionState.CLOSED) {
						icon = MaterialIcon.CANCEL;
					} else {
						icon = MaterialIcon.HELP_OUTLINE;
					}
					return new BaseTemplateRecord<>(icon, state.name());
				});
		table.addColumn("bufferSize", "Cmd Buf Size", new NumberField(0)).setDefaultWidth(90)
				.setValueExtractor(record -> record.getClientBackPressureInfo() != null ? record.getClientBackPressureInfo().unconsumedCommandsCount() : null);
		table.addColumn("readyToReceive", "Ready To Receive", new CheckBox()).setDefaultWidth(40)
				.setValueExtractor(record -> record.getClientBackPressureInfo() != null && record.getClientBackPressureInfo().remainingRequestedCommands() > 0);

		addSumStatsColumns(table, "sentData", "Data Sent", record -> record.getStatistics().getSentDataStats());
		addSumStatsColumns(table, "receivedData", "Data Recvd.", record -> record.getStatistics().getReceivedDataStats());

		addCountStatsColumns(table, "command", "Commands", record -> record.getStatistics().getCommandStats());
		addCountStatsColumns(table, "commandResult", "CmdResults", record -> record.getStatistics().getCommandResultStats());
		addCountStatsColumns(table, "event", "Events", record -> record.getStatistics().getEventStats());
		addCountStatsColumns(table, "query", "Queries", record -> record.getStatistics().getQueryStats());
		addCountStatsColumns(table, "queryResult", "QuResults", record -> record.getStatistics().getQueryResultStats());

		table.setModel(new StatsTableModel(baseTableModel));

		table.onSingleRowSelected.addListener((record) -> {
			commandStatsTableModel.setStats(record.getStatistics());
			eventStatsTableModel.setStats(record.getStatistics());
			commandResultStatsTableModel.setStats(record.getStatistics());
			queryStatsTableModel.setStats(record.getStatistics());
			queryResultStatsTableModel.setStats(record.getStatistics());
		});

		return table;
	}

	private void addCountStatsColumns(Table<SessionStatsTableRecord> table, String propertyNamePrefix, String displayNameInfix, Function<SessionStatsTableRecord, CountStats> countStatsExtractor) {
		table.addColumn(propertyNamePrefix + "Total", displayNameInfix, new NumberField(0)).setDefaultWidth(110)
				.setValueExtractor(record -> countStatsExtractor.apply(record).getCount());
		table.addColumn(propertyNamePrefix + "LastMinute", displayNameInfix + " (1m)", new NumberField(0)).setDefaultWidth(110)
				.setValueExtractor(record -> countStatsExtractor.apply(record).getCountLastMinute());
//		table.addColumn(propertyNamePrefix + "Last10Seconds", displayNameInfix + "(10s)", new NumberField(0)).setDefaultWidth(120)
//				.setValueExtractor(record -> countStatsExtractor.apply(record).getCountLast10Seconds());
	}

	private void addSumStatsColumns(Table<SessionStatsTableRecord> table, String propertyNamePrefix, String displayNameInfix, Function<SessionStatsTableRecord, SumStats> sumStatsExtractor) {
		table.addColumn(propertyNamePrefix + "Total", displayNameInfix, new NumberField(0)).setDefaultWidth(110)
				.setValueExtractor(record -> sumStatsExtractor.apply(record).getSum());
		table.addColumn(propertyNamePrefix + "LastMinute", displayNameInfix + " (1m)", new NumberField(0)).setDefaultWidth(110)
				.setValueExtractor(record -> sumStatsExtractor.apply(record).getSumLastMinute());
//		table.addColumn(propertyNamePrefix + "Last10Seconds", displayNameInfix + "(10s)", new NumberField(0)).setDefaultWidth(120)
//				.setValueExtractor(record -> sumStatsExtractor.apply(record).getSumLast10Seconds());
	}

	public Table<SessionStatsTableRecord> getTable() {
		return table;
	}

	private VerticalLayout createDetailVerticalLayout() {
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setCssStyle("overflow", "auto");
		verticalLayout.addComponent(new Label("Commands"));
		commandStatsTableModel = new CountStatsTableModel(UiSessionStatistics::getCommandStats);
		verticalLayout.addComponent(createCountStatsTable(commandStatsTableModel), new FlexSizingPolicy(300, FlexSizeUnit.PIXEL, 0, 0));
		verticalLayout.addComponent(new Label("Events"));
		eventStatsTableModel = new CountStatsTableModel(UiSessionStatistics::getEventStats);
		verticalLayout.addComponent(createCountStatsTable(eventStatsTableModel), new FlexSizingPolicy(300, FlexSizeUnit.PIXEL, 0, 0));
		verticalLayout.addComponent(new Label("CommandResults"));
		commandResultStatsTableModel = new CountStatsTableModel(UiSessionStatistics::getCommandResultStats);
		verticalLayout.addComponent(createCountStatsTable(commandResultStatsTableModel), new FlexSizingPolicy(300, FlexSizeUnit.PIXEL, 0, 0));
		verticalLayout.addComponent(new Label("Queries"));
		queryStatsTableModel = new CountStatsTableModel(UiSessionStatistics::getQueryStats);
		verticalLayout.addComponent(createCountStatsTable(queryStatsTableModel), new FlexSizingPolicy(300, FlexSizeUnit.PIXEL, 0, 0));
		verticalLayout.addComponent(new Label("QueryResults"));
		queryResultStatsTableModel = new CountStatsTableModel(UiSessionStatistics::getQueryResultStats);
		verticalLayout.addComponent(createCountStatsTable(queryResultStatsTableModel), new FlexSizingPolicy(300, FlexSizeUnit.PIXEL, 0, 0));
		return verticalLayout;
	}

	public Component getDetailVerticalLayout() {
		return detailVerticalLayout;
	}

	private ToolbarButtonGroup createDetailsToolbarButtonGroup() {
		ToolbarButtonGroup toolbarButtonGroup = new ToolbarButtonGroup();
		toolbarButtonGroup.addButton(ToolbarButton.createSmall(MaterialIcon.REFRESH, "Refresh")).onClick.addListener(() -> {
			commandStatsTableModel.refresh();
			eventStatsTableModel.refresh();
			commandResultStatsTableModel.refresh();
			queryStatsTableModel.refresh();
			queryResultStatsTableModel.refresh();
		});
		return toolbarButtonGroup;
	}

	public ToolbarButtonGroup getDetailsToolbarButtonGroup() {
		return detailsToolbarButtonGroup;
	}

	private static class CountStatEntry {
		private final String clazz;
		private final long count;

		public CountStatEntry(String clazz, long count) {
			this.clazz = clazz;
			this.count = count;
		}
	}

	private Table<CountStatEntry> createCountStatsTable(CountStatsTableModel model) {
		Table<CountStatEntry> table = Table.<CountStatEntry>builder()
				.withForceFitWidth(true)
				.build();
		table.addColumn("className", null, "Class", new TextField(), 400).setValueExtractor(entry -> entry.clazz);
		table.addColumn("count", null, "Count", new NumberField(0), 80).setMinWidth(80).setMaxWidth(120).setValueExtractor(entry -> entry.count);
		table.setModel(model);
		return table;
	}

	private static class StatsTableModel extends AbstractTableModel<SessionStatsTableRecord> {
		private final SessionStatsSharedBaseTableModel baseTableModel;

		public StatsTableModel(SessionStatsSharedBaseTableModel baseTableModel) {
			this.baseTableModel = baseTableModel;
			baseTableModel.onUpdated.addListener(() -> {
				if (SessionContext.current().getClientBackPressureInfo().unconsumedCommandsCount() < 100) {
					this.onAllDataChanged.fire();
				} else {
					LOGGER.info("Not sending updates due to high amount of unconsumed commands!");
				}
			});
		}

		@Override
		public int getCount() {
			return baseTableModel.getRecords().size();
		}

		@Override
		public List<SessionStatsTableRecord> getRecords(int startIndex, int length) {
			Comparator<SessionStatsTableRecord> comparator = (o1, o2) -> 0;
			if (sorting != null) {
				switch (sorting.getFieldName()) {
					case "startTime":
						comparator = Comparator.comparing(record -> record.getStatistics().getStartTime());
						break;
					case "endTime":
						comparator = Comparator.comparing(record -> record.getStatistics().getEndTime());
						break;
					case "sessionId":
						comparator = Comparator.comparing(record -> record.getStatistics().getSessionId().toString());
						break;
					case "name":
						comparator = Comparator.comparing(record -> record.getStatistics().getName());
						break;
					case "state":
						comparator = Comparator.comparing(record -> record.getStatistics().getState());
						break;
					case "bufferSize":
						comparator = Comparator.comparing(record -> record.getClientBackPressureInfo() != null ? record.getClientBackPressureInfo().unconsumedCommandsCount() : -1);
						break;
					case "readyToReceive":
						comparator = Comparator.comparing(record -> record.getClientBackPressureInfo() != null && record.getClientBackPressureInfo().remainingRequestedCommands() > 0);
						break;
					case "sentDataTotal":
						comparator = Comparator.comparing(record -> record.getStatistics().getSentDataStats().getSum());
						break;
					case "sentDataLastMinute":
						comparator = Comparator.comparing(record -> record.getStatistics().getSentDataStats().getSumLastMinute());
						break;
					case "receivedDataTotal":
						comparator = Comparator.comparing(record -> record.getStatistics().getReceivedDataStats().getSum());
						break;
					case "receivedDataLastMinute":
						comparator = Comparator.comparing(record -> record.getStatistics().getReceivedDataStats().getSumLastMinute());
						break;
					case "commandTotal":
						comparator = Comparator.comparing(record -> record.getStatistics().getCommandStats().getCount());
						break;
					case "commandLastMinute":
						comparator = Comparator.comparing(record -> record.getStatistics().getCommandStats().getCountLastMinute());
						break;
					case "commandResultTotal":
						comparator = Comparator.comparing(record -> record.getStatistics().getCommandResultStats().getCount());
						break;
					case "commandResultLastMinute":
						comparator = Comparator.comparing(record -> record.getStatistics().getCommandResultStats().getCountLastMinute());
						break;
					case "eventTotal":
						comparator = Comparator.comparing(record -> record.getStatistics().getEventStats().getCount());
						break;
					case "eventLastMinute":
						comparator = Comparator.comparing(record -> record.getStatistics().getEventStats().getCountLastMinute());
						break;
					case "queryTotal":
						comparator = Comparator.comparing(record -> record.getStatistics().getQueryStats().getCount());
						break;
					case "queryLastMinute":
						comparator = Comparator.comparing(record -> record.getStatistics().getQueryStats().getCountLastMinute());
						break;
					case "queryResultTotal":
						comparator = Comparator.comparing(record -> record.getStatistics().getQueryResultStats().getCount());
						break;
					case "queryResultLastMinute":
						comparator = Comparator.comparing(record -> record.getStatistics().getQueryResultStats().getCountLastMinute());
						break;

				}
				if (sorting.getSortDirection() == SortDirection.DESC) {
					comparator = comparator.reversed();
				}
			}
			return baseTableModel.getRecords().stream()
					.sorted(comparator)
					.skip(startIndex)
					.limit(length)
					.collect(Collectors.toList());
		}
	}

	private class CountStatsTableModel extends AbstractTableModel<CountStatEntry> {

		private final Function<UiSessionStatistics, CountStats> countStatExtractor;
		private UiSessionStatistics stats;

		private CountStatsTableModel(Function<UiSessionStatistics, CountStats> countStatExtractor) {
			this.countStatExtractor = countStatExtractor;
		}

		public void setStats(UiSessionStatistics stats) {
			this.stats = stats;
			onAllDataChanged.fire();
		}

		@Override
		public int getCount() {
			if (stats == null) {
				return 0;
			}
			return countStatExtractor.apply(stats).getCountByTypeId().size();
		}

		@Override
		public List<CountStatEntry> getRecords(int startIndex, int length) {
			if (stats == null) {
				return List.of();
			}
			Comparator<CountStatEntry> comparator = (o1, o2) -> 0;
			if (sorting != null) {
				comparator = Objects.equals(sorting.getFieldName(), "className") ? Comparator.comparing(e -> e.clazz) : Comparator.comparing(e -> e.count);
				if (sorting.getSortDirection() == SortDirection.DESC || sorting.getFieldName() == null) {
					comparator = comparator.reversed();
				}
			}
			return countStatExtractor.apply(stats).getCountByTypeId().entrySet().stream()
					.map(e -> new CountStatEntry(e.getKey(), e.getValue()))
					.sorted(comparator)
					.skip(startIndex)
					.limit(length)
					.collect(Collectors.toList());
		}

		public void refresh() {
			onAllDataChanged.fire();
		}
	}
}
