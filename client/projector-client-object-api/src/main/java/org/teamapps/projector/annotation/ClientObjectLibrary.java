package org.teamapps.projector.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ClientObjectLibrary {
	Class<? extends org.teamapps.projector.clientobject.ClientObjectLibrary> value();
}
