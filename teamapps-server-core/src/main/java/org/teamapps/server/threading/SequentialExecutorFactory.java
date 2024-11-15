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
package org.teamapps.server.threading;

import java.util.concurrent.ExecutorService;

/**
 * Creates {@link ExecutorService}s that will guarantee the sequential execution of submitted tasks.
 * Note that this guarantee is <b>NOT</b> given with {@link java.util.concurrent.ThreadPoolExecutor}.
 * <p>
 * The most naive implementation will just return {@link java.util.concurrent.Executors#newSingleThreadExecutor()},
 * but most operating systems have limits on the number of threads allowed, so this is not a good solution,
 * and not a good idea for other reasons, too.
 */
public interface SequentialExecutorFactory {

	ExecutorService createExecutor(String name);

}
