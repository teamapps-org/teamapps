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
package org.teamapps.projector.component.chart.forcelayout;

import java.util.Objects;

public class LinkId<RECORD> {

	private final RECORD source;
	private final RECORD target;

	public LinkId(RECORD source, RECORD target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LinkId<?> linkId = (LinkId<?>) o;
		return (source.equals(linkId.source) && target.equals(linkId.target)) ||
				(source.equals(linkId.target) && target.equals(linkId.source));
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(source) + Objects.hashCode(target);
	}
}
