/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.webrtc.apiclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamAndKinds {

	@JsonProperty("stream")
	private final String streamUuid;
	private final Set<MediaKind> kinds;

	public StreamAndKinds(String streamUuid, Set<MediaKind> kinds) {
		this.streamUuid = streamUuid;
		this.kinds = kinds;
	}

	public String getStreamUuid() {
		return streamUuid;
	}

	public Set<MediaKind> getKinds() {
		return kinds;
	}
}
