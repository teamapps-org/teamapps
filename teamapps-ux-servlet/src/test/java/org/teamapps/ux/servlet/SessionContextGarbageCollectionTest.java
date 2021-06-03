/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
package org.teamapps.ux.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.core.TeamAppsUploadManager;
import org.teamapps.core.TeamAppsUxSessionManager;
import org.teamapps.dto.UiClientInfo;
import org.teamapps.dto.UiSessionClosingReason;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.IconProvider;
import org.teamapps.json.TeamAppsObjectMapperFactory;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.uisession.TeamAppsUiSessionManager;
import org.teamapps.util.threading.CompletableFutureChainSequentialExecutorFactory;
import org.teamapps.ux.component.infiniteitemview.InfiniteItemView;
import org.teamapps.ux.component.infiniteitemview.ListInfiniteItemViewModel;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.template.BaseTemplateRecord;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SessionContextGarbageCollectionTest {

	@Test
	@Ignore // NOTE: Execute with -Xmx50M
	public void testSessionContextGarbageCollection() throws Exception {
		ObjectMapper objectMapper = TeamAppsObjectMapperFactory.create();
		TeamAppsUiSessionManager uiSessionManager = new TeamAppsUiSessionManager(new TeamAppsConfiguration(), objectMapper);
		TeamAppsUxSessionManager teamAppsUxSessionManager = new TeamAppsUxSessionManager(new CompletableFutureChainSequentialExecutorFactory(10), context -> {
			RootPanel rootPanel = new RootPanel();
			InfiniteItemView<BaseTemplateRecord<?>> component = new InfiniteItemView<>();
			component.setModel(new ListInfiniteItemViewModel<>(IntStream.range(0, 100).mapToObj(i -> new BaseTemplateRecord<>(MaterialIcon.ALARM_ON, "item" + i, "asdfkj")).collect(Collectors.toList())));
			rootPanel.setContent(component);
			context.addRootComponent(null, rootPanel);
		}, uiSessionManager, Mockito.mock(IconProvider.class), Mockito.mock(TeamAppsUploadManager.class));
		uiSessionManager.setUiSessionListener(teamAppsUxSessionManager);

		for (int i = 0; i < 100_000; i++) {
			System.out.println(i);
			teamAppsUxSessionManager.onUiSessionStarted(new QualifiedUiSessionId("" + i, "" + i), createDummyClientInfo(), null);
			teamAppsUxSessionManager.onUiSessionClosed(new QualifiedUiSessionId("" + i, "" + i), UiSessionClosingReason.TERMINATED_BY_CLIENT);
		}
	}

	private static UiClientInfo createDummyClientInfo() {
		UiClientInfo info = new UiClientInfo();
		info.setIp("0.0.0.0");
		info.setUserAgentString("asdf");
		info.setPreferredLanguageIso("en");
		info.setScreenWidth(1920);
		info.setScreenHeight(1080);
		info.setViewPortWidth(1900);
		info.setViewPortHeight(950);
		info.setHighDensityScreen(false);
		info.setTimezoneIana("CET");
		info.setTimezoneOffsetMinutes(60);
		info.setClientTokens(Collections.emptyList());
		info.setClientUrl("http://localhost:8080");
		return info;
	}


}
