package org.teamapps.projector.icon;

public interface IconStyle<I extends Icon> {

	I apply(I icon);

}
