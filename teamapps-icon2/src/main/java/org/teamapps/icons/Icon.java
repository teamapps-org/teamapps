package org.teamapps.icons;

/**
 * @param <ICON> Must be a self class reference.
 * @param <STYLE> The style class these icons support.
 */
public interface Icon<ICON extends Icon<ICON, STYLE>, STYLE> {

	/**
	 * Creates a copy of this icon with the specified style. The style may be null!
	 * @param style The style to apply. May be null!
	 * @return A copy of this icon with the specified style.
	 */
	ICON withStyle(STYLE style);

	/**
	 * @return The style of this icon. May be null!
	 */
	STYLE getStyle();

}
