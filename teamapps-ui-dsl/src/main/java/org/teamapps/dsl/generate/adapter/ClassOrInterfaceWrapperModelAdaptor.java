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
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;
import org.teamapps.dsl.generate.wrapper.ClassOrInterfaceWrapper;

public class ClassOrInterfaceWrapperModelAdaptor extends ReferencableEntityModelAdaptor<ClassOrInterfaceWrapper<?>> {

	private final TeamAppsIntermediateDtoModel model;

	public ClassOrInterfaceWrapperModelAdaptor(TeamAppsIntermediateDtoModel model) {
		super();
		this.model = model;
	}

	@Override
	public Object getProperty(Interpreter interpreter, ST seld, ClassOrInterfaceWrapper<?> context, Object property, String propertyName) throws STNoSuchPropertyException {
		if ("allSuperInterfaces".equals(propertyName)) {
			return context.getAllInterfaces(false);
		} else if ("Identifier".equals(propertyName)) {
			return context.getName();
		} else if ("effectiveTypeScriptImports".equals(propertyName)) {
			return model.getEffectiveImports(context, true);
		} else if ("effectiveJavaImports".equals(propertyName)) {
			return model.getEffectiveImports(context, false);
		} else {
			return super.getProperty(interpreter, seld, context, property, propertyName);
		}
	}

	@Override
	protected String getTypeScriptIdentifier(ClassOrInterfaceWrapper<?> node) {
		return node.getName();
	}

	@Override
	protected String getJsonIdentifier(ClassOrInterfaceWrapper<?> node) {
		return node.getName();
	}

	@Override
	protected String getJavaClassName(ClassOrInterfaceWrapper<?> node) {
		return "Dto" + StringUtils.capitalize(node.getName());
	}

}
