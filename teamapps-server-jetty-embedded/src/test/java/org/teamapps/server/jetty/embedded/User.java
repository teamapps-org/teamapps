package org.teamapps.server.jetty.embedded;

import org.teamapps.common.format.Color;
import org.teamapps.icons.Icon;

public class User {
	private Icon<?, ?> icon;
	private String firstName;
	private Color color;

	User(Icon<?, ?> icon, String firstName, Color color) {
		this.icon = icon;
		this.firstName = firstName;
		this.color = color;
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public String getFirstName() {
		return firstName;
	}

	public Color getColor() {
		return color;
	}

	public void setIcon(Icon<?, ?> icon) {
		this.icon = icon;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}