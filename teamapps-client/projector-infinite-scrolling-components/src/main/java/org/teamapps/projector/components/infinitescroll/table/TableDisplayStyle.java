package org.teamapps.projector.components.infinitescroll.table;

public enum TableDisplayStyle {

	TABLE, LIST;

	public DtoTableDisplayStyle toDto() {
		return DtoTableDisplayStyle.valueOf(name());
	}

}
