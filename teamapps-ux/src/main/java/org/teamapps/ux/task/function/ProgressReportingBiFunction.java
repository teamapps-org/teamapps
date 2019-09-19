package org.teamapps.ux.task.function;

import org.teamapps.ux.task.ProgressMonitor;

public interface ProgressReportingBiFunction<T, U, R> {

	R apply(T t, U u, ProgressMonitor progressMonitor);

}
