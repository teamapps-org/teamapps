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
package org.teamapps.projector.components.calendar;

import com.ibm.icu.util.ULocale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.event.Disposable;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.components.calendar.dto.DtoCalendar;
import org.teamapps.projector.components.calendar.dto.DtoCalendarEventClientRecord;
import org.teamapps.projector.components.calendar.dto.DtoCalendarEventRenderingStyle;
import org.teamapps.projector.components.calendar.dto.DtoWeekDay;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.dto.DtoComponent;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.i18n.TeamAppsTranslationKeys;
import org.teamapps.projector.session.CurrentSessionContext;
import org.teamapps.projector.template.Template;
import org.teamapps.ux.cache.record.legacy.CacheManipulationHandle;
import org.teamapps.ux.cache.record.legacy.ClientRecordCache;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;

import java.lang.invoke.MethodHandles;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = CalendarComponentLibrary.class)
public class Calendar<CEVENT extends CalendarEvent> extends AbstractComponent {

	private final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public final ProjectorEvent<EventClickedEventData<CEVENT>> onEventClicked = createProjectorEventBoundToUiEvent(DtoCalendar.EventClickedEvent.TYPE_ID);
	public final ProjectorEvent<EventMovedEventData<CEVENT>> onEventMoved = createProjectorEventBoundToUiEvent(DtoCalendar.EventMovedEvent.TYPE_ID);
	public final ProjectorEvent<DayClickedEventData> onDayClicked = createProjectorEventBoundToUiEvent(DtoCalendar.DayClickedEvent.TYPE_ID);
	public final ProjectorEvent<IntervalSelectedEventData> onIntervalSelected = createProjectorEventBoundToUiEvent(DtoCalendar.IntervalSelectedEvent.TYPE_ID);
	public final ProjectorEvent<ViewChangedEventData> onViewChanged = createProjectorEventBoundToUiEvent(DtoCalendar.ViewChangedEvent.TYPE_ID);
	public final ProjectorEvent<LocalDate> onMonthHeaderClicked = createProjectorEventBoundToUiEvent(DtoCalendar.MonthHeaderClickedEvent.TYPE_ID);
	public final ProjectorEvent<WeekHeaderClickedEventData> onWeekHeaderClicked = createProjectorEventBoundToUiEvent(DtoCalendar.WeekHeaderClickedEvent.TYPE_ID);
	public final ProjectorEvent<LocalDate> onDayHeaderClicked = createProjectorEventBoundToUiEvent(DtoCalendar.DayHeaderClickedEvent.TYPE_ID);

	private CalendarModel<CEVENT> model;
	private PropertyProvider<CEVENT> propertyProvider = new BeanPropertyExtractor<>();

	private final ClientRecordCache<CEVENT, DtoCalendarEventClientRecord> recordCache = new ClientRecordCache<>(this::createUiCalendarEventClientRecord);

	private CalendarEventTemplateDecider<CEVENT> templateDecider = //(calendarEvent, viewMode) -> null;
			createStaticTemplateDecider(
					BaseTemplate.LIST_ITEM_MEDIUM_ICON_TWO_LINES,
					BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE,
					BaseTemplate.LIST_ITEM_MEDIUM_ICON_TWO_LINES
			);

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

	private Color defaultBackgroundColor = new RgbaColor(154, 204, 228);
	private Color defaultBorderColor = new RgbaColor(154, 204, 228);

	private int minYearViewMonthTileWidth = 175;
	private int maxYearViewMonthTileWidth = 0;

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
		toggleEventListening(DtoCalendar.DataNeededEvent.TYPE_ID, true);

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

		uiRecord.setTimeGridTemplate(timeGridTemplate != null ? timeGridTemplate.createClientReference() : null);
		uiRecord.setDayGridTemplate(dayGridTemplate != null ? dayGridTemplate.createClientReference() : null);
		uiRecord.setMonthGridTemplate(monthGridTemplate != null ? monthGridTemplate.createClientReference() : null);

		uiRecord.setIcon(getSessionContext().resolveIcon(calendarEvent.getIcon()));
		uiRecord.setTitle(calendarEvent.getTitle());

		uiRecord.setStart(calendarEvent.getStart());
		uiRecord.setEnd(calendarEvent.getEnd());
		// uiRecord.setAsString(calendarEvent.getRecord() != null ? calendarEvent.getRecord().toString() : null);
		uiRecord.setAllDay(calendarEvent.isAllDay());
		uiRecord.setAllowDragOperations(calendarEvent.isAllowDragOperations());
		uiRecord.setBackgroundColor(calendarEvent.getBackgroundColor() != null ? calendarEvent.getBackgroundColor().toHtmlColorString() : null);
		uiRecord.setBorderColor(calendarEvent.getBorderColor() != null ? calendarEvent.getBorderColor().toHtmlColorString() : null);
		uiRecord.setRendering(calendarEvent.getRendering() != null ? calendarEvent.getRendering().toUiCalendarEventRenderingStyle() : DtoCalendarEventRenderingStyle.DEFAULT);

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
	public void handleUiEvent(String name, JsonWrapper params) {
		switch (event.getTypeId()) {
			case DtoCalendar.EventClickedEvent.TYPE_ID -> {
				var clickEvent = event.as(DtoCalendar.EventClickedEventWrapper.class);
				CEVENT calendarEvent = recordCache.getRecordByClientId(clickEvent.getEventId());
				if (calendarEvent != null) {
					onEventClicked.fire(new EventClickedEventData<>(calendarEvent, clickEvent.getIsDoubleClick()));
				}
			}
			case DtoCalendar.EventMovedEvent.TYPE_ID -> {
				var eventMovedEvent = event.as(DtoCalendar.EventMovedEventWrapper.class);
				CEVENT calendarEvent = recordCache.getRecordByClientId(eventMovedEvent.getEventId());
				if (calendarEvent != null) {
					onEventMoved.fire(new EventMovedEventData<>(calendarEvent, Instant.ofEpochMilli(eventMovedEvent.getNewStart()), Instant.ofEpochMilli(eventMovedEvent.getNewEnd())));
				}
			}
			case DtoCalendar.DayClickedEvent.TYPE_ID -> {
				var dayClickedEvent = event.as(DtoCalendar.DayClickedEventWrapper.class);
				onDayClicked.fire(new DayClickedEventData(timeZone, Instant.ofEpochMilli(dayClickedEvent.getDate()), dayClickedEvent.getIsDoubleClick()));

			}
			case DtoCalendar.IntervalSelectedEvent.TYPE_ID -> {
				var selectionEvent = event.as(DtoCalendar.IntervalSelectedEventWrapper.class);
				onIntervalSelected.fire(new IntervalSelectedEventData(timeZone, Instant.ofEpochMilli(selectionEvent.getStart()), Instant.ofEpochMilli(selectionEvent.getEnd()),
						selectionEvent.getAllDay()));
			}
			case DtoCalendar.ViewChangedEvent.TYPE_ID -> {
				var viewChangedEvent = event.as(DtoCalendar.ViewChangedEventWrapper.class);
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
			}
			case DtoCalendar.DataNeededEvent.TYPE_ID -> {
				var dataNeededEvent = event.as(DtoCalendar.DataNeededEventWrapper.class);
				Instant queryStart = Instant.ofEpochMilli(dataNeededEvent.getRequestIntervalStart());
				Instant queryEnd = Instant.ofEpochMilli(dataNeededEvent.getRequestIntervalEnd());
				queryAndSendCalendarData(queryStart, queryEnd);
			}
			case DtoCalendar.MonthHeaderClickedEvent.TYPE_ID -> {
				var clickEvent = event.as(DtoCalendar.MonthHeaderClickedEventWrapper.class);
				LocalDate startOfMonth = Instant.ofEpochMilli(clickEvent.getMonthStartDate()).atZone(timeZone).toLocalDate();
				onMonthHeaderClicked.fire(startOfMonth);
			}
			case DtoCalendar.WeekHeaderClickedEvent.TYPE_ID -> {
				var clickEvent = event.as(DtoCalendar.WeekHeaderClickedEventWrapper.class);
				LocalDate startOfWeek = Instant.ofEpochMilli(clickEvent.getWeekStartDate()).atZone(timeZone).toLocalDate();
				onWeekHeaderClicked.fire(new WeekHeaderClickedEventData(timeZone, clickEvent.getYear(), clickEvent.getWeek(), startOfWeek));
			}
			case DtoCalendar.DayHeaderClickedEvent.TYPE_ID -> {
				var clickEvent = event.as(DtoCalendar.DayHeaderClickedEventWrapper.class);
				LocalDate date = Instant.ofEpochMilli(clickEvent.getDate()).atZone(timeZone).toLocalDate();
				onDayHeaderClicked.fire(date);
			}
		}
	}

	private void queryAndSendCalendarData(Instant queryStart, Instant queryEnd) {
		List<CEVENT> calendarEvents = query(queryStart, queryEnd);
		CacheManipulationHandle<List<DtoCalendarEventClientRecord>> cacheResponse = recordCache.replaceRecords(calendarEvents);
		if (isRendered()) {
			final DtoCalendar.SetCalendarDataCommand setCalendarDataCommand = new DtoCalendar.SetCalendarDataCommand(cacheResponse.getAndClearResult());
			getSessionContext().sendCommandIfRendered(this, () -> setCalendarDataCommand.get(), aVoid -> cacheResponse.commit());
		} else {
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
		java.util.Calendar.this.getClientObjectChannel().sendCommandIfRendered(new DtoCalendar.SetViewModeCommand(activeViewMode.toUiCalendarViewMode()), null);
		refreshEvents();
	}

	public LocalDate getDisplayedDate() {
		return displayedDate;
	}

	public void setDisplayedDate(LocalDate displayedDate) {
		this.displayedDate = displayedDate;
		java.util.Calendar.this.getClientObjectChannel().sendCommandIfRendered(new DtoCalendar.SetDisplayedDateCommand(displayedDate.atStartOfDay(timeZone).toInstant().toEpochMilli()), null);
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
		java.util.Calendar.this.getClientObjectChannel().sendCommandIfRendered(new DtoCalendar.SetTimeZoneIdCommand(timeZone.getId()), null);
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
