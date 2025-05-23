package org.teamapps.projector.clientobject;

/**
 * An interface for {@link ClientObject ClientObjects} that have a {@link #show()} method,
 * both on the server and client side. See {@code Showable.ts}.
 */
public interface Showable extends ClientObject {

	/**
	 * Shows this object to the user.
	 * <p>
	 * This is the server side method, corresponding to the client side one. See {@code Showable.ts}.
	 */
	void show();

}
