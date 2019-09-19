package org.teamapps.ux.task;

/**
 * Only the first invocation of cancel(), complete() or fail() will be honored!
 */
public interface ProgressMonitor {

	void start();

	void setStatusMessage(String statusMessage);

	void setProgress(double progress);

	void setProgress(double progress, String statusMessage);

	void markCanceled();

	void markCanceled(String statusMessage);

	void markCompleted();

	void markCompleted(String statusMessage);

	void markFailed();

	void markFailed(String message);

	void setCancelable(boolean cancelable);

	boolean isCancelable();

	boolean isCancellationRequested();

}