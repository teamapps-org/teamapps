/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.ux.cache.record;

public class EqualsHashCodeWrapper<T> {

	private final T t;
	private final EqualsAndHashCode<T> equalsAndHashCode;

	public EqualsHashCodeWrapper(T t, EqualsAndHashCode<T> equalsAndHashCode) {
		this.t = t;
		this.equalsAndHashCode = equalsAndHashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EqualsHashCodeWrapper)) {
			return false;
		}
		if (equalsAndHashCode == null) {
			return this.t.equals(((EqualsHashCodeWrapper<?>) o).t);
		} else {
			return equalsAndHashCode.getEquals().test(t, ((EqualsHashCodeWrapper<?>) o).t);
		}
	}

	@Override
	public int hashCode() {
		if (equalsAndHashCode == null) {
			return t.hashCode();
		} else {
			return equalsAndHashCode.getHashCode().hashCode(t);
		}
	}
}
