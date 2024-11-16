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
package org.teamapps.projector.component.notificationbar;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.core.CoreComponentLibrary;
import org.teamapps.projector.component.core.DtoNotificationBar;
import org.teamapps.projector.component.core.DtoNotificationBarClientObjectChannel;
import org.teamapps.projector.component.core.DtoNotificationBarEventHandler;
import org.teamapps.projector.event.ProjectorEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.teamapps.projector.component.notificationbar.NotificationBarItemClosedEvent.ClosingReason.TIMEOUT;
import static org.teamapps.projector.component.notificationbar.NotificationBarItemClosedEvent.ClosingReason.USER;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class NotificationBar extends AbstractComponent implements DtoNotificationBarEventHandler {

	private final DtoNotificationBarClientObjectChannel clientObjectChannel = new DtoNotificationBarClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<NotificationBarItemClosedEvent> onItemClosed = new ProjectorEvent<>(clientObjectChannel::toggleItemClosedEvent);
	public final ProjectorEvent<NotificationBarItem> onItemClicked = new ProjectorEvent<>(clientObjectChannel::toggleItemClickedEvent);
	public final ProjectorEvent<NotificationBarItem> onItemActionLinkClicked = new ProjectorEvent<>(clientObjectChannel::toggleItemActionLinkClickedEvent);

	private final Map<String, NotificationBarItem> itemsByUiId = new LinkedHashMap<>();

	public NotificationBar() {
	}

	@Override
	public void handleItemClicked(String id) {
		NotificationBarItem item = itemsByUiId.get(id);
		if (item != null) {
			item.onClicked.fire();
			onItemClicked.fire(item);
		}
	}

	@Override
	public void handleItemActionLinkClicked(String id) {
		NotificationBarItem item = itemsByUiId.get(id);
		if (item != null) {
			item.onActionLinkClicked.fire();
			onItemActionLinkClicked.fire(item);
		}
	}

	@Override
	public void handleItemClosed(DtoNotificationBar.ItemClosedEventWrapper event) {
		NotificationBarItem item = itemsByUiId.get(event.getId());
		if (item != null) {
			NotificationBarItemClosedEvent.ClosingReason reason = event.isWasTimeout() ? TIMEOUT : USER;
			item.onClosed.fire(reason);
			onItemClosed.fire(new NotificationBarItemClosedEvent(item, reason));
		}
	}


	@Override
	public DtoComponent createConfig() {
		DtoNotificationBar ui = new DtoNotificationBar();
		mapAbstractConfigProperties(ui);
		ui.setInitialItems(itemsByUiId.values().stream()
				.map(NotificationBarItem::toUiNotificationBarItem)
				.collect(Collectors.toList()));
		return ui;
	}

	public void addItem(NotificationBarItem item) {
		itemsByUiId.put(item.getUiId(), item);
		item.setListener(() -> clientObjectChannel.updateItem(item.toUiNotificationBarItem()));
		clientObjectChannel.addItem(item.toUiNotificationBarItem());
	}

	public void removeItem(NotificationBarItem item) {
		itemsByUiId.remove(item.getUiId());
		item.setListener(null);
		clientObjectChannel.removeItem(item.getUiId(), null);
	}
}
