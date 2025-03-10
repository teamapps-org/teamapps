/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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

import java.util.function.BiPredicate;

public class EqualsAndHashCode<T> {
	private final BiPredicate<T, Object> equals;
	private final HashCodeFunction<T> hashCode;

	public static <T> EqualsAndHashCode<T> bypass() {
		return new EqualsAndHashCode<>(Object::equals, Object::hashCode);
	}

	public static <T> EqualsAndHashCode<T> identity() {
		return new EqualsAndHashCode<>((t, o) -> t == o, System::identityHashCode);
	}

	public EqualsAndHashCode(BiPredicate<T, Object> equals, HashCodeFunction<T> hashCode) {
		this.equals = equals;
		this.hashCode = hashCode;
	}

	public BiPredicate<T, Object> getEquals() {
		return equals;
	}

	public HashCodeFunction<T> getHashCode() {
		return hashCode;
	}

	public interface HashCodeFunction<T> {
		int hashCode(T o);
	}
}
