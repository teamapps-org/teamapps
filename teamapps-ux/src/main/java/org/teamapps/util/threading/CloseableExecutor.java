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
