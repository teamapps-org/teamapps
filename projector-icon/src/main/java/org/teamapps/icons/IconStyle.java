package org.teamapps.icons;

public interface IconStyle<I extends Icon> {

	I apply(I icon);

}
