package org.teamapps.ux.component.mobile;

import org.junit.Test;
import org.teamapps.dto.UiMobileLayout;
import org.teamapps.testutil.UxTestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MobileLayoutNavigationTest {
	@Test
	public void navigationIsOptInAndStaleOrUnknownTargetsAreRejected() {
		UxTestUtil.doWithMockedSessionContext(() -> {
			MobileLayout layout = new MobileLayout();
			assertNull(((UiMobileLayout) layout.createUiComponent()).getNavigationState());
			List<String> requests = new ArrayList<>();
			layout.onNavigationRequested.addListener(entry -> { requests.add(entry); });
			layout.setNavigationState(List.of("home", "list", "detail"), "detail", true, true);
			var initial = ((UiMobileLayout) layout.createUiComponent()).getNavigationState();
			assertEquals("detail", initial.getCurrentEntry());
			layout.handleUiEvent(new UiMobileLayout.NavigationRequestedEvent(layout.getId(), initial.getRevision(), "list"));
			layout.setNavigationState(List.of("home", "list"), "list", true, true);
			layout.handleUiEvent(new UiMobileLayout.NavigationRequestedEvent(layout.getId(), initial.getRevision(), "home"));
			var current = ((UiMobileLayout) layout.createUiComponent()).getNavigationState();
			layout.handleUiEvent(new UiMobileLayout.NavigationRequestedEvent(layout.getId(), current.getRevision(), "missing"));
			assertEquals(List.of("list"), requests);
			layout.clearNavigationState();
			layout.handleUiEvent(new UiMobileLayout.NavigationRequestedEvent(layout.getId(), current.getRevision(), "home"));
			assertEquals(List.of("list"), requests);
		}).join();
	}

	@Test
	public void navigationDoesNotChangeContentOrAcceptAmbiguousPaths() {
		UxTestUtil.doWithMockedSessionContext(() -> {
			MobileLayout layout = new MobileLayout();
			MobileLayout originalContent = new MobileLayout();
			layout.setContent(originalContent);
			layout.setNavigationState(List.of("home", "list"), "list", false, true);
			assertSame(originalContent, layout.getContent());
			assertThrows(IllegalArgumentException.class, () -> layout.setNavigationState(List.of("home", "home"), "home", true, true));
			assertThrows(IllegalArgumentException.class, () -> layout.setNavigationState(List.of("home"), "missing", true, true));
		}).join();
	}
}
