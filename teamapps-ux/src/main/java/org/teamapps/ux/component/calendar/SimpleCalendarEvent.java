package org.teamapps.ux.component.calendar;

import org.teamapps.icons.api.Icon;

import java.time.Instant;

public class SimpleCalendarEvent<PAYLOAD> extends AbstractCalendarEvent {

	private Icon icon;
	private String image;
	private String caption;
	private String badge;

	private PAYLOAD payload;

	public SimpleCalendarEvent(Instant start, Instant end, Icon icon, String caption) {
		super(start, end);
		this.icon = icon;
		this.caption = caption;
	}

	public SimpleCalendarEvent(long start, long end, Icon icon, String caption) {
		super(start, end);
		this.icon = icon;
		this.caption = caption;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTitle() {
		return caption;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getBadge() {
		return badge;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}

	public PAYLOAD getPayload() {
		return payload;
	}

	public void setPayload(PAYLOAD payload) {
		this.payload = payload;
	}
}
