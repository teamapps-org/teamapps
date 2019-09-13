package org.teamapps.ux.task;

/**
 * Only the first invocation of cancel(), complete() or fail() will be honored!
 */
public interface ProgressMonitor {

    void start();

    void setProgress(double progress);
    void setStatusString(String statusString);

    void markCanceled();
    void markCompleted();
    void markFailed(String message, Exception e);

    boolean isCancellationRequested();
    
}