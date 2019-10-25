package org.teamapps.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class MultiKeySequentialExecutor<K> {

	private static Logger LOGGER = LoggerFactory.getLogger(MultiKeySequentialExecutor.class);

	private Map<K, CompletableFuture<?>> lastFutureByKey = new HashMap<>(); // synchronized on submit method level
	private ExecutorService pool;

	public MultiKeySequentialExecutor(int nThreads) {
		pool = Executors.newFixedThreadPool(nThreads);
	}

	public MultiKeySequentialExecutor(ExecutorService executorService) {
		pool = executorService;
	}

	public synchronized CompletableFuture<Void> submit(K key, Runnable task) {
		return this.submit(key, () -> {
			task.run();
			return null;
		});
	}

	public synchronized <V> CompletableFuture<V> submit(K key, Supplier<V> task) {
		CompletableFuture<V> returnedFuture = lastFutureByKey.computeIfAbsent(key, (k) -> CompletableFuture.completedFuture(null))
				.thenApplyAsync(o -> task.get(), pool);
		lastFutureByKey.put(key, returnedFuture.exceptionally(throwable -> {
			LOGGER.error("Error while executing: ", throwable);
			return null; // do not interrupt the execution chain!!
		}));
		return returnedFuture;
	}

	public synchronized void closeForKey(K key) {
		CompletableFuture<?> lastFuture = lastFutureByKey.get(key);
		if (lastFuture != null) {
			lastFuture.cancel(false);
			lastFutureByKey.remove(key);
		}
	}

	public SequentialExecutor getExecutorForKey(K key) {
		return new SequentialExecutor(key);
	}

	public class SequentialExecutor implements Executor {
		private final K key;

		public SequentialExecutor(K key) {
			this.key = key;
		}

		@Override
		public void execute(Runnable command) {
			MultiKeySequentialExecutor.this.submit(key, command);
		}

		public CompletableFuture<Void> submit(Runnable runnable) {
			return MultiKeySequentialExecutor.this.submit(key, runnable);
		}

		public <V> CompletableFuture<V> submit(Supplier<V> task) {
			return MultiKeySequentialExecutor.this.submit(key, task);
		}

		public void close() {
			MultiKeySequentialExecutor.this.closeForKey(key);
		}
	}


}