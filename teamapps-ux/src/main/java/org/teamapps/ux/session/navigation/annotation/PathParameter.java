package org.teamapps.ux.session.navigation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PathParameter {

	/**
	 * Defines the name of the path template parameter whose value will be used to initialize the value of the annotated
	 * method parameter.
	 * 
	 * @see RoutingPath#value()
	 */
	String value();

}
