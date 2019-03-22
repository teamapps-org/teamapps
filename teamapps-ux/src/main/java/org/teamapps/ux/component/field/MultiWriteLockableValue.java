/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.field;

import java.util.HashSet;
import java.util.Set;

/**
 * Used to write-lock a value for client-writes, as long as not all server value changes have been applied to the client.
 *
 * This code is not thread-safe since all access to ux components is sequential due to exclusive access to sessions (see {@link org.teamapps.ux.session.CurrentSessionContext}).
 */
public class MultiWriteLockableValue<VALUE> {

	private VALUE value;
	private Set<Object> locks = new HashSet<>();

	public MultiWriteLockableValue(VALUE value) {
		this.value = value;
	}

	public Lock writeAndLock(VALUE value) {
		Object lockHandle = new Object();
		locks.add(lockHandle);
		this.value = value;
		return () -> locks.remove(lockHandle);
	}

	public void writeIfNotLocked(VALUE value) {
		if (locks.isEmpty()) {
			this.value = value;
		}
	}

	public boolean isLocked() {
		return !locks.isEmpty();
	}

	public VALUE read() {
		return value;
	}

	public interface Lock {
		void release();
	}
}
