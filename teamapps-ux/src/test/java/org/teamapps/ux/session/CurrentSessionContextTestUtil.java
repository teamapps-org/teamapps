package org.teamapps.ux.session;

public class CurrentSessionContextTestUtil {

	public static void set(SessionContext sessionContext) {
		CurrentSessionContext.set(sessionContext);
	}
	
	public static void unset() {
		CurrentSessionContext.unset();
	}

}