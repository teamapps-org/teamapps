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

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiCommand;
import org.teamapps.dto.UiEntranceAnimation;
import org.teamapps.dto.UiExitAnimation;
import org.teamapps.dto.UiRootPanel;
import org.teamapps.event.Event;
import org.teamapps.icons.api.Icon;
import org.teamapps.icons.api.IconTheme;
import org.teamapps.util.UiUtil;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.notification.Notification;
import org.teamapps.ux.component.notification.NotificationPosition;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.template.TemplateReference;
import org.teamapps.ux.component.window.Window;
import org.teamapps.ux.resource.Resource;

import java.io.File;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface SessionContext {

	ClientInfo getClientInfo();

	long getLastClientEventTimestamp();

	void setLastClientEventTimestamp(long timestamp);

	boolean isOpen();

	void destroy();

	Event<Void> onDestroyed();

	<T> void queueCommand(UiCommand<T> command);
	
	<T> void queueCommand(UiCommand<T> command, Consumer<T> resultCallback);

	void flushCommands();

	IconTheme getIconTheme();

	void setIconTheme(IconTheme theme);

	default Locale getLanguageLocale() {
		return getConfiguration().getLanguageLocale();
	}

	default ZoneId getTimeZone() {
		return getConfiguration().getTimeZone();
	}

	default String resolveIcon(Icon icon) {
		if (icon == null) {
			return null;
		}
		return icon.getQualifiedIconId(getIconTheme());
	}

	Locale getLocale();

	ResourceBundle getMessageBundle();


	String getLocalized(String key, Object... parameters);


	SessionStore getSessionStore();

	default void registerComponent(Component component) {
		getSessionStore().setSessionValue(component.getId(), component);
	}

	default void unregisterComponent(Component component) {
		getSessionStore().setSessionValue(component.getId(), null);
	}

	default Component getComponent(String componentId) {
		return (Component) getSessionStore().getSessionValue(componentId);
	}

	String createFileLink(File file);

	default String createResourceLink(Supplier<InputStream> inputStreamSupplier, long length) {
		return createResourceLink(inputStreamSupplier, length, null);
	}

	String createResourceLink(Supplier<InputStream> inputStreamSupplier, long length, String resourceName);

	String createResourceLink(Supplier<InputStream> inputStreamSupplier, long length, String resourceName, String uniqueIdentifier);

	Resource getBinaryResource(int resourceId);

	File getUploadedFileByUuid(String uuid);

	TemplateReference registerTemplate(String id, Template template);

	void registerTemplates(Map<String, Template> templates);

	Template getTemplate(String id);

	/**
	 * Runs the specified runnable with this SessionContext set as CurrentSessionContext.
	 * Flushes the queued commands after execution.
	 * @param runnable the code to be executed.
	 */
	void runWithContext(Runnable runnable);

	// =========================================
	//  C O N V E N I E N C E   M E T H O D S
	// =========================================

	default void showWindow(Window window, int animationDuration) {
		queueCommand(new UiRootPanel.ShowWindowCommand(window.createUiComponentReference(), animationDuration));
	}

	default void closeWindow(Window window, int animationDuration) {
		queueCommand(new UiRootPanel.CloseWindowCommand(window.getId(), animationDuration));
	}

	default void closeWindow(String windowId, int animationDuration) {
		queueCommand(new UiRootPanel.CloseWindowCommand(windowId, animationDuration));
	}

	default void downloadFile(String fileUrl, String downloadFileName) {
		queueCommand(new UiRootPanel.DownloadFileCommand(fileUrl, downloadFileName));
	}

	SessionConfiguration getConfiguration();

	void setConfiguration(SessionConfiguration config);

	default void registerBackgroundImage(String id, String image, String blurredImage) {
		queueCommand(new UiRootPanel.RegisterBackgroundImageCommand(id, image, blurredImage));
	}

	default void setBackgroundImage(String id, int animationDuration) {
		queueCommand(new UiRootPanel.SetBackgroundImageCommand(id, animationDuration));
	}

	default void setBackgroundColor(Color color, int animationDuration) {
		queueCommand(new UiRootPanel.SetBackgroundColorCommand(UiUtil.createUiColor(color), animationDuration));
	}

	default void exitFullScreen() {
		queueCommand(new UiRootPanel.ExitFullScreenCommand());
	}

	default void addRootComponent(String containerElementId, RootPanel rootPanel) {
		queueCommand(new UiRootPanel.BuildRootPanelCommand(containerElementId, rootPanel.createUiComponentReference()));
	}

	default void addClientToken(String token) {
		queueCommand(new UiRootPanel.AddClientTokenCommand(token));
	}

	default void removeClientToken(String token) {
		queueCommand(new UiRootPanel.RemoveClientTokenCommand(token));
	}

	default void clearClientTokens() {
		queueCommand(new UiRootPanel.ClearClientTokensCommand());
	}

	default void showNotification(Notification notification) {
		queueCommand(new UiRootPanel.ShowNotificationCommand(notification.createUiNotification()));
	}

	default void showNotification(Icon icon, String caption) {
		Notification notification = Notification.createWithIconAndCaption(icon, caption);
		notification.setPosition(NotificationPosition.TOP_RIGHT);
		notification.setEntranceAnimation(UiEntranceAnimation.SLIDE_IN_UP);
		notification.setExitAnimation(UiExitAnimation.SLIDE_OUT_UP);
		notification.setDismissable(true);
		notification.setShowProgressBar(false);
		notification.setDisplayTimeInMillis(5000);
		showNotification(notification);
	}

	default void showNotification(Icon icon, String caption, String description, boolean dismissable, int displayTimeInMillis, boolean showProgress) {
		Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
		notification.setPosition(NotificationPosition.TOP_RIGHT);
		notification.setEntranceAnimation(UiEntranceAnimation.SLIDE_IN_LEFT);
		notification.setExitAnimation(UiExitAnimation.FADE_OUT_UP);
		notification.setDismissable(dismissable);
		notification.setDisplayTimeInMillis(displayTimeInMillis);
		notification.setShowProgressBar(showProgress);
		showNotification(notification);
	}


}
