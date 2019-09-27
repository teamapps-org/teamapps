package org.teamapps.app.multi;

public class ApplicationLaunchInfo {

	private final boolean closable;
	private final boolean preload;
	private final boolean display;

	public ApplicationLaunchInfo(boolean closable, boolean preload, boolean display) {
		this.closable = closable;
		this.preload = preload;
		this.display = display;
	}

	public boolean isClosable() {
		return closable;
	}

	public boolean isPreload() {
		return preload;
	}

	public boolean isDisplay() {
		return display;
	}
}
