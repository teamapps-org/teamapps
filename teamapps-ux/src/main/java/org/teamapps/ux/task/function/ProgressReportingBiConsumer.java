package org.teamapps.ux.task.function;

import org.teamapps.ux.task.ProgressMonitor;

public interface ProgressReportingBiConsumer<T, U> {

	void accept(T t, U u, ProgressMonitor progressMonitor);

}
