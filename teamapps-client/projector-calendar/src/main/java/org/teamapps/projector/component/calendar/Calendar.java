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
package org.teamapps.projector.component.calendar;

import com.ibm.icu.util.ULocale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.commons.event.Disposable;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientrecordcache.CacheManipulationHandle;
import org.teamapps.projector.clientrecordcache.ClientRecordCache;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.essential.toolbar.ToolbarButton;
import org.teamapps.projector.component.essential.toolbar.ToolbarButtonGroup;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.i18n.TeamAppsTranslationKeys;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

import java.lang.invoke.MethodHandles;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = CalendarComponentLibrary.class)
public class Calendar<CEVENT extends CalendarEvent> extends AbstractComponent implements DtoCalendarEventHandler {

	private final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final DtoCalendarClientObjectChannel clientObjectChannel = new DtoCalendarClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<EventClickedEventData<CEVENT>> onEventClicked = new ProjectorEvent<>(clientObjectChannel::toggleEventClickedEvent);
	public final ProjectorEvent<EventMovedEventData<CEVENT>> onEventMoved = new ProjectorEvent<>(clientObjectChannel::toggleEventMovedEvent);
	public final ProjectorEvent<DayClickedEventData> onDayClicked = new ProjectorEvent<>(clientObjectChannel::toggleDayClickedEvent);
	public final ProjectorEvent<IntervalSelectedEventData> onIntervalSelected = new ProjectorEvent<>(clientObjectChannel::toggleIntervalSelectedEvent);
	public final ProjectorEvent<ViewChangedEventData> onViewChanged = new ProjectorEvent<>(clientObjectChannel::toggleViewChangedEvent);
	public final ProjectorEvent<LocalDate> onMonthHeaderClicked = new ProjectorEvent<>(clientObjectChannel::toggleMonthHeaderClickedEvent);
	public final ProjectorEvent<WeekHeaderClickedEventData> onWeekHeaderClicked = new ProjectorEvent<>(clientObjectChannel::toggleWeekHeaderClickedEvent);
	public final ProjectorEvent<LocalDate> onDayHeaderClicked = new ProjectorEvent<>(clientObjectChannel::toggleDayHeaderClickedEvent);

	private CalendarModel<CEVENT> model;
	private PropertyProvider<CEVENT> propertyProvider = new BeanPropertyExtractor<>();

	private final ClientRecordCache<CEVENT, DtoCalendarEventClientRecord> recordCache = new ClientRecordCache<>(this::createUiCalendarEventClientRecord);

	private CalendarEventTemplateDecider<CEVENT> templateDecider = //(calendarEvent, viewMode) -> null;
			createStaticTemplateDecider(
					BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES,
					BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE,
					BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES
			);

	private CalendarViewMode activeViewMode = CalendarViewMode.MONTH;
	private LocalDate displayedDate = LocalDate.now();
	private final boolean showHeader;
	private final boolean tableBorder;
	private final boolean showWeekNumbers;
	private final int businessHoursStart;
	private final int businessHoursEnd;
	private final DayOfWeek firstDayOfWeek;
	private final List<DayOfWeek> workingDays;
	private final Color tableHeaderBackgroundColor;

	private Color defaultBackgroundColor = new RgbaColor(154, 204, 228);
	private Color defaultBorderColor = new RgbaColor(154, 204, 228);

	private final int minYearViewMonthTileWidth;
	private final int maxYearViewMonthTileWidth;

	private ULocale locale = getSessionContext().getULocale();
	private ZoneId timeZone = getSessionContext().getTimeZone();

	private boolean navigateOnHeaderClicks = true;

	private Disposable onCalendarDataChangedListener;

	Calendar(
			boolean showHeader,
			boolean tableBorder,
			boolean showWeekNumbers,
			int businessHoursStart,
			int businessHoursEnd,
			DayOfWeek firstDayOfWeek,
			List<DayOfWeek> workingDays,
			Color tableHeaderBackgroundColor,
			ULocale locale,
			int minYearViewMonthTileWidth,
			int maxYearViewMonthTileWidth
	) {
		clientObjectChannel.toggleDataNeededEvent(true);

		this.showHeader = showHeader;
		this.tableBorder = tableBorder;
		this.showWeekNumbers = showWeekNumbers;
		this.businessHoursStart = businessHoursStart;
		this.businessHoursEnd = businessHoursEnd;
		this.firstDayOfWeek = firstDayOfWeek;
		this.workingDays = workingDays;
		this.tableHeaderBackgroundColor = tableHeaderBackgroundColor;
		this.locale = locale;
		this.minYearViewMonthTileWidth = minYearViewMonthTileWidth;
		this.maxYearViewMonthTileWidth = maxYearViewMonthTileWidth;
	}

	public static <CEVENT extends CalendarEvent> CalendarBuilder<CEVENT> builder() {
		return new CalendarBuilder<>();
	}

	private DtoCalendarEventClientRecord createUiCalendarEventClientRecord(CEVENT calendarEvent) {
		Template timeGridTemplate = getTemplateForRecord(calendarEvent, CalendarViewMode.WEEK);
		Template dayGridTemplate = getTemplateForRecord(calendarEvent, CalendarViewMode.MONTH);
		Template monthGridTemplate = getTemplateForRecord(calendarEvent, CalendarViewMode.YEAR);

		HashSet<String> propertyNames = new HashSet<>();
		if (timeGridTemplate != null) {
			propertyNames.addAll(timeGridTemplate.getPropertyNames());
		}
		if (dayGridTemplate != null) {
			propertyNames.addAll(dayGridTemplate.getPropertyNames());
		}
		if (monthGridTemplate != null) {
			propertyNames.addAll(monthGridTemplate.getPropertyNames());
		}

		Map<String, Object> values = propertyProvider.getValues(calendarEvent, propertyNames);
		DtoCalendarEventClientRecord uiRecord = new DtoCalendarEventClientRecord();
		uiRecord.setValues(values);

		uiRecord.setTimeGridTemplate(timeGridTemplate != null ? timeGridTemplate : null);
		uiRecord.setDayGridTemplate(dayGridTemplate != null ? dayGridTemplate : null);
		uiRecord.setMonthGridTemplate(monthGridTemplate != null ? monthGridTemplate : null);

		uiRecord.setIcon(getSessionContext().resolveIcon(calendarEvent.getIcon()));
		uiRecord.setTitle(calendarEvent.getTitle());

		uiRecord.setStart(calendarEvent.getStart());
		uiRecord.setEnd(calendarEvent.getEnd());
		// uiRecord.setAsString(calendarEvent.getRecord() != null ? calendarEvent.getRecord().toString() : null);
		uiRecord.setAllDay(calendarEvent.isAllDay());
		uiRecord.setAllowDragOperations(calendarEvent.isAllowDragOperations());
		uiRecord.setBackgroundColor(calendarEvent.getBackgroundColor() != null ? calendarEvent.getBackgroundColor().toHtmlColorString() : null);
		uiRecord.setBorderColor(calendarEvent.getBorderColor() != null ? calendarEvent.getBorderColor().toHtmlColorString() : null);
		uiRecord.setRendering(calendarEvent.getRendering() != null ? calendarEvent.getRendering() : CalendarEventRenderingStyle.DEFAULT);

		return uiRecord;
	}

	private Template getTemplateForRecord(CEVENT record, CalendarViewMode viewMode) {
		return templateDecider.getTemplate(record, viewMode);
	}

	public void setModel(CalendarModel<CEVENT> model) {
		if (this.model != null) {
			unregisterModelEventListeners();
		}
		this.model = model;
		if (model != null) {
			onCalendarDataChangedListener = model.onCalendarDataChanged().addListener((aVoid) -> refreshEvents());
		}
		refreshEvents();
	}

	private void unregisterModelEventListeners() {
		if (onCalendarDataChangedListener != null) {
			onCalendarDataChangedListener.dispose();
		}
	}

	@Override
	public DtoComponent createConfig() {
		DtoCalendar uiCalendar = new DtoCalendar();
		mapAbstractUiComponentProperties(uiCalendar);
		uiCalendar.setActiveViewMode(activeViewMode.toUiCalendarViewMode());
		uiCalendar.setDisplayedDate(displayedDate.atStartOfDay(timeZone).toInstant().toEpochMilli());
		uiCalendar.setShowHeader(showHeader);
		uiCalendar.setTableBorder(tableBorder);
		uiCalendar.setShowWeekNumbers(showWeekNumbers);
		uiCalendar.setBusinessHoursStart(businessHoursStart);
		uiCalendar.setBusinessHoursEnd(businessHoursEnd);
		uiCalendar.setFirstDayOfWeek(DtoWeekDay.valueOf(firstDayOfWeek.name()));
		uiCalendar.setWorkingDays(workingDays.stream().map(workingDay -> DtoWeekDay.valueOf(workingDay.name())).collect(Collectors.toList()));
		uiCalendar.setTableHeaderBackgroundColor(tableHeaderBackgroundColor != null ? tableHeaderBackgroundColor.toHtmlColorString() : null);
		uiCalendar.setNavigateOnHeaderClicks(navigateOnHeaderClicks);
		uiCalendar.setLocale(locale.toLanguageTag());
		uiCalendar.setTimeZoneId(timeZone.getId());
		uiCalendar.setMinYearViewMonthTileWidth(minYearViewMonthTileWidth);
		uiCalendar.setMaxYearViewMonthTileWidth(maxYearViewMonthTileWidth);

		Instant queryStart = activeViewMode.getDisplayStart(displayedDate, firstDayOfWeek).atStartOfDay(timeZone).toInstant();
		Instant queryEnd = activeViewMode.getDisplayEnd(displayedDate, firstDayOfWeek).atStartOfDay(timeZone).toInstant();
		List<CEVENT> initialCalendarEvents = query(queryStart, queryEnd);
		CacheManipulationHandle<List<DtoCalendarEventClientRecord>> cacheResponse = recordCache.replaceRecords(initialCalendarEvents);
		cacheResponse.commit();
		uiCalendar.setInitialData(cacheResponse.getAndClearResult());

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
	public void handleEventClicked(DtoCalendar.EventClickedEventWrapper event) {
		CEVENT calendarEvent = recordCache.getRecordByClientId(event.getEventId());
		if (calendarEvent != null) {
			onEventClicked.fire(new EventClickedEventData<>(calendarEvent, event.isDoubleClick()));
		}
	}

	@Override
	public void handleEventMoved(DtoCalendar.EventMovedEventWrapper event) {
		CEVENT calendarEvent = recordCache.getRecordByClientId(event.getEventId());
		if (calendarEvent != null) {
			onEventMoved.fire(new EventMovedEventData<>(calendarEvent, Instant.ofEpochMilli(event.getNewStart()), Instant.ofEpochMilli(event.getNewEnd())));
		}
	}

	@Override
	public void handleDayClicked(DtoCalendar.DayClickedEventWrapper event) {
		onDayClicked.fire(new DayClickedEventData(timeZone, Instant.ofEpochMilli(event.getDate()), event.isDoubleClick()));
	}

	@Override
	public void handleIntervalSelected(DtoCalendar.IntervalSelectedEventWrapper event) {
		onIntervalSelected.fire(new IntervalSelectedEventData(timeZone, Instant.ofEpochMilli(event.getStart()), Instant.ofEpochMilli(event.getEnd()),
				event.isAllDay()));
	}

	@Override
	public void handleDayHeaderClicked(long dateMillis) {
		LocalDate date = Instant.ofEpochMilli(dateMillis).atZone(timeZone).toLocalDate();
		onDayHeaderClicked.fire(date);
	}

	@Override
	public void handleWeekHeaderClicked(DtoCalendar.WeekHeaderClickedEventWrapper event) {
		LocalDate startOfWeek = Instant.ofEpochMilli(event.getWeekStartDate()).atZone(timeZone).toLocalDate();
		onWeekHeaderClicked.fire(new WeekHeaderClickedEventData(timeZone, event.getYear(), event.getWeek(), startOfWeek));
	}

	@Override
	public void handleMonthHeaderClicked(DtoCalendar.MonthHeaderClickedEventWrapper event) {
		LocalDate startOfMonth = Instant.ofEpochMilli(event.getMonthStartDate()).atZone(timeZone).toLocalDate();
		onMonthHeaderClicked.fire(startOfMonth);
	}

	@Override
	public void handleViewChanged(DtoCalendar.ViewChangedEventWrapper event) {
		this.displayedDate = Instant.ofEpochMilli(event.getMainIntervalStart()).atZone(timeZone).toLocalDate();
		this.activeViewMode = CalendarViewMode.valueOf(event.getViewMode().name());
		onViewChanged.fire(new ViewChangedEventData(
				timeZone,
				activeViewMode,
				Instant.ofEpochMilli(event.getMainIntervalStart()),
				Instant.ofEpochMilli(event.getMainIntervalEnd()),
				Instant.ofEpochMilli(event.getDisplayedIntervalStart()),
				Instant.ofEpochMilli(event.getDisplayedIntervalEnd())
		));
	}

	@Override
	public void handleDataNeeded(DtoCalendar.DataNeededEventWrapper event) {
		Instant queryStart = Instant.ofEpochMilli(event.getRequestIntervalStart());
		Instant queryEnd = Instant.ofEpochMilli(event.getRequestIntervalEnd());
		queryAndSendCalendarData(queryStart, queryEnd);
	}

	private void queryAndSendCalendarData(Instant queryStart, Instant queryEnd) {
		List<CEVENT> calendarEvents = query(queryStart, queryEnd);
		CacheManipulationHandle<List<DtoCalendarEventClientRecord>> cacheResponse = recordCache.replaceRecords(calendarEvents);
		boolean sent = clientObjectChannel.setCalendarData(cacheResponse.getAndClearResult(), aVoid -> cacheResponse.commit());
		if (!sent) {
			cacheResponse.commit();
		}
	}

	public ToolbarButtonGroup createViewModesToolbarButtonGroup() {
		ToolbarButtonGroup group = new ToolbarButtonGroup();

		ToolbarButton yearViewButton = ToolbarButton.createSmall(MaterialIcon.EVENT_NOTE, getSessionContext().getLocalized(TeamAppsTranslationKeys.YEAR.getKey()));
		yearViewButton.onClick.addListener(toolbarButtonClickEvent -> this.setActiveViewMode(CalendarViewMode.YEAR));
		group.addButton(yearViewButton);

		ToolbarButton monthViewButton = ToolbarButton.createSmall(MaterialIcon.DATE_RANGE, getSessionContext().getLocalized(TeamAppsTranslationKeys.MONTH.getKey()));
		monthViewButton.onClick.addListener(toolbarButtonClickEvent -> this.setActiveViewMode(CalendarViewMode.MONTH));
		group.addButton(monthViewButton);

		ToolbarButton weekViewButton = ToolbarButton.createSmall(MaterialIcon.VIEW_WEEK, getSessionContext().getLocalized(TeamAppsTranslationKeys.WEEK.getKey()));
		weekViewButton.onClick.addListener(toolbarButtonClickEvent -> this.setActiveViewMode(CalendarViewMode.WEEK));
		group.addButton(weekViewButton);

		ToolbarButton dayViewButton = ToolbarButton.createSmall(MaterialIcon.VIEW_DAY, getSessionContext().getLocalized(TeamAppsTranslationKeys.DAY.getKey()));
		dayViewButton.onClick.addListener(toolbarButtonClickEvent -> this.setActiveViewMode(CalendarViewMode.DAY));
		group.addButton(dayViewButton);

		return group;
	}

	public ToolbarButtonGroup createNavigationButtonGroup() {
		ToolbarButtonGroup group = new ToolbarButtonGroup();

		ToolbarButton forwardButton = ToolbarButton.createSmall(MaterialIcon.NAVIGATE_BEFORE,
				getSessionContext().getLocalized(TeamAppsTranslationKeys.PREVIOUS.getKey()));
		forwardButton.onClick.addListener(toolbarButtonClickEvent -> this.setDisplayedDate(activeViewMode.decrement(displayedDate)));
		group.addButton(forwardButton);

		ToolbarButton backButton = ToolbarButton.createSmall(MaterialIcon.NAVIGATE_NEXT, getSessionContext().getLocalized(TeamAppsTranslationKeys.NEXT.getKey()));
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
		clientObjectChannel.setViewMode(activeViewMode.toUiCalendarViewMode());
		refreshEvents();
	}

	public LocalDate getDisplayedDate() {
		return displayedDate;
	}

	public void setDisplayedDate(LocalDate displayedDate) {
		this.displayedDate = displayedDate;
		clientObjectChannel.setDisplayedDate(displayedDate.atStartOfDay(timeZone).toInstant().toEpochMilli());
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

	public boolean isTableBorder() {
		return tableBorder;
	}

	public boolean isShowWeekNumbers() {
		return showWeekNumbers;
	}

	public int getBusinessHoursStart() {
		return businessHoursStart;
	}

	public int getBusinessHoursEnd() {
		return businessHoursEnd;
	}

	public DayOfWeek getFirstDayOfWeek() {
		return firstDayOfWeek;
	}

	public List<DayOfWeek> getWorkingDays() {
		return workingDays;
	}

	public Color getTableHeaderBackgroundColor() {
		return tableHeaderBackgroundColor;
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

	public PropertyProvider<CEVENT> getPropertyProvider() {
		return propertyProvider;
	}

	public void setPropertyProvider(PropertyProvider<CEVENT> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	public void setPropertyExtractor(PropertyExtractor<CEVENT> propertyExtractor) {
		this.setPropertyProvider(propertyExtractor);
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

	static <CEVENT extends CalendarEvent> CalendarEventTemplateDecider<CEVENT> createStaticTemplateDecider(Template timeGridTemplate, Template dayGridTemplate, Template monthGridTemplate) {
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


	public Locale getLocale() {
		return locale.toLocale();
	}

	public ULocale getULocale() {
		return locale;
	}

	public ZoneId getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
		clientObjectChannel.setTimeZoneId(timeZone.getId());
	}

	public int getMinYearViewMonthTileWidth() {
		return minYearViewMonthTileWidth;
	}

	public int getMaxYearViewMonthTileWidth() {
		return maxYearViewMonthTileWidth;
	}

	public boolean isNavigateOnHeaderClicks() {
		return navigateOnHeaderClicks;
	}

	public void setNavigateOnHeaderClicks(boolean navigateOnHeaderClicks) {
		this.navigateOnHeaderClicks = navigateOnHeaderClicks;
	}
}
