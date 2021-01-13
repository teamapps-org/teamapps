/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class MultiKeySequentialExecutorTest {

	@Test
	public void executionOrderOneKey() throws Exception {
		int numberOfExecutions = 1000;

		MultiKeySequentialExecutor<String> executor = new MultiKeySequentialExecutor<>(2);

		IntList executionOrderCheckingList = new IntArrayList();

		CompletableFuture<Boolean> lastFuture = null;
		for (int i = 0; i < numberOfExecutions; i++) {
			final int iFinal = i;
			lastFuture = executor.submit("myKey", () -> executionOrderCheckingList.add(iFinal));
		}

		CompletableFuture<Void> allDoneFuture = lastFuture.thenRun(() -> {
			checkIntListContents(executionOrderCheckingList, numberOfExecutions);
		});

		allDoneFuture.get(1, TimeUnit.SECONDS);
	}

	@Test
	public void executionOrderMultipleKey() throws Exception {
		int numberOfExecutions = 1000;

		MultiKeySequentialExecutor<String> executor = new MultiKeySequentialExecutor<>(2);

		IntList executionOrderCheckingList1 = new IntArrayList();
		IntList executionOrderCheckingList2 = new IntArrayList();
		IntList executionOrderCheckingList3 = new IntArrayList();

		CompletableFuture<Boolean> lastFuture1 = null;
		CompletableFuture<Boolean> lastFuture2 = null;
		CompletableFuture<Boolean> lastFuture3 = null;
		for (int i = 0; i < numberOfExecutions; i++) {
			final int iFinal = i;
			lastFuture1 = executor.submit("myKey1", () -> executionOrderCheckingList1.add(iFinal));
			lastFuture2 = executor.submit("myKey2", () -> executionOrderCheckingList2.add(iFinal));
			lastFuture3 = executor.submit("myKey3", () -> executionOrderCheckingList3.add(iFinal));
		}

		CompletableFuture.allOf(
				lastFuture1.thenRun(() -> {
					checkIntListContents(executionOrderCheckingList1, numberOfExecutions);
				}),
				lastFuture2.thenRun(() -> {
					checkIntListContents(executionOrderCheckingList2, numberOfExecutions);
				}),
				lastFuture3.thenRun(() -> {
					checkIntListContents(executionOrderCheckingList3, numberOfExecutions);
				})
		)
				.get();
	}

	@Test
	public void executionContinuesAfterException() throws Exception {
		MultiKeySequentialExecutor<String> executor = new MultiKeySequentialExecutor<>(2);

		executor.submit("a", () -> {
			throw new RuntimeException();
		});

		boolean[] secondWasExecuted = new boolean[]{false};
		executor.submit("a", () -> {
			secondWasExecuted[0] = true;
		})
				.get();

		Assert.assertTrue(secondWasExecuted[0]);
	}

	@Test
	@Ignore
	public void performance() throws InterruptedException, ExecutionException {
		MultiKeySequentialExecutor<Integer> executor = new MultiKeySequentialExecutor<>(20);
		long now = System.currentTimeMillis();
		long in5seconds = now + 3000;
		AtomicBoolean shutdown = new AtomicBoolean(false);
		AtomicBoolean done = new AtomicBoolean(false);

		Runnable task = () -> {
			while (System.currentTimeMillis() < in5seconds) {

			}
			if (done.get()) {
				System.err.println("AFTER DONE!");
			}
		};

		ExecutorService x = Executors.newFixedThreadPool(20);
		for (int i = 0; i < 1000_000; i++) {
			x.submit(() -> {
				executor.submit(ThreadLocalRandom.current().nextInt(0, 10), task);
				if (shutdown.get()) {
					System.err.println("After shutdown??");
				}
			});
		}
		x.shutdown();
		x.awaitTermination(10_000, TimeUnit.MILLISECONDS);
		System.out.println("Shutdown = true");
		shutdown.set(true);

		CompletableFuture[] completionFutures = IntStream.range(0, 10)
				.mapToObj(i -> executor.submit(i, task))
				.toArray(CompletableFuture[]::new);
		CompletableFuture.allOf(completionFutures)
				.thenRun(() -> {
					done.set(true);
					System.err.println("Done after " + (System.currentTimeMillis() - in5seconds) + "ms");
				})
				.get();

		Thread.sleep(1000);
	}

	private void checkIntListContents(IntList executionOrderCheckingList, int rangeMax) {
		Assertions.assertThat(executionOrderCheckingList.toIntArray())
				.containsExactly(IntStream.range(0, rangeMax).toArray());
	}
}
