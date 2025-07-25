package org.teamapps.projector.component.calendar;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.session.CurrentSessionContext;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

public class CalendarBuilder<CEVENT extends CalendarEvent> {

	private CalendarModel<CEVENT> model;
	private PropertyProvider<CEVENT> propertyProvider = new BeanPropertyExtractor<>();
	private CalendarEventTemplateDecider<CEVENT> templateDecider = //(calendarEvent, viewMode) -> null;
			Calendar.createStaticTemplateDecider(
					BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES,
					BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE,
					BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES
			);
	private CalendarViewMode activeViewMode = CalendarViewMode.MONTH;
	private LocalDate displayedDate = LocalDate.now();
	private Color defaultBackgroundColor = new RgbaColor(154, 204, 228);
	private Color defaultBorderColor = new RgbaColor(154, 204, 228);
	private ZoneId timeZone = SessionContext.current().getTimeZone();
	private boolean navigateOnHeaderClicks = true;

	// immutable
	private boolean showHeader = false;
	private boolean tableBorder = false;
	private boolean showWeekNumbers = true;
	private int businessHoursStart = 8;
	private int businessHoursEnd = 17;
	private DayOfWeek firstDayOfWeek = CurrentSessionContext.get().getFirstDayOfWeek();
	private List<DayOfWeek> workingDays = java.util.Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
	private Color tableHeaderBackgroundColor;
	private Locale locale = SessionContext.current().getLocale();
	private int minYearViewMonthTileWidth = 175;
	private int maxYearViewMonthTileWidth = 0;

	CalendarBuilder() {
	}

	public Calendar<CEVENT> build() {
		Calendar<CEVENT> calendar = new Calendar<>(
				showHeader                   ,
				tableBorder                  ,
				showWeekNumbers              ,
				businessHoursStart           ,
				businessHoursEnd             ,
				firstDayOfWeek               ,
				workingDays                  ,
				tableHeaderBackgroundColor   ,
				locale                       ,
				minYearViewMonthTileWidth    ,
				maxYearViewMonthTileWidth
		);
		calendar.setModel(model);
		calendar.setPropertyProvider(propertyProvider);
		calendar.setTemplateDecider(templateDecider);
		calendar.setActiveViewMode(activeViewMode);
		calendar.setDisplayedDate(displayedDate);
		calendar.setDefaultBackgroundColor(defaultBackgroundColor);
		calendar.setDefaultBorderColor(defaultBorderColor);
		calendar.setTimeZone(timeZone);
		calendar.setNavigateOnHeaderClicks(navigateOnHeaderClicks);
		return calendar;
	}

	public CalendarBuilder<CEVENT> withModel(CalendarModel<CEVENT> model) {
		this.model = model;
		return this;
	}

	public CalendarBuilder<CEVENT> withPropertyProvider(PropertyProvider<CEVENT> propertyProvider) {
		this.propertyProvider = propertyProvider;
		return this;
	}

	public CalendarBuilder<CEVENT> withTemplateDecider(CalendarEventTemplateDecider<CEVENT> templateDecider) {
		this.templateDecider = templateDecider;
		return this;
	}

	public CalendarBuilder<CEVENT> withActiveViewMode(CalendarViewMode activeViewMode) {
		this.activeViewMode = activeViewMode;
		return this;
	}

	public CalendarBuilder<CEVENT> withDisplayedDate(LocalDate displayedDate) {
		this.displayedDate = displayedDate;
		return this;
	}

	public CalendarBuilder<CEVENT> withShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
		return this;
	}

	public CalendarBuilder<CEVENT> withTableBorder(boolean tableBorder) {
		this.tableBorder = tableBorder;
		return this;
	}

	public CalendarBuilder<CEVENT> withShowWeekNumbers(boolean showWeekNumbers) {
		this.showWeekNumbers = showWeekNumbers;
		return this;
	}

	public CalendarBuilder<CEVENT> withBusinessHoursStart(int businessHoursStart) {
		this.businessHoursStart = businessHoursStart;
		return this;
	}

	public CalendarBuilder<CEVENT> withBusinessHoursEnd(int businessHoursEnd) {
		this.businessHoursEnd = businessHoursEnd;
		return this;
	}

	public CalendarBuilder<CEVENT> withFirstDayOfWeek(DayOfWeek firstDayOfWeek) {
		this.firstDayOfWeek = firstDayOfWeek;
		return this;
	}

	public CalendarBuilder<CEVENT> withWorkingDays(List<DayOfWeek> workingDays) {
		this.workingDays = workingDays;
		return this;
	}

	public CalendarBuilder<CEVENT> withTableHeaderBackgroundColor(Color tableHeaderBackgroundColor) {
		this.tableHeaderBackgroundColor = tableHeaderBackgroundColor;
		return this;
	}

	public CalendarBuilder<CEVENT> withDefaultBackgroundColor(Color defaultBackgroundColor) {
		this.defaultBackgroundColor = defaultBackgroundColor;
		return this;
	}

	public CalendarBuilder<CEVENT> withDefaultBorderColor(Color defaultBorderColor) {
		this.defaultBorderColor = defaultBorderColor;
		return this;
	}

	public CalendarBuilder<CEVENT> withMinYearViewMonthTileWidth(int minYearViewMonthTileWidth) {
		this.minYearViewMonthTileWidth = minYearViewMonthTileWidth;
		return this;
	}

	public CalendarBuilder<CEVENT> withMaxYearViewMonthTileWidth(int maxYearViewMonthTileWidth) {
		this.maxYearViewMonthTileWidth = maxYearViewMonthTileWidth;
		return this;
	}

	public CalendarBuilder<CEVENT> withLocale(Locale locale) {
		this.locale = locale;
		return this;
	}

	public CalendarBuilder<CEVENT> withTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
		return this;
	}

	public CalendarBuilder<CEVENT> withNavigateOnHeaderClicks(boolean navigateOnHeaderClicks) {
		this.navigateOnHeaderClicks = navigateOnHeaderClicks;
		return this;
	}

}
