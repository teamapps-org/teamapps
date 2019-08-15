package org.teamapps.ux.component.charting.common;

import org.teamapps.dto.UiTreeGraphNodeIcon;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.session.CurrentSessionContext;

public class GraphNodeIcon {

	private final Icon icon;
	private final int size;

	public GraphNodeIcon(Icon icon, int size) {
		this.icon = icon;
		this.size = size;
	}

	public UiTreeGraphNodeIcon createUiTreeGraphNodeIcon() {
		return new UiTreeGraphNodeIcon(CurrentSessionContext.get().resolveIcon(icon), size);
	}

	public Icon getIcon() {
		return icon;
	}

	public int getSize() {
		return size;
	}
}
