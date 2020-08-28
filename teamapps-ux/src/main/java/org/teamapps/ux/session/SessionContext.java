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
package org.teamapps.ux.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.common.format.Color;
import org.teamapps.dto.UiCommand;
import org.teamapps.dto.UiRootPanel;
import org.teamapps.dto.UiSessionClosingReason;
import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;
import org.teamapps.icons.api.IconTheme;
import org.teamapps.server.UxServerContext;
import org.teamapps.uisession.ClientBackPressureInfo;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.uisession.UiCommandExecutor;
import org.teamapps.uisession.UiCommandWithResultCallback;
import org.teamapps.uisession.UiSessionActivityState;
import org.teamapps.util.MultiKeySequentialExecutor;
import org.teamapps.util.NamedThreadFactory;
import org.teamapps.ux.component.ClientObject;
import org.teamapps.ux.component.animation.EntranceAnimation;
import org.teamapps.ux.component.animation.ExitAnimation;
import org.teamapps.ux.component.notification.Notification;
import org.teamapps.ux.component.notification.NotificationPosition;
import org.teamapps.ux.component.popup.Popup;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.template.TemplateReference;
import org.teamapps.ux.component.window.Window;
import org.teamapps.ux.i18n.RankingTranslationProvider;
import org.teamapps.ux.i18n.TeamAppsTranslationProviderFactory;
import org.teamapps.ux.i18n.TranslationProvider;
import org.teamapps.ux.icon.IconBundle;
import org.teamapps.ux.icon.TeamAppsIconBundle;
import org.teamapps.ux.json.UxJacksonSerializationTemplate;
import org.teamapps.ux.resource.Resource;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SessionContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionContext.class);
	private static final MultiKeySequentialExecutor<SessionContext> sessionMultiKeyExecutor = new MultiKeySequentialExecutor<>(new ThreadPoolExecutor(
			Runtime.getRuntime().availableProcessors() / 2 + 1,
			Runtime.getRuntime().availableProcessors() * 2,
			60, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(),
			new NamedThreadFactory("teamapps-session-executor", true)
	));

	public final Event<UiSessionActivityState> onActivityStateChanged = new Event<>();
	public final Event<Void> onDestroyed = new Event<>();
	/**
	 * Decorators around all executions inside this SessionContext. These will be invoked when the Thread is already bound to the SessionContext, so SessionContext.current() will
	 * return this instance.
	 */
	public final ExecutionDecoratorStack executionDecorators = new ExecutionDecoratorStack();

	private boolean active = true;
	private volatile boolean destroyed = false;

	private final QualifiedUiSessionId sessionId;
	private final ClientInfo clientInfo;
	private final HttpSession httpSession;
	private final UiCommandExecutor commandExecutor;
	private final UxServerContext serverContext;
	private final UxJacksonSerializationTemplate uxJacksonSerializationTemplate;
	private final HashMap<String, ClientObject> clientObjectsById = new HashMap<>();
	private IconTheme iconTheme;
	private final ClientSessionResourceProvider sessionResourceProvider;

	private final RankingTranslationProvider rankingTranslationProvider;

	private final Map<String, Template> registeredTemplates = new ConcurrentHashMap<>();
	private SessionConfiguration sessionConfiguration = SessionConfiguration.createDefault();

	private List<Locale> acceptedLanguages;
	private final Map<String, Icon> bundleIconByKey = new HashMap<>();

	public SessionContext(QualifiedUiSessionId sessionId, ClientInfo clientInfo, HttpSession httpSession, UiCommandExecutor commandExecutor, UxServerContext serverContext, IconTheme iconTheme,
	                      ObjectMapper jacksonObjectMapper) {
		this.sessionId = sessionId;
		this.httpSession = httpSession;
		this.clientInfo = clientInfo;
		this.commandExecutor = commandExecutor;
		this.serverContext = serverContext;
		this.iconTheme = iconTheme;
		this.sessionResourceProvider = new ClientSessionResourceProvider(sessionId);
		this.uxJacksonSerializationTemplate = new UxJacksonSerializationTemplate(jacksonObjectMapper, this);
		this.rankingTranslationProvider = new RankingTranslationProvider();
		rankingTranslationProvider.addTranslationProvider(TeamAppsTranslationProviderFactory.createProvider());
		addIconBundle(TeamAppsIconBundle.createBundle());
	}

	public static SessionContext current() {
		return CurrentSessionContext.get();
	}

	public static SessionContext currentOrNull() {
		return CurrentSessionContext.getOrNull();
	}

	public void addTranslationProvider(TranslationProvider translationProvider) {
		rankingTranslationProvider.addTranslationProvider(translationProvider);
	}

	public void addIconBundle(IconBundle iconBundle) {
		iconBundle.getEntries().forEach(entry -> bundleIconByKey.put(entry.getKey(), entry.getIcon()));
	}

	public Icon getIcon(String key) {
		return bundleIconByKey.get(key);
	}

	public Locale getLocale() {
		return sessionConfiguration.getLanguageLocale();
	}

	public List<Locale> getAcceptedLanguages() {
		if (acceptedLanguages != null) {
			return acceptedLanguages;
		} else {
			return Arrays.asList(getLocale(), Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN);
		}
	}

	public String getLocalized(String key, Object... parameters) {
		String value = rankingTranslationProvider.getTranslation(key, getAcceptedLanguages());
		if (parameters != null) {
			return MessageFormat.format(value, parameters);
		}
		return value;
	}

	public boolean isActive() {
		return active;
	}

	public void handleActivityStateChangedInternal(boolean active) {
		this.active = active;
		onActivityStateChanged.fire(new UiSessionActivityState(active));
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void destroy() {
		commandExecutor.closeSession(sessionId, UiSessionClosingReason.TERMINATED_BY_APPLICATION);
	}

	public void handleSessionDestroyedInternal() {
		onDestroyed.fireIgnoringExceptions(null);
		runWithContext(() -> { // Do in runWithContext() with forceEnqueue=true so all onDestroyed handlers have already been executed before disabling any more executions inside the context!
			destroyed = true;
			sessionMultiKeyExecutor.closeForKey(this);
		}, true);
	}

	public Event<Void> onDestroyed() {
		return onDestroyed;
	}

	public <RESULT> void queueCommand(UiCommand<RESULT> command, Consumer<RESULT> resultCallback) {
		if (CurrentSessionContext.get() != this) {
			String errorMessage = "Trying to queue a command for foreign/null SessionContext (CurrentSessionContext.get() != this)."
					+ " Please use SessionContext.runWithContext(Runnable). NOTE: The command will not get queued!";
			LOGGER.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		Consumer<RESULT> wrappedCallback = resultCallback != null ? result -> this.runWithContext(() -> resultCallback.accept(result)) : null;
		
		uxJacksonSerializationTemplate.doWithUxJacksonSerializers(() -> commandExecutor.sendCommand(sessionId, new UiCommandWithResultCallback<>(command, wrappedCallback)));
	}

	public <RESULT> void queueCommand(UiCommand<RESULT> command) {
		this.queueCommand(command, null);
	}

	public ClientInfo getClientInfo() {
		return clientInfo;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	/**
	 * @deprecated no more needed. commands are sent as early as the client can handle them.
	 */
	@Deprecated
	public void flushCommands() {
		// TODO remove methods
	}

	public ClientBackPressureInfo getClientBackPressureInfo() {
		return commandExecutor.getClientBackPressureInfo(sessionId);
	}

	public IconTheme getIconTheme() {
		return iconTheme;
	}

	public void setIconTheme(IconTheme theme) {
		this.iconTheme = theme;
	}

	public String createFileLink(File file) {
		return sessionResourceProvider.createFileLink(file);
	}

	public String createResourceLink(Resource resource, String uniqueIdentifier) {
		return sessionResourceProvider.createResourceLink(resource, uniqueIdentifier);
	}

	public Resource getBinaryResource(int resourceId) {
		return sessionResourceProvider.getBinaryResource(resourceId);
	}

	public File getUploadedFileByUuid(String uuid) {
		return this.serverContext.getUploadedFileByUuid(uuid);
	}

	public TemplateReference registerTemplate(String id, Template template) {
		registeredTemplates.put(id, template);
		queueCommand(new UiRootPanel.RegisterTemplateCommand(id, template.createUiTemplate()));
		return new TemplateReference(template, id);
	}

	public void registerTemplates(Map<String, Template> templates) {
		registeredTemplates.putAll(templates);
		queueCommand(new UiRootPanel.RegisterTemplatesCommand(templates.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().createUiTemplate()))));
	}

	public Template getTemplate(String id) {
		return registeredTemplates.get(id);
	}

	public CompletableFuture<Void> runWithContext(Runnable runnable) {
		return this.runWithContext(runnable, false);
	}

	/**
	 * @param runnable
	 * @param forceEnqueue No synchronous execution! Enqueue this at the end of this SessionContext's work queue.
	 * @return
	 */
	public CompletableFuture<Void> runWithContext(Runnable runnable, boolean forceEnqueue) {
		if (destroyed) {
			LOGGER.info("This SessionContext ({}) is already destroyed. Not sending command.", sessionId);
			return CompletableFuture.failedFuture(new IllegalStateException("SessionContext destroyed."));
		}
		if (CurrentSessionContext.getOrNull() == this && !forceEnqueue) {
			// Fast lane! This thread is already bound to this context. Just execute the runnable.
			runnable.run();
			return CompletableFuture.completedFuture(null);
		} else {
			return sessionMultiKeyExecutor.submit(this, () -> {
				CurrentSessionContext.set(this);
				try {
					executionDecorators.createWrappedRunnable(runnable).run();
				} finally {
					CurrentSessionContext.unset();
				}
			});
		}
	}

	/**
	 * Adds a decorator that gets invoked whenever a Thread is bound to this SessionContext.
	 * The decorator will be called right <strong>after</strong> the Thread is bound to this SessionContext, so SessionContext.current() will return this instance.
	 *
	 * @param decorator
	 * @param outer Whether to add this decorator as outermost or innermost execution wrapper.
	 */
	public void addExecutionDecorator(ExecutionDecorator decorator, boolean outer) {
		if (outer) {
			executionDecorators.addOuterDecorator(decorator);
		} else {
			executionDecorators.addInnerDecorator(decorator);
		}
	}

	public SessionConfiguration getConfiguration() {
		return sessionConfiguration;
	}

	public void setConfiguration(SessionConfiguration config) {
		this.sessionConfiguration = config;
		queueCommand(new UiRootPanel.SetConfigCommand(config.createUiConfiguration()));
	}

	public void showPopupAtCurrentMousePosition(Popup popup) {
		queueCommand(new UiRootPanel.ShowPopupAtCurrentMousePositionCommand(popup.createUiReference()));
	}

	public void showPopup(Popup popup) {
		queueCommand(new UiRootPanel.ShowPopupCommand(popup.createUiReference()));
	}

	public ZoneId getTimeZone() {
		return getConfiguration().getTimeZone();
	}

	public String resolveIcon(Icon icon) {
		if (icon == null) {
			return null;
		}
		return sessionConfiguration.getIconPath() + "/-1/" + icon.getQualifiedIconId(getIconTheme());
	}

	public void registerClientObject(ClientObject clientObject) {
		clientObjectsById.put(clientObject.getId(), clientObject);
	}

	public void unregisterClientObject(ClientObject clientObject) {
		clientObjectsById.remove(clientObject.getId());
	}

	public ClientObject getClientObject(String clientObjectId) {
		return clientObjectsById.get(clientObjectId);
	}

	public String createResourceLink(Resource resource) {
		return createResourceLink(resource, null);
	}

	public void showWindow(Window window, int animationDuration) {
		window.show(animationDuration);
	}

	public void downloadFile(String fileUrl, String downloadFileName) {
		runWithContext(() -> queueCommand(new UiRootPanel.DownloadFileCommand(fileUrl, downloadFileName)));
	}

	public void downloadFile(File file, String downloadFileName) {
		runWithContext(() -> queueCommand(new UiRootPanel.DownloadFileCommand(createFileLink(file), downloadFileName)));
	}

	public void registerBackgroundImage(String id, String image, String blurredImage) {
		queueCommand(new UiRootPanel.RegisterBackgroundImageCommand(id, image, blurredImage));
	}

	public void setBackgroundImage(String id, int animationDuration) {
		queueCommand(new UiRootPanel.SetBackgroundImageCommand(id, animationDuration));
	}

	public void setBackgroundColor(Color color, int animationDuration) {
		queueCommand(new UiRootPanel.SetBackgroundColorCommand(color != null ? color.toHtmlColorString() : null, animationDuration));
	}

	public void exitFullScreen() {
		queueCommand(new UiRootPanel.ExitFullScreenCommand());
	}

	public void addRootComponent(String containerElementId, RootPanel rootPanel) {
		queueCommand(new UiRootPanel.BuildRootPanelCommand(containerElementId, rootPanel.createUiReference()));
	}

	public void addClientToken(String token) {
		queueCommand(new UiRootPanel.AddClientTokenCommand(token));
	}

	public void removeClientToken(String token) {
		queueCommand(new UiRootPanel.RemoveClientTokenCommand(token));
	}

	public void clearClientTokens() {
		queueCommand(new UiRootPanel.ClearClientTokensCommand());
	}

	public void showNotification(Notification notification, NotificationPosition position, EntranceAnimation entranceAnimation, ExitAnimation exitAnimation) {
		runWithContext(() -> {
			queueCommand(new UiRootPanel.ShowNotificationCommand(notification.createUiReference(), position.toUiNotificationPosition(), entranceAnimation.toUiEntranceAnimation(),
					exitAnimation.toUiExitAnimation()));
		});
	}

	public void showNotification(Notification notification, NotificationPosition position) {
		runWithContext(() -> {
			showNotification(notification, position, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
		});
	}

	public void showNotification(Icon icon, String caption) {
		runWithContext(() -> {
			Notification notification = Notification.createWithIconAndCaption(icon, caption);
			notification.setDismissible(true);
			notification.setShowProgressBar(false);
			notification.setDisplayTimeInMillis(5000);
			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
		});
	}

	public void showNotification(Icon icon, String caption, String description) {
		runWithContext(() -> {
			Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
			notification.setDismissible(true);
			notification.setShowProgressBar(false);
			notification.setDisplayTimeInMillis(5000);
			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
		});
	}

	public void showNotification(Icon icon, String caption, String description, boolean dismissable, int displayTimeInMillis, boolean showProgress) {
		runWithContext(() -> {
			Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
			notification.setDismissible(dismissable);
			notification.setDisplayTimeInMillis(displayTimeInMillis);
			notification.setShowProgressBar(showProgress);
			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
		});
	}
}
