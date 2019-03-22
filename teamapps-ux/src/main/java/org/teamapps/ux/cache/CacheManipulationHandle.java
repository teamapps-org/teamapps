/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.cache;

public class CacheManipulationHandle<R> {
	private ClientRecordCache cache;
	private R result;
	private Runnable committingActions;
	private int operationSequenceNumber;

	public CacheManipulationHandle(ClientRecordCache cache, int operationSequenceNumber, R result, Runnable committingActions) {
		this.cache = cache;
		this.operationSequenceNumber = operationSequenceNumber;
		this.result = result;
		this.committingActions = committingActions;
	}

	public R getResult() {
		return result;
	}

	public void commit() {
		if (cache.getOperationInvalidationSequenceNumber() < operationSequenceNumber) {
			committingActions.run();
		}
	}
}
