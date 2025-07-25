

package testapp.test.formfield.datetime;

import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.material.MaterialIcon;

public class TimeZoneEntry {
	private String timeZoneId;
	private Icon icon = MaterialIcon.HELP;

	public TimeZoneEntry() {
	}

	public TimeZoneEntry(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public String getCaption() {
		return timeZoneId;
	}

	public String getId() {
		return timeZoneId;
	}
}