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
