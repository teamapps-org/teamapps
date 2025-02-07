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
package org.teamapps.projector.server.threading;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompletableFutureChainSequentialExecutorFactoryTest {

	@Test
	public void executionOrderOneKey() throws Exception {
		int numberOfExecutions = 1000;

		CompletableFutureChainSequentialExecutorFactory executorFactory = new CompletableFutureChainSequentialExecutorFactory(2);

		IntList executionOrderCheckingList = new IntArrayList();

		Future<Boolean> lastFuture = null;
		ExecutorService executor = executorFactory.createExecutor();
		for (int i = 0; i < numberOfExecutions; i++) {
			final int iFinal = i;
			lastFuture = executor
					.submit(() -> executionOrderCheckingList.add(iFinal));
		}

		lastFuture.get();
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
		ExecutorService executor1 = executor.createExecutor();
		ExecutorService executor2 = executor.createExecutor();
		ExecutorService executor3 = executor.createExecutor();
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

		executor.createExecutor().submit(() -> {
			throw new RuntimeException();
		});

		boolean[] secondWasExecuted = new boolean[]{false};
		executor.createExecutor().submit(() -> {
			secondWasExecuted[0] = true;
		})
				.get();

		assertTrue(secondWasExecuted[0]);
	}

	private void checkIntListContents(IntList executionOrderCheckingList, int rangeMax) {
		Assertions.assertThat(executionOrderCheckingList.toIntArray())
				.containsExactly(IntStream.range(0, rangeMax).toArray());
	}
}
