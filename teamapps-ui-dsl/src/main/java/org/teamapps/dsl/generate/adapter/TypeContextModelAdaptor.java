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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;
import org.teamapps.dsl.TeamAppsDtoParser.TypeContext;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel.*;

public class TypeContextModelAdaptor extends PojoModelAdaptor<TypeContext> {

	static final BiMap<String, String> PRIMITIVE_TYPE_TO_WRAPPER_TYPE = HashBiMap.create();
	private static final Map<String, String> PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE = new HashMap<>();
	private static final Map<String, String> PRIMITIVE_TYPE_TO_DEFAULT_VALUE = new HashMap<>();
	private static final Map<String, String> TYPESCRIPT_PRIMITIVE_TYPE_TO_DEFAULT_VALUE = new HashMap<>();

	static {
		PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("boolean", "Boolean");
		PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("char", "Character");
		PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("byte", "Byte");
		PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("short", "Short");
		PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("int", "Integer");
		PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("long", "Long");
		PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("float", "Float");
		PRIMITIVE_TYPE_TO_WRAPPER_TYPE.put("double", "Double");

		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("boolean", "boolean");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("char", "string");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("byte", "number");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("short", "number");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("int", "number");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("long", "number");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("float", "number");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("double", "number");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("Boolean", "boolean");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("Character", "string");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("Byte", "number");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("Short", "number");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("Integer", "number");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("Long", "number");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("Float", "number");
		PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.put("Double", "number");

		PRIMITIVE_TYPE_TO_DEFAULT_VALUE.put("boolean", "false");
		PRIMITIVE_TYPE_TO_DEFAULT_VALUE.put("char", "0");
		PRIMITIVE_TYPE_TO_DEFAULT_VALUE.put("byte", "0");
		PRIMITIVE_TYPE_TO_DEFAULT_VALUE.put("short", "0");
		PRIMITIVE_TYPE_TO_DEFAULT_VALUE.put("int", "0");
		PRIMITIVE_TYPE_TO_DEFAULT_VALUE.put("long", "0");
		PRIMITIVE_TYPE_TO_DEFAULT_VALUE.put("float", "0");
		PRIMITIVE_TYPE_TO_DEFAULT_VALUE.put("double", "0");
	}

	private final TeamAppsIntermediateDtoModel model;

	public TypeContextModelAdaptor(TeamAppsIntermediateDtoModel model) {
		this.model = model;
	}

	@Override
	public Object getProperty(Interpreter interpreter, ST self, TypeContext context, Object property, String propertyName) throws STNoSuchPropertyException {
		if ("javaTypeString".equals(propertyName)) {
			return getJavaTypeString(context, false);
		} else if ("javaNonPrimitiveTypeString".equals(propertyName)) {
			return getJavaTypeString(context, true);
		} else if ("primitiveTypeName".equals(propertyName)) {
			return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.inverse().getOrDefault(context.getText(), getJavaTypeString(context, false));
		} else if ("javaTypeWrapperString".equals(propertyName)) {
			return getJavaTypeWrapperString(context);
		} else if ("isString".equals(propertyName)) {
			return "String".equals(context.getText());
		} else if ("isEnum".equals(propertyName)) {
			return model.findReferencedEnum(context).isPresent();
		} else if ("isList".equals(propertyName)) {
			return isList(context);
		} else if ("isDictionary".equals(propertyName)) {
			return isDictionary(context);
		} else if ("isReferenceList".equals(propertyName)) {
			return isList(context) && model.isDtoClassOrInterface(getFirstTypeArgument(context));
		} else if ("firstTypeArgument".equals(propertyName)) {
			return getFirstTypeArgument(context);
		} else if ("isDtoClassOrInterface".equals(propertyName)) {
			return model.isDtoClassOrInterface(context);
		} else if ("isDtoType".equals(propertyName)) {
			return model.isDtoType(context);
		} else if ("isPrimitiveType".equals(propertyName)) {
			return isPrimitiveType(context);
		} else if ("isPrimitiveOrWrapperType".equals(propertyName)) {
			return isPrimitiveType(context) || isPrimitiveWrapperType(context);
		} else if ("isPrimitiveNumberOrNumberWrapperType".equals(propertyName)) {
			return Arrays.asList("byte", "Byte", "short", "Short", "int", "Integer", "long", "Long", "float", "Float", "double", "Double").contains(context.getText());
		} else if ("isBoolean".equals(propertyName)) {
			return "boolean".equals(context.getText());
		} else if ("primitiveType".equals(propertyName)) {
			if (isPrimitiveType(context)) {
				return context.getText();
			} else if (isPrimitiveWrapperType(context)) {
				return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.get(context.getText());
			} else {
				throw new IllegalArgumentException(context.getText() + " is not a primitive type!");
			}
		} else if ("isUiClientObjectReferenceOrCollectionOfThese".equals(propertyName)) {
			return isClientObjectReferenceOrCollectionOfThese(context);
		} else if ("objectType".equals(propertyName)) {
			if (isPrimitiveType(context)) {
				return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.get(context.getText());
			} else {
				return context.getText();
			}
		} else if ("defaultValue".equals(propertyName)) {
			return PRIMITIVE_TYPE_TO_DEFAULT_VALUE.getOrDefault(context.getText(), "null");
		} else if ("typeScriptType".equals(propertyName)) {
			return getTypeScriptTypeName(context);
		} else if ("isObjectReference".equals(propertyName)) {
			if (isObject(context)) {
				return true;
			} else {
				TypeContext firstTypeArgument = getFirstTypeArgument(context);
				return firstTypeArgument != null && isObject(firstTypeArgument);
			}
		} else {
			return super.getProperty(interpreter, self, context, property, propertyName);
		}
	}

	private static boolean isPrimitiveType(TypeContext typeContext) {
		return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.keySet().contains(typeContext.getText());
	}

	private static boolean isPrimitiveWrapperType(TypeContext typeContext) {
		return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.values().contains(typeContext.getText());
	}

	private String getJavaTypeString(TypeContext typeContext, boolean forceNonPrimitive) {
		if (isList(typeContext)) {
			TypeContext firstTypeArgument = getFirstTypeArgument(typeContext);
			if (isObject(firstTypeArgument)) {
				return "List";
			} else {
				return "List<" + getJavaTypeString(firstTypeArgument, true) + ">";
			}
		} else if (isDictionary(typeContext)) {
			return "Map<String, " + getJavaTypeString(typeContext.typeReference().typeArguments().typeArgument(0).type(), true) + ">";
		} else if (isUiClientObjectReference(typeContext)) {
			return "DtoReference";
		} else if (model.isDtoType(typeContext)) {
			return "Dto" + typeContext.getText();
		} else if (isPrimitiveType(typeContext) && forceNonPrimitive) {
			return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.get(typeContext.getText());
		} else {
			return typeContext.getText();
		}
	}

	private String getJavaTypeWrapperString(TypeContext typeContext) {
		if (isList(typeContext)) {
			TypeContext firstTypeArgument = getFirstTypeArgument(typeContext);
			return "List<" + getJavaTypeWrapperString(firstTypeArgument) + ">";
		} else if (isDictionary(typeContext)) {
			return "Map<String, " + getJavaTypeWrapperString(typeContext.typeReference().typeArguments().typeArgument(0).type()) + ">";
		} else if (model.isDtoClassOrInterface(typeContext)) {
			return getJavaTypeString(typeContext, false) + "Wrapper";
		} else if (isObject(typeContext)) {
			return "DtoJsonWrapper";
		} else {
			return getJavaTypeString(typeContext, false);
		}
	}

	private boolean isUiClientObjectReference(TypeContext typeContext) {
		return typeContext.typeReference() != null && typeContext.typeReference().referenceTypeModifier() != null;
	}

	private boolean isClientObjectReferenceOrCollectionOfThese(TypeContext typeContext) {
		while (typeContext != null) {
			if (isUiClientObjectReference(typeContext)) {
				return true;
			}
			typeContext = getFirstTypeArgument(typeContext);
		}
		return false;
	}

	private String getTypeScriptTypeName(TypeContext typeContext) {
		if (isObject(typeContext)) {
			return "any";
		} else if ("String".equals(typeContext.getText())) {
			return "string";
		} else if (isList(typeContext)) {
			return getTypeScriptTypeName(getFirstTypeArgument(typeContext)) + "[]";
		} else if (isDictionary(typeContext)) {
			return "{[name: string]: " + getTypeScriptTypeName(getFirstTypeArgument(typeContext)) + "}";
		} else if (isUiClientObjectReference(typeContext)) {
			return "unknown";
		} else if (model.isDtoType(typeContext)) {
			return "Dto" + typeContext.getText();
		} else if (PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.containsKey(typeContext.getText())) {
			return PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.get(typeContext.getText());
		} else {
			return typeContext.getText();
		}
	}

	private TypeContext getFirstTypeArgument(TypeContext typeContext) {
		if (typeContext.typeReference() != null && typeContext.typeReference().typeArguments() != null && !typeContext.typeReference().typeArguments().typeArgument().isEmpty()) {
			return typeContext.typeReference().typeArguments().typeArgument(0).type();
		} else {
			return null;
		}
	}
}
