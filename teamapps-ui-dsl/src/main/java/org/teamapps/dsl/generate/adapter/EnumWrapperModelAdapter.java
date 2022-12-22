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

import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;
import org.teamapps.dsl.generate.wrapper.EnumWrapper;

public class EnumWrapperModelAdapter extends ReferencableEntityModelAdaptor<EnumWrapper> {

	public EnumWrapperModelAdapter() {
		super();
	}

	@Override
	public Object getProperty(Interpreter interpreter, ST self, EnumWrapper context, Object property, String propertyName) throws STNoSuchPropertyException {
		switch (propertyName) {
			case "packageName":
				return context.getPackageName();
			default:
				return super.getProperty(interpreter, self, context, property, propertyName);
		}
	}

	@Override
	protected String getTypeScriptIdentifier(EnumWrapper node) {
		return node.getName();
	}

	@Override
	protected String getJsonIdentifier(EnumWrapper node) {
		return node.getName();
	}

	@Override
	protected String getJavaClassName(EnumWrapper node) {
		return "Dto" + StringUtils.capitalize(node.getName());
	}
}
