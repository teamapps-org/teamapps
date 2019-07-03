package org.teamapps.ux.component.calendar;

import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.template.BaseTemplateRecord;

import java.time.Instant;

public class SimpleCalendarEvent<PAYLOAD> extends CalendarEvent<BaseTemplateRecord<PAYLOAD>> {

	public SimpleCalendarEvent(Instant start, Instant end, BaseTemplateRecord<PAYLOAD> record) {
		super(start, end, record);
	}

	public SimpleCalendarEvent(long start, long end, BaseTemplateRecord<PAYLOAD> record) {
		super(start, end, record);
	}

	public static <PAYLOAD> SimpleCalendarEvent<PAYLOAD> create(Instant start, Instant end, Icon icon, String  caption, String description) {
		BaseTemplateRecord<PAYLOAD> record = new BaseTemplateRecord<PAYLOAD>(icon, caption, description);
		return new SimpleCalendarEvent<>(start, end, record);
	}
	public static <PAYLOAD> SimpleCalendarEvent<PAYLOAD> create(long start, long end, Icon icon, String caption, String description) {
		BaseTemplateRecord<PAYLOAD> record = new BaseTemplateRecord<>(icon, caption, description);
		return new SimpleCalendarEvent<>(start, end, record);
	}

	public Icon getIcon() {
		return getRecord().getIcon();
	}

	public BaseTemplateRecord<PAYLOAD> setIcon(Icon icon) {
		return getRecord().setIcon(icon);
	}

	public String getImage() {
		return getRecord().getImage();
	}

	public BaseTemplateRecord<PAYLOAD> setImage(String image) {
		return getRecord().setImage(image);
	}

	public String getCaption() {
		return getRecord().getCaption();
	}

	public BaseTemplateRecord<PAYLOAD> setCaption(String caption) {
		return getRecord().setCaption(caption);
	}

	public String getDescription() {
		return getRecord().getDescription();
	}

	public BaseTemplateRecord<PAYLOAD> setDescription(String description) {
		return getRecord().setDescription(description);
	}

	public String getBadge() {
		return getRecord().getBadge();
	}

	public BaseTemplateRecord<PAYLOAD> setBadge(String badge) {
		return getRecord().setBadge(badge);
	}

	public PAYLOAD getPayload() {
		return getRecord().getPayload();
	}

	public BaseTemplateRecord<PAYLOAD> setPayload(PAYLOAD payload) {
		return getRecord().setPayload(payload);
	}
}
