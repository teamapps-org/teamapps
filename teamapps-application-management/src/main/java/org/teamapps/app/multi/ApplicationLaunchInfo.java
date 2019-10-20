package org.teamapps.app.multi;

import org.teamapps.icons.api.Icon;
import org.teamapps.theme.Theme;

public class ApplicationLaunchInfo {


	public static ApplicationLaunchInfo createNotAccessibleInfo() {
		return new ApplicationLaunchInfo();
	}

	public static ApplicationLaunchInfo create(Icon icon, String title, String description) {
		return new ApplicationLaunchInfo(ApplicationGroup.EMPTY_INSTANCE, icon, title, description, null, true, false, false);
	}

	public static ApplicationLaunchInfo create(ApplicationGroup applicationGroup, Icon icon, String title, String description) {
		return new ApplicationLaunchInfo(applicationGroup, icon, title, description, null, true, false, false);
	}

	public static ApplicationLaunchInfo create(ApplicationGroup applicationGroup, Icon icon, String title, String description, Theme theme, boolean closable, boolean preload, boolean display) {
		return new ApplicationLaunchInfo(applicationGroup, icon, title, description, theme, closable, preload, display);
	}

	private boolean accessible = true;
	private ApplicationGroup applicationGroup;
	private Icon icon;
	private String title;
	private String description;
	private Theme theme;
	private boolean closable;
	private boolean preload;
	private boolean display;

	private ApplicationLaunchInfo() {
		this.accessible = false;
	}

	public ApplicationLaunchInfo(ApplicationGroup applicationGroup, Icon icon, String title, String description, Theme theme, boolean closable, boolean preload, boolean display) {
		this.applicationGroup = applicationGroup != null ? applicationGroup : ApplicationGroup.EMPTY_INSTANCE;
		this.icon = icon;
		this.title = title;
		this.description = description;
		this.theme = theme;
		this.closable = closable;
		this.preload = preload;
		this.display = display;
	}

	public boolean isAccessible() {
		return accessible;
	}

	public ApplicationGroup getApplicationGroup() {
		return applicationGroup;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Theme getTheme() {
		return theme;
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
