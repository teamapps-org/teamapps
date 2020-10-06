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
package org.teamapps.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class MultiKeySequentialExecutor<K> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MultiKeySequentialExecutor.class);

	private final AtomicReference<MinMaxAverageStats> delayStats = new AtomicReference<>(new MinMaxAverageStats());
	private final AtomicReference<MinMaxAverageStats> executionTimeStats= new AtomicReference<>(new MinMaxAverageStats());

	private final Map<K, SequentialExecutor> sequentialExecutors = new ConcurrentHashMap<>(); // synchronized on submit method level
	private final ExecutorService pool;

	public MultiKeySequentialExecutor(int nThreads) {
		this(Executors.newFixedThreadPool(nThreads));
	}

	public MultiKeySequentialExecutor(ExecutorService executorService) {
		pool = executorService;

		ScheduledExecutorService statsLogExecutorService = Executors.newSingleThreadScheduledExecutor();
		statsLogExecutorService.scheduleAtFixedRate(() -> {
			MinMaxAverageStats delayStats = this.delayStats.getAndSet(new MinMaxAverageStats());
			MinMaxAverageStats executionTimeStats = this.executionTimeStats.getAndSet(new MinMaxAverageStats());
			if (delayStats.max > 3000) {
				LOGGER.warn("Delays critical: min: {}, max: {}, avg: {}, count: {}", delayStats.getMin(), delayStats.getMax(), delayStats.getAvg(), delayStats.getCount());
			}
			if (executionTimeStats.max > 1000) {
				LOGGER.warn("Execution times critical: min: {}, max: {}, avg: {}, count: {}", executionTimeStats.getMin(), executionTimeStats.getMax(), executionTimeStats.getAvg(), executionTimeStats.getCount());
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	public CompletableFuture<Void> submit(K key, Runnable task) {
		return sequentialExecutors.computeIfAbsent(key, k -> new SequentialExecutor())
				.submit(task);
	}

	public <V> CompletableFuture<V> submit(K key, Supplier<V> task) {
		return sequentialExecutors.computeIfAbsent(key, k -> new SequentialExecutor())
				.submit(task);
	}

	public void closeForKey(K key) {
		sequentialExecutors.compute(key, (k, sequentialExecutor) -> {
			if (sequentialExecutor != null) {
				sequentialExecutor.close();
			}
			return null;
		});
	}

	public SequentialExecutor getExecutorForKey(K key) {
		return sequentialExecutors.get(key);
	}

	public class SequentialExecutor implements Executor {
		private CompletableFuture<?> lastFuture = CompletableFuture.completedFuture(null);
		private final AtomicInteger queueSize = new AtomicInteger(0);
		private boolean closed = false;

		@Override
		public void execute(Runnable command) {
			submit(command);
		}

		public synchronized CompletableFuture<Void> submit(Runnable runnable) {
			return this.submit(() -> {
				runnable.run();
				return null;
			});
		}

		public synchronized <V> CompletableFuture<V> submit(Supplier<V> task) {
			if (this.closed) {
				LOGGER.debug("SequentialExecutor already closed.");
				return CompletableFuture.failedFuture(new SequentialExecutorClosedException());
			}
			int queueSize = this.queueSize.incrementAndGet();
			LOGGER.debug("Queue size: {}", queueSize);
			if (queueSize > 500) { // the queue gets quite long when destroying a session, since there are very many listeners to the destroyed event
				LOGGER.warn("Queue is very long: {}", queueSize);
			}
			long submitTime = System.currentTimeMillis();
			CompletableFuture<V> returnedFuture = lastFuture.thenApplyAsync(o -> {
				long executionStartTime = System.currentTimeMillis();
				long delay = executionStartTime - submitTime;
				delayStats.getAndUpdate(minMaxAverageStats -> minMaxAverageStats.push(delay));
				V result = task.get();
				long executionTime = System.currentTimeMillis() - executionStartTime;
				executionTimeStats.getAndUpdate(minMaxAverageStats -> minMaxAverageStats.push(executionTime));
				this.queueSize.decrementAndGet();
				return result;
			}, pool);
			lastFuture = returnedFuture.exceptionally(throwable -> {
				LOGGER.error("Error while executing: ", throwable);
				return null; // do not interrupt the execution chain!!
			});
			return returnedFuture;
		}

		// private since it does not remove itself from the map!
		private synchronized void close() {
			this.closed = true;
		}

	}

	public static class SequentialExecutorClosedException extends RuntimeException {
	}

	private static class MinMaxAverageStats {
		private final long min;
		private final long max;
		private final long total;
		private final long count;

		public MinMaxAverageStats() {
			this.min = Long.MAX_VALUE;
			this.max = 0;
			this.total = 0;
			this.count = 0;
		}

		public MinMaxAverageStats(long min, long max, long total, long count) {
			this.min = min;
			this.max = max;
			this.total = total;
			this.count = count;
		}

		public MinMaxAverageStats push(long time) {
			return new MinMaxAverageStats(Math.min(time, min), Math.max(time, max), this.total + time, this.count + 1);
		}

		public long getMin() {
			return min;
		}

		public long getMax() {
			return max;
		}

		public long getAvg() {
			return total / count;
		}

		public long getCount() {
			return count;
		}
	}
}
