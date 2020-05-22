/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.ux.component.absolutelayout;

import org.teamapps.dto.UiAnimationEasing;

public enum AnimationEasing {

	/**
	 * Default value.
	 * Specifies a transition effect with a slow start, then fast, then end slowly (equivalent to cubic-bezier(0.25,0.1,0.25,1))
	 */
	EASE("ease"),
	/**
	 * Specifies a transition effect with the same speed from start to end (equivalent to cubic-bezier(0,0,1,1))
	 */
	LINEAR("linear"),
	/**
	 * Specifies a transition effect with a slow start (equivalent to cubic-bezier(0.42,0,1,1))
	 */
	EASE_IN("ease-in"),
	/**
	 * Specifies a transition effect with a slow end (equivalent to cubic-bezier(0,0,0.58,1))
	 */
	EASE_OUT("ease-out"),
	/**
	 * Specifies a transition effect with a slow start and end (equivalent to cubic-bezier(0.42,0,0.58,1))
	 */
	EASE_IN_OUT("ease-in-out"),
	/**
	 * Equivalent to steps(1, start)
	 */
	STEP_START("step-start"),
	/**
	 * Equivalent to steps(1, end)
	 */
	STEP_END("step-end");

	private final String cssString;

	AnimationEasing(String cssString) {
		this.cssString = cssString;
	}

	public String getCssString() {
		return cssString;
	}

	public UiAnimationEasing toUiAnimationEasing() {
		return UiAnimationEasing.valueOf(this.name());
	}
}
