/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.commons.util.ExceptionUtil;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CompletableFutureChainSequentialExecutorFactory implements SequentialExecutorFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final AtomicReference<MinMaxAverageStats> delayStats = new AtomicReference<>(new MinMaxAverageStats());
	private final AtomicReference<MinMaxAverageStats> executionTimeStats= new AtomicReference<>(new MinMaxAverageStats());

	private final ExecutorService pool;

	public CompletableFutureChainSequentialExecutorFactory(int nThreads) {
		this(Executors.newFixedThreadPool(nThreads));
	}

	public CompletableFutureChainSequentialExecutorFactory(ExecutorService executorService) {
		pool = executorService;

		ScheduledExecutorService statsLogExecutorService = Executors.newSingleThreadScheduledExecutor();
		statsLogExecutorService.scheduleAtFixedRate(() -> {
			MinMaxAverageStats delayStats = this.delayStats.getAndSet(new MinMaxAverageStats());
			MinMaxAverageStats executionTimeStats = this.executionTimeStats.getAndSet(new MinMaxAverageStats());
			if (delayStats.getMax() > 3000) {
				LOGGER.warn("Delays critical: min: {}, max: {}, avg: {}, count: {}", delayStats.getMin(), delayStats.getMax(), delayStats.getAvg(), delayStats.getCount());
			}
			if (executionTimeStats.getMax() > 1000) {
				LOGGER.warn("Execution times critical: min: {}, max: {}, avg: {}, count: {}", executionTimeStats.getMin(), executionTimeStats.getMax(), executionTimeStats.getAvg(), executionTimeStats.getCount());
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	public ExecutorService createExecutor() {
		return createExecutor("unnamed");
	}

	public ExecutorService createExecutor(String name) {
		return new SequentialExecutor(name);
	}

	public class SequentialExecutor extends AbstractExecutorService {
		private final String name;
		private CompletableFuture<?> lastFuture = CompletableFuture.completedFuture(null);
		private final AtomicInteger queueSize = new AtomicInteger(0);
		private boolean closed = false;

		public SequentialExecutor(String name) {
			this.name = name;
		}

		@Override
		public void execute(Runnable command) {
			submit(command);
		}

		public CompletableFuture<Void> submit(Runnable runnable) {
			return this.submit(() -> {
				runnable.run();
				return null;
			});
		}

		public synchronized <V> CompletableFuture<V> submit(Callable<V> task) {
			if (this.closed) {
				LOGGER.info("{}: SequentialExecutor already closed.", name);
				return CompletableFuture.failedFuture(new SequentialExecutorClosedException());
			}
			int queueSize = this.queueSize.incrementAndGet();
			LOGGER.trace("{}: Queue size: {}", name, queueSize);
			if (queueSize >= 500 && queueSize % 10 == 0) { // the queue gets quite long when destroying a session, since there are very many listeners to the destroyed event
				LOGGER.warn("{}: Queue is very long: {}", name, queueSize);
			}
			long submitTime = System.currentTimeMillis();
			CompletableFuture<V> returnedFuture = lastFuture.thenApplyAsync(o -> {
				long executionStartTime = System.currentTimeMillis();
				long delay = executionStartTime - submitTime;
				delayStats.getAndUpdate(minMaxAverageStats -> minMaxAverageStats.push(delay));
				V result = ExceptionUtil.runWithSoftenedExceptions(task);
				long executionTime = System.currentTimeMillis() - executionStartTime;
				executionTimeStats.getAndUpdate(minMaxAverageStats -> minMaxAverageStats.push(executionTime));
				this.queueSize.decrementAndGet();
				return result;
			}, pool);
			if (!closed) { // Go 100% sure! shutdown() might be executed as a task!
				lastFuture = returnedFuture.exceptionally(throwable -> {
					LOGGER.error("{}: Error while executing: ", name, throwable);
					return null; // do not interrupt the execution chain!!
				});
			}
			return returnedFuture;
		}

		@Override
		public synchronized void shutdown() {
			this.closed = true;
			this.lastFuture = null; // prevent memory leak!
		}

		@Override
		public boolean isShutdown() {
			return this.closed;
		}

		@Override
		public boolean isTerminated() {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Runnable> shutdownNow() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
			throw new UnsupportedOperationException();
		}

	}

	public static class SequentialExecutorClosedException extends RuntimeException {
	}

}
