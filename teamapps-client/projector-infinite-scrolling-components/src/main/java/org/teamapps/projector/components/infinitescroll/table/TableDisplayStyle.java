package org.teamapps.projector.components.infinitescroll.table;

import org.teamapps.projector.components.infinitescroll.dto.DtoTableDisplayStyle;

public enum TableDisplayStyle {

	TABLE, LIST;

	public DtoTableDisplayStyle toDto() {
		return DtoTableDisplayStyle.valueOf(name());
	}

}
