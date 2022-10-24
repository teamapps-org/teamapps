package org.teamapps.ux.session.navigation;

import org.junit.Test;
import org.teamapps.testutil.UxTestUtil;
import org.teamapps.ux.session.SessionContext;

import static org.mockito.Mockito.*;

public class RelativeRouterRegistrationPointTest {

	@Test
	public void registerRouter() {
		Router routerMock = mock(Router.class);
		SessionContext sessionContextMock = mock(SessionContext.class);
		when(sessionContextMock.registerRouter("my/{path}/x", routerMock, false))
				.thenReturn(mock(RouterRegistration.class));

		RelativeRouterRegistrationPoint rrrp = new RelativeRouterRegistrationPoint("my-prefix", sessionContextMock);

		UxTestUtil.runWithSessionContext(sessionContextMock, () -> {
			rrrp.registerRouter("my/{path}/x", routerMock, false);
		});

		verify(sessionContextMock, times(1)).registerRouter("/my-prefix/my/{path}/x", routerMock, false);
	}
}