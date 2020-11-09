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
package org.teamapps.ux.component.progress;

import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiProgressDisplay;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.task.ObservableProgress;
import org.teamapps.ux.task.ProgressChangeEventData;
import org.teamapps.ux.task.ProgressStatus;

import java.util.function.Consumer;

/**
 * This component displays progress information.
 * <p>
 * It is typically used to visualize the progress of an {@link ObservableProgress}.
 * However, it can also be used manually, without attaching a progress.
 */
public class ProgressDisplay extends AbstractComponent {

	public final Event<Void> onClicked = new Event<>();
	public final Event<Void> onCancelButtonClicked = new Event<>();

	private Icon icon;
	private String taskName;
	private String statusMessage;
	private double progress;
	private ProgressStatus status = ProgressStatus.NOT_YET_STARTED;
	private boolean cancelable;

	private ObservableProgress observedProgress;
	
	private final Consumer<ProgressChangeEventData> observedProgressChangeListener = data -> {
		setStatus(data.getStatus());
		setStatusMessage(data.getStatusMessage());
		setProgress(data.getProgress());
		setCancelable(data.isCancelable());
		this.updateUi();
	};

	public ProgressDisplay() {
		this(null, null, null);
	}

	public ProgressDisplay(Icon icon, String taskName) {
		this(icon, taskName, null);
	}

	public ProgressDisplay(Icon icon, String taskName, ObservableProgress progress) {
		this.icon = icon;
		this.taskName = taskName;
		setObservedProgress(progress);
	}

	@Override
	public UiProgressDisplay createUiComponent() {
		UiProgressDisplay ui = new UiProgressDisplay();
		mapAbstractUiComponentProperties(ui);
		ui.setIcon(getSessionContext().resolveIcon(icon));
		ui.setTaskName(taskName);
		ui.setStatusMessage(statusMessage);
		ui.setProgress(progress);
		ui.setStatus(status.toUiProgressStatus());
		ui.setCancelable(cancelable);
		return ui;
	}

	private void updateUi() {
		queueCommandIfRendered(() -> new UiProgressDisplay.UpdateCommand(getId(), createUiComponent()));
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_PROGRESS_DISPLAY_CLICKED: {
				UiProgressDisplay.ClickedEvent clickedEvent = (UiProgressDisplay.ClickedEvent) event;
				this.onClicked.fire(null);
				break;
			}
			case UI_PROGRESS_DISPLAY_CANCEL_BUTTON_CLICKED: {
				UiProgressDisplay.CancelButtonClickedEvent cancelButtonClickedEvent = (UiProgressDisplay.CancelButtonClickedEvent) event;
				if (this.observedProgress != null) {
					this.observedProgress.requestCancellation();
				}
				this.onCancelButtonClicked.fire(null);
				break;
			}

		}

	}

	public void setObservedProgress(ObservableProgress observableProgress) {
		if (this.observedProgress != null) {
			this.observedProgress.onChanged().removeListener(this.observedProgressChangeListener);
		}

		this.observedProgress = observableProgress;

		if (observableProgress != null) {
			observableProgress.onChanged().addListener(this.observedProgressChangeListener);
			this.statusMessage = observableProgress.getStatusMessage();
			this.progress = observableProgress.getProgress();
			this.status = observableProgress.getStatus();
			this.cancelable = observableProgress.isCancelable();
			this.updateUi();
		}
	}

	public ObservableProgress getObservedProgress() {
		return observedProgress;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
		updateUi();
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
		updateUi();
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
		updateUi();
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
		updateUi();
	}

	public ProgressStatus getStatus() {
		return status;
	}

	public void setStatus(ProgressStatus status) {
		this.status = status;
		updateUi();
	}

	public boolean isCancelable() {
		return cancelable;
	}

	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
		updateUi();
	}


}
