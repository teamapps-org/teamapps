

package testapp.common;

import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.Template;

import java.util.concurrent.atomic.AtomicInteger;

public class TemplateComboBoxEntry {

	private static final AtomicInteger idCount = new AtomicInteger();
	
	private final int id = idCount.incrementAndGet();
	private final Icon icon;
	private final String caption;
	private final Template template;

	public TemplateComboBoxEntry (String caption, Template template) {
		this(MaterialIcon.HELP, caption, template);
	}

	public TemplateComboBoxEntry(Icon icon, String caption, Template template) {
		this.icon = icon;
		this.caption = caption;
		this.template = template;
	}

	public int getId() {
		return id;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getCaption() {
		return caption;
	}

	public Template getTemplate() {
		return template;
	}

	@Override
	public String toString() {
		return caption;
	}
}
