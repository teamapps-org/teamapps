package org.teamapps.projector.clientobject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectorComponent {
	Class<? extends ComponentLibrary> library();
}
