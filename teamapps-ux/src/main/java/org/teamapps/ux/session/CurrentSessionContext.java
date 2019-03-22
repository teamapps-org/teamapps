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
package org.teamapps.ux.session;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;

public class CurrentSessionContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(CurrentSessionContext.class);
	private static final ThreadLocal<Deque<LockableSessionContext>> CURRENT_CONTEXT = new ThreadLocal<>();

	@NotNull
	public static SessionContext get() {
		LockableSessionContext sessionContext = getContextStack().peekLast();
		if (sessionContext == null) {
			String errorMessage = "CurrentSessionContext is not set but requested! Please use SessionContext.runWithContext(Runnable) to set the context.";
			IllegalStateException illegalStateException = new IllegalStateException(errorMessage);
			LOGGER.error(errorMessage, illegalStateException);
			throw illegalStateException;
		}
		return sessionContext;
	}

	public static LockableSessionContext getOrNull() {
		return getContextStack().peekLast();
	}

	/*package-private*/
	static void pushContext(LockableSessionContext sessionContext) {
		getContextStack().addLast(sessionContext);
	}

	/*package-private*/
	static void popContext() {
		getContextStack().removeLast();
	}

	private static Deque<LockableSessionContext> getContextStack() {
		Deque<LockableSessionContext> contextStack = CURRENT_CONTEXT.get();
		if (contextStack == null) {
			contextStack = new ArrayDeque<>();
			CURRENT_CONTEXT.set(contextStack);
		}
		return contextStack;
	}
}
