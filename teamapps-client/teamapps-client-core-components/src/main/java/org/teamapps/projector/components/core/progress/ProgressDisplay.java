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
package org.teamapps.projector.components.core.progress;

import org.teamapps.commons.event.Disposable;
import org.teamapps.icons.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.components.core.CoreComponentLibrary;
import org.teamapps.projector.dto.DtoProgressDisplay;
import org.teamapps.projector.dto.DtoProgressDisplayClientObjectChannel;
import org.teamapps.projector.dto.DtoProgressDisplayEventHandler;
import org.teamapps.projector.event.ProjectorEvent;

/**
 * This component displays progress information.
 * <p>
 * It is typically used to visualize the progress of an {@link ObservableProgress}.
 * However, it can also be used manually, without attaching a progress.
 */
@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class ProgressDisplay extends AbstractComponent implements DtoProgressDisplayEventHandler {

	private final DtoProgressDisplayClientObjectChannel clientObjectChannel = new DtoProgressDisplayClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Void> onClicked = new ProjectorEvent<>(clientObjectChannel::toggleClickedEvent);
	public final ProjectorEvent<Void> onCancelButtonClicked = new ProjectorEvent<>(clientObjectChannel::toggleClickedEvent);

	private Icon<?, ?> icon;
	private String taskName;
	private String statusMessage;
	private double progress;
	private ProgressStatus status = ProgressStatus.NOT_YET_STARTED;
	private boolean cancelable;

	private ObservableProgress observedProgress;

	private Disposable observedProgressChangeListener;

	public ProgressDisplay() {
		this(null, null, null);
	}

	public ProgressDisplay(Icon<?, ?> icon, String taskName) {
		this(icon, taskName, null);
	}

	public ProgressDisplay(Icon<?, ?> icon, String taskName, ObservableProgress progress) {
		this.icon = icon;
		this.taskName = taskName;
		setObservedProgress(progress);
	}

	@Override
	public DtoProgressDisplay createConfig() {
		DtoProgressDisplay ui = new DtoProgressDisplay();
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
		clientObjectChannel.update(createConfig());
	}

	@Override
	public void handleClicked() {
		this.onClicked.fire();
	}

	@Override
	public void handleCancelButtonClicked() {
		if (this.observedProgress != null) {
			this.observedProgress.requestCancellation();
		}
		this.onCancelButtonClicked.fire();
	}

	public void setObservedProgress(ObservableProgress observableProgress) {
		if (this.observedProgress != null) {
			observedProgressChangeListener.dispose();
		}

		this.observedProgress = observableProgress;

		if (observableProgress != null) {
			observedProgressChangeListener = observableProgress.onChanged().addListener(data -> {
				setStatus(data.getStatus());
				setStatusMessage(data.getStatusMessage());
				setProgress(data.getProgress());
				setCancelable(data.isCancelable());
				this.updateUi();
			});
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

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public void setIcon(Icon<?, ?> icon) {
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
