package org.teamapps.event;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class EventTest {

	@Test
	public void registrationListenerOnlyCalledWhenListenersChangeFromEmptyToNonEmptyAndViceVersa() {
		EventListenerRegistrationStatusListener registrationListener = mock(EventListenerRegistrationStatusListener.class);

		verify(registrationListener, times(0)).listeningStatusChanged(anyBoolean());

		Event<Object> event = new Event<>(registrationListener);

		Disposable disposable1 = event.addListener(() -> {
		});

		verify(registrationListener, times(1)).listeningStatusChanged(true);
		verify(registrationListener, times(1)).listeningStatusChanged(anyBoolean());

		Disposable disposable2 = event.addListener(() -> {
		});

		verify(registrationListener, times(1)).listeningStatusChanged(anyBoolean());

		disposable1.dispose();

		verify(registrationListener, times(1)).listeningStatusChanged(anyBoolean());

		disposable2.dispose();

		verify(registrationListener, times(1)).listeningStatusChanged(false);
		verify(registrationListener, times(2)).listeningStatusChanged(anyBoolean());
	}


}