/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.cache;

public class CacheManipulationHandle<R> {
	private final ClientRecordCache cache;
	private R result;
	private final Runnable committingActions;
	private final int operationSequenceNumber;

	public CacheManipulationHandle(ClientRecordCache cache, int operationSequenceNumber, R result, Runnable committingActions) {
		this.cache = cache;
		this.operationSequenceNumber = operationSequenceNumber;
		this.result = result;
		this.committingActions = committingActions;
	}

	public R getAndClearResult() {
		R result = this.result;
		this.result = null;
		return result;
	}

	public void commit() {
		if (cache.getOperationInvalidationSequenceNumber() < operationSequenceNumber) {
			committingActions.run();
		}
	}
}
