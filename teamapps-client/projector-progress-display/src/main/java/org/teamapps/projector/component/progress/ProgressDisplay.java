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
package org.teamapps.projector.component.progress;

import org.teamapps.commons.event.Disposable;
import org.teamapps.icons.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.event.ProjectorEvent;

/**
 * This component displays progress information.
 * <p>
 * It is typically used to visualize the progress of an {@link ObservableProgress}.
 * However, it can also be used manually, without attaching a progress.
 */
@ClientObjectLibrary(value = ProgressComponentLibrary.class)
public class ProgressDisplay extends AbstractComponent implements DtoProgressDisplayEventHandler {

	private final DtoProgressDisplayClientObjectChannel clientObjectChannel = new DtoProgressDisplayClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Void> onClicked = new ProjectorEvent<>(clientObjectChannel::toggleClickedEvent);
	public final ProjectorEvent<Void> onCancelButtonClicked = new ProjectorEvent<>(clientObjectChannel::toggleClickedEvent);

	private Icon<?, ?> icon;
	private String taskName;
	private ObservableProgress progress;

	private Disposable listenerDisposable;

	public ProgressDisplay(Icon<?, ?> icon, String taskName, ObservableProgress progress) {
		this.icon = icon;
		this.taskName = taskName;
		setProgress(progress);
	}

	@Override
	public DtoProgressDisplay createConfig() {
		DtoProgressDisplay ui = new DtoProgressDisplay();
		mapAbstractConfigProperties(ui);
		ui.setIcon(getSessionContext().resolveIcon(icon));
		ui.setTaskName(taskName);
		ui.setStatusMessage(progress.getStatusMessage());
		ui.setProgress(progress.getProgress());
		ui.setStatus(progress.getStatus());
		ui.setCancelable(progress.isCancelable());
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
		if (this.progress != null) {
			this.progress.requestCancellation();
		}
		this.onCancelButtonClicked.fire();
	}

	public void setProgress(ObservableProgress progress) {
		if (this.progress != null) {
			listenerDisposable.dispose();
		}
		this.progress = progress;

		if (progress != null) {
			listenerDisposable = progress.onChanged().addListener(data -> this.updateUi());
			this.updateUi();
		}
	}

	public ObservableProgress getProgress() {
		return progress;
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

}
