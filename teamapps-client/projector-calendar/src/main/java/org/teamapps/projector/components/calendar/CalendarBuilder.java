package org.teamapps.projector.components.calendar;

import com.ibm.icu.util.ULocale;
import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.session.CurrentSessionContext;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

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
	private ULocale locale = SessionContext.current().getULocale();
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

	public void withModel(CalendarModel<CEVENT> model) {
		this.model = model;
	}

	public void withPropertyProvider(PropertyProvider<CEVENT> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	public void withTemplateDecider(CalendarEventTemplateDecider<CEVENT> templateDecider) {
		this.templateDecider = templateDecider;
	}

	public void withActiveViewMode(CalendarViewMode activeViewMode) {
		this.activeViewMode = activeViewMode;
	}

	public void withDisplayedDate(LocalDate displayedDate) {
		this.displayedDate = displayedDate;
	}

	public void withShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}

	public void withTableBorder(boolean tableBorder) {
		this.tableBorder = tableBorder;
	}

	public void withShowWeekNumbers(boolean showWeekNumbers) {
		this.showWeekNumbers = showWeekNumbers;
	}

	public void withBusinessHoursStart(int businessHoursStart) {
		this.businessHoursStart = businessHoursStart;
	}

	public void withBusinessHoursEnd(int businessHoursEnd) {
		this.businessHoursEnd = businessHoursEnd;
	}

	public void withFirstDayOfWeek(DayOfWeek firstDayOfWeek) {
		this.firstDayOfWeek = firstDayOfWeek;
	}

	public void withWorkingDays(List<DayOfWeek> workingDays) {
		this.workingDays = workingDays;
	}

	public void withTableHeaderBackgroundColor(Color tableHeaderBackgroundColor) {
		this.tableHeaderBackgroundColor = tableHeaderBackgroundColor;
	}

	public void withDefaultBackgroundColor(Color defaultBackgroundColor) {
		this.defaultBackgroundColor = defaultBackgroundColor;
	}

	public void withDefaultBorderColor(Color defaultBorderColor) {
		this.defaultBorderColor = defaultBorderColor;
	}

	public void withMinYearViewMonthTileWidth(int minYearViewMonthTileWidth) {
		this.minYearViewMonthTileWidth = minYearViewMonthTileWidth;
	}

	public void withMaxYearViewMonthTileWidth(int maxYearViewMonthTileWidth) {
		this.maxYearViewMonthTileWidth = maxYearViewMonthTileWidth;
	}

	public void withLocale(ULocale locale) {
		this.locale = locale;
	}

	public void withTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
	}

	public void withNavigateOnHeaderClicks(boolean navigateOnHeaderClicks) {
		this.navigateOnHeaderClicks = navigateOnHeaderClicks;
	}

}
