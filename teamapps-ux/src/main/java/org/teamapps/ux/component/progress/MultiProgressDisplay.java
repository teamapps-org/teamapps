package org.teamapps.ux.component.progress;

import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.task.ObservableProgress;

public interface MultiProgressDisplay extends Component {

	void addProgress(Icon icon, String taskName, ObservableProgress progress);

	// default <T> ProgressCompletableFuture<T> addTask(ProgressReportingSupplier<T> supplier) {
	// 	return addTask(supplier, ProgressCompletableFuture.ASYNC_POOL);
	// }
	//
	// default <T> ProgressCompletableFuture<T> addTask(ProgressReportingSupplier<T> supplier, Executor executor) {
	// 	ProgressCompletableFuture<T> future = ProgressCompletableFuture.supplyAsync(supplier, executor);
	// 	addProgress(future.getProgress());
	// 	return future;
	// }
	//
	// static ProgressCompletableFuture<Void> runAsync(ProgressReportingRunnable runnable) {
	// 	return ProgressCompletableFuture.runAsync(runnable);
	// }
	//
	// static ProgressCompletableFuture<Void> runAsync(ProgressReportingRunnable runnable, Executor executor) {
	// 	return ProgressCompletableFuture.runAsync(runnable, executor);
	// }

}
