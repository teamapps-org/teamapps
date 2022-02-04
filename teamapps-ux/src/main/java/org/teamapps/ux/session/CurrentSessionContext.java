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
package org.teamapps.ux.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrentSessionContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(CurrentSessionContext.class);
	private static final ThreadLocal<SessionContext> CURRENT_CONTEXT = new ThreadLocal<>();

	public static SessionContext get() {
		SessionContext sessionContext = CURRENT_CONTEXT.get();
		if (sessionContext == null) {
			String errorMessage = "CurrentSessionContext is not set but requested! Please use SessionContext.runWithContext(Runnable) to set the context.";
			IllegalStateException illegalStateException = new IllegalStateException(errorMessage);
			LOGGER.error(errorMessage, illegalStateException);
			throw illegalStateException;
		}
		return sessionContext;
	}

	public static void throwIfNotSameAs(SessionContext sessionContext) {
		SessionContext currentSessionContext = CURRENT_CONTEXT.get();
		if (currentSessionContext != sessionContext) {
			String errorMessage;
			if (currentSessionContext == null) {
				errorMessage = "CurrentSessionContext is not set! Please use SessionContext.runWithContext(Runnable) to set the context.";
			} else {
				errorMessage = "CurrentSessionContext is set to a different session context than expected! Please use SessionContext.runWithContext(Runnable) to set the context.";
			}
			IllegalStateException illegalStateException = new IllegalStateException(errorMessage);
			LOGGER.error(errorMessage, illegalStateException);
			throw illegalStateException;
		}
	}

	public static SessionContext getOrNull() {
		return CURRENT_CONTEXT.get();
	}

	/*package-private*/
	static void set(SessionContext sessionContext) {
		CURRENT_CONTEXT.set(sessionContext);
	}

	/*package-private*/
	static void unset() {
		CURRENT_CONTEXT.remove();
	}

}
