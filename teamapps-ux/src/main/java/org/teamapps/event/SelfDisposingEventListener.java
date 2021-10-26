package org.teamapps.event;

public interface SelfDisposingEventListener<E> {

	void handle(E eventData, Disposable disposable);

}
