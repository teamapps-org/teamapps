/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
package org.teamapps.ux.session;

import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.ext.ParamConverterProvider;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.teamapps.dto.UiNotification;
import org.teamapps.dto.UiQuery;
import org.teamapps.dto.UiRootPanel;
import org.teamapps.dto.UiWindow;
import org.teamapps.icons.SessionIconProvider;
import org.teamapps.server.UxServerContext;
import org.teamapps.testutil.UxTestUtil;
import org.teamapps.uisession.UiCommandWithResultCallback;
import org.teamapps.uisession.UiSession;
import org.teamapps.ux.component.div.Div;
import org.teamapps.ux.component.notification.Notification;
import org.teamapps.ux.component.notification.NotificationPosition;
import org.teamapps.ux.component.popup.Popup;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.window.Window;
import org.teamapps.ux.session.navigation.Location;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.teamapps.common.TeamAppsVersion.TEAMAPPS_VERSION;

public class SessionContextClientObjectGcTest {

	private final UiSession uiSession = Mockito.mock(UiSession.class);

	private SessionContext createSessionContext(boolean clientObjectGarbageCollectionEnabled) {
		ClientInfo clientInfo = new ClientInfo("ip", 1024, 768, 1000, 700, "en", false, "Europe/Berlin", 120, Collections.emptyList(),
				"userAgentString", Mockito.mock(Location.class), Collections.emptyMap(), TEAMAPPS_VERSION);
		return new SessionContext(
				uiSession,
				Executors.newSingleThreadExecutor(),
				clientInfo, SessionConfiguration.createForClientInfo(clientInfo), Mockito.mock(HttpSession.class),
				Mockito.mock(UxServerContext.class),
				Mockito.mock(SessionIconProvider.class),
				"",
				Mockito.mock(ParamConverterProvider.class),
				clientObjectGarbageCollectionEnabled
		);
	}

	@Test
	public void unreferencedComponentGetsCollectedAndClientSideCounterpartDestroyed() {
		SessionContext sessionContext = createSessionContext(true);
		String componentId = renderThrowAwayComponent(sessionContext);

		Awaitility.await().atMost(30, SECONDS).until(() -> {
			System.gc();
			UxTestUtil.runWithSessionContext(sessionContext, sessionContext::drainCollectedClientObjects);
			return !destroyCommandsSentFor(componentId).isEmpty();
		});

		assertThat(sessionContext.getClientObject(componentId)).isNull();
		assertThat(destroyCommandsSentFor(componentId)).hasSize(1);
		assertThat(sessionContext.getCollectedClientObjectsCount()).isEqualTo(1);
	}

	@Test
	public void componentsStayReferencedWhenGcIsDisabled() {
		SessionContext sessionContext = createSessionContext(false);
		String componentId = renderThrowAwayComponent(sessionContext);

		attemptGc(sessionContext);

		assertThat(sessionContext.getClientObject(componentId)).isNotNull();
		assertThat(destroyCommandsSentFor(componentId)).isEmpty();
	}

	@Test
	public void closedWindowStaysReferencedWhenGcIsDisabled() {
		SessionContext sessionContext = createSessionContext(false);
		AtomicReference<String> id = new AtomicReference<>();
		UxTestUtil.runWithSessionContext(sessionContext, () -> {
			Window window = new Window();
			window.show(0);
			id.set(window.getId());
			window.close(0);
		});

		attemptGc(sessionContext);

		assertThat(sessionContext.getClientObject(id.get())).isNotNull();
		assertThat(destroyCommandsSentFor(id.get())).isEmpty();
	}

	@Test
	public void rootPanelStaysReferencedAlthoughApplicationDropsIt() {
		SessionContext sessionContext = createSessionContext(true);
		AtomicReference<String> id = new AtomicReference<>();
		UxTestUtil.runWithSessionContext(sessionContext, () -> {
			RootPanel rootPanel = sessionContext.addRootPanel();
			id.set(rootPanel.getId());
		});

		attemptGc(sessionContext);

		assertThat(sessionContext.getClientObject(id.get())).isNotNull();
	}

	@Test
	public void allRootPanelsForTheSameSelectorStayReferenced() {
		SessionContext sessionContext = createSessionContext(true);
		AtomicReference<String> firstId = new AtomicReference<>();
		AtomicReference<String> secondId = new AtomicReference<>();
		UxTestUtil.runWithSessionContext(sessionContext, () -> {
			firstId.set(sessionContext.addRootPanel().getId()); // container selector "body"
			secondId.set(sessionContext.addRootPanel().getId()); // same selector: the client appends, displaying both
		});

		attemptGc(sessionContext);

		// UiRootPanel.buildRootPanel appends to the container element, so both root panels remain displayed
		// and must both stay strongly referenced (see SessionContext#attachedRootComponents)
		assertThat(sessionContext.getClientObject(firstId.get())).isNotNull();
		assertThat(sessionContext.getClientObject(secondId.get())).isNotNull();
		assertThat(destroyCommandsSentFor(firstId.get())).isEmpty();
		assertThat(destroyCommandsSentFor(secondId.get())).isEmpty();
	}

	@Test
	public void shownWindowIsPinnedAndCollectableAfterServerSideClose() {
		SessionContext sessionContext = createSessionContext(true);
		AtomicReference<String> id = new AtomicReference<>();
		UxTestUtil.runWithSessionContext(sessionContext, () -> {
			Window window = new Window();
			window.show(0);
			id.set(window.getId());
		});

		attemptGc(sessionContext);
		assertThat(sessionContext.getClientObject(id.get())).isNotNull();

		UxTestUtil.runWithSessionContext(sessionContext, () -> ((Window) sessionContext.getClientObject(id.get())).close(0));
		awaitCollected(sessionContext, id.get());
	}

	@Test
	public void shownWindowIsCollectableAfterClientSideClose() {
		SessionContext sessionContext = createSessionContext(true);
		AtomicReference<String> id = new AtomicReference<>();
		UxTestUtil.runWithSessionContext(sessionContext, () -> {
			Window window = new Window();
			window.show(0);
			id.set(window.getId());
		});

		attemptGc(sessionContext);
		assertThat(sessionContext.getClientObject(id.get())).isNotNull();

		UxTestUtil.runWithSessionContext(sessionContext,
				() -> ((Window) sessionContext.getClientObject(id.get())).handleUiEvent(new UiWindow.ClosedEvent(id.get())));
		awaitCollected(sessionContext, id.get());
	}

	@Test
	public void shownPopupIsPinnedAndCollectableAfterClose() {
		SessionContext sessionContext = createSessionContext(true);
		AtomicReference<String> id = new AtomicReference<>();
		UxTestUtil.runWithSessionContext(sessionContext, () -> {
			Popup popup = new Popup(new Div());
			sessionContext.showPopup(popup);
			id.set(popup.getId());
		});

		attemptGc(sessionContext);
		assertThat(sessionContext.getClientObject(id.get())).isNotNull();

		UxTestUtil.runWithSessionContext(sessionContext, () -> ((Popup) sessionContext.getClientObject(id.get())).close());
		awaitCollected(sessionContext, id.get());
	}

	@Test
	public void shownNotificationIsPinnedAndCollectableAfterClientSideClose() {
		SessionContext sessionContext = createSessionContext(true);
		AtomicReference<String> id = new AtomicReference<>();
		UxTestUtil.runWithSessionContext(sessionContext, () -> {
			Notification notification = new Notification(new Div());
			sessionContext.showNotification(notification, NotificationPosition.TOP_RIGHT);
			id.set(notification.getId());
		});

		attemptGc(sessionContext);
		assertThat(sessionContext.getClientObject(id.get())).isNotNull();

		UxTestUtil.runWithSessionContext(sessionContext,
				() -> ((Notification) sessionContext.getClientObject(id.get())).handleUiEvent(new UiNotification.ClosedEvent(id.get(), true)));
		awaitCollected(sessionContext, id.get());
	}

	@Test
	public void explicitlyUnrenderedComponentDoesNotGetDestroyedTwice() {
		SessionContext sessionContext = createSessionContext(true);
		AtomicReference<String> id = new AtomicReference<>();
		UxTestUtil.runWithSessionContext(sessionContext, () -> {
			Div div = new Div();
			div.render();
			id.set(div.getId());
			div.unrender();
		});
		assertThat(destroyCommandsSentFor(id.get())).hasSize(1);

		// simulate the client acknowledging the destroy command, which unregisters the component
		ArgumentCaptor<UiCommandWithResultCallback> commandCaptor = ArgumentCaptor.forClass(UiCommandWithResultCallback.class);
		Mockito.verify(uiSession, Mockito.atLeastOnce()).sendCommand(commandCaptor.capture());
		UiCommandWithResultCallback destroyCommandWithCallback = commandCaptor.getAllValues().stream()
				.filter(c -> c.getUiCommand() instanceof UiRootPanel.DestroyComponentCommand)
				.findFirst().orElseThrow();
		UxTestUtil.runWithSessionContext(sessionContext, () -> destroyCommandWithCallback.getResultCallback().accept(null));
		assertThat(sessionContext.getClientObject(id.get())).isNull();

		// let Mockito forget the recorded invocations (they strongly reference the component via the result callback)
		Mockito.clearInvocations(uiSession);
		attemptGc(sessionContext);

		assertThat(destroyCommandsSentFor(id.get())).isEmpty();
	}

	@Test
	public void registrySizeStaysBoundedWhenContentIsReplacedRepeatedly() {
		SessionContext sessionContext = createSessionContext(true);
		AtomicReference<RootPanel> rootPanel = new AtomicReference<>();
		UxTestUtil.runWithSessionContext(sessionContext, () -> rootPanel.set(sessionContext.addRootPanel()));

		for (int i = 0; i < 100; i++) {
			UxTestUtil.runWithSessionContext(sessionContext, () -> rootPanel.get().setContent(new Div(new Div())));
		}
		assertThat(sessionContext.getClientObjectCount()).isGreaterThanOrEqualTo(200); // the leak, before collection

		// all discarded content (100 iterations à 2 Divs, minus the 2 still attached) must get collected
		Awaitility.await().atMost(30, SECONDS).until(() -> {
			System.gc();
			UxTestUtil.runWithSessionContext(sessionContext, sessionContext::drainCollectedClientObjects);
			return sessionContext.getCollectedClientObjectsCount() >= 198;
		});
		// the root panel and the currently attached Divs must survive
		assertThat(sessionContext.getClientObject(rootPanel.get().getId())).isNotNull();
		assertThat(sessionContext.getClientObject(((Div) rootPanel.get().getContent()).getId())).isNotNull();
	}

	@Test
	public void eventsAndQueriesForUnknownComponentsAreTolerated() {
		SessionContext sessionContext = createSessionContext(true);

		UxTestUtil.runWithSessionContext(sessionContext,
				() -> sessionContext.getAsUiSessionListenerInternal().onUiEvent("session-id", new UiWindow.ClosedEvent("unknown-component-id")));

		UiQuery query = Mockito.mock(UiQuery.class);
		Mockito.when(query.getComponentId()).thenReturn("unknown-component-id");
		AtomicBoolean resultCallbackCalled = new AtomicBoolean();
		AtomicReference<Object> result = new AtomicReference<>();
		UxTestUtil.runWithSessionContext(sessionContext,
				() -> sessionContext.getAsUiSessionListenerInternal().onUiQuery("session-id", query, r -> {
					resultCallbackCalled.set(true);
					result.set(r);
				}));

		assertThat(resultCallbackCalled).isTrue();
		assertThat(result.get()).isNull();
		// the session must not have been destroyed
		Mockito.verify(uiSession, Mockito.never()).close(Mockito.any());
	}

	private String renderThrowAwayComponent(SessionContext sessionContext) {
		AtomicReference<String> id = new AtomicReference<>();
		UxTestUtil.runWithSessionContext(sessionContext, () -> {
			Div div = new Div();
			div.render();
			id.set(div.getId());
			assertThat(sessionContext.getClientObject(id.get())).isNotNull();
		});
		return id.get();
	}

	private void awaitCollected(SessionContext sessionContext, String componentId) {
		Awaitility.await().atMost(30, SECONDS).until(() -> {
			System.gc();
			UxTestUtil.runWithSessionContext(sessionContext, sessionContext::drainCollectedClientObjects);
			return sessionContext.getClientObject(componentId) == null;
		});
	}

	private void attemptGc(SessionContext sessionContext) {
		for (int i = 0; i < 5; i++) {
			System.gc();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			UxTestUtil.runWithSessionContext(sessionContext, sessionContext::drainCollectedClientObjects);
		}
	}

	private List<UiRootPanel.DestroyComponentCommand> destroyCommandsSentFor(String componentId) {
		ArgumentCaptor<UiCommandWithResultCallback> commandCaptor = ArgumentCaptor.forClass(UiCommandWithResultCallback.class);
		Mockito.verify(uiSession, Mockito.atLeast(0)).sendCommand(commandCaptor.capture());
		return commandCaptor.getAllValues().stream()
				.map(UiCommandWithResultCallback::getUiCommand)
				.filter(c -> c instanceof UiRootPanel.DestroyComponentCommand)
				.map(c -> (UiRootPanel.DestroyComponentCommand) c)
				.filter(c -> componentId.equals(c.getId()))
				.collect(Collectors.toList());
	}
}
