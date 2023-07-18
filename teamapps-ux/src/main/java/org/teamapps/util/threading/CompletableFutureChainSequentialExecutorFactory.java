/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.util.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.teamapps.common.util.ExceptionUtil.softenExceptions;

public class CompletableFutureChainSequentialExecutorFactory implements SequentialExecutorFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompletableFutureChainSequentialExecutorFactory.class);

	private final AtomicReference<MinMaxAverageStats> delayStats = new AtomicReference<>(new MinMaxAverageStats());
	private final AtomicReference<MinMaxAverageStats> executionTimeStats = new AtomicReference<>(new MinMaxAverageStats());

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

	public CloseableExecutor createExecutor() {
		return createExecutor("unnamed");
	}

	public CloseableExecutor createExecutor(String name) {
		return new SequentialExecutor(name);
	}

	public class SequentialExecutor implements CloseableExecutor {
		private final String name;
		private CompletableFuture<?> lastFuture = CompletableFuture.completedFuture(null);
		private final AtomicInteger queueSize = new AtomicInteger(0);

		public SequentialExecutor(String name) {
			this.name = name;
		}

		@Override
		public synchronized void execute(Runnable command) {
			int queueSize = this.queueSize.incrementAndGet();
			LOGGER.trace("{}: Queue size: {}", name, queueSize);
			if (queueSize >= 500 && queueSize % 10 == 0) { // the queue gets quite long when destroying a session, since there are very many listeners to the destroyed event
				LOGGER.warn("{}: Queue is very long: {}", name, queueSize);
			}
			long submitTime = System.currentTimeMillis();
			lastFuture = lastFuture.thenApplyAsync(o -> {
				long executionStartTime = System.currentTimeMillis();
				long delay = executionStartTime - submitTime;
				delayStats.getAndUpdate(minMaxAverageStats -> minMaxAverageStats.push(delay));
				Object result = softenExceptions(() -> {
					command.run();
					return null;
				});
				long executionTime = System.currentTimeMillis() - executionStartTime;
				executionTimeStats.getAndUpdate(minMaxAverageStats -> minMaxAverageStats.push(executionTime));
				this.queueSize.decrementAndGet();
				return result;
			}, pool).exceptionally(throwable -> {
				LOGGER.error("{}: Error while executing: ", name, throwable);
				return null; // do not interrupt the execution chain!!
			});
		}

		@Override
		public void close() {
			// nothing to do here
		}
	}

	public static class SequentialExecutorClosedException extends RuntimeException {
	}

}
