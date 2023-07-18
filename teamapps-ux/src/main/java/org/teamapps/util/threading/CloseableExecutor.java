package org.teamapps.util.threading;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public interface CloseableExecutor extends Executor {

	void close();

	static CloseableExecutor fromExecutorService(ExecutorService executorService) {
		return new CloseableExecutor() {
			@Override
			public void close() {
				executorService.shutdown();
			}

			@Override
			public void execute(Runnable command) {
				executorService.execute(command);
			}
		};
	}

}
