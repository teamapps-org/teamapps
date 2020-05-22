/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

/**
 * Only the first invocation of cancel(), complete() or fail() will be honored!
 */
public interface ProgressMonitor {

	void start();

	void setStatusMessage(String statusMessage);

	void setProgress(double progress);

	void setProgress(double progress, String statusMessage);

	void markCanceled();

	void markCanceled(String statusMessage);

	void markCompleted();

	void markCompleted(String statusMessage);

	void markFailed();

	void markFailed(String message);

	void setCancelable(boolean cancelable);

	boolean isCancelable();

	boolean isCancellationRequested();

}
