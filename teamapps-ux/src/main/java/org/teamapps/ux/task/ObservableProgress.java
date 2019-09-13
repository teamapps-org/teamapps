package org.teamapps.ux.task;

import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;

public interface ObservableProgress {
	Icon getIcon();
	String getTaskName();
	String getStatusString();
	double getProgress();
	boolean isProgressUnknown(); // progress < 0

	boolean isCancelable();
	void requestCancellation(); // will get ignored when not cancelable!
	boolean isCancellationRequested();

	ProgressStatus getStatus();

	Event<Void> onStarted();
	Event<Double> onProgressChanged();
	Event<String> onStatusStringChanged();
	Event<Void> onCompleted();
	Event<Void> onCancellationRequested();
	Event<Void> onCanceled();
	Event<TaskFailureInfo> onFailed();
}
