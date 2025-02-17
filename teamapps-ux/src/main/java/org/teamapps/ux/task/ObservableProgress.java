/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.task;

import org.teamapps.event.Event;

public interface ObservableProgress {

	Event<ProgressChangeEventData> onChanged();

	ProgressStatus getStatus();

	String getStatusMessage();

	double getProgress();

	default boolean isProgressUnknown() {
		return getProgress() < 0;
	}

	boolean isCancelable();

	void requestCancellation(); // will get ignored when not cancelable!

	boolean isCancellationRequested();

}
