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

import org.teamapps.icons.Icon;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.components.core.CoreComponentLibrary;
import org.teamapps.projector.components.core.div.Div;
import org.teamapps.projector.components.core.flexcontainer.VerticalLayout;
import org.teamapps.projector.components.core.notification.Notification;
import org.teamapps.projector.components.core.notification.NotificationPosition;
import org.teamapps.projector.components.core.notification.Notifications;
import org.teamapps.projector.dto.DtoDefaultMultiProgressDisplay;
import org.teamapps.projector.dto.DtoDefaultMultiProgressDisplayClientObjectChannel;
import org.teamapps.projector.dto.DtoMultiProgressDisplay;
import org.teamapps.projector.dto.DtoMultiProgressDisplayEventHandler;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.format.Spacing;
import org.teamapps.projector.i18n.TeamAppsTranslationKeys;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class DefaultMultiProgressDisplay extends AbstractComponent implements MultiProgressDisplay, DtoMultiProgressDisplayEventHandler {

	private final DtoDefaultMultiProgressDisplayClientObjectChannel clientObjectChannel = new DtoDefaultMultiProgressDisplayClientObjectChannel(getClientObjectChannel());
	
	public final ProjectorEvent<Void> onClicked = new ProjectorEvent<>(clientObjectChannel::toggleClickedEvent);

	private final List<ObservableProgress> progresses = new ArrayList<>();
	private final Notification progressListNotification;
	private final VerticalLayout progressListVerticalLayout;
	private final Div noEntriesDiv;

	private boolean showNotificationOnProgressAdded = true;
	private int notificationDisplayTimeMillis = 3000;
	private final NotificationPosition notificationPosition = NotificationPosition.BOTTOM_RIGHT;
	private int listEntryRemainTimeout = 5000;

	private boolean showingNotificationWithoutTimeout = false;

	public DefaultMultiProgressDisplay() {
		progressListVerticalLayout = new VerticalLayout();
		noEntriesDiv = new Div();
		noEntriesDiv.setInnerHtml("<div style=\"text-align: center\">" + getSessionContext().getLocalized(TeamAppsTranslationKeys.NO_RUNNING_TASKS.getKey()) + "</div>");
		progressListVerticalLayout.addComponent(noEntriesDiv);
		progressListNotification = new Notification(progressListVerticalLayout);
		progressListNotification.setPadding(new Spacing(2, 4, 2, 4));
		progressListNotification.setShowProgressBar(false);
		progressListNotification.onClosed.addListener(byUser -> {
			showingNotificationWithoutTimeout = false;
		});
	}

	@Override
	public DtoDefaultMultiProgressDisplay createConfig() {
		DtoDefaultMultiProgressDisplay ui = new DtoDefaultMultiProgressDisplay();
		mapAbstractUiComponentProperties(ui);
		ui.setRunningCount(progresses.size());
		ui.setStatusMessages(progresses.stream().map(p -> p.getStatusMessage()).collect(Collectors.toList()));
		return ui;
	}

	@Override
	public void handleClicked(DtoMultiProgressDisplay.ClickedEventWrapper eventObject) {
		this.onClicked.fire(null);
		if (showingNotificationWithoutTimeout) {
			this.showingNotificationWithoutTimeout = false;
			progressListNotification.close();
		} else {
			this.showingNotificationWithoutTimeout = true;
			if (progresses.size() > 0) {
				progressListNotification.setDisplayTimeInMillis(-1);
			} else {
				progressListNotification.setDisplayTimeInMillis(2000);
			}
			Notifications.showNotification(getSessionContext(), progressListNotification, notificationPosition);
		}
	}

	@Override
	public void addProgress(Icon<?, ?> icon, String taskName, ObservableProgress progress) {
		this.progresses.add(progress);
		progressListVerticalLayout.removeComponent(noEntriesDiv);
		ProgressDisplay progressDisplay = new ProgressDisplay(icon, taskName, progress);
		progressDisplay.setCssStyle("margin", "2 0 2 0");
		progressListVerticalLayout.addComponent(progressDisplay);
		progress.onChanged().addListener(data -> {
			if (EnumSet.of(ProgressStatus.CANCELED, ProgressStatus.COMPLETE, ProgressStatus.FAILED).contains(data.getStatus())) {
				showNotificationDueToUpdate();
				this.progresses.remove(progress);
				this.update();

				new CompletableFuture<Void>().completeOnTimeout(null, listEntryRemainTimeout, TimeUnit.MILLISECONDS)
						.handle((aVoid, throwable) -> {
							return getSessionContext().runWithContext(() -> {
								progressListVerticalLayout.removeComponent(progressDisplay);
								if (progressListVerticalLayout.getComponents().isEmpty()) {
									progressListVerticalLayout.addComponent(noEntriesDiv);
								}
								if (this.progresses.isEmpty()) {
									showingNotificationWithoutTimeout = false;
									this.progressListNotification.close();
								}
								return null;
							});
						});
			}
		});
		showNotificationDueToUpdate();
		this.update();
	}

	private void showNotificationDueToUpdate() {
		if (!showingNotificationWithoutTimeout) {
			progressListNotification.setDisplayTimeInMillis(notificationDisplayTimeMillis);
			Notifications.showNotification(getSessionContext(), progressListNotification, notificationPosition);
		}
	}

	private void update() {
		clientObjectChannel.update(createConfig());
	}

	public boolean isShowNotificationOnProgressAdded() {
		return showNotificationOnProgressAdded;
	}

	public void setShowNotificationOnProgressAdded(boolean showNotificationOnProgressAdded) {
		this.showNotificationOnProgressAdded = showNotificationOnProgressAdded;
	}

	public int getNotificationDisplayTimeMillis() {
		return notificationDisplayTimeMillis;
	}

	public void setNotificationDisplayTimeMillis(int notificationDisplayTimeMillis) {
		this.notificationDisplayTimeMillis = notificationDisplayTimeMillis;
	}

	public int getListEntryRemainTimeout() {
		return listEntryRemainTimeout;
	}

	public void setListEntryRemainTimeout(int listEntryRemainTimeout) {
		this.listEntryRemainTimeout = listEntryRemainTimeout;
	}
}
