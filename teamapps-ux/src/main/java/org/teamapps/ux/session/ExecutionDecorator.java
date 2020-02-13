package org.teamapps.ux.session;

public interface ExecutionDecorator {

	void wrapExecution(Runnable runnable);

}
