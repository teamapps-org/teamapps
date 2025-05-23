package org.teamapps.projector.annotation;

import org.teamapps.projector.clientobject.ClientMessageHandler;
import org.teamapps.projector.clientobject.ClientObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation that specifies the library implementation for a client object.
 * <p>
 * This is necessary to associate a server-side component with its corresponding
 * client-side implementation library.
 * <p>
 * This annotation should be put on all {@link ClientObject} implementations.
 *
 * <h2>Usage Example:</h2>
 * <pre>
 * {@code
 * @ClientObjectLibrary(MyClientLibrary.class)
 * public class MyComponent implements ClientObject {
 *     // Component implementation
 * }
 * }
 * </pre>
 *
 * @see ClientObject
 * @see ClientMessageHandler
 * @see ClientObjectTypeName
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientObjectLibrary {

	/**
	 * Specifies the class that implements the client-side library for this component.
	 *
	 * @return A class that extends {@link org.teamapps.projector.clientobject.ClientObjectLibrary}
	 */
	Class<? extends org.teamapps.projector.clientobject.ClientObjectLibrary> value();
}
