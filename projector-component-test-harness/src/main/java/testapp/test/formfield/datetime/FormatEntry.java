

package testapp.test.formfield.datetime;


import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.session.config.DateTimeFormatDescriptor;

public class FormatEntry {
	private DateTimeFormatDescriptor format;
	private String caption;
	private Icon icon = MaterialIcon.HELP;

	public FormatEntry() {
	}

	public FormatEntry(DateTimeFormatDescriptor format, String caption) {
		this.format = format;
		this.caption = caption;
	}

	public DateTimeFormatDescriptor getFormat() {
		return format;
	}

	public void setFormat(DateTimeFormatDescriptor format) {
		this.format = format;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	@Override
	public String toString() {
		return caption;
	}
}