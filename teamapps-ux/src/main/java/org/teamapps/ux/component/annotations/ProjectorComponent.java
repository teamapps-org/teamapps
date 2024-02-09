package org.teamapps.ux.component.annotations;

import org.teamapps.ux.component.ComponentLibrary;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectorComponent {
	Class<? extends ComponentLibrary> library();
}
