package org.teamapps.ux.component.animation;

import org.teamapps.dto.UiRepeatableAnimation;

public enum RepeatableAnimation {

	BOUNCE,
	FLASH,
	PULSE,
	RUBBER_BAND,
	SHAKE_X,
	SHAKE_Y,
	HEAD_SHAKE,
	SWING,
	TADA,
	WOBBLE,
	JELLO,
	HEART_BEAT,
	FLIP,
	BLINK,
	BLINK_SUBTLE;

	public UiRepeatableAnimation toUiRepeatableAnimation() {
		return UiRepeatableAnimation.valueOf(name());
	}

}
