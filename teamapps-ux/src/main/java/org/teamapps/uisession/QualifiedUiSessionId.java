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
package org.teamapps.uisession;

import java.util.Objects;

public class QualifiedUiSessionId {

	private final String httpSessionId;
	private final String uiSessionId;

	public QualifiedUiSessionId(String httpSessionId, String uiSessionId) {
		if (httpSessionId == null) {
			throw new NullPointerException("httpSessionId may not be null!");
		}
		if (uiSessionId == null) {
			throw new NullPointerException("uiSessionId may not be null!");
		}
		this.httpSessionId = httpSessionId;
		this.uiSessionId = uiSessionId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		QualifiedUiSessionId that = (QualifiedUiSessionId) o;
		return Objects.equals(httpSessionId, that.httpSessionId) && Objects.equals(uiSessionId, that.uiSessionId);
	}

	@Override
	public int hashCode() {
		int result = httpSessionId.hashCode();
		result = 31 * result + uiSessionId.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return httpSessionId + ':' + uiSessionId;
	}

	public String getHttpSessionId() {
		return httpSessionId;
	}

	public String getUiSessionId() {
		return uiSessionId;
	}
}
