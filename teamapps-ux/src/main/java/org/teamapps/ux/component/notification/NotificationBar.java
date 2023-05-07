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
package org.teamapps.ux.component.notification;

import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoNotificationBar;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.teamapps.ux.component.notification.NotificationBarItemClosedEvent.ClosingReason.TIMEOUT;
import static org.teamapps.ux.component.notification.NotificationBarItemClosedEvent.ClosingReason.USER;

@TeamAppsComponent(library = CoreComponentLibrary.class)
public class NotificationBar extends AbstractComponent {

	public final ProjectorEvent<NotificationBarItemClosedEvent> onItemClosed = createProjectorEventBoundToUiEvent(DtoNotificationBar.ItemClosedEvent.TYPE_ID);
	public final ProjectorEvent<NotificationBarItem> onItemClicked = createProjectorEventBoundToUiEvent(DtoNotificationBar.ItemClickedEvent.TYPE_ID);
	public final ProjectorEvent<NotificationBarItem> onItemActionLinkClicked = createProjectorEventBoundToUiEvent(DtoNotificationBar.ItemActionLinkClickedEvent.TYPE_ID);

	private final Map<String, NotificationBarItem> itemsByUiId = new LinkedHashMap<>();

	public NotificationBar() {
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoNotificationBar.ItemClickedEvent.TYPE_ID -> {
				var e = event.as(DtoNotificationBar.ItemClickedEventWrapper.class);
				NotificationBarItem item = itemsByUiId.get(e.getId());
				if (item != null) {
					item.onClicked.fire();
					onItemClicked.fire(item);
				}
			}
			case DtoNotificationBar.ItemActionLinkClickedEvent.TYPE_ID -> {
				var e = event.as(DtoNotificationBar.ItemActionLinkClickedEventWrapper.class);
				NotificationBarItem item = itemsByUiId.get(e.getId());
				if (item != null) {
					item.onActionLinkClicked.fire();
					onItemActionLinkClicked.fire(item);
				}
			}
			case DtoNotificationBar.ItemClosedEvent.TYPE_ID -> {
				var e = event.as(DtoNotificationBar.ItemClosedEventWrapper.class);
				NotificationBarItem item = itemsByUiId.get(e.getId());
				if (item != null) {
					NotificationBarItemClosedEvent.ClosingReason reason = e.getWasTimeout() ? TIMEOUT : USER;
					item.onClosed.fire(reason);
					onItemClosed.fire(new NotificationBarItemClosedEvent(item, reason));
				}
			}
		}

	}

	@Override
	public DtoComponent createDto() {
		DtoNotificationBar ui = new DtoNotificationBar();
		mapAbstractUiComponentProperties(ui);
		ui.setInitialItems(itemsByUiId.values().stream()
				.map(NotificationBarItem::toUiNotificationBarItem)
				.collect(Collectors.toList()));
		return ui;
	}

	public void addItem(NotificationBarItem item) {
		itemsByUiId.put(item.getUiId(), item);
		item.setListener(() -> sendCommandIfRendered(() -> new DtoNotificationBar.UpdateItemCommand(item.toUiNotificationBarItem())));
		sendCommandIfRendered(() -> new DtoNotificationBar.AddItemCommand(item.toUiNotificationBarItem()));
	}

	public void removeItem(NotificationBarItem item) {
		itemsByUiId.remove(item.getUiId());
		item.setListener(null);
		sendCommandIfRendered(() -> new DtoNotificationBar.RemoveItemCommand(item.getUiId(), null));
	}

}
