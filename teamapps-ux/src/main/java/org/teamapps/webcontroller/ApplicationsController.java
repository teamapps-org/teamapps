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
package org.teamapps.webcontroller;

import org.teamapps.data.value.SimpleDataRecord;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.application.*;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.animation.PageTransition;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.common.format.Color;
import org.teamapps.ux.component.itemview.ItemGroup;
import org.teamapps.ux.component.itemview.ItemView;
import org.teamapps.ux.component.itemview.ItemViewItemBackgroundMode;
import org.teamapps.ux.component.login.LoginWindow;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.tabpanel.Tab;
import org.teamapps.ux.component.tabpanel.TabPanel;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.workspacelayout.SimpleWorkSpaceLayout;
import org.teamapps.ux.component.workspacelayout.WorkSpaceLayoutView;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.ux.session.StylingTheme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.teamapps.webcontroller.SimpleApplicationWebController.CMD_LOGOUT;
import static org.teamapps.ux.component.template.BaseTemplate.*;

public class ApplicationsController implements ApplicationDesktop {

	private final RootPanel rootPanel;
	private final LoginWindow loginWindow;
	private final List<ApplicationBuilder> applicationBuilders;
	private ApplicationBuilder settingsBuilder;
	private List<String> preLaunchedApps;
	private Map<String, String> customAppBackgroundByAppName;
	private Map<String, StylingTheme> stylingThemeByAppName;
	private final SessionContext context;
	private TabPanel tabPanel;
	private Panel applicationsListingPanel;
	private Map<Tab, String> applicationNameByTab = new HashMap<>();
	private String lastBackground;

	private Map<Tab, String> applicationNameByApplicationCustomTab = new HashMap<>();

	public ApplicationsController(RootPanel rootPanel, LoginWindow loginWindow, List<ApplicationBuilder> applicationBuilders, ApplicationBuilder settingsBuilder, List<String> preLaunchedAppNames, Map<String, String> customAppBackgroundByAppName, Map<String, StylingTheme> stylingThemeByAppName, SessionContext context) {
		this.rootPanel = rootPanel;
		this.loginWindow = loginWindow;
		this.applicationBuilders = applicationBuilders;
		this.settingsBuilder = settingsBuilder;
		this.preLaunchedApps = preLaunchedAppNames;
		this.customAppBackgroundByAppName = customAppBackgroundByAppName;
		this.stylingThemeByAppName = stylingThemeByAppName;
		this.context = context;
	}

	public void createUi() {

		boolean isMobile = isMobile();

		ItemView<SimpleDataRecord, SimpleDataRecord> applicationsListing = new ItemView<>();
		applicationsListing.setHeaderPropertyExtractor(SimpleDataRecord::getValue);
		applicationsListing.setGroupHeaderTemplate(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		applicationsListing.setItemBackgroundMode(ItemViewItemBackgroundMode.LIGHT);
		applicationsListing.setHorizontalPadding(2);
		applicationsListing.setVerticalPadding(0);
		applicationsListing.setGroupSpacing(5);
		Map<String, ItemGroup<SimpleDataRecord, SimpleDataRecord>> itemGroupsByApplicationGroupId = new HashMap<>();

		applicationsListingPanel = new Panel(MaterialIcon.APPS, "Anwendungen", applicationsListing);
		TextField appFilterField = new TextField();
		appFilterField.setShowClearButton(true);
		//appFilterField.setEmptyText(context.getMessageBundle().getString("Search") + "...");
		appFilterField.onTextInput.addListener(applicationsListing::setFilter);
		applicationsListingPanel.setRightHeaderField(appFilterField, MaterialIcon.SEARCH, 100, 200);
		applicationsListingPanel.setAlwaysShowHeaderFieldIcons(true);


		Map<String, ApplicationBuilder> validBuilderMap = new HashMap<>();
		for (ApplicationBuilder builder : applicationBuilders) {
			if (builder.isApplicationAccessible()) {
				ApplicationInfo applicationInfo = builder.getApplicationInfo();
				validBuilderMap.put(applicationInfo.getName(), builder);

				ApplicationGroup applicationGroup = applicationInfo.getApplicationGroup();
				ItemGroup<SimpleDataRecord, SimpleDataRecord> group = itemGroupsByApplicationGroupId.computeIfAbsent(applicationGroup.getId(), applicationGroupId -> {
					SimpleDataRecord headerData = new SimpleDataRecord(PROPERTY_CAPTION, applicationGroup.getCaption())
							.setValue(PROPERTY_ICON, applicationGroup.getIcon());
					ItemGroup<SimpleDataRecord, SimpleDataRecord> itemGroup = new ItemGroup<>(headerData, BaseTemplate.APPLICATION_LISTING);
					itemGroup.setButtonWidth(isMobile? 0 : 300);
					applicationsListing.addGroup(itemGroup);
					itemGroup.setItemPropertyExtractor(SimpleDataRecord::getValue);
					return itemGroup;
				});
				group.addItem(new SimpleDataRecord(PROPERTY_ID, applicationInfo.getName())

						.setValue(PROPERTY_ICON, applicationInfo.getIcon())
						.setValue(PROPERTY_CAPTION, applicationInfo.getTitle())
						.setValue(PROPERTY_DESCRIPTION, applicationInfo.getDescription()));
			}
		}

		if (isMobile) {


			SimpleDataRecord headerData = new SimpleDataRecord(PROPERTY_CAPTION, "Abmelden").setValue(PROPERTY_ICON, MaterialIcon.EXIT_TO_APP);
			ItemGroup<SimpleDataRecord, SimpleDataRecord> group = new ItemGroup<>(headerData, BaseTemplate.APPLICATION_LISTING);
			group.setItemPropertyExtractor(SimpleDataRecord::getValue);
			group.setButtonWidth(0);
			applicationsListing.addGroup(group);



			group.addItem(new SimpleDataRecord(PROPERTY_ID, CMD_LOGOUT)
					.setValue(PROPERTY_ICON, MaterialIcon.EXIT_TO_APP)
					.setValue(PROPERTY_CAPTION, "Abmelden")
					.setValue(PROPERTY_DESCRIPTION, "Vom System abmelden"));
		}

		tabPanel = new TabPanel();
		Map<String, Tab> tabsByApplicationItemId = new HashMap<>();
		tabPanel.onTabClosed.addListener(tab -> tabsByApplicationItemId.values().remove(tab));
		applicationsListing.onItemClicked.addListener(eventData -> {
			SimpleDataRecord item = eventData.getItem();
			String applicationName = (String) item.getValue(PROPERTY_ID);
			if (applicationName.equals(CMD_LOGOUT)) {
				loginWindow.getPasswordField().setValue(null);
				rootPanel.setContent(loginWindow.getElegantPanel(), PageTransition.MOVE_TO_BOTTOM_VS_SCALE_UP, 0);
			} else {
				loadApplication(applicationName, validBuilderMap, applicationsListingPanel, tabsByApplicationItemId, tabPanel, rootPanel);
			}
		});

		tabPanel.onTabSelected.addListener(tab -> {
			String applicationName = applicationNameByTab.get(tab);
			if (applicationName == null) {
				applicationName = applicationNameByApplicationCustomTab.get(tab);
			}
			checkThemeAndBackground(applicationName);
		});

		if (isMobile) {
			rootPanel.setContent(applicationsListingPanel);
		} else {
			SimpleWorkSpaceLayout workSpaceLayout = new SimpleWorkSpaceLayout();
			workSpaceLayout.getCenterViewGroup().addView(new WorkSpaceLayoutView(workSpaceLayout, applicationsListingPanel, null, false, false));
			applicationsListingPanel.setBodyBackgroundColor(Color.WHITE.withAlpha(.92f));

			Tab applicationsTab = new Tab(MaterialIcon.APPS, "Applications", workSpaceLayout);
			tabPanel.addTab(applicationsTab, true);

			if (settingsBuilder != null) {
				Tab settingsTab = new Tab(MaterialIcon.MORE_VERT, "Einstellungen", null, true);
				settingsTab.onSelected.addListener(aVoid -> {
					settingsTab.setContent(settingsBuilder.createApplication(this).getUi());
				});
				settingsTab.setRightSide(true);
				tabPanel.addTab(settingsTab);
			}

			Tab logoutTab = new Tab(MaterialIcon.EXIT_TO_APP, "Logout", null, true);
			logoutTab.setRightSide(true);
			logoutTab.onSelected.addListener(aVoid -> {
				context.setBackgroundImage("login", 1000);
				rootPanel.setContent(loginWindow.getElegantPanel(), PageTransition.MOVE_TO_BOTTOM_VS_SCALE_UP, 300);
			});
			tabPanel.addTab(logoutTab);
			rootPanel.setContent(tabPanel);
		}

		for (String preLaunchedApp : preLaunchedApps) {
			loadApplication(preLaunchedApp, validBuilderMap, applicationsListingPanel, tabsByApplicationItemId, tabPanel, rootPanel);
		}

	}

	private void loadApplication(String applicationName, Map<String, ApplicationBuilder> validBuilderMap, Panel applicationsListingPanel, Map<String, Tab> tabsByApplicationItemId, TabPanel tabPanel, RootPanel rootPanel) {
		ApplicationBuilder applicationBuilder = validBuilderMap.get(applicationName);
		if (!isMobile()) {
			Tab tab = tabsByApplicationItemId.computeIfAbsent(applicationName, name -> {
				Application application = applicationBuilder.createApplication(this);
				ApplicationInfo applicationInfo = applicationBuilder.getApplicationInfo();
				Tab newTab = new Tab(applicationInfo.getIcon(), applicationInfo.getTitle(), application.getUi());
				tabPanel.addTab(newTab, true);
				applicationNameByTab.put(newTab, applicationName);
				newTab.onClosed.addListener(aVoid -> applicationNameByTab.remove(newTab));
				return newTab.setCloseable(true);
			});
			tab.select();
		} else {
			Application application = applicationBuilder.createApplication(this);
			//todo: remove old application!
			rootPanel.setContent(application.getUi());
		}
		checkThemeAndBackground(applicationName);
	}

	private void checkThemeAndBackground(String applicationName) {
		if (applicationName == null) {
			//todo: settings for app listing
			context.setBackgroundImage("default", 1000);
			context.getConfiguration().setTheme(StylingTheme.DEFAULT);
			context.setConfiguration(context.getConfiguration());
		} else {
			if (customAppBackgroundByAppName.containsKey(applicationName)) {
				context.setBackgroundImage(applicationName, 1000);
				lastBackground = applicationName;
			} else if (lastBackground != null && !lastBackground.equals("default")) {
				context.setBackgroundImage("default", 1000);
				lastBackground = "default";
			}
			context.getConfiguration().setTheme(stylingThemeByAppName.getOrDefault(applicationName, StylingTheme.DEFAULT));
			context.setConfiguration(context.getConfiguration());
		}
	}

	public void showApplication(ApplicationInfo applicationInfo, Application application) {
		if (!isMobile()) {
			Tab newTab = new Tab(applicationInfo.getIcon(), applicationInfo.getTitle(), application.getUi());
			tabPanel.addTab(newTab, true);
		} else {
			rootPanel.setContent(application.getUi());
		}
	}

	@Override
	public Component getApplicationsListingComponent() {
		return applicationsListingPanel;
	}

	public void showTopLevelComponent(String originatingAppName, Icon icon, String title, Component component) {
		if (!isMobile()) {
			Tab newTab = new Tab(icon, title, component);
			newTab.setCloseable(true);
			applicationNameByApplicationCustomTab.put(newTab, originatingAppName);
			newTab.onClosed.addListener(aVoid -> applicationNameByApplicationCustomTab.remove(newTab));
			tabPanel.addTab(newTab, true);
		} else {
			rootPanel.setContent(component);
		}
	}

	@Override
	public void removeTopLevelComponent(Component component) {
		tabPanel.getTabs().stream()
				.filter(tab -> Objects.equals(component, tab.getContent()))
				.findAny()
				.ifPresent(tabToRemove -> tabPanel.removeTab(tabToRemove));
	}

	public boolean isMobile() {
		return context.getClientInfo().isMobileDevice();
	}

}
