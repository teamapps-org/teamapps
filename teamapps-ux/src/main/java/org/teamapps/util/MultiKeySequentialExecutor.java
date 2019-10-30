package org.teamapps.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MultiKeySequentialExecutor<K> {

	private static Logger LOGGER = LoggerFactory.getLogger(MultiKeySequentialExecutor.class);

	private Map<K, SequentialExecutor> sequentialExecutors = new ConcurrentHashMap<>(); // synchronized on submit method level
	private ExecutorService pool;

	public MultiKeySequentialExecutor(int nThreads) {
		pool = Executors.newFixedThreadPool(nThreads);
	}

	public MultiKeySequentialExecutor(ExecutorService executorService) {
		pool = executorService;
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
		sequentialExecutors.computeIfAbsent(key, k -> new SequentialExecutor())
				.close();
	}

	public SequentialExecutor getExecutorForKey(K key) {
		return sequentialExecutors.get(key);
	}

	public class SequentialExecutor implements Executor {
		private CompletableFuture<?> lastFuture = CompletableFuture.completedFuture(null);
		private AtomicInteger queueSize = new AtomicInteger(0);

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
			int queueSize = this.queueSize.incrementAndGet();
			LOGGER.debug("Queue size: {}", queueSize);
			if (queueSize > 100) {
				LOGGER.warn("Queue is very long: {}", queueSize);
			}
			long submitTime = System.currentTimeMillis();
			CompletableFuture<V> returnedFuture = lastFuture.thenApplyAsync(o -> {
				long executionStartTime = System.currentTimeMillis();
				long delay = executionStartTime - submitTime;
				if (delay > 1000) {
					LOGGER.warn("Execution delay high: {}",  delay);
				}
				V result = task.get();
				long executionTime = System.currentTimeMillis() - executionStartTime;
				if (executionTime > 1000) {
					LOGGER.warn("Execution time long: {}", executionTime);
				}
				this.queueSize.decrementAndGet();
				return result;
			}, pool);
			lastFuture = returnedFuture.exceptionally(throwable -> {
				LOGGER.error("Error while executing: ", throwable);
				return null; // do not interrupt the execution chain!!
			});
			return returnedFuture;
		}

		public synchronized void close() {
			lastFuture.cancel(false);
		}
	}


}