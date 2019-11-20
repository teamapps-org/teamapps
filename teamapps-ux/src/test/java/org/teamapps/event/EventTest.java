package org.teamapps.event;

import org.junit.Assert;
import org.junit.Test;
import org.teamapps.testutil.UxTestUtil;
import org.teamapps.ux.session.SessionContext;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class EventTest {

	@Test
	public void testAddRemoveConsumerNonBound() {
		Event<String> event = new Event<>();
		Assert.assertEquals(0, event.getListeners().size());

		Consumer<String> listener = s -> {
		};
		event.addListener(listener);
		Assert.assertEquals(1, event.getListeners().size());

		event.removeListener(listener);
		Assert.assertEquals(0, event.getListeners().size());
	}

	@Test
	public void testAddRemoveConsumerBoundToSessionContext() throws ExecutionException, InterruptedException {
		Event<String> event = new Event<>();
		Assert.assertEquals(0, event.getListeners().size());
		Consumer<String> listener = s -> {};

		UxTestUtil.doWithMockedSessionContext(() -> {
			event.addListener(listener);
			Assert.assertEquals(1, event.getListeners().size());
		}).get();

		event.removeListener(listener);
		Assert.assertEquals(0, event.getListeners().size());
	}

	@Test
	public void testAddRemoveRunnableNonBound() {
		Event<String> event = new Event<>();
		Assert.assertEquals(0, event.getListeners().size());

		Runnable listener = () -> {};
		event.addListener(listener);
		Assert.assertEquals(1, event.getListeners().size());

		event.removeListener(listener);
		Assert.assertEquals(0, event.getListeners().size());
	}

	@Test
	public void testAddRemoveRunnableBoundToSessionContext() throws ExecutionException, InterruptedException {
		Event<String> event = new Event<>();
		Assert.assertEquals(0, event.getListeners().size());
		Runnable listener = () -> {};

		UxTestUtil.doWithMockedSessionContext(() -> {
			event.addListener(listener);
			Assert.assertEquals(1, event.getListeners().size());
		}).get();

		event.removeListener(listener);
		Assert.assertEquals(0, event.getListeners().size());
	}

	@Test
	public void testBoundListenersGetRemovedWhenSessionContextGetsDestroyed() throws ExecutionException, InterruptedException {
		SessionContext sessionContext = UxTestUtil.createDummySessionContext();

		Event<String> event = new Event<>();
		Assert.assertEquals(0, event.getListeners().size());

		Consumer<String> boundConsumerListener = s -> {};
		Runnable boundRunnableListener = () -> {};

		Consumer<String> unboundConsumerListener = s -> {};
		Runnable unboundRunnableListener = () -> {};

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
}