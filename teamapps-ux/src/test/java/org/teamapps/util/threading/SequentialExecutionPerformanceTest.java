package org.teamapps.util.threading;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SequentialExecutionPerformanceTest {

	@Test
	@Ignore
	public void testA() throws Exception {
		SequentialExecutorFactory factory = Executors::newSingleThreadExecutor;

		int numberOfThreads = 10_000;

		// will fail on OSX!
		List<ExecutorService> executors = IntStream.range(0, numberOfThreads)
				.mapToObj(i -> factory.createExecutor())
				.collect(Collectors.toList());

		for (int i = 0; i < numberOfThreads; i++) {
			AtomicInteger atomicInteger = new AtomicInteger();
			for (int j = 0; j < 1000; j++) {
				executors.get(i).submit((Runnable) atomicInteger::incrementAndGet);
			}
		}

		CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfThreads + 1);
		for (int i = 0; i < numberOfThreads; i++) {
			executors.get(i).submit(() -> {
				try {
					cyclicBarrier.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
			});
		}

		cyclicBarrier.await();
		System.out.println("done");
	}

}
