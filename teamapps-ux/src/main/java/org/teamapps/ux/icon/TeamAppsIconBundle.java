package org.teamapps.ux.icon;

import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.api.Icon;

public enum TeamAppsIconBundle implements IconBundleEntry {

	ADD(MaterialIcon.ADD),
	SAVE(MaterialIcon.SAVE),
	DELETE(MaterialIcon.DELETE),
	UNDO(MaterialIcon.UNDO),
	CANCEL(MaterialIcon.CANCEL),

	FILTER(MaterialIcon.FILTER),
	SEARCH(MaterialIcon.SEARCH),
	SELECTION(MaterialIcon.SELECT_ALL),
	REMOVE(MaterialIcon.REMOVE),

	BACK(MaterialIcon.NAVIGATE_BEFORE),
	PREVIOUS(MaterialIcon.NAVIGATE_BEFORE),
	NEXT(MaterialIcon.NAVIGATE_NEXT),

	YEAR(MaterialIcon.EVENT_NOTE),
	MONTH(MaterialIcon.DATE_RANGE),
	WEEK(MaterialIcon.VIEW_WEEK),
	DAY(MaterialIcon.VIEW_DAY),

	APPLICATION_LAUNCHER(MaterialIcon.VIEW_MODULE),
	TREE(MaterialIcon.TOC),
	VIEWS(MaterialIcon.VIEW_CAROUSEL),
	TOOLBAR(MaterialIcon.SUBTITLES),

	UPLOAD(MaterialIcon.BACKUP),

	;

	public static IconBundle createBundle() {
		return IconBundle.create(values());
	}

	private final Icon icon;

	TeamAppsIconBundle(Icon icon) {
		this.icon = icon;
	}

	@Override
	public String getKey() {
		return "teamApps_" + name();
	}

	@Override
	public Icon getIcon() {
		return icon;
	}


}
