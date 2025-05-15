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
package org.teamapps.ux.component.notification;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiNotificationBar;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.teamapps.ux.component.notification.NotificationBarItemClosedEvent.ClosingReason.TIMEOUT;
import static org.teamapps.ux.component.notification.NotificationBarItemClosedEvent.ClosingReason.USER;

public class NotificationBar extends AbstractComponent {

	public final Event<NotificationBarItemClosedEvent> onItemClosed = new Event<>();
	public final Event<NotificationBarItem> onItemClicked = new Event<>();
	public final Event<NotificationBarItem> onItemActionLinkClicked = new Event<>();

	private final Map<String, NotificationBarItem> itemsByUiId = new LinkedHashMap<>();

	public NotificationBar() {
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_NOTIFICATION_BAR_ITEM_CLICKED: {
				UiNotificationBar.ItemClickedEvent e = (UiNotificationBar.ItemClickedEvent) event;
				NotificationBarItem item = itemsByUiId.get(e.getId());
				if (item != null) {
					item.onClicked.fire();
					onItemClicked.fire(item);
				}
				break;
			}
			case UI_NOTIFICATION_BAR_ITEM_ACTION_LINK_CLICKED: {
				UiNotificationBar.ItemActionLinkClickedEvent e = (UiNotificationBar.ItemActionLinkClickedEvent) event;
				NotificationBarItem item = itemsByUiId.get(e.getId());
				if (item != null) {
					item.onActionLinkClicked.fire();
					onItemActionLinkClicked.fire(item);
				}
				break;
			}
			case UI_NOTIFICATION_BAR_ITEM_CLOSED: {
				UiNotificationBar.ItemClosedEvent e = (UiNotificationBar.ItemClosedEvent) event;
				NotificationBarItem item = itemsByUiId.get(e.getId());
				if (item != null) {
					NotificationBarItemClosedEvent.ClosingReason reason = e.getWasTimeout() ? TIMEOUT : USER;
					item.onClosed.fire(reason);
					onItemClosed.fire(new NotificationBarItemClosedEvent(item, reason));
				}
				break;
			}
		}
	}

	@Override
	public UiComponent createUiComponent() {
		UiNotificationBar ui = new UiNotificationBar();
		mapAbstractUiComponentProperties(ui);
		ui.setInitialItems(itemsByUiId.values().stream()
				.map(NotificationBarItem::toUiNotificationBarItem)
				.collect(Collectors.toList()));
		return ui;
	}

	public void addItem(NotificationBarItem item) {
		if (itemsByUiId.containsValue(item)) {
			return;
		}
		itemsByUiId.put(item.getUiId(), item);
		item.setListener(() -> queueCommandIfRendered(() -> new UiNotificationBar.UpdateItemCommand(getId(), item.toUiNotificationBarItem())));
		queueCommandIfRendered(() -> new UiNotificationBar.AddItemCommand(getId(), item.toUiNotificationBarItem()));
	}

	public void removeItem(NotificationBarItem item) {
		if (itemsByUiId.remove(item.getUiId()) != null) {
			item.setListener(null);
			queueCommandIfRendered(() -> new UiNotificationBar.RemoveItemCommand(getId(), item.getUiId(), null));
		}
	}

	public List<NotificationBarItem> getItems() {
		return List.copyOf(itemsByUiId.values());
	}

}
