/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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

import org.teamapps.projector.event.ProjectorEvent;

public class Progress implements ProgressMonitor, ObservableProgress {

	public final ProjectorEvent<ProgressChangeEventData> onChanged = new ProjectorEvent<>();

	private String statusMessage;
	private double progress = -1;
	private boolean cancelable = false;
	private ProgressStatus status = ProgressStatus.NOT_YET_STARTED;

	@Override
	public ProjectorEvent<ProgressChangeEventData> onChanged() {
		return onChanged;
	}

	@Override
	public String getStatusMessage() {
		return statusMessage;
	}

	@Override
	public double getProgress() {
		return progress;
	}

	@Override
	public boolean isCancelable() {
		return cancelable;
	}

	@Override
	public void requestCancellation() {
		if (status.ordinal() < ProgressStatus.CANCELLATION_REQUESTED.ordinal()) {
			this.status = ProgressStatus.CANCELLATION_REQUESTED;
			fireChangeEvent();
		}
	}

	@Override
	public boolean isCancellationRequested() {
		return status == ProgressStatus.CANCELLATION_REQUESTED;
	}

	@Override
	public ProgressStatus getStatus() {
		return status;
	}

	@Override
	public void start() {
		startIfNotYetStarted();
	}

	private void startIfNotYetStarted() {
		if (status.ordinal() < ProgressStatus.RUNNING.ordinal()) {
			status = ProgressStatus.RUNNING;
			fireChangeEvent();
		}
	}

	@Override
	public void setProgress(double progress) {
		startIfNotYetStarted();
		if (status.ordinal() == ProgressStatus.RUNNING.ordinal()) {
			this.progress = progress;
			fireChangeEvent();
		}
	}

	@Override
	public void setProgress(double progress, String statusMessage) {
		startIfNotYetStarted();
		if (status.ordinal() == ProgressStatus.RUNNING.ordinal()) {
			this.progress = progress;
			this.statusMessage = statusMessage;
			fireChangeEvent();
		}
	}

	@Override
	public void setStatusMessage(String statusMessage) {
		startIfNotYetStarted();
		if (status.ordinal() >= ProgressStatus.RUNNING.ordinal()) {
			this.statusMessage = statusMessage;
			fireChangeEvent();
		}
	}

	@Override
	public void markCanceled() {
		if (status.ordinal() < ProgressStatus.CANCELED.ordinal()) {
			this.status = ProgressStatus.CANCELED;
			fireChangeEvent();
		}
	}

	@Override
	public void markCanceled(String statusMessage) {
		if (status.ordinal() < ProgressStatus.CANCELED.ordinal()) {
			this.status = ProgressStatus.CANCELED;
		}
		if (this.status == ProgressStatus.CANCELED) { // allow for changing the status message even if done!
			this.statusMessage = statusMessage;
			fireChangeEvent();
		}
	}

	@Override
	public void markCompleted() {
		if (status.ordinal() < ProgressStatus.CANCELED.ordinal()) {
			this.status = ProgressStatus.COMPLETE;
			fireChangeEvent();
		}
	}

	@Override
	public void markCompleted(String statusMessage) {
		if (status.ordinal() < ProgressStatus.CANCELED.ordinal()) {
			this.status = ProgressStatus.COMPLETE;
		}
		if (this.status == ProgressStatus.COMPLETE) { // allow for changing the status message even if done!
			this.statusMessage = statusMessage;
			fireChangeEvent();
		}
	}

	@Override
	public void markFailed() {
		if (status.ordinal() < ProgressStatus.CANCELED.ordinal()) {
			this.status = ProgressStatus.FAILED;
			fireChangeEvent();
		}
	}

	@Override
	public void markFailed(String message) {
		if (status.ordinal() < ProgressStatus.CANCELED.ordinal()) {
			this.status = ProgressStatus.FAILED;
		}
		if (this.status == ProgressStatus.FAILED) { // allow for changing the status message even if done!
			this.statusMessage = message;
			fireChangeEvent();
		}
	}

	@Override
	public void setCancelable(boolean cancelable) {
		if (status.ordinal() < ProgressStatus.CANCELED.ordinal()) {
			this.cancelable = cancelable;
			fireChangeEvent();
		}
	}

	private void fireChangeEvent() {
		this.onChanged().fire(new ProgressChangeEventData(status, statusMessage, progress, cancelable));
	}
}
