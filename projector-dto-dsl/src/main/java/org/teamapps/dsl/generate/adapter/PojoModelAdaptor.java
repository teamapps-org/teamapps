/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.dsl.generate.adapter;

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.compiler.STException;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

import java.lang.reflect.Method;
import java.util.Set;

public class PojoModelAdaptor<T> implements ModelAdaptor<T> {

	private static final Set<String> optionalProperties = Set.of(
			"superClass",
			"required"
	);

	@Override
	public Object getProperty(Interpreter interpreter, ST self, T context, Object property, String propertyName) throws STNoSuchPropertyException {
		return invoke(context, propertyName);
	}

	public static <T> Object invoke(T context, String propertyName) {
		Method m = null;
		try {
			String mn = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
			m = context.getClass().getMethod(mn);
		} catch (NoSuchMethodException e) {
		}
		if (m == null) {
			try {
				String mn = "is" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
				m = context.getClass().getMethod(mn);

			} catch (NoSuchMethodException e) {
			}
		}
		if (m == null) {
			try {
				m = context.getClass().getMethod(propertyName);
			} catch (NoSuchMethodException e) {
			}
		}
		if (m != null) {
			try {
				return m.invoke(context);
			} catch (Exception e) {
				throw new STException("Exception while invoking method " + m.getName() + " for property " + propertyName, e);
			}
		} else if (optionalProperties.contains(propertyName)) {
			return null;
		} else {
			throw new STNoSuchPropertyException(null, context, propertyName);
		}
	}

}
