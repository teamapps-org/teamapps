package org.teamapps.ux.session.navigation;

public class WithNavigationHistoryChangeOperation<T> {

	private final T value;
	private final NavigationHistoryOperation navigationHistoryOperation;

	public WithNavigationHistoryChangeOperation(T value, NavigationHistoryOperation navigationHistoryOperation) {
		this.value = value;
		this.navigationHistoryOperation = navigationHistoryOperation;
	}

	public T getValue() {
		return value;
	}

	public NavigationHistoryOperation getNavigationHistoryOperation() {
		return navigationHistoryOperation;
	}
}
