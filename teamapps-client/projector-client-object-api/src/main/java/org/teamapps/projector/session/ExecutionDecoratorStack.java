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
package org.teamapps.projector.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExecutionDecoratorStack {

	private final List<ExecutionDecorator> decorators = Collections.synchronizedList(new ArrayList<>());

	public void addOuterDecorator(ExecutionDecorator decorator) {
		decorators.add(decorator);
	}

	public void addInnerDecorator(ExecutionDecorator decorator) {
		decorators.add(0, decorator);
	}

	public void removeDecorator(ExecutionDecorator decorator) {
		decorators.remove(decorator);
	}

	public void clear() {
		decorators.clear();
	}

	public Runnable createWrappedRunnable(Runnable r) {
		if (decorators.isEmpty()) {
			return r;
		}
		synchronized (decorators) {
			Runnable outerRunnable = r;
			for (ExecutionDecorator decorator : decorators) {
				final Runnable innerRunnable = outerRunnable;
				outerRunnable = () -> {
					decorator.wrapExecution(innerRunnable);
				};
			}
			return outerRunnable;
		}
	}

}
