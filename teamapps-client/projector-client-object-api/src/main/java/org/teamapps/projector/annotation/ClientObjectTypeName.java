package org.teamapps.projector.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies the type name to be used on the client side while creating the client object.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientObjectTypeName {
	String value();
}
