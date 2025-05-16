package org.teamapps.projector.clientobject;

/**
 * A client object that can be invoked on the client-side.
 * <p>
 * The client-side implementation needs to implement the Invokable interface defined in Invokable.ts.
 * <p>
 * Note that there is no server-side invocation method in this interface. This only marks client-objects as
 * invocable on the client side. Whether they actually are cannot be checked by the Java compiler.
 */
public interface ClientSideInvokable extends ClientObject {
}
