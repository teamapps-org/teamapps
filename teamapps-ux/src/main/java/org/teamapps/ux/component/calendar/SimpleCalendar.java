package org.teamapps.ux.component.calendar;

import java.util.List;

public class SimpleCalendar<PAYLOAD> extends Calendar<SimpleCalendarEvent<PAYLOAD>> {

	public SimpleCalendar() {
		super(new SimpleCalendarModel<PAYLOAD>());
	}

	public SimpleCalendar(List<SimpleCalendarEvent<PAYLOAD>> events) {
		super(new SimpleCalendarModel<>(events));
	}

	public void setEvents(List<SimpleCalendarEvent<PAYLOAD>> events) {
		((SimpleCalendarModel<PAYLOAD>) getModel()).setEvents(events);
	}

	public void addEvent(SimpleCalendarEvent<PAYLOAD> event) {
		((SimpleCalendarModel<PAYLOAD>) getModel()).addEvent(event);
	}

	public void removeEvent(SimpleCalendarEvent<PAYLOAD> event) {
		((SimpleCalendarModel<PAYLOAD>) getModel()).removeEvent(event);
	}
}
