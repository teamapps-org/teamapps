package org.teamapps.ux.task.function;

import org.teamapps.ux.task.ProgressMonitor;

public interface ProgressReportingConsumer<T> {

	void accept(T t, ProgressMonitor progressMonitor);

}
