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
