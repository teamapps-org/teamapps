

package testapp.test;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.calendar.*;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.treecomponents.datetime.LocalDateField;
import org.teamapps.projector.icon.material.MaterialIcon;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.util.DemoDataGenerator;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalendarTest extends AbstractComponentTest<Calendar<SimpleCalendarEvent<Void>>> {

	private static final List<String> TIME_ZONE_ID_STRINGS = Arrays.asList(
			"UTC",
			"Etc/GMT-12",
			"Etc/GMT+12",
			"Australia/Darwin",
			"Australia/Sydney",
			"America/Argentina/Buenos_Aires",
			"Africa/Cairo",
			"America/Anchorage",
			"America/Sao_Paulo",
			"Asia/Dhaka",
			"Africa/Harare",
			"America/St_Johns",
			"America/Chicago",
			"Asia/Shanghai",
			"Africa/Addis_Ababa",
			"Europe/Berlin",
			"America/Indiana/Indianapolis",
			"Asia/Kolkata",
			"Asia/Tokyo",
			"Pacific/Apia",
			"Asia/Yerevan",
			"Pacific/Auckland",
			"Asia/Karachi",
			"America/Phoenix",
			"America/Puerto_Rico",
			"America/Los_Angeles",
			"Pacific/Guadalcanal",
			"Asia/Ho_Chi_Minh"
	);
	private ComboBox<CalendarViewMode> activeViewModeComboBox;
	private LocalDateField displayedDateField;

	public CalendarTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		ConfigurationFieldGenerator<Calendar> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());
		activeViewModeComboBox = fieldGenerator.createComboBoxForEnum("activeViewMode");
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "View mode", activeViewModeComboBox);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Time zone", fieldGenerator.createComboBoxForList("timeZone", TIME_ZONE_ID_STRINGS, cal -> cal.getTimeZone().getId(), (cal, zoneIdString) -> getComponent().setTimeZone(ZoneId.of(zoneIdString))));
		displayedDateField = fieldGenerator.createLocalDateField("displayedDate", false);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Displayed date", displayedDateField);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show header", fieldGenerator.createCheckBox("showHeader"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Draw table borders", fieldGenerator.createCheckBox("tableBorder"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show week numbers", fieldGenerator.createCheckBox("showWeekNumbers"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Business hours (start)", fieldGenerator.createNumberField("businessHoursStart", 0, 0, 24, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Business hours (end)", fieldGenerator.createNumberField("businessHoursEnd", 0, 0, 24, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "First day of week", fieldGenerator.createComboBoxForEnum("firstDayOfWeek"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Working days", fieldGenerator.createTagComboBoxForEnum("workingDays", DayOfWeek.class));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Table header background color", fieldGenerator.createColorPicker("tableHeaderBackgroundColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Min year view month tile width", fieldGenerator.createNumberField("minYearViewMonthTileWidth", 0, 0, 500, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Max year view month tile width", fieldGenerator.createNumberField("maxYearViewMonthTileWidth", 0, 0, 500, false));
	}

	@Override
	public Calendar createComponent() {
		SimpleCalendarModel<Void> model = new SimpleCalendarModel<>();
		Calendar<SimpleCalendarEvent<Void>> calendar = Calendar.<SimpleCalendarEvent<Void>>builder()
				.withModel(model)
				.build();

		calendar.onViewChanged.addListener(viewChangedEvent -> {
			activeViewModeComboBox.setValue(viewChangedEvent.getViewMode());
			displayedDateField.setValue(viewChangedEvent.getMainIntervalStartAsLocalDate());
		});

		model.setEvents(generateCalendarEvents());

		return calendar;
	}

	private List<SimpleCalendarEvent<Void>> generateCalendarEvents() {
		ArrayList<SimpleCalendarEvent<Void>> events = new ArrayList<>();

		LocalDateTime startLocalTime = LocalDate.now().atStartOfDay();

		for (int i = 0; i < 24; i++) {
			ZonedDateTime start = startLocalTime.plusHours(i).atZone(ZoneOffset.UTC);
			ZonedDateTime end = startLocalTime.plusHours(i+1).atZone(ZoneOffset.UTC);

			RgbaColor baseColor = DemoDataGenerator.randomColor();
			RgbaColor borderColor = baseColor.withLuminance(.5f);
			Color backgroundColor = borderColor.withLuminance(.85f);
			String title = "" + i;

			SimpleCalendarEvent<Void> event = new SimpleCalendarEvent<>(start.toInstant(), end.toInstant(), DemoDataGenerator.randomIcon(), title);
			event.setBorderColor(borderColor);
			event.setBackgroundColor(backgroundColor);
			event.setRendering(CalendarEventRenderingStyle.DEFAULT);
			event.setAllDay(false);
			event.setAllowDragOperations(true);

			events.add(event);
		}
		for (int i = 0; i < 366; i++) {
			if (i % 3 == 0) {
				continue;
			}
			ZonedDateTime start = startLocalTime.plusDays(i * 3).atZone(ZoneOffset.UTC);
			ZonedDateTime end = start.plus(i % 3 * 45, ChronoUnit.MINUTES);

			RgbaColor baseColor = DemoDataGenerator.randomColor();
			RgbaColor borderColor = baseColor.withLuminance(.5f);
			Color backgroundColor = borderColor.withLuminance(.85f);
			String title = DemoDataGenerator.randomWords(2, true);

			SimpleCalendarEvent<Void> event = new SimpleCalendarEvent<>(start.toInstant(), end.toInstant(), DemoDataGenerator.randomIcon(), title);
			event.setBorderColor(borderColor);
			event.setBackgroundColor(backgroundColor);
			event.setRendering(CalendarEventRenderingStyle.DEFAULT);
			event.setAllDay(true);
			event.setAllowDragOperations(true);

			events.add(event);
		}
		return events;
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/Calendar.html";
	}

}
