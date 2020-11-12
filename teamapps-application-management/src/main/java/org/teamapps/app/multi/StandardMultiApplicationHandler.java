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
package org.teamapps.app.multi;

import org.teamapps.app.ComponentBuilder;
import org.teamapps.app.ComponentUpdateHandler;
import org.teamapps.auth.AuthenticationProvider;
import org.teamapps.auth.LoginHandler;
import org.teamapps.auth.SessionAuthenticatedUserResolver;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.config.ClientConfigProvider;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;
import org.teamapps.theme.Theme;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.itemview.SimpleItem;
import org.teamapps.ux.component.itemview.SimpleItemGroup;
import org.teamapps.ux.component.itemview.SimpleItemView;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.tabpanel.Tab;
import org.teamapps.ux.component.tabpanel.TabPanel;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StandardMultiApplicationHandler<USER> implements MultiApplicationHandler<USER> {

	private final SessionAuthenticatedUserResolver<USER> sessionAuthenticatedUserResolver;
	private final ApplicationLaunchInfoProvider<USER> applicationLaunchInfoProvider;
	private final List<ComponentBuilder> componentBuilders;
	private final List<LogoutHandler<USER>> logoutHandlers;
	private ComponentBuilder loggedOutComponentBuilder;
	private Function<USER, ApplicationHandlerCaptions> userApplicationCaptionsFunction = user -> new ApplicationHandlerCaptions();

	private ClientConfigProvider<USER> clientConfigProvider;

	private Icon applicationLauncherIcon = MaterialIcon.APPS;
	private Icon logoutIcon = MaterialIcon.DELETE;


	public StandardMultiApplicationHandler(AuthenticationProvider<USER> authenticationProvider, ApplicationLaunchInfoProvider<USER> applicationLaunchInfoProvider, ClientConfigProvider<USER> clientConfigProvider) {
		this(authenticationProvider.getSessionAuthenticatedUserResolver(), applicationLaunchInfoProvider, clientConfigProvider, null);
	}

	public StandardMultiApplicationHandler(LoginHandler<USER> loginHandler, ApplicationLaunchInfoProvider<USER> applicationLaunchInfoProvider) {
		this(loginHandler.getAuthenticationProvider().getSessionAuthenticatedUserResolver(), applicationLaunchInfoProvider, loginHandler.getClientConfigProvider(), loginHandler);
	}

	public StandardMultiApplicationHandler(SessionAuthenticatedUserResolver<USER> sessionAuthenticatedUserResolver, ApplicationLaunchInfoProvider<USER> applicationLaunchInfoProvider) {
		this(sessionAuthenticatedUserResolver, applicationLaunchInfoProvider, null, null);
	}

	public StandardMultiApplicationHandler(SessionAuthenticatedUserResolver<USER> sessionAuthenticatedUserResolver, ApplicationLaunchInfoProvider<USER> applicationLaunchInfoProvider, ClientConfigProvider<USER> clientConfigProvider, ComponentBuilder loggedOutComponentBuilder) {
		this.sessionAuthenticatedUserResolver = sessionAuthenticatedUserResolver;
		this.applicationLaunchInfoProvider = applicationLaunchInfoProvider;
		this.logoutHandlers = new ArrayList<>();
		this.componentBuilders = new ArrayList<>();
		this.loggedOutComponentBuilder = loggedOutComponentBuilder != null ? loggedOutComponentBuilder : updateHandler -> new Panel(MaterialIcon.ERROR, "No logged out component builder registered");
		this.clientConfigProvider = clientConfigProvider != null ? clientConfigProvider : ClientConfigProvider.create();
	}

	public void setLoggedOutComponentBuilder(ComponentBuilder loggedOutComponentBuilder) {
		this.loggedOutComponentBuilder = loggedOutComponentBuilder;
	}

	public void setClientConfigProvider(ClientConfigProvider<USER> clientConfigProvider) {
		this.clientConfigProvider = clientConfigProvider;
	}

	@Override
	public void addApplication(ComponentBuilder componentBuilder) {
		componentBuilders.add(componentBuilder);
	}

	@Override
	public void addLogoutHandler(LogoutHandler<USER> logoutHandler) {
		logoutHandlers.add(logoutHandler);
	}

	@Override
	public ApplicationLauncherComponentProvider getApplicationLauncherComponentProvider(ComponentUpdateHandler updateHandler) {
		return () -> createApplication(updateHandler,null, true);
	}

	@Override
	public Component buildComponent(ComponentUpdateHandler updateHandler) {
		USER user = sessionAuthenticatedUserResolver.getUser();
		List<Map.Entry<ApplicationGroup, List<ApplicationInfo>>> sortedApplicationGroups = getSortedUserApplicationGroups(user);

		SessionContext context = CurrentSessionContext.get();
		boolean isMobile = context.getClientInfo().isMobileDevice();

		if (isMobile) {
			return createApplication(updateHandler, null, false);
		} else {
			TabPanel tabPanel = new TabPanel();
			createApplication(updateHandler, tabPanel, false);
			return tabPanel;
		}
	}

	private Component createApplication(ComponentUpdateHandler updateHandler, TabPanel tabPanel, boolean launcherPanelOnly) {
		USER user = sessionAuthenticatedUserResolver.getUser();
		ApplicationHandlerCaptions captions = userApplicationCaptionsFunction.apply(user);
		List<Map.Entry<ApplicationGroup, List<ApplicationInfo>>> sortedApplicationGroups = getSortedUserApplicationGroups(user);
		SessionContext context = CurrentSessionContext.get();
		Map<ComponentBuilder, Tab> tabByComponentBuilder = new HashMap<>();
		Map<Tab, ApplicationInfo> applicationInfoByTab = new HashMap<>();
		SimpleItemView<ApplicationInfo> applicationLauncher = new SimpleItemView<>();
		if (!launcherPanelOnly && tabPanel == null) {
			for (Map.Entry<ApplicationGroup, List<ApplicationInfo>> entry : sortedApplicationGroups) {
				for (ApplicationInfo info : entry.getValue()) {
					if (info.getLaunchInfo().isPreload() && info.getLaunchInfo().isDisplay()) {
						return info.getComponentBuilder().buildComponent(updateHandler);
					}
				}
			}
		}
		Tab homeTab = null;
		Panel panel = new Panel(applicationLauncherIcon, captions.getApplications());
		TextField applicationsSearchField = new TextField();
		applicationsSearchField.setShowClearButton(true);
		applicationsSearchField.setEmptyText(captions.getSearch() + "...");
		applicationsSearchField.onTextInput().addListener(applicationLauncher::setFilter);
		panel.setRightHeaderField(applicationsSearchField);
		panel.setContent(applicationLauncher);

		if (tabPanel != null) {
			Panel framePanel = new Panel();
			framePanel.setHideTitleBar(true);
			framePanel.setPadding(5);
			framePanel.setContent(panel);
			framePanel.setBodyBackgroundColor(RgbaColor.WHITE.withAlpha(0.001f));
			panel.setBodyBackgroundColor(RgbaColor.WHITE.withAlpha(0.7f));
			homeTab = new Tab(applicationLauncherIcon, captions.getHome(), framePanel);
			tabPanel.addTab(homeTab, true);
		}

		sortedApplicationGroups.forEach(entry -> {
			ApplicationGroup applicationGroup = entry.getKey();
			List<ApplicationInfo> applicationInfos = entry.getValue();
			SimpleItemGroup<ApplicationInfo> itemGroup = new SimpleItemGroup<>(applicationGroup.getIcon(), applicationGroup.getTitle());
			itemGroup.setButtonWidth(tabPanel != null ? 300 : 0.97f);
			applicationInfos.forEach(info -> {
				ComponentBuilder componentBuilder = info.getComponentBuilder();
				ApplicationLaunchInfo launchInfo = info.getLaunchInfo();
				SimpleItem<ApplicationInfo> item = itemGroup.addItem(launchInfo.getIcon(), launchInfo.getTitle(), launchInfo.getDescription());
				itemGroup.setItemTemplate(BaseTemplate.LIST_ITEM_EXTRA_VERY_LARGE_ICON_TWO_LINES);
				item.setPayload(info);
				if (!launcherPanelOnly) {
					if (tabPanel != null) {
						if (launchInfo.isPreload()) {
							Tab tab = new Tab(launchInfo.getIcon(), launchInfo.getTitle(), componentBuilder.buildComponent(updateHandler));
							tab.setCloseable(launchInfo.isClosable());
							tabByComponentBuilder.put(componentBuilder, tab);
							applicationInfoByTab.put(tab, info);
							tabPanel.addTab(tab, launchInfo.isDisplay());
						}
					}
				}
				item.onClick.addListener(aVoid -> {
					if (tabPanel != null) {
						Tab tab = tabByComponentBuilder.get(componentBuilder);
						if (tab != null) {
							tab.select();
						} else {
							tab = new Tab(launchInfo.getIcon(), launchInfo.getTitle(), componentBuilder.buildComponent(updateHandler));
							tab.setCloseable(launchInfo.isClosable());
							tabByComponentBuilder.put(componentBuilder, tab);
							applicationInfoByTab.put(tab, info);
							tabPanel.addTab(tab, true);
						}
					} else {
						updateHandler.updateComponent(componentBuilder.buildComponent(updateHandler));
					}
					if (launchInfo.getTheme() != null) {
						Theme theme = launchInfo.getTheme();
						theme.getBackground().registerAndApply(context);
						clientConfigProvider.setUserTheme(user, theme); //todo only update the theme not the full config -> SessionContext api change
					} else {
						clientConfigProvider.setUserTheme(user, clientConfigProvider.getUserTheme(user));
					}
				});
			});
			applicationLauncher.addGroup(itemGroup);
		});


		if (tabPanel == null) {
			SimpleItemGroup<ApplicationInfo> itemGroup = new SimpleItemGroup<>(logoutIcon, captions.getLogout());
			itemGroup.setButtonWidth(0.97f);
			itemGroup.setItemTemplate(BaseTemplate.LIST_ITEM_EXTRA_VERY_LARGE_ICON_TWO_LINES);
			SimpleItem<ApplicationInfo> item = itemGroup.addItem(logoutIcon, captions.getLogout(), "");
			item.onClick.addListener(aVoid -> handleLogout(user, updateHandler));
			return panel;
		} else {
			Tab finalHomeTab = homeTab;
			tabPanel.onTabSelected.addListener(tab -> {
				ApplicationInfo applicationInfo = applicationInfoByTab.get(tab);
				if (tab.equals(finalHomeTab)) {
					clientConfigProvider.setUserTheme(user, clientConfigProvider.getUserTheme(user));
				} else if (applicationInfo == null) {
					handleLogout(user, updateHandler);
				} else {
					if (applicationInfo.getLaunchInfo().getTheme() != null) {
						Theme theme = applicationInfo.getLaunchInfo().getTheme();
						theme.getBackground().registerAndApply(context);
						clientConfigProvider.setUserTheme(user, theme); //todo only update the theme not the full config -> SessionContext api change
					} else {
						clientConfigProvider.setUserTheme(user, clientConfigProvider.getUserTheme(user));
					}
				}
			});
			tabPanel.onTabClosed.addListener(tab -> {
				ApplicationInfo applicationInfo = applicationInfoByTab.get(tab);
				tabByComponentBuilder.remove(applicationInfo.getComponentBuilder());
			});

			Tab tab = new Tab(logoutIcon, captions.getLogout(), null).setRightSide(true).setLazyLoading(true);
			tabPanel.addTab(tab);
			return null;
		}
	}

	private List<Map.Entry<ApplicationGroup, List<ApplicationInfo>>> getSortedUserApplicationGroups(USER user) {
		Map<ApplicationGroup, List<ApplicationInfo>> appInfosByGroup = new HashMap<>();
		componentBuilders.forEach(componentBuilder -> {
			ApplicationLaunchInfo launchInfo = applicationLaunchInfoProvider.getApplicationLaunchInfo(user, componentBuilder);
			if (launchInfo.isAccessible()) {
				appInfosByGroup.computeIfAbsent(launchInfo.getApplicationGroup(), applicationGroup -> new ArrayList<>()).add(new ApplicationInfo(componentBuilder, launchInfo));
			}
		});
		return appInfosByGroup.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList());
	}

	private void handleLogout(USER user, ComponentUpdateHandler updateHandler) {
		updateHandler.updateComponent(loggedOutComponentBuilder.buildComponent(updateHandler));
		logoutHandlers.forEach(handler -> handler.handleUserLogout(user));
	}

	public void setApplicationLauncherIcon(Icon applicationLauncherIcon) {
		this.applicationLauncherIcon = applicationLauncherIcon;
	}

	public void setLogoutIcon(Icon logoutIcon) {
		this.logoutIcon = logoutIcon;
	}

	public void setUserApplicationCaptionsFunction(Function<USER, ApplicationHandlerCaptions> userApplicationCaptionsFunction) {
		this.userApplicationCaptionsFunction = userApplicationCaptionsFunction;
	}

	static class ApplicationInfo {
		private final ComponentBuilder componentBuilder;
		private final ApplicationLaunchInfo launchInfo;

		ApplicationInfo(ComponentBuilder componentBuilder, ApplicationLaunchInfo launchInfo) {
			this.componentBuilder = componentBuilder;
			this.launchInfo = launchInfo;
		}

		public ComponentBuilder getComponentBuilder() {
			return componentBuilder;
		}

		public ApplicationLaunchInfo getLaunchInfo() {
			return launchInfo;
		}
	}
}
