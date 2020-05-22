/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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
