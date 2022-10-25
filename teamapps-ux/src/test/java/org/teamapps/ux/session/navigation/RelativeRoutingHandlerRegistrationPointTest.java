package org.teamapps.ux.session.navigation;

import org.junit.Test;
import org.teamapps.testutil.UxTestUtil;
import org.teamapps.ux.session.SessionContext;

import static org.mockito.Mockito.*;

public class RelativeRoutingHandlerRegistrationPointTest {

	@Test
	public void registerRouter() {
		RoutingHandler handlerMock = mock(RoutingHandler.class);
		SessionContext sessionContextMock = mock(SessionContext.class);
		when(sessionContextMock.registerRoutingHandler("my/{path}/x", false, handlerMock, false))
				.thenReturn(mock(RoutingHandlerRegistration.class));

		SubRouter rrrp = new SubRouter("my-prefix", sessionContextMock);

		UxTestUtil.runWithSessionContext(sessionContextMock, () -> {
			rrrp.registerRoutingHandler("my/{path}/x", false, handlerMock, false);
		});

		verify(sessionContextMock, times(1)).registerRoutingHandler("/my-prefix/my/{path}/x", false, handlerMock, false);
	}
}