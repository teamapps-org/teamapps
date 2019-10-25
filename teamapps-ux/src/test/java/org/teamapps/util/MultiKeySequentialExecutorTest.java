package org.teamapps.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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

	private void checkIntListContents(IntList executionOrderCheckingList, int rangeMax) {
		Assertions.assertThat(executionOrderCheckingList.toIntArray())
				.containsExactly(IntStream.range(0, rangeMax).toArray());
	}
}