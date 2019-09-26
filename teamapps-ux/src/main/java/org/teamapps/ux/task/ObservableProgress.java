package org.teamapps.ux.task;

import org.teamapps.event.Event;

public interface ObservableProgress {

	Event<ProgressChangeEventData> onChanged();

	ProgressStatus getStatus();

	String getStatusMessage();

	double getProgress();

	default boolean isProgressUnknown() {
		return getProgress() < 0;
	}

	boolean isCancelable();

	void requestCancellation(); // will get ignored when not cancelable!

	boolean isCancellationRequested();

}
