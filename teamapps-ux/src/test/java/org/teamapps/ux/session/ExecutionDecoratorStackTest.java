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
package org.teamapps.ux.session;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ExecutionDecoratorStackTest {

	@Test
	public void createWrappedRunnable() {
		ExecutionDecoratorStack stack = new ExecutionDecoratorStack();

		final List<String> invocationTraces = new ArrayList<>();

		ExecutionDecorator decorator1 = createDecorator(invocationTraces, "before 1", "after 1");
		ExecutionDecorator decorator2 = createDecorator(invocationTraces, "before 2", "after 2");
		ExecutionDecorator decorator3 = createDecorator(invocationTraces, "before 3", "after 3");

		stack.addOuterDecorator(decorator1);
		stack.addOuterDecorator(decorator2);
		stack.addOuterDecorator(decorator3);

		stack.createWrappedRunnable(() -> invocationTraces.add("actual execution"))
				.run();

		Assertions.assertThat(invocationTraces).containsExactly(
				"before 3", "before 2", "before 1",
				"actual execution",
				"after 1", "after 2", "after 3"
		);
	}

	@Test
	public void createWrappedRunnableAppendingInnerDecorator() {
		ExecutionDecoratorStack stack = new ExecutionDecoratorStack();

		final List<String> invocationTraces = new ArrayList<>();

		ExecutionDecorator decorator1 = createDecorator(invocationTraces, "before 1", "after 1");
		ExecutionDecorator decorator2 = createDecorator(invocationTraces, "before 2", "after 2");
		ExecutionDecorator decorator3 = createDecorator(invocationTraces, "before 3", "after 3");

		stack.addOuterDecorator(decorator1);
		stack.addOuterDecorator(decorator2);
		stack.addInnerDecorator(decorator3);

		stack.createWrappedRunnable(() -> invocationTraces.add("actual execution"))
				.run();

		Assertions.assertThat(invocationTraces).containsExactly(
				"before 2", "before 1", "before 3",
				"actual execution",
				"after 3", "after 1", "after 2"
		);
	}

	private ExecutionDecorator createDecorator(List<String> invocationTraces, String s, String s2) {
		return runnable -> {
			invocationTraces.add(s);
			runnable.run();
			invocationTraces.add(s2);
		};
	}
}
