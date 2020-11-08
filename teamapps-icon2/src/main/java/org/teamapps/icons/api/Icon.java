package org.teamapps.icons.api;

public interface Icon<ICON extends Icon<ICON, STYLE>, STYLE> {

	ICON withStyle(STYLE style);

	STYLE getStyle();

}
