/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.ux.session.navigation.annotation;

import org.teamapps.ux.session.navigation.RouteHandler;

import java.lang.annotation.*;

/**
 * Marks a method as {@link RouteHandler} method.
 * The specified path template needs to match in order to apply the method.
 * <p>
 * Path templates are relative, since every router has a path prefix (including SessionContext).
 * Path templates are normalized, meaning: a leading '/' will be added and trailing slashes will be removed automatically.
 * <p>
 * Example:
 *
 * <pre>
 * &#64;Routing("/foo/{id}")
 * String setFooId(&#64;PathParameter("id") Integer id,
 *                 &#64;QueryParameter("filter") String filter) {
 * 	...
 * }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RoutingPaths.class)
public @interface RoutingPath {

	/**
	 * Defines the path template.
	 * <p>
	 * Path templates do not need to do any URL encoding.
	 * <p>
	 * Do not include query parameters nor matrix parameters. (Matrix parameters are not supported at all in TeamApps.)
	 * <p>
	 * Embedded template parameters are allowed and are of the form:
	 * <pre>
	 *   param = "{" *WSP name *WSP [ ":" *WSP regex *WSP ] "}"
	 *   name = (ALPHA / DIGIT / "_")*(ALPHA / DIGIT / "." / "_" / "-" ) ; \w[\w\.-]*
	 *   regex = *( nonbrace / "{" *nonbrace "}" ) ; where nonbrace is any char other than "{" and "}"
	 * </pre>
	 */
	String value();

	/**
	 * Whether the path template should NOT match longer paths.
	 * <p>
	 * Path templates match paths with more path elements by default. For example "/foo" matches "/foo/bar". Specify false to prevent
	 * this behavior.
	 */
	boolean exact() default false;
}
