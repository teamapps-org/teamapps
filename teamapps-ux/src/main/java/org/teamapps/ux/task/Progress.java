package org.teamapps.ux.task;

import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;

public class Progress implements ProgressMonitor, ObservableProgress {

	public final Event<Void> onStarted = new Event<>();
	public final Event<Double> onProgressChanged = new Event<>();
	public final Event<String> onStatusStringChanged = new Event<>();
	public final Event<Void> onCompleted = new Event<>();
	public final Event<Void> onCancellationRequested = new Event<>();
	public final Event<Void> onCanceled = new Event<>();
	public final Event<TaskFailureInfo> onFailed = new Event<>();

	private Icon icon;
	private String taskName;
	private String statusString;
	private double progress = -1;
	private final boolean cancelable;
	private ProgressStatus status = ProgressStatus.NOT_YET_STARTED;

	public Progress(Icon icon, String taskName, boolean cancelable) {
		this.icon = icon;
		this.taskName = taskName;
		this.cancelable = cancelable;
	}

	@Override
	public  Icon getIcon() {
		return icon;
	}

	@Override
	public  String getTaskName() {
		return taskName;
	}

	@Override
	public  String getStatusString() {
		return statusString;
	}

	@Override
	public  double getProgress() {
		return progress;
	}

	@Override
	public  boolean isProgressUnknown() {
		return progress < 0;
	}

	@Override
	public  boolean isCancelable() {
		return cancelable;
	}

	@Override
	public  void requestCancellation() {
		if (!cancelable) {
			return;
		}
		if (status.ordinal() < ProgressStatus.CANCELLATION_REQUESTED.ordinal()) {
			this.status = ProgressStatus.CANCELLATION_REQUESTED;
			this.onCancellationRequested().fire(null);
		}
	}

	@Override
	public  boolean isCancellationRequested() {
		return status == ProgressStatus.CANCELLATION_REQUESTED;
	}

	@Override
	public  ProgressStatus getStatus() {
		return status;
	}

	@Override
	public  Event<Void> onStarted() {
		return onStarted;
	}

	@Override
	public  Event<Double> onProgressChanged() {
		return onProgressChanged;
	}

	@Override
	public  Event<String> onStatusStringChanged() {
		return onStatusStringChanged;
	}

	@Override
	public  Event<Void> onCompleted() {
		return onCompleted;
	}

	@Override
	public  Event<Void> onCancellationRequested() {
		return onCancellationRequested;
	}

	@Override
	public  Event<Void> onCanceled() {
		return onCanceled;
	}

	@Override
	public  Event<TaskFailureInfo> onFailed() {
		return onFailed;
	}

	@Override
	public  void start() {
		if (status.ordinal() < ProgressStatus.RUNNING.ordinal()) {
			// System.out.println("Progress.start");
			status = ProgressStatus.RUNNING;
			onStarted.fire(null);
		}
	}

	@Override
	public  void setProgress(double progress) {
		// System.out.println("progress = [" + progress + "]");
		if (status.ordinal() < ProgressStatus.RUNNING.ordinal()) {
			start();
		}
		if (status.ordinal() == ProgressStatus.RUNNING.ordinal()) {
			this.progress = progress;
			onProgressChanged.fire(progress);
		}
	}

	@Override
	public  void setStatusString(String statusString) {
		// System.out.println("statusString = [" + statusString + "]");
		if (status.ordinal() < ProgressStatus.RUNNING.ordinal()) {
			start();
		}
		if (status.ordinal() == ProgressStatus.RUNNING.ordinal()) {
			this.statusString = statusString;
			onStatusStringChanged.fire(statusString);
		}
	}

	@Override
	public  void markCanceled() {
		// System.out.println("Progress.markCanceled");
		if (status.ordinal() < ProgressStatus.CANCELED.ordinal()) {
			this.status = ProgressStatus.CANCELED;
			this.onCanceled.fire(null);
		}
	}

	@Override
	public  void markCompleted() {
		// System.out.println("Progress.markCompleted");
		if (status.ordinal() < ProgressStatus.CANCELED.ordinal()) {
			this.status = ProgressStatus.COMPLETE;
			this.onCompleted.fire(null);
		}
	}

	@Override
	public  void markFailed(String message, Exception e) {
		// System.out.println("message = [" + message + "], e = [" + e + "]");
		if (status.ordinal() < ProgressStatus.CANCELED.ordinal()) {
			this.status = ProgressStatus.FAILED;
			this.onFailed.fire(new TaskFailureInfo(message, e));
		}
	}
}
