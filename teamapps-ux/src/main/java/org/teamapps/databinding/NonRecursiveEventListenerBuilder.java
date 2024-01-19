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
package org.teamapps.databinding;

import java.util.function.Consumer;

public class NonRecursiveEventListenerBuilder {
	private final ThreadLocal<Boolean> processing = new ThreadLocal<>();

	public <EVENT_DATA> Consumer<EVENT_DATA> create(Consumer<EVENT_DATA> handler) {
		return (eventData) -> {
			if (!isAlreadyProcessing()) {
				processing.set(true);
				try {
					handler.accept(eventData);
				} finally {
					processing.set(false);
				}
			}
		};
	}

	private boolean isAlreadyProcessing() {
		return processing.get() != null && processing.get();
	}
}
