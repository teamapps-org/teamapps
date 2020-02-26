package org.teamapps.ux.task;

import org.junit.Assert;
import org.junit.Test;

public class ProgressCompletableFutureTest {

	public static final String EXCEPTION_RESULT = "exception thrown";

	@Test
	public void testRunAsyncException() throws Exception {
		String result = ProgressCompletableFuture.<String>supplyAsync(progressMonitor -> {
			throw new RuntimeException();
		})
				.thenApply(a -> a)
				.exceptionally(throwable -> EXCEPTION_RESULT)
				.get();

		Assert.assertEquals(EXCEPTION_RESULT, result);
	}
}