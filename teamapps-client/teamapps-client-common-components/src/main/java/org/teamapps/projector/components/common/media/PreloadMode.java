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
package org.teamapps.projector.components.common.media;

import org.teamapps.projector.components.common.dto.DtoMediaPreloadMode;

public enum  PreloadMode {
	/**
	 * Indicates that the video should not be preloaded.
	 */
	NONE,

	/**
	 *  Indicates that only video metadata (e.g. length) is fetched.
	 *
	 *  This is the default setting!
	 */
	METADATA,

	/**
	 * Indicates that the whole video file can be downloaded, even if the user is not expected to use it.
	 */
	AUTO;

	public DtoMediaPreloadMode toUiPreloadMode() {
		return DtoMediaPreloadMode.valueOf(this.name());
	}
}
