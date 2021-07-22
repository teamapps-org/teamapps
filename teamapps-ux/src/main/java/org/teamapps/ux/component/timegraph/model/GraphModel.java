package org.teamapps.ux.component.timegraph.model;

import org.teamapps.event.Event;
import org.teamapps.ux.component.timegraph.Interval;
import org.teamapps.ux.component.timegraph.TimePartitioning;
import org.teamapps.ux.component.timegraph.datapoints.GraphData;

import java.time.ZoneId;

public interface GraphModel<D extends GraphData> {

	Event<Void> onDataChanged();

	Interval getDomainX();

	D getData(TimePartitioning zoomLevel, ZoneId zoneId, Interval neededInterval, Interval displayedInterval);

}
