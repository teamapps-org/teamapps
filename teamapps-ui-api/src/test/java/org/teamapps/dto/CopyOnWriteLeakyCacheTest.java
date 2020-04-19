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