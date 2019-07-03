package org.teamapps.ux.component.calendar;

import org.teamapps.ux.component.template.BaseTemplateRecord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleCalendarModel<PAYLOAD> extends AbstractCalendarModel<BaseTemplateRecord<PAYLOAD>> {

	private List<SimpleCalendarEvent<PAYLOAD>> events;

	public SimpleCalendarModel(List<SimpleCalendarEvent<PAYLOAD>> events) {
		this.events = new ArrayList<>(events);
	}

	public SimpleCalendarModel() {
		this(new ArrayList<>());
	}

	@Override
	public List<CalendarEvent<BaseTemplateRecord<PAYLOAD>>> getEventsForInterval(Instant start, Instant end) {
		return events.stream()
				.filter(event -> event.getEndAsLong() >= start.toEpochMilli() && event.getStartAsLong() < end.toEpochMilli())
				.collect(Collectors.toList());
	}

	public List<SimpleCalendarEvent<PAYLOAD>> getEvents() {
		return events;
	}

	public void setEvents(List<SimpleCalendarEvent<PAYLOAD>> events) {
		this.events = events;
		this.onCalendarDataChanged.fire(null);
	}

	public void addEvent(SimpleCalendarEvent<PAYLOAD> event) {
		this.events.add(event);
		this.onCalendarDataChanged.fire(null);
	}

	public void removeEvent(SimpleCalendarEvent<PAYLOAD> event) {
		this.events.remove(event);
		this.onCalendarDataChanged.fire(null);
	}
	
}