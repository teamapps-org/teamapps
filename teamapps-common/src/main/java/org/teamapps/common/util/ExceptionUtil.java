/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;

public class ExceptionUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ExceptionUtil() {}

	public static void softenExceptions(RunnableWithExceptions runnable) {
		try {
			runnable.run();
		} catch (Exception e) {
			throw softenedException(e);
		}
	}

	public static <V> V softenExceptions(Callable<V> callable) {
		try {
			return callable.call();
		} catch (Exception e) {
			throw softenedException(e);
		}
	}

	public static <T extends RuntimeException> T softenedException(final Throwable e) {
		return uncheck(e);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> T uncheck(Throwable throwable) throws T {
		throw (T) throwable;
	}

}
