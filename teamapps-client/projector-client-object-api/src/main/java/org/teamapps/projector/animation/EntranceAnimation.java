/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.projector.animation;

import org.teamapps.projector.dto.DtoEntranceAnimation;

public enum EntranceAnimation {
	BACK_IN_DOWN,
	BACK_IN_LEFT,
	BACK_IN_RIGHT,
	BACK_IN_UP,

	LIGHT_SPEED_IN_RIGHT,
	LIGHT_SPEED_IN_LEFT,
	JACK_IN_THE_BOX,
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
	FADE_IN_TOP_LEFT,
	FADE_IN_TOP_RIGHT,
	FADE_IN_BOTTOM_LEFT,
	FADE_IN_BOTTOM_RIGHT,

	BOUNCE_IN,
	BOUNCE_IN_DOWN,
	BOUNCE_IN_LEFT,
	BOUNCE_IN_RIGHT,
	BOUNCE_IN_UP;

	public DtoEntranceAnimation toUiEntranceAnimation() {
		return DtoEntranceAnimation.valueOf(name());
	}
}
