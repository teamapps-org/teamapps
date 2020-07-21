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
package org.teamapps.ux.component.calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.common.format.Color;
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.dto.UiCalendar;
import org.teamapps.dto.UiCalendarEventClientRecord;
import org.teamapps.dto.UiCalendarEventRenderingStyle;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiWeekDay;
import org.teamapps.event.Event;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.cache.CacheManipulationHandle;
import org.teamapps.ux.cache.ClientRecordCache;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.i18n.TeamAppsDictionary;
import org.teamapps.ux.icon.TeamAppsIconBundle;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.teamapps.util.UiUtil.createUiColor;

public class Calendar<CEVENT extends CalendarEvent> extends AbstractComponent {

	private final Logger LOGGER = LoggerFactory.getLogger(Calendar.class);

	public final Event<EventClickedEventData<CEVENT>> onEventClicked = new Event<>();
	public final Event<EventMovedEventData<CEVENT>> onEventMoved = new Event<>();
	public final Event<DayClickedEventData> onDayClicked = new Event<>();
	public final Event<IntervalSelectedEventData> onIntervalSelected = new Event<>();
	public final Event<ViewChangedEventData> onViewChanged = new Event<>();
	public final Event<LocalDate> onMonthHeaderClicked = new Event<>();
	public final Event<WeeHeaderClickedEventData> onWeekHeaderClicked = new Event<>();
	public final Event<LocalDate> onDayHeaderClicked = new Event<>();

	private CalendarModel<CEVENT> model;
	private PropertyExtractor<CEVENT> propertyExtractor = new BeanPropertyExtractor<>();

	private ClientRecordCache<CEVENT, UiCalendarEventClientRecord> recordCache = new ClientRecordCache<>(this::createUiCalendarEventClientRecord);

	private CalendarEventTemplateDecider<CEVENT> templateDecider = //(calendarEvent, viewMode) -> null;
			createStaticTemplateDecider(
					BaseTemplate.LIST_ITEM_MEDIUM_ICON_TWO_LINES,
					BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE,
					BaseTemplate.LIST_ITEM_MEDIUM_ICON_TWO_LINES
			);

	private int templateIdCounter = 0;
	private final Map<Template, String> templateIdsByTemplate = new HashMap<>();

	private CalendarViewMode activeViewMode = CalendarViewMode.MONTH;
	private LocalDate displayedDate = LocalDate.now();
	private boolean showHeader = false;
	private boolean tableBorder = false;
	private boolean showWeekNumbers = true;
	private int businessHoursStart = 8;
	private int businessHoursEnd = 17;
	private DayOfWeek firstDayOfWeek = CurrentSessionContext.get().getConfiguration().getFirstDayOfWeek();
	private List<DayOfWeek> workingDays = java.util.Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
	private Color tableHeaderBackgroundColor;

	private Color defaultBackgroundColor = new Color(154, 204, 228);
	private Color defaultBorderColor = new Color(154, 204, 228);

	private int minYearViewMonthTileWidth = 175;
	private int maxYearViewMonthTileWidth = 0;

	private ZoneId timeZone = getSessionContext().getTimeZone();

	private boolean navigateOnHeaderClicks = true;

	private Consumer<Void> onCalendarDataChangedListener = (aVoid) -> {
		refreshEvents();
	};

	public Calendar() {
		this(null);
	}

	public Calendar(CalendarModel<CEVENT> model) {
		if (model != null) {
			setModel(model);
		}
	}

	private UiCalendarEventClientRecord createUiCalendarEventClientRecord(CEVENT calendarEvent) {
		Template timeGridTemplate = getTemplateForRecord(calendarEvent, CalendarViewMode.WEEK);
		Template dayGridTemplate = getTemplateForRecord(calendarEvent, CalendarViewMode.MONTH);
		Template monthGridTemplate = getTemplateForRecord(calendarEvent, CalendarViewMode.YEAR);

		HashSet<String> dataKeys = new HashSet<>();
		if (timeGridTemplate != null) {
			dataKeys.addAll(timeGridTemplate.getDataKeys());
		}
		if (dayGridTemplate != null) {
			dataKeys.addAll(dayGridTemplate.getDataKeys());
		}
		if (monthGridTemplate != null) {
			dataKeys.addAll(monthGridTemplate.getDataKeys());
		}

		Map<String, Object> values = propertyExtractor.getValues(calendarEvent, dataKeys);
		UiCalendarEventClientRecord uiRecord = new UiCalendarEventClientRecord();
		uiRecord.setValues(values);

		uiRecord.setTimeGridTemplateId(templateIdsByTemplate.get(timeGridTemplate));
		uiRecord.setDayGridTemplateId(templateIdsByTemplate.get(dayGridTemplate));
		uiRecord.setMonthGridTemplateId(templateIdsByTemplate.get(monthGridTemplate));

		uiRecord.setIcon(getSessionContext().resolveIcon(calendarEvent.getIcon()));
		uiRecord.setTitle(calendarEvent.getTitle());

		uiRecord.setStart(calendarEvent.getStart());
		uiRecord.setEnd(calendarEvent.getEnd());
		// uiRecord.setAsString(calendarEvent.getRecord() != null ? calendarEvent.getRecord().toString() : null);
		uiRecord.setAllDay(calendarEvent.isAllDay());
		uiRecord.setAllowDragOperations(calendarEvent.isAllowDragOperations());
		uiRecord.setBackgroundColor(calendarEvent.getBackgroundColor() != null ? createUiColor(calendarEvent.getBackgroundColor()) : null);
		uiRecord.setBorderColor(calendarEvent.getBorderColor() != null ? createUiColor(calendarEvent.getBorderColor()) : null);
		uiRecord.setRendering(calendarEvent.getRendering() != null ? calendarEvent.getRendering().toUiCalendarEventRenderingStyle() : UiCalendarEventRenderingStyle.DEFAULT);

		return uiRecord;
	}

	private Template getTemplateForRecord(CEVENT record, CalendarViewMode viewMode) {
		Template template = templateDecider.getTemplate(record, viewMode);
		if (template != null && !templateIdsByTemplate.containsKey(template)) {
			String id = "" + templateIdCounter++;
			this.templateIdsByTemplate.put(template, id);
			queueCommandIfRendered(() -> new UiCalendar.RegisterTemplateCommand(getId(), id, template.createUiTemplate()));
		}
		return template;
	}

	public void setModel(CalendarModel<CEVENT> model) {
		if (this.model != null) {
			unregisterModelEventListeners();
		}
		this.model = model;
		if (model != null) {
			model.onCalendarDataChanged().addListener(onCalendarDataChangedListener);
		}
		refreshEvents();
	}

	private void unregisterModelEventListeners() {
		this.model.onCalendarDataChanged().removeListener(onCalendarDataChangedListener);
	}

	@Override
	public UiComponent createUiComponent() {
		UiCalendar uiCalendar = new UiCalendar();
		mapAbstractUiComponentProperties(uiCalendar);
		uiCalendar.setActiveViewMode(activeViewMode.toUiCalendarViewMode());
		uiCalendar.setDisplayedDate(displayedDate.atStartOfDay(timeZone).toInstant().toEpochMilli());
		uiCalendar.setShowHeader(showHeader);
		uiCalendar.setTableBorder(tableBorder);
		uiCalendar.setShowWeekNumbers(showWeekNumbers);
		uiCalendar.setBusinessHoursStart(businessHoursStart);
		uiCalendar.setBusinessHoursEnd(businessHoursEnd);
		uiCalendar.setFirstDayOfWeek(firstDayOfWeek != null ? UiWeekDay.valueOf(firstDayOfWeek.name()) : null);
		uiCalendar.setWorkingDays(workingDays.stream().map(workingDay -> UiWeekDay.valueOf(workingDay.name())).collect(Collectors.toList()));
		uiCalendar.setTableHeaderBackgroundColor(tableHeaderBackgroundColor != null ? createUiColor(tableHeaderBackgroundColor) : null);
		uiCalendar.setNavigateOnHeaderClicks(navigateOnHeaderClicks);
		uiCalendar.setTimeZoneId(timeZone.getId());
		uiCalendar.setMinYearViewMonthTileWidth(minYearViewMonthTileWidth);
		uiCalendar.setMaxYearViewMonthTileWidth(maxYearViewMonthTileWidth);

		Instant queryStart = activeViewMode.getDisplayStart(displayedDate, firstDayOfWeek).atStartOfDay(timeZone).toInstant();
		Instant queryEnd = activeViewMode.getDisplayEnd(displayedDate, firstDayOfWeek).atStartOfDay(timeZone).toInstant();
		List<CEVENT> initialCalendarEvents = query(queryStart, queryEnd);
		CacheManipulationHandle<List<UiCalendarEventClientRecord>> cacheResponse = recordCache.replaceRecords(initialCalendarEvents);
		cacheResponse.commit();
		uiCalendar.setInitialData(cacheResponse.getAndClearResult());

		uiCalendar.setTemplates(templateIdsByTemplate.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> entry.getKey().createUiTemplate())));

		return uiCalendar;
	}

	private List<CEVENT> query(Instant queryStart, Instant queryEnd) {
		List<CEVENT> events;
		if (model != null) {
			events = model.getEventsForInterval(queryStart, queryEnd);
			LOGGER.debug("Query: " + queryStart + " - " + queryEnd + " --> events:" + events.size());
		} else {
			events = Collections.emptyList();
		}
		return events;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_CALENDAR_EVENT_CLICKED: {
				UiCalendar.EventClickedEvent clickEvent = (UiCalendar.EventClickedEvent) event;
				CEVENT calendarEvent = recordCache.getRecordByClientId(clickEvent.getEventId());
				if (calendarEvent != null) {
					onEventClicked.fire(new EventClickedEventData<>(calendarEvent, clickEvent.getIsDoubleClick()));
				}
				break;
			}
			case UI_CALENDAR_EVENT_MOVED: {
				UiCalendar.EventMovedEvent eventMovedEvent = (UiCalendar.EventMovedEvent) event;
				CEVENT calendarEvent = recordCache.getRecordByClientId(eventMovedEvent.getEventId());
				if (calendarEvent != null) {
					onEventMoved.fire(new EventMovedEventData<>(calendarEvent, Instant.ofEpochMilli(eventMovedEvent.getNewStart()), Instant.ofEpochMilli(eventMovedEvent.getNewEnd())));
				}

				break;
			}
			case UI_CALENDAR_DAY_CLICKED: {
				UiCalendar.DayClickedEvent dayClickedEvent = (UiCalendar.DayClickedEvent) event;
				onDayClicked.fire(new DayClickedEventData(timeZone, Instant.ofEpochMilli(dayClickedEvent.getDate()), dayClickedEvent.getIsDoubleClick()));
				break;
			}
			case UI_CALENDAR_INTERVAL_SELECTED: {
				UiCalendar.IntervalSelectedEvent selectionEvent = (UiCalendar.IntervalSelectedEvent) event;
				onIntervalSelected.fire(new IntervalSelectedEventData(timeZone, Instant.ofEpochMilli(selectionEvent.getStart()), Instant.ofEpochMilli(selectionEvent.getEnd()),
						selectionEvent.getAllDay()));
				break;
			}
			case UI_CALENDAR_VIEW_CHANGED: {
				UiCalendar.ViewChangedEvent viewChangedEvent = (UiCalendar.ViewChangedEvent) event;
				this.displayedDate = Instant.ofEpochMilli(viewChangedEvent.getMainIntervalStart()).atZone(timeZone).toLocalDate();
				this.activeViewMode = CalendarViewMode.valueOf(viewChangedEvent.getViewMode().name());
				onViewChanged.fire(new ViewChangedEventData(
						timeZone,
						activeViewMode,
						Instant.ofEpochMilli(viewChangedEvent.getMainIntervalStart()),
						Instant.ofEpochMilli(viewChangedEvent.getMainIntervalEnd()),
						Instant.ofEpochMilli(viewChangedEvent.getDisplayedIntervalStart()),
						Instant.ofEpochMilli(viewChangedEvent.getDisplayedIntervalEnd())
				));
				break;
			}
			case UI_CALENDAR_DATA_NEEDED: {
				UiCalendar.DataNeededEvent dataNeededEvent = (UiCalendar.DataNeededEvent) event;
				Instant queryStart = Instant.ofEpochMilli(dataNeededEvent.getRequestIntervalStart());
				Instant queryEnd = Instant.ofEpochMilli(dataNeededEvent.getRequestIntervalEnd());
				queryAndSendCalendarData(queryStart, queryEnd);
				break;
			}
			case UI_CALENDAR_MONTH_HEADER_CLICKED: {
				UiCalendar.MonthHeaderClickedEvent clickEvent = (UiCalendar.MonthHeaderClickedEvent) event;
				LocalDate startOfMonth = Instant.ofEpochMilli(clickEvent.getMonthStartDate()).atZone(timeZone).toLocalDate();
				onMonthHeaderClicked.fire(startOfMonth);
				break;
			}
			case UI_CALENDAR_WEEK_HEADER_CLICKED: {
				UiCalendar.WeekHeaderClickedEvent clickEvent = (UiCalendar.WeekHeaderClickedEvent) event;
				LocalDate startOfWeek = Instant.ofEpochMilli(clickEvent.getWeekStartDate()).atZone(timeZone).toLocalDate();
				onWeekHeaderClicked.fire(new WeeHeaderClickedEventData(timeZone, clickEvent.getYear(), clickEvent.getWeek(), startOfWeek));
				break;
			}
			case UI_CALENDAR_DAY_HEADER_CLICKED: {
				UiCalendar.DayHeaderClickedEvent clickEvent = (UiCalendar.DayHeaderClickedEvent) event;
				LocalDate date = Instant.ofEpochMilli(clickEvent.getDate()).atZone(timeZone).toLocalDate();
				onDayHeaderClicked.fire(date);
				break;
			}
		}
	}

	private void queryAndSendCalendarData(Instant queryStart, Instant queryEnd) {
		List<CEVENT> calendarEvents = query(queryStart, queryEnd);
		CacheManipulationHandle<List<UiCalendarEventClientRecord>> cacheResponse = recordCache.replaceRecords(calendarEvents);
		if (isRendered()) {
			getSessionContext().queueCommand(new UiCalendar.SetCalendarDataCommand(getId(), cacheResponse.getAndClearResult()), aVoid -> cacheResponse.commit());
		} else {
			cacheResponse.commit();
		}
	}

	public ToolbarButtonGroup createViewModesToolbarButtonGroup() {
		ToolbarButtonGroup group = new ToolbarButtonGroup();

		ToolbarButton yearViewButton = ToolbarButton.createSmall(getSessionContext().getIcon(TeamAppsIconBundle.YEAR.getKey()), getSessionContext().getLocalized(TeamAppsDictionary.YEAR.getKey()));
		yearViewButton.onClick.addListener(toolbarButtonClickEvent -> this.setActiveViewMode(CalendarViewMode.YEAR));
		group.addButton(yearViewButton);

		ToolbarButton monthViewButton = ToolbarButton.createSmall(getSessionContext().getIcon(TeamAppsIconBundle.MONTH.getKey()), getSessionContext().getLocalized(TeamAppsDictionary.MONTH.getKey()));
		monthViewButton.onClick.addListener(toolbarButtonClickEvent -> this.setActiveViewMode(CalendarViewMode.MONTH));
		group.addButton(monthViewButton);

		ToolbarButton weekViewButton = ToolbarButton.createSmall(getSessionContext().getIcon(TeamAppsIconBundle.WEEK.getKey()), getSessionContext().getLocalized(TeamAppsDictionary.WEEK.getKey()));
		weekViewButton.onClick.addListener(toolbarButtonClickEvent -> this.setActiveViewMode(CalendarViewMode.WEEK));
		group.addButton(weekViewButton);

		ToolbarButton dayViewButton = ToolbarButton.createSmall(getSessionContext().getIcon(TeamAppsIconBundle.DAY.getKey()), getSessionContext().getLocalized(TeamAppsDictionary.DAY.getKey()));
		dayViewButton.onClick.addListener(toolbarButtonClickEvent -> this.setActiveViewMode(CalendarViewMode.DAY));
		group.addButton(dayViewButton);

		return group;
	}

	public ToolbarButtonGroup createNavigationButtonGroup() {
		ToolbarButtonGroup group = new ToolbarButtonGroup();

		ToolbarButton forwardButton = ToolbarButton.createSmall(getSessionContext().getIcon(TeamAppsIconBundle.PREVIOUS.getKey()), getSessionContext().getLocalized(TeamAppsDictionary.PREVIOUS.getKey()));
		forwardButton.onClick.addListener(toolbarButtonClickEvent -> this.setDisplayedDate(activeViewMode.decrement(displayedDate)));
		group.addButton(forwardButton);

		ToolbarButton backButton = ToolbarButton.createSmall(getSessionContext().getIcon(TeamAppsIconBundle.NEXT.getKey()), getSessionContext().getLocalized(TeamAppsDictionary.NEXT.getKey()));
		backButton.onClick.addListener(toolbarButtonClickEvent -> this.setDisplayedDate(activeViewMode.increment(displayedDate)));
		group.addButton(backButton);

		return group;
	}

	public void refreshEvents() {
		Instant queryStart = activeViewMode.getDisplayStart(displayedDate, firstDayOfWeek).atStartOfDay(timeZone).toInstant();
		Instant queryEnd = activeViewMode.getDisplayEnd(displayedDate, firstDayOfWeek).atStartOfDay(timeZone).toInstant();
		queryAndSendCalendarData(queryStart, queryEnd);
	}

	public CalendarModel getModel() {
		return model;
	}

	public CalendarViewMode getActiveViewMode() {
		return activeViewMode;
	}

	public void setActiveViewMode(CalendarViewMode activeViewMode) {
		this.activeViewMode = activeViewMode;
		queueCommandIfRendered(() -> new UiCalendar.SetViewModeCommand(getId(), activeViewMode.toUiCalendarViewMode()));
		refreshEvents();
	}

	public LocalDate getDisplayedDate() {
		return displayedDate;
	}

	public void setDisplayedDate(LocalDate displayedDate) {
		this.displayedDate = displayedDate;
		queueCommandIfRendered(() -> new UiCalendar.SetDisplayedDateCommand(getId(), displayedDate.atStartOfDay(timeZone).toInstant().toEpochMilli()));
	}

	public void setDisplayDateOneUnitPrevious() {
		switch (getActiveViewMode()) {
			case YEAR:
				setDisplayedDate(getDisplayedDate().minusYears(1));
				break;
			case MONTH:
				setDisplayedDate(getDisplayedDate().minusMonths(1));
				break;
			case WEEK:
				setDisplayedDate(getDisplayedDate().minusWeeks(1));
				break;
			case DAY:
				setDisplayedDate(getDisplayedDate().minusDays(1));
				break;
		}
	}

	public void setDisplayDateOneUnitNext() {
		switch (getActiveViewMode()) {
			case YEAR:
				setDisplayedDate(getDisplayedDate().plusYears(1));
				break;
			case MONTH:
				setDisplayedDate(getDisplayedDate().plusMonths(1));
				break;
			case WEEK:
				setDisplayedDate(getDisplayedDate().plusWeeks(1));
				break;
			case DAY:
				setDisplayedDate(getDisplayedDate().plusDays(1));
				break;
		}
	}

	public boolean isShowHeader() {
		return showHeader;
	}

	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
		reRenderIfRendered();
	}

	public boolean isTableBorder() {
		return tableBorder;
	}

	public void setTableBorder(boolean tableBorder) {
		this.tableBorder = tableBorder;
		reRenderIfRendered();
	}

	public boolean isShowWeekNumbers() {
		return showWeekNumbers;
	}

	public void setShowWeekNumbers(boolean showWeekNumbers) {
		this.showWeekNumbers = showWeekNumbers;
		reRenderIfRendered();
	}

	public int getBusinessHoursStart() {
		return businessHoursStart;
	}

	public void setBusinessHoursStart(int businessHoursStart) {
		this.businessHoursStart = businessHoursStart;
		reRenderIfRendered();
	}

	public int getBusinessHoursEnd() {
		return businessHoursEnd;
	}

	public void setBusinessHoursEnd(int businessHoursEnd) {
		this.businessHoursEnd = businessHoursEnd;
		reRenderIfRendered();
	}

	public DayOfWeek getFirstDayOfWeek() {
		return firstDayOfWeek;
	}

	public void setFirstDayOfWeek(DayOfWeek firstDayOfWeek) {
		this.firstDayOfWeek = firstDayOfWeek;
		reRenderIfRendered();
	}

	public List<DayOfWeek> getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(List<DayOfWeek> workingDays) {
		this.workingDays = workingDays;
		reRenderIfRendered();
	}

	public Color getTableHeaderBackgroundColor() {
		return tableHeaderBackgroundColor;
	}

	public void setTableHeaderBackgroundColor(Color tableHeaderBackgroundColor) {
		this.tableHeaderBackgroundColor = tableHeaderBackgroundColor;
		reRenderIfRendered();
	}

	public Color getDefaultBackgroundColor() {
		return defaultBackgroundColor;
	}

	public void setDefaultBackgroundColor(Color defaultBackgroundColor) {
		this.defaultBackgroundColor = defaultBackgroundColor;
	}

	public Color getDefaultBorderColor() {
		return defaultBorderColor;
	}

	public void setDefaultBorderColor(Color defaultBorderColor) {
		this.defaultBorderColor = defaultBorderColor;
	}

	public PropertyExtractor<CEVENT> getPropertyExtractor() {
		return propertyExtractor;
	}

	public void setPropertyExtractor(PropertyExtractor<CEVENT> propertyExtractor) {
		this.propertyExtractor = propertyExtractor;
	}

	public CalendarEventTemplateDecider<CEVENT> getTemplateDecider() {
		return templateDecider;
	}

	public void setTemplateDecider(CalendarEventTemplateDecider<CEVENT> templateDecider) {
		this.templateDecider = templateDecider;
	}

	public void setTemplates(Template timeGridTemplate, Template dayGridTemplate, Template monthGridTemplate) {
		this.templateDecider = createStaticTemplateDecider(timeGridTemplate, dayGridTemplate, monthGridTemplate);
	}

	private CalendarEventTemplateDecider<CEVENT> createStaticTemplateDecider(Template timeGridTemplate, Template dayGridTemplate, Template monthGridTemplate) {
		return (record, viewMode) -> {
			switch (viewMode) {
				case DAY:
				case WEEK:
					return timeGridTemplate;
				case MONTH:
					return dayGridTemplate;
				case YEAR:
					return monthGridTemplate;
				default:
					throw new IllegalArgumentException("Unknown view mode: " + viewMode);
			}
		};
	}

	public ZoneId getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
		queueCommandIfRendered(() -> new UiCalendar.SetTimeZoneIdCommand(getId(), timeZone.getId()));
	}

	public int getMinYearViewMonthTileWidth() {
		return minYearViewMonthTileWidth;
	}

	public void setMinYearViewMonthTileWidth(int minYearViewMonthTileWidth) {
		this.minYearViewMonthTileWidth = minYearViewMonthTileWidth;
		reRenderIfRendered();
	}

	public int getMaxYearViewMonthTileWidth() {
		return maxYearViewMonthTileWidth;
	}

	public void setMaxYearViewMonthTileWidth(int maxYearViewMonthTileWidth) {
		this.maxYearViewMonthTileWidth = maxYearViewMonthTileWidth;
		reRenderIfRendered();
	}

	public boolean isNavigateOnHeaderClicks() {
		return navigateOnHeaderClicks;
	}

	public void setNavigateOnHeaderClicks(boolean navigateOnHeaderClicks) {
		this.navigateOnHeaderClicks = navigateOnHeaderClicks;
	}
}
