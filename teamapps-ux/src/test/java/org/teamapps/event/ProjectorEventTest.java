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
package org.teamapps.event;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.testutil.UxTestUtil;
import org.teamapps.projector.session.SessionContext;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.teamapps.testutil.UxTestUtil.createDummySessionContext;

public class ProjectorEventTest {

	@Test
	public void testAddRemoveConsumerNonBound() {
		ProjectorEvent<String> event = new ProjectorEvent<>();
		Assert.assertEquals(0, event.getListeners().size());

		Consumer<String> listener = s -> {
		};
		Disposable disposable = event.addListener(listener);
		Assert.assertEquals(1, event.getListeners().size());

		disposable.dispose();
		Assert.assertEquals(0, event.getListeners().size());
	}

	@Test
	public void testAddRemoveConsumerBoundToSessionContext() throws ExecutionException, InterruptedException {
		ProjectorEvent<String> event = new ProjectorEvent<>();
		Assert.assertEquals(0, event.getListeners().size());
		Consumer<String> listener = s -> {
		};

		AtomicReference<Disposable> disposable = new AtomicReference<>();
		UxTestUtil.doWithMockedSessionContext(() -> {
			disposable.set(event.addListener(listener));
			Assert.assertEquals(1, event.getListeners().size());
		}).get();

		disposable.get().dispose();
		Assert.assertEquals(0, event.getListeners().size());
	}

	@Test
	public void testAddRemoveRunnableNonBound() {
		ProjectorEvent<String> event = new ProjectorEvent<>();
		Assert.assertEquals(0, event.getListeners().size());

		Runnable listener = () -> {
		};
		Disposable disposable = event.addListener(listener);
		Assert.assertEquals(1, event.getListeners().size());

		disposable.dispose();
		Assert.assertEquals(0, event.getListeners().size());
	}

	@Test
	public void testAddRemoveRunnableBoundToSessionContext() throws ExecutionException, InterruptedException {
		ProjectorEvent<String> event = new ProjectorEvent<>();
		Assert.assertEquals(0, event.getListeners().size());
		Runnable listener = () -> {
		};

		AtomicReference<Disposable> disposable = new AtomicReference<>();
		UxTestUtil.doWithMockedSessionContext(() -> {
			disposable.set(event.addListener(listener));
			Assert.assertEquals(1, event.getListeners().size());
		}).get();

		disposable.get().dispose();
		Assert.assertEquals(0, event.getListeners().size());
	}

	@Test
	public void disposableShouldRemoveRunnableListener() {
		ProjectorEvent<String> event = new ProjectorEvent<>();

		Disposable d = event.addListener(() -> {
		});
		assertThat(event.getListeners()).hasSize(1);
		d.dispose();
		assertThat(event.getListeners()).isEmpty();
	}

	@Test
	public void disposableShouldRemoveOnlyOneRunnableListener() {
		ProjectorEvent<String> event = new ProjectorEvent<>();
		Runnable listener = () -> {
		};

		Disposable d = event.addListener(listener);
		Disposable d2 = event.addListener(listener);
		assertThat(event.getListeners()).hasSize(2);
		d.dispose();
		assertThat(event.getListeners()).hasSize(1);
	}

	@Test
	public void disposableShouldRemoveConsumerListener() {
		ProjectorEvent<String> event = new ProjectorEvent<>();

		Disposable d = event.addListener(s -> {
		});
		assertThat(event.getListeners()).hasSize(1);
		d.dispose();
		assertThat(event.getListeners()).isEmpty();
	}

	@Test
	public void disposableShouldRemoveOnlyOneConsumerListener() {
		ProjectorEvent<String> event = new ProjectorEvent<>();
		Consumer<String> listener = s -> {
		};

		Disposable d = event.addListener(listener);
		Disposable d2 = event.addListener(listener);
		assertThat(event.getListeners()).hasSize(2);
		d.dispose();
		assertThat(event.getListeners()).hasSize(1);
	}

	@Test
	public void disposableShouldRemoveRunnableListenerBoundToSessionContext() throws ExecutionException, InterruptedException {
		ProjectorEvent<String> event = new ProjectorEvent<>();

		AtomicReference<Disposable> d = new AtomicReference<>();
		UxTestUtil.doWithMockedSessionContext(() -> {
			d.set(event.addListener(() -> {
			}));
		}).get();
		assertThat(event.getListeners()).hasSize(1);
		d.get().dispose();
		assertThat(event.getListeners()).isEmpty();
	}

	@Test
	public void disposableShouldRemoveOnlyOneRunnableListenerBoundToSessionContext() throws ExecutionException, InterruptedException {
		ProjectorEvent<String> event = new ProjectorEvent<>();

		AtomicReference<Disposable> d = new AtomicReference<>();
		UxTestUtil.doWithMockedSessionContext(() -> {
			Runnable listener = () -> {
			};
			d.set(event.addListener(listener));
			event.addListener(listener);
		}).get();
		assertThat(event.getListeners()).hasSize(2);
		d.get().dispose();
		assertThat(event.getListeners()).hasSize(1);
	}

	@Test
	public void disposableShouldRemoveConsumerListenerBoundToSessionContext() throws ExecutionException, InterruptedException {
		ProjectorEvent<String> event = new ProjectorEvent<>();

		AtomicReference<Disposable> d = new AtomicReference<>();
		UxTestUtil.doWithMockedSessionContext(() -> {
			d.set(event.addListener(s -> {
			}));
		}).get();
		assertThat(event.getListeners()).hasSize(1);
		d.get().dispose();
		assertThat(event.getListeners()).isEmpty();
	}

	@Test
	public void disposableShouldRemoveOnlyOneConsumerListenerBoundToSessionContext() throws ExecutionException, InterruptedException {
		ProjectorEvent<String> event = new ProjectorEvent<>();

		AtomicReference<Disposable> d = new AtomicReference<>();
		UxTestUtil.doWithMockedSessionContext(() -> {
			Consumer<String> listener = s -> {
			};
			d.set(event.addListener(listener));
			event.addListener(listener);
		}).get();
		assertThat(event.getListeners()).hasSize(2);
		d.get().dispose();
		assertThat(event.getListeners()).hasSize(1);
	}

	@Test
	public void testBoundListenersGetRemovedWhenSessionContextGetsDestroyed() throws ExecutionException, InterruptedException {
		SessionContext sessionContext = createDummySessionContext();

		ProjectorEvent<String> event = new ProjectorEvent<>();
		Assert.assertEquals(0, event.getListeners().size());

		Consumer<String> boundConsumerListener = s -> {
		};
		Runnable boundRunnableListener = () -> {
		};

		Consumer<String> unboundConsumerListener = s -> {
		};
		Runnable unboundRunnableListener = () -> {
		};

		event.addListener(unboundConsumerListener);
		Assert.assertEquals(1, event.getListeners().size());
		sessionContext.runWithContext(() -> {
			event.addListener(boundConsumerListener);
			event.addListener(boundRunnableListener);
			Assert.assertEquals(3, event.getListeners().size());
		}).get();
		event.addListener(unboundRunnableListener);
		Assert.assertEquals(4, event.getListeners().size());

		sessionContext.onDestroyed().fire();
		Assert.assertEquals(2, event.getListeners().size());
	}

	@Test
	public void testSelfDisposingEventListener() {
		ProjectorEvent<Integer> event = new ProjectorEvent<>();
		AtomicInteger invocationCount = new AtomicInteger();

		event.addListener((i, disposable) -> {
			invocationCount.incrementAndGet();
			if (i == 3) {
				disposable.dispose();
			}
		});

		assertThat(invocationCount).hasValue(0);
		assertThat(event.getListeners()).hasSize(1);
		event.fire(1);
		assertThat(invocationCount).hasValue(1);
		assertThat(event.getListeners()).hasSize(1);
		event.fire(2);
		assertThat(invocationCount).hasValue(2);
		assertThat(event.getListeners()).hasSize(1);
		event.fire(3);
		assertThat(invocationCount).hasValue(3);
		assertThat(event.getListeners()).hasSize(0);
		event.fire(4);
		assertThat(invocationCount).hasValue(3);
		assertThat(event.getListeners()).hasSize(0);
	}

	@Test
	public void testSelfDisposingEventListenerBoundToSessionContext() throws ExecutionException, InterruptedException {
		SessionContext sessionContext = UxTestUtil.createDummySessionContext();
		ProjectorEvent<Integer> event = new ProjectorEvent<>();
		AtomicInteger invocationCount = new AtomicInteger();

		sessionContext.runWithContext(() -> {
			event.addListener((i, disposable) -> {
				invocationCount.incrementAndGet();
				if (i == 3) {
					disposable.dispose();
				}
			});
		}).get();

		Awaitility.await().atMost(1, SECONDS).untilAsserted(() -> {
			assertThat(invocationCount).hasValue(0);
			assertThat(event.getListeners()).hasSize(1);
		});
		event.fire(1);
		event.fire(2);
		Awaitility.await().atMost(1, SECONDS).untilAsserted(() -> {
			assertThat(invocationCount).hasValue(2);
			assertThat(event.getListeners()).hasSize(1);
		});
		event.fire(3);
		Awaitility.await().atMost(1, SECONDS).untilAsserted(() -> {
			assertThat(invocationCount).hasValue(3);
			assertThat(event.getListeners()).hasSize(0);
		});
		event.fire(4);
		Awaitility.await().atMost(1, SECONDS).untilAsserted(() -> {
			assertThat(invocationCount).hasValue(3);
			assertThat(event.getListeners()).hasSize(0);
		});
	}
}
