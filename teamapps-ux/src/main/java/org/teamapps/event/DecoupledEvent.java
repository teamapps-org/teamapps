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
package org.teamapps.event;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Forces events to be handled in a different Thread than the firing one.
 */
public class DecoupledEvent<EVENT_DATA> extends Event<EVENT_DATA> {

	private final Executor executor;

	public DecoupledEvent(Executor executor) {
		this.executor = executor;
	}

	@Override
	protected void invokeListener(EVENT_DATA eventData, Consumer<EVENT_DATA> listener) {
		executor.execute(() -> super.invokeListener(eventData, listener));
	}
}
