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
package org.teamapps.dto;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class CopyOnWriteLeakyCacheTest {

	@Test
	public void computeIfAbsent() throws ExecutionException, InterruptedException, IllegalAccessException, NoSuchFieldException {
		CopyOnWriteLeakyCache<Integer, String> cache = new CopyOnWriteLeakyCache<>();

		CompletableFuture[] completableFutures = IntStream.range(0, 10)
				.mapToObj(threadNumber -> CompletableFuture.runAsync(() -> {
					for (int i = 0; i < 10_000; i++) {
						cache.computeIfAbsent(i % 100, ii -> {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							return "v" + ii;
						});
					}
				}))
				.toArray(CompletableFuture[]::new);
		CompletableFuture.allOf(completableFutures).get();

		Field cacheField = CopyOnWriteLeakyCache.class.getDeclaredField("map");
		cacheField.setAccessible(true);
		Map<Integer, String> map = (Map<Integer, String>) cacheField.get(cache);
		for (int i = 0; i < 100; i++) {
			assertThat(map.get(i)).isEqualTo("v" + i); // Note that this assertion may indeed fail, since the cache is leaky regarding concurrencly - but it is very unlikely!
		}
	}
}
