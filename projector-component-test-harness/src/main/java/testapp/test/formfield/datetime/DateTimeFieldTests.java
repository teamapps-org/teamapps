

package testapp.test.formfield.datetime;

import org.teamapps.projector.session.config.DateTimeFormatDescriptor;
import org.teamapps.projector.session.config.FullLongMediumShortType;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class DateTimeFieldTests {

	public static final List<FormatEntry> DATE_FORMAT_ENTRIES = Arrays.asList(
			new FormatEntry(DateTimeFormatDescriptor.forDate(FullLongMediumShortType.SHORT), "SHORT"),
			new FormatEntry(DateTimeFormatDescriptor.forDate(FullLongMediumShortType.MEDIUM), "MEDIUM"),
			new FormatEntry(DateTimeFormatDescriptor.forDate(FullLongMediumShortType.LONG), "LONG"),
			new FormatEntry(DateTimeFormatDescriptor.forDate(FullLongMediumShortType.FULL), "FULL")
	);

	public static final List<FormatEntry> TIME_FORMAT_ENTRIES = Arrays.asList(
			new FormatEntry(DateTimeFormatDescriptor.forTime(FullLongMediumShortType.SHORT), "SHORT"),
			new FormatEntry(DateTimeFormatDescriptor.forTime(FullLongMediumShortType.MEDIUM), "MEDIUM"),
			new FormatEntry(DateTimeFormatDescriptor.forTime(FullLongMediumShortType.LONG), "LONG"),
			new FormatEntry(DateTimeFormatDescriptor.forTime(FullLongMediumShortType.FULL), "FULL")
	);

	public static final List<TimeZoneEntry> ZONE_IDS = ZoneId.getAvailableZoneIds().stream()
			.map(TimeZoneEntry::new)
			.collect(Collectors.toList());

}
