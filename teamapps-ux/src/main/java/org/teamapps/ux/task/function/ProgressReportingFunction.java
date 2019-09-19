package org.teamapps.ux.task.function;

import org.teamapps.ux.task.ProgressMonitor;

public interface ProgressReportingFunction<T, R> {

	R apply(T t, ProgressMonitor progressMonitor);

}
