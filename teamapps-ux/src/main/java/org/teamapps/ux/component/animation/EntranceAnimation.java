package org.teamapps.ux.component.animation;

import org.teamapps.dto.UiEntranceAnimation;

public enum EntranceAnimation {
	LIGHTSPEED_IN,
	ROLL_IN,
	ZOOM_IN,
	ZOOM_IN_DOWN,
	ZOOM_IN_LEFT,
	ZOOM_IN_RIGHT,
	ZOOM_IN_UP,
	SLIDE_IN_UP,
	SLIDE_IN_DOWN,
	SLIDE_IN_LEFT,
	SLIDE_IN_RIGHT,
	ROTATE_IN,
	ROTATE_IN_DOWNLEFT,
	ROTATE_IN_DOWNRIGHT,
	ROTATE_IN_UPLEFT,
	ROTATE_IN_UPRIGHT,
	FLIP_IN_X,
	FLIP_IN_Y,
	FADE_IN,
	FADE_IN_DOWN,
	FADE_IN_DOWNBIG,
	FADE_IN_LEFT,
	FADE_IN_LEFTBIG,
	FADE_IN_RIGHT,
	FADE_IN_RIGHTBIG,
	FADE_IN_UP,
	FADE_IN_UPBIG,
	BOUNCE_IN,
	BOUNCE_IN_DOWN,
	BOUNCE_IN_LEFT,
	BOUNCE_IN_RIGHT,
	BOUNCE_IN_UP;

	public UiEntranceAnimation toUiEntranceAnimation() {
		return UiEntranceAnimation.valueOf(name());
	}
}