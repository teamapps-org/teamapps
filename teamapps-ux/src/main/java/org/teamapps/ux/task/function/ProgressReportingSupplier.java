package org.teamapps.ux.task.function;

import org.teamapps.ux.task.ProgressMonitor;

public interface ProgressReportingSupplier<T> {

	T get(ProgressMonitor progressMonitor);

}
