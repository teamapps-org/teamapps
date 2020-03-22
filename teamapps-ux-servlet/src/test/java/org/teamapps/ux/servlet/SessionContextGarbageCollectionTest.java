package org.teamapps.ux.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.dto.UiClientInfo;
import org.teamapps.dto.UiSessionClosingReason;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.json.TeamAppsObjectMapperFactory;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.uisession.TeamAppsUiSessionManager;
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
		TeamAppsUxClientGate teamAppsUxClientGate = new TeamAppsUxClientGate(context -> {
			RootPanel rootPanel = new RootPanel();
			InfiniteItemView<BaseTemplateRecord> component = new InfiniteItemView<>();
			component.setModel(new ListInfiniteItemViewModel<>(IntStream.range(0, 100).mapToObj(i -> new BaseTemplateRecord(MaterialIcon.ALARM_ON, "item" + i, "asdfkj")).collect(Collectors.toList())));
			rootPanel.setContent(component);
			context.addRootComponent(null, rootPanel);
		}, uiSessionManager, objectMapper);
		uiSessionManager.setUiSessionListener(teamAppsUxClientGate);

		for (int i = 0; i < 100_000; i++) {
			System.out.println(i);
			teamAppsUxClientGate.onUiSessionStarted(new QualifiedUiSessionId("" + i, "" + i), createDummyClientInfo(), null);
			teamAppsUxClientGate.onUiSessionClosed(new QualifiedUiSessionId("" + i, "" + i), UiSessionClosingReason.TERMINATED_BY_CLIENT);
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