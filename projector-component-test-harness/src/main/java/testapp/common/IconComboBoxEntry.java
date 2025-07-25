

package testapp.common;

import org.teamapps.projector.icon.Icon;

public class IconComboBoxEntry {

	private org.teamapps.projector.icon.Icon icon;
	private String caption;

	public IconComboBoxEntry(Icon icon, String caption) {
		this.icon = icon;
		this.caption = caption;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	public String toString() {
		return caption;
	}
}
