package org.teamapps.ux.component.calendar;

import org.teamapps.data.extract.BeanPropertyExtractor;

import java.util.List;

public class SimpleCalendar<PAYLOAD> extends Calendar<SimpleCalendarEvent<PAYLOAD>> {

	public SimpleCalendar() {
		super(new SimpleCalendarModel<PAYLOAD>());
		setPropertyExtractor(new BeanPropertyExtractor<SimpleCalendarEvent<PAYLOAD>>().addProperty("description",
				event -> event.getStartInstant().atZone(getTimeZone()).format(getSessionContext().getConfiguration().getTimeFormatter())
				+ "\u2009-\u2009" + event.getEndInstant().atZone(getTimeZone()).format(getSessionContext().getConfiguration().getTimeFormatter())));
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
