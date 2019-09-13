/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import org.teamapps.dto.UiCommand;
import org.teamapps.dto.UiRootPanel;
import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;
import org.teamapps.icons.api.IconTheme;
import org.teamapps.server.UxServerContext;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.ux.caption.MultiResourceBundle;
import org.teamapps.ux.component.popup.Popup;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.template.TemplateReference;
import org.teamapps.ux.json.UxJacksonSerializationTemplate;
import org.teamapps.ux.resource.Resource;
import org.teamapps.ux.task.ObservableProgress;
import org.teamapps.ux.task.Progress;
import org.teamapps.ux.task.ProgressReportingRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleSessionContext implements LockableSessionContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSessionContext.class);
	private static final int LOCK_TIMEOUT = 60_000;
	private static final Function<Locale, ResourceBundle> DEFAULT_RESOURCE_BUNDLE_PROVIDER = locale -> ResourceBundle.getBundle("org.teamapps.ux.i18n.DefaultCaptions", locale, new UTF8Control());

	private final Event<Void> onDestroyed = new Event<>();

	private final ReentrantLock lock = new ReentrantLock();

	private boolean isValid = true;
	private long lastClientEventTimeStamp;

	private final QualifiedUiSessionId sessionId;
	private final ClientInfo clientInfo;
	private final CommandDispatcher commandDispatcher;
	private final UxServerContext serverContext;
	private final UxJacksonSerializationTemplate uxJacksonSerializationTemplate;
	private final SessionStore sessionStore;
	private IconTheme iconTheme;
	private ClientSessionResourceProvider sessionResourceProvider;

	private Function<Locale, ResourceBundle> customMessageBundleProvider = DEFAULT_RESOURCE_BUNDLE_PROVIDER;
	private ResourceBundle messagesBundle;

	private Map<String, Template> registeredTemplates = new ConcurrentHashMap<>();
	private SessionConfiguration sessionConfiguration = SessionConfiguration.createDefault();

	public SimpleSessionContext(QualifiedUiSessionId sessionId, ClientInfo clientInfo, CommandDispatcher commandDispatcher, UxServerContext serverContext, IconTheme iconTheme,
	                            ObjectMapper jacksonObjectMapper) {
		this.sessionId = sessionId;
		this.clientInfo = clientInfo;
		this.commandDispatcher = commandDispatcher;
		this.serverContext = serverContext;
		this.sessionStore = new SimpleSessionStore();
		this.iconTheme = iconTheme;
		this.sessionResourceProvider = new ClientSessionResourceProvider(sessionId);
		this.uxJacksonSerializationTemplate = new UxJacksonSerializationTemplate(jacksonObjectMapper, this);
		this.lastClientEventTimeStamp = System.currentTimeMillis();
		updateMessageBundle();
	}

	public void setCustomMessageBundleProvider(Function<Locale, ResourceBundle> provider) {
		this.customMessageBundleProvider = provider;
		updateMessageBundle();
	}

	private void updateMessageBundle() {
		if (customMessageBundleProvider != null) {
			ResourceBundle customResourceBundle = customMessageBundleProvider.apply(sessionConfiguration.getLanguageLocale());
			ResourceBundle defaultResourceBundle = DEFAULT_RESOURCE_BUNDLE_PROVIDER.apply(sessionConfiguration.getLanguageLocale());
			this.messagesBundle = new MultiResourceBundle(customResourceBundle, defaultResourceBundle);
		} else {
			this.messagesBundle = DEFAULT_RESOURCE_BUNDLE_PROVIDER.apply(sessionConfiguration.getLanguageLocale());
		}
	}

	@Override
	public Locale getLocale() {
		return sessionConfiguration.getLanguageLocale();
	}

	@Override
	public ResourceBundle getMessageBundle() {
		return messagesBundle;
	}

	@Override
	public String getLocalized(String key, Object... parameters) {
		String value = messagesBundle.getString(key);
		if (parameters != null) {
			return MessageFormat.format(value, parameters);
		}
		return value;
	}

	@Override
	public long getLastClientEventTimestamp() {
		return lastClientEventTimeStamp;
	}

	@Override
	public void setLastClientEventTimestamp(long timestamp) {
		lastClientEventTimeStamp = timestamp;
	}

	@Override
	public boolean isOpen() {
		return isValid;
	}

	@Override
	public void destroy() {
		isValid = false;
		commandDispatcher.close();
		runWithContext(() -> {
			onDestroyed.fire(null);
		});
	}

	@Override
	public Event<Void> onDestroyed() {
		return onDestroyed;
	}

	@Override
	public <RESULT> void queueCommand(UiCommand<RESULT> command, Consumer<RESULT> resultCallback) {
		if (CurrentSessionContext.get() != this) {
			String errorMessage = "Trying to queue a command for foreign/null SessionContext (CurrentSessionContext.get() != this)."
					+ " Please use SessionContext.runWithContext(Runnable). NOTE: The command will not get queued!";
			LOGGER.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		Consumer<RESULT> wrappedCallback = resultCallback != null ? result -> this.runWithContext(() -> resultCallback.accept(result)) : null;
		commandDispatcher.queueCommand(command, wrappedCallback);
	}

	@Override
	public <RESULT> void queueCommand(UiCommand<RESULT> command) {
		this.queueCommand(command, null);
	}

	@Override
	public ClientInfo getClientInfo() {
		return clientInfo;
	}

	@Override
	public void flushCommands() {
		uxJacksonSerializationTemplate.doWithUxJacksonSerializers(commandDispatcher::flushCommands);
	}

	@Override
	public IconTheme getIconTheme() {
		return iconTheme;
	}

	@Override
	public void setIconTheme(IconTheme theme) {
		this.iconTheme = theme;
	}

	@Override
	public SessionStore getSessionStore() {
		return sessionStore;
	}

	@Override
	public String createFileLink(File file) {
		return sessionResourceProvider.createFileLink(file);
	}

	@Override
	public String createResourceLink(Resource resource, String uniqueIdentifier) {
		return sessionResourceProvider.createResourceLink(resource, uniqueIdentifier);
	}

	@Override
	public Resource getBinaryResource(int resourceId) {
		return sessionResourceProvider.getBinaryResource(resourceId);
	}

	@Override
	public File getUploadedFileByUuid(String uuid) {
		return this.serverContext.getUploadedFileByUuid(uuid);
	}

	@Override
	public TemplateReference registerTemplate(String id, Template template) {
		registeredTemplates.put(id, template);
		queueCommand(new UiRootPanel.RegisterTemplateCommand(id, template.createUiTemplate()));
		return new TemplateReference(template);
	}

	@Override
	public void registerTemplates(Map<String, Template> templates) {
		registeredTemplates.putAll(templates);
		queueCommand(new UiRootPanel.RegisterTemplatesCommand(templates.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().createUiTemplate()))));
	}

	@Override
	public Template getTemplate(String id) {
		return registeredTemplates.get(id);
	}

	@Override
	public void lock(long timeoutMillis) {
		try {
			boolean locked = lock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS);
			if (!locked) {
				String errorMessage = "Could not acquire lock for SessionContext " + sessionId + " after " + timeoutMillis + "ms.";
				LOGGER.error(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
		} catch (InterruptedException e) {
			String errorMessage = "Could not acquire lock for SessionContext " + sessionId + ". Thread was interrupted while waiting for the lock!";
			LOGGER.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
	}

	@Override
	public void unlock() {
		lock.unlock();
	}

	/**
	 * Does the following:
	 * <ol>
	 * <li>Releases the current SessionContext (A) lock (if present)</li>
	 * <li>Acquires the lock for this SessionContext (B)</li>
	 * <li>Sets this SessionContext (B) as the current context (CurrentSessionContext).</li>
	 * <li>Executes the specified Runnable.</li>
	 * <li>Sets back the last SessionContext (A) as current context.</li>
	 * <li>Releases the lock for this SessionContext (B)</li>
	 * <li>Reacquires the lock for the last (A)</li>
	 * </ol>
	 *
	 * @param runnable the code to be executed.
	 */
	@Override
	public void runWithContext(Runnable runnable) {
		if (CurrentSessionContext.getOrNull() == this) {
			// Fast lane! This context is already bound to this thread. Just execute the runnable.
			runnable.run();
		} else {
			if (CurrentSessionContext.getOrNull() != null) {
				// unlock the previously locked sessionContext (WITHOUT POPPING IT!)
				CurrentSessionContext.getOrNull().unlock();
			}
			try {
				long startTime = System.currentTimeMillis();
				lock(LOCK_TIMEOUT);
				try {
					CurrentSessionContext.pushContext(this);
					try {
						runnable.run();
					} catch (Exception e) {
						LOGGER.error("Exception while executing runnable! Context: " + sessionId.toString(), e);
						throw e;
					} finally {
						CurrentSessionContext.popContext();
					}
				} finally {
					unlock();
					long endTime = System.currentTimeMillis();
					if (endTime - startTime > LOCK_TIMEOUT) {
						String message = "The execution within the session context {} took dangerously long (longer than the session's lock timeout). This might have caused other threads to fail "
								+ "acquiring the session's lock. Stacktrace follows:";
						LOGGER.warn(message, sessionId, new RuntimeException(message));
					}
				}
			} finally {
				if (CurrentSessionContext.getOrNull() != null) {
					// relock the previously locked sessionContext
					CurrentSessionContext.getOrNull().lock(Long.MAX_VALUE); // we WANT to wait here.
				}
			}
			this.flushCommands();
		}
	}

	@Override
	public SessionConfiguration getConfiguration() {
		return sessionConfiguration;
	}

	@Override
	public void setConfiguration(SessionConfiguration config) {
		this.sessionConfiguration = config;
		updateMessageBundle();
		queueCommand(new UiRootPanel.SetConfigCommand(config.createUiConfiguration()));
	}

	@Override
	public void showPopupAtCurrentMousePosition(Popup popup) {
		queueCommand(new UiRootPanel.ShowPopupAtCurrentMousePositionCommand(popup.createUiComponentReference()));
	}

	@Override
	public void showPopup(Popup popup) {
		queueCommand(new UiRootPanel.ShowPopupCommand(popup.createUiComponentReference()));
	}

	@Override
	public ObservableProgress executeBackgroundTask(Icon icon, String taskName, boolean cancelable, ProgressReportingRunnable runnable) {
		return executeBackgroundTask(icon, taskName, cancelable, runnable, ForkJoinPool.commonPool());
	}

	@Override
	public ObservableProgress executeBackgroundTask(Icon icon, String taskName, boolean cancelable, ProgressReportingRunnable runnable, Executor executor) {
		Progress progress = new Progress(icon, taskName, cancelable);
		executor.execute(() -> {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			progress.start();
			try {
				runnable.run(progress);
			} catch (Exception e) {
				LOGGER.error("Error in background task", e);
				progress.markFailed(this.getLocalized("dict.error"), e);
				return;
			}
			progress.markCompleted();
		});
		return progress;
	}

	public static class UTF8Control extends ResourceBundle.Control {

		private String resourceFileSuffix;

		public UTF8Control() {
			this("properties");
		}

		public UTF8Control(String resourceFileSuffix) {
			this.resourceFileSuffix = resourceFileSuffix;
		}

		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
										boolean reload) throws IllegalAccessException, InstantiationException, IOException {
			// The below is a copy of the default implementation.
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, resourceFileSuffix);
			ResourceBundle bundle = null;
			InputStream stream = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection connection = url.openConnection();
					if (connection != null) {
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			if (stream != null) {
				try {
					// Only this line is changed to make it to read properties files as UTF-8.
					bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
				} finally {
					stream.close();
				}
			}
			return bundle;
		}
	}
}
