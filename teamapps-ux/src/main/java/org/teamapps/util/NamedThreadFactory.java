/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
package org.teamapps.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;
	private final boolean deamon;

	public NamedThreadFactory(String name, boolean deamon) {
		namePrefix = name + "-";
		this.deamon = deamon;
	}

	public Thread newThread(Runnable r) {
		Thread t = new Thread(null, r, namePrefix + threadNumber.getAndIncrement());
		t.setDaemon(deamon);
		return t;
	}
}
