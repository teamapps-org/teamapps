package org.teamapps.projector.clientobject;

import org.teamapps.projector.resource.Resource;

/**
 * Provides access to the client-side resources for a client object library.
 * A client object library is a set of client and server implementations of {@link ClientObject ClientObjects} that
 * are bundled together as a library.
 * <p>
 * {@link ClientObject} implementations need to be annotated with
 * {@link org.teamapps.projector.annotation.ClientObjectLibrary} to associate them with a {@link ClientObjectLibrary}.
 * <p>
 * The library is responsible for providing the main JavaScript and CSS resources,
 * as well as any additional resources that may be requested by the client.
 *
 * @see org.teamapps.projector.annotation.ClientObjectLibrary
 * @see ClientObject
 */
public interface ClientObjectLibrary {

	/**
	 * Returns the main JavaScript module file of this library as a {@link Resource}.
	 * <p>
	 * This resource contains the JavaScript code that implements the client-side
	 * functionality for the associated components.
	 *
	 * @return the main JavaScript resource
	 */
	Resource getMainJsResource();

	/**
	 * Returns the main CSS resource for this library as a {@link Resource}.
	 * <p>
	 * This resource contains the CSS styles for the associated components.
	 * <p>
	 * The default implementation returns null, indicating that no CSS stylesheet is needed
	 * or otherwise provided.
	 *
	 * @return the main CSS resource, or null if not available
	 */
	default Resource getMainCssResource() {
		return null;
	}

	/**
	 * Returns a specific resource based on the provided path.
	 * <p>
	 * This method is called when the client requests a resource from this library.
	 * The path is relative to the library's base path.
	 *
	 * @param pathInfo the path of the requested resource
	 * @return the requested resource, or null if the resource is not found
	 */
	Resource getResource(String pathInfo);

}
