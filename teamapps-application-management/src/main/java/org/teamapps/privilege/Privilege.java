package org.teamapps.privilege;

import org.teamapps.icons.api.Icon;

public class Privilege {

	private final String name;
	private Icon icon;
	private String caption;
	private String description;

	public Privilege(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Icon getIcon() {
		return icon;
	}

	public Privilege setIcon(Icon icon) {
		this.icon = icon;
		return this;
	}

	public String getCaption() {
		return caption;
	}

	public Privilege setCaption(String caption) {
		this.caption = caption;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Privilege setDescription(String description) {
		this.description = description;
		return this;
	}
}
