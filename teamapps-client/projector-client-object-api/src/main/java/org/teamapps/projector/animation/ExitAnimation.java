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

import org.teamapps.projector.dto.DtoExitAnimation;

public enum ExitAnimation {
	BACK_OUT_DOWN,
	BACK_OUT_LEFT,
	BACK_OUT_RIGHT,
	BACK_OUT_UP,

	LIGHT_SPEED_OUT_RIGHT,
	LIGHT_SPEED_OUT_LEFT,
	ROLL_OUT,
	HINGE,

	ZOOM_OUT,
	ZOOM_OUT_DOWN,
	ZOOM_OUT_LEFT,
	ZOOM_OUT_RIGHT,
	ZOOM_OUT_UP,

	SLIDE_OUT_UP,
	SLIDE_OUT_DOWN,
	SLIDE_OUT_LEFT,
	SLIDE_OUT_RIGHT,

	ROTATE_OUT,
	ROTATE_OUT_DOWNLEFT,
	ROTATE_OUT_DOWNRIGHT,
	ROTATE_OUT_UPLEFT,
	ROTATE_OUT_UPRIGHT,

	FLIP_OUT_X,
	FLIP_OUT_Y,

	FADE_OUT,
	FADE_OUT_DOWN,
	FADE_OUT_DOWNBIG,
	FADE_OUT_LEFT,
	FADE_OUT_LEFTBIG,
	FADE_OUT_RIGHT,
	FADE_OUT_RIGHTBIG,
	FADE_OUT_UP,
	FADE_OUT_UPBIG,
	FADE_OUT_TOP_LEFT,
	FADE_OUT_TOP_RIGHT,
	FADE_OUT_BOTTOM_RIGHT,
	FADE_OUT_BOTTOM_LEFT,

	BOUNCE_OUT,
	BOUNCE_OUT_DOWN,
	BOUNCE_OUT_LEFT,
	BOUNCE_OUT_RIGHT,
	BOUNCE_OUT_UP;

	public DtoExitAnimation toUiExitAnimation() {
		return DtoExitAnimation.valueOf(name());
	}
}
