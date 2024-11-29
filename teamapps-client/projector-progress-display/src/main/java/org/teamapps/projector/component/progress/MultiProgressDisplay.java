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

import org.teamapps.icons.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.notification.NotificationPosition;

import java.time.Duration;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@ClientObjectLibrary(value = ProgressComponentLibrary.class)
public class MultiProgressDisplay extends AbstractComponent implements DtoMultiProgressDisplayEventHandler, Component {

	private final DtoMultiProgressDisplayClientObjectChannel clientObjectChannel = new DtoMultiProgressDisplayClientObjectChannel(getClientObjectChannel());
	
	public final ProjectorEvent<Void> onOpened = new ProjectorEvent<>(clientObjectChannel::toggleOpenedEvent);
	public final ProjectorEvent<Void> onClosed = new ProjectorEvent<>(clientObjectChannel::toggleClosedEvent);

	private final Map<ObservableProgress, ProgressDisplay> progressDisplaysByProgress = new IdentityHashMap<>();

	private final NotificationPosition notificationPosition = NotificationPosition.BOTTOM_RIGHT;
	private int displayTimeout = 3000;

	/**
	 * The timeout after which a progress will be removed after completing/failing/being canceled.
	 */
	private Duration progressRemovalTimeout = Duration.ofSeconds(5);

	public MultiProgressDisplay() {
	}

	@Override
	public DtoMultiProgressDisplay createConfig() {
		DtoMultiProgressDisplay ui = new DtoMultiProgressDisplay();
		mapAbstractConfigProperties(ui);
		ui.setPosition(notificationPosition);
		return ui;
	}

	@Override
	public void handleOpened() {
		this.onOpened.fire();
	}

	@Override
	public void handleClosed() {
		this.onClosed.fire();
	}

	public void addProgress(Icon icon, String taskName, ObservableProgress progress) {
		ProgressDisplay progressDisplay = new ProgressDisplay(icon, taskName, progress);
		this.progressDisplaysByProgress.put(progress, progressDisplay);
		progress.onChanged().addListener(data -> {
			if (EnumSet.of(ProgressStatus.CANCELED, ProgressStatus.COMPLETE, ProgressStatus.FAILED).contains(data.getStatus())) {
				clientObjectChannel.open(displayTimeout);
				new CompletableFuture<Void>().completeOnTimeout(null, progressRemovalTimeout.toMillis(), TimeUnit.MILLISECONDS)
						.handle((aVoid, throwable) -> getSessionContext().runWithContext(() -> removeProgress(progress)));
			}
		});
		clientObjectChannel.open(displayTimeout);
		clientObjectChannel.add(progressDisplay);
	}

	public void removeProgress(ObservableProgress progress) {
		ProgressDisplay progressDisplay = progressDisplaysByProgress.remove(progress);
		if (progressDisplay != null) {
			clientObjectChannel.remove(progressDisplay);
		}
	}

	public void open() {
		clientObjectChannel.open(-1);
	}

	public void close() {
		clientObjectChannel.close();
	}

	public int getDisplayTimeout() {
		return displayTimeout;
	}

	public void setDisplayTimeout(int displayTimeout) {
		this.displayTimeout = displayTimeout;
	}

	public Duration getProgressRemovalTimeout() {
		return progressRemovalTimeout;
	}

	public void setProgressRemovalTimeout(Duration progressRemovalTimeout) {
		this.progressRemovalTimeout = progressRemovalTimeout;
	}
}
