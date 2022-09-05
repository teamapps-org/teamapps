package org.teamapps.ux.component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TeamAppsComponent {
	Class<? extends ComponentLibrary> library();
}
