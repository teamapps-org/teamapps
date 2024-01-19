/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.teamapps.common.util.ExceptionUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.IntStream;

public class CompletableFutureChainSequentialExecutorFactoryTest {

	@Test
	public void executionOrderOneKey() throws Exception {
		int numberOfExecutions = 1000;

		CompletableFutureChainSequentialExecutorFactory executorFactory = new CompletableFutureChainSequentialExecutorFactory(2);

		IntList executionOrderCheckingList = new IntArrayList();

		CyclicBarrier barrier = new CyclicBarrier(2);
		CloseableExecutor executor = executorFactory.createExecutor();
		for (int i = 0; i < numberOfExecutions; i++) {
			final int iFinal = i;
			executor.execute(() -> {
				executionOrderCheckingList.add(iFinal);
				if (iFinal == numberOfExecutions - 1) {
					ExceptionUtil.softenExceptions(() -> barrier.await());
				}
			});
		}

		barrier.await();
		checkIntListContents(executionOrderCheckingList, numberOfExecutions);
	}

	@Test
	public void executionOrderMultipleKey() throws Exception {
		int numberOfExecutions = 1000;

		CompletableFutureChainSequentialExecutorFactory executor = new CompletableFutureChainSequentialExecutorFactory(2);

		IntList executionOrderCheckingList1 = new IntArrayList();
		IntList executionOrderCheckingList2 = new IntArrayList();
		IntList executionOrderCheckingList3 = new IntArrayList();

		CompletableFuture<Boolean> lastFuture1 = null;
		CompletableFuture<Boolean> lastFuture2 = null;
		CompletableFuture<Boolean> lastFuture3 = null;
		CloseableExecutor executor1 = executor.createExecutor();
		CloseableExecutor executor2 = executor.createExecutor();
		CloseableExecutor executor3 = executor.createExecutor();
		for (int i = 0; i < numberOfExecutions; i++) {
			final int iFinal = i;
			lastFuture1 = CompletableFuture.supplyAsync(() -> executionOrderCheckingList1.add(iFinal), executor1);
			lastFuture2 = CompletableFuture.supplyAsync(() -> executionOrderCheckingList2.add(iFinal), executor2);
			lastFuture3 = CompletableFuture.supplyAsync(() -> executionOrderCheckingList3.add(iFinal), executor3);
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
		CompletableFutureChainSequentialExecutorFactory executor = new CompletableFutureChainSequentialExecutorFactory(2);

		executor.createExecutor().execute(() -> {
			throw new RuntimeException();
		});

		CyclicBarrier barrier = new CyclicBarrier(2);
		boolean[] secondWasExecuted = new boolean[]{false};
		executor.createExecutor().execute(() -> {
			secondWasExecuted[0] = true;
			ExceptionUtil.softenExceptions(() -> barrier.await());
		});

		barrier.await();
		Assert.assertTrue(secondWasExecuted[0]);
	}

	private void checkIntListContents(IntList executionOrderCheckingList, int rangeMax) {
		Assertions.assertThat(executionOrderCheckingList.toIntArray())
				.containsExactly(IntStream.range(0, rangeMax).toArray());
	}
}
