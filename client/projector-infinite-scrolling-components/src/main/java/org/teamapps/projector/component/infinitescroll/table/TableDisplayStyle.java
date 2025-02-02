package org.teamapps.projector.component.infinitescroll.table;

public enum TableDisplayStyle {

	TABLE, LIST;

	public DtoTableDisplayStyle toDto() {
		return DtoTableDisplayStyle.valueOf(name());
	}

}
