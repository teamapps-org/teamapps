package org.teamapps.projector.clientobject;

/**
 * This is a marker interface for client objects that provide an invoke method on the client side.
 * A client object that can be invoked on the client-side, fulfilling the Invokable on the client-side.
 * <p>
 * The client-side implementation needs to implement the Invokable interface
 * defined in Invokable.ts.
 * <p>
 * Note that there is no forced server-side invocation method in this interface, although it might
 * make sense for implementations to have an "invoke" method on the server-side, too.
 */
public interface InvokableClientObject extends ClientObject {

}
