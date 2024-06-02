/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.projector.clientrecordcache;

/**
 * Represents the result of a manipulation to the cache, on the server side.
 * Also provides the facility to commit the change to the client side cache (when the client acknowledged the change).
 * @param <R>
 */
public class CacheManipulationHandle<R> {
	private R result;
	private final Runnable committingActions;

	public CacheManipulationHandle(R result, Runnable committingActions) {
		this.result = result;
		this.committingActions = committingActions;
	}

	public R getAndClearResult() {
		R result = this.result;
		this.result = null;
		return result;
	}

	public void commit() {
		committingActions.run();
	}
}
