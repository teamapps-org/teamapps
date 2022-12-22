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
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

public abstract class ReferencableEntityModelAdaptor<N> extends PojoModelAdaptor<N> {

	@Override
	public Object getProperty(Interpreter interpreter, ST self, N context, Object property, String propertyName) throws STNoSuchPropertyException {
		if ("typeScriptIdentifier".equals(propertyName)) {
			return getTypeScriptIdentifier(context);
		} else if ("_type".equals(propertyName)) {
			return getJsonIdentifier(context);
		} else if ("javaClassName".equals(propertyName)) {
			return getJavaClassName(context);
		} else {
			return super.getProperty(interpreter, self, context, property, propertyName);
		}
	}

	protected abstract String getTypeScriptIdentifier(N node);
	
	protected abstract String getJsonIdentifier(N node);

	protected abstract String getJavaClassName(N node);
}
