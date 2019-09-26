package org.teamapps.ux.component.progress;

import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.task.ObservableProgress;
import org.teamapps.ux.task.ProgressCompletableFuture;
import org.teamapps.ux.task.function.ProgressReportingRunnable;
import org.teamapps.ux.task.function.ProgressReportingSupplier;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

public interface MultiProgressDisplay extends Component {

	void addProgress(Icon icon, String taskName, ObservableProgress progress);

	default <T> ProgressCompletableFuture<T> addTask(Icon icon, String taskName, ProgressReportingSupplier<T> supplier) {
		return addTask(icon, taskName, supplier, ProgressCompletableFuture.ASYNC_POOL);
	}

	default <T> ProgressCompletableFuture<T> addTask(Icon icon, String taskName, ProgressReportingSupplier<T> supplier, Executor executor) {
		ProgressCompletableFuture<T> future = ProgressCompletableFuture.supplyAsync(supplier, executor);
		addProgress(icon, taskName, future.getProgress());
		return future;
	}

	default ProgressCompletableFuture<Void> addTask(Icon icon, String taskName, ProgressReportingRunnable runnable) {
		return addTask(icon, taskName, runnable, ProgressCompletableFuture.ASYNC_POOL);
	}

	default ProgressCompletableFuture<Void> addTask(Icon icon, String taskName, ProgressReportingRunnable runnable, Executor executor) {
		ProgressCompletableFuture<Void> future = ProgressCompletableFuture.runAsync(runnable, executor);
		addProgress(icon, taskName, future.getProgress());
		return future;
	}

	default <T> ProgressCompletableFuture<T> addTask(Icon icon, String taskName, Supplier<T> supplier) {
		return addTask(icon, taskName, supplier, ProgressCompletableFuture.ASYNC_POOL);
	}

	default <T> ProgressCompletableFuture<T> addTask(Icon icon, String taskName, Supplier<T> supplier, Executor executor) {
		ProgressCompletableFuture<T> future = ProgressCompletableFuture.supplyAsync(supplier, executor);
		addProgress(icon, taskName, future.getProgress());
		return future;
	}

	default ProgressCompletableFuture<Void> addTask(Icon icon, String taskName, Runnable runnable) {
		return addTask(icon, taskName, runnable, ProgressCompletableFuture.ASYNC_POOL);
	}

	default ProgressCompletableFuture<Void> addTask(Icon icon, String taskName, Runnable runnable, Executor executor) {
		ProgressCompletableFuture<Void> future = ProgressCompletableFuture.runAsync(runnable, executor);
		addProgress(icon, taskName, future.getProgress());
		return future;
	}

}
