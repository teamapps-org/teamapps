/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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

import org.teamapps.event.Event;

import java.util.function.Consumer;

public class ObservableSessionValue<T> {

	private final Event<T> onChanged = new Event<>();
	private final Event<T> onBeforeChanged = new Event<>();
	private T value;

	private final boolean notifyOnChangeOnly;

	public ObservableSessionValue() {
		this(true);
	}

	public ObservableSessionValue(boolean notifyOnChangeOnly) {
		this.notifyOnChangeOnly = notifyOnChangeOnly;
	}

	public void set(T value) {
		if (notifyOnChangeOnly) {
			updateOnChange(value);
		} else {
			updateAlways(value);
		}
	}

	private void updateAlways(T value) {
		onBeforeChanged.fireIfChanged(value);
		this.value = value;
		onChanged.fireIfChanged(value);
	}

	private void updateOnChange(T value) {
		onBeforeChanged.fire(value);
		this.value = value;
		onChanged.fire(value);
	}

	public T get() {
		return value;
	}

	public void addOnChangeListener(Consumer<T> listener) {
		onChanged.addListener(listener);
	}

	public void removeOnChangeListener(Consumer<T> listener) {
		onChanged.removeListener(listener);
	}

	public void addOnBeforeChangeListener(Consumer<T> listener) {
		onBeforeChanged.addListener(listener);
	}

	public void removeBeforeOnChangeListener(Consumer<T> listener) {
		onBeforeChanged.removeListener(listener);
	}

}
