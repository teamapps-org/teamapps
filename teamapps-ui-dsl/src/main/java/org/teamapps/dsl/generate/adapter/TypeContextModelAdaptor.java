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
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.TeamAppsGeneratorException;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TypeContextModelAdaptor extends PojoModelAdaptor {

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
	public Object getProperty(Interpreter interpreter, ST self, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
		TeamAppsDtoParser.TypeContext typeContext = (TeamAppsDtoParser.TypeContext) o;

		if ("javaTypeString".equals(propertyName)) {
			return getJavaTypeString(typeContext, false);
		} else if ("javaNonPrimitiveTypeString".equals(propertyName)) {
			return getJavaTypeString(typeContext, true);
		} else if ("primitiveTypeName".equals(propertyName)) {
			return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.inverse().getOrDefault(typeContext.getText(), getJavaTypeString(typeContext, false));
		} else if ("javaTypeWrapperString".equals(propertyName)) {
			return getJavaTypeWrapperString(typeContext);
		} else if ("isString".equals(propertyName)) {
			return "String".equals(typeContext.getText());
		} else if ("isEnum".equals(propertyName)) {
			return model.findReferencedEnum(typeContext) != null;
		} else if ("isList".equals(propertyName)) {
			return isList(typeContext);
		} else if ("isDictionary".equals(propertyName)) {
			return isDictionary(typeContext);
		} else if ("isReferenceList".equals(propertyName)) {
			return isList(typeContext) && model.isDtoClassOrInterface(getFirstTypeArgument(typeContext));
		} else if ("firstTypeArgument".equals(propertyName)) {
			return getFirstTypeArgument(typeContext);
		} else if ("isDtoClassOrInterface".equals(propertyName)) {
			return model.isDtoClassOrInterface(typeContext);
		} else if ("isDtoType".equals(propertyName)) {
			return model.isDtoType(typeContext);
		} else if ("isPrimitiveType".equals(propertyName)) {
			return isPrimitiveType(typeContext);
		} else if ("isPrimitiveOrWrapperType".equals(propertyName)) {
			return isPrimitiveType(typeContext) || isPrimitiveWrapperType(typeContext);
		} else if ("isPrimitiveNumberOrNumberWrapperType".equals(propertyName)) {
			return Arrays.asList("byte", "Byte", "short", "Short", "int", "Integer", "long", "Long", "float", "Float", "double", "Double").contains(typeContext.getText());
		} else if ("isBoolean".equals(propertyName)) {
			return "boolean".equals(typeContext.getText());
		} else if ("primitiveType".equals(propertyName)) {
			if (isPrimitiveType(typeContext)) {
				return typeContext.getText();
			} else if (isPrimitiveWrapperType(typeContext)) {
				return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.get(typeContext.getText());
			} else {
				throw new IllegalArgumentException(typeContext.getText() + " is not a primitive type!");
			}
		} else if ("isUiClientObjectReferenceOrCollectionOfThese".equals(propertyName)) {
			return isUiClientObjectReferenceOrCollectionOfThese(typeContext);
		} else if ("objectType".equals(propertyName)) {
			if (isPrimitiveType(typeContext)) {
				return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.get(typeContext.getText());
			} else {
				return typeContext.getText();
			}
		} else if ("defaultValue".equals(propertyName)) {
			return PRIMITIVE_TYPE_TO_DEFAULT_VALUE.getOrDefault(typeContext.getText(), "null");
		} else if ("typeScriptType".equals(propertyName)) {
			return getTypeScriptTypeName(typeContext);
		} else if ("isObjectReference".equals(propertyName)) {
			if (isObject(typeContext)) {
				return true;
			} else {
				TeamAppsDtoParser.TypeContext firstTypeArgument = getFirstTypeArgument(typeContext);
				return firstTypeArgument != null && isObject(firstTypeArgument);
			}
		} else {
			return super.getProperty(interpreter, self, o, property, propertyName);
		}
	}

	private static boolean isPrimitiveType(TeamAppsDtoParser.TypeContext typeContext) {
		return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.keySet().contains(typeContext.getText());
	}

	private static boolean isPrimitiveWrapperType(TeamAppsDtoParser.TypeContext typeContext) {
		return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.values().contains(typeContext.getText());
	}

	private String getJavaTypeString(TeamAppsDtoParser.TypeContext typeContext, boolean forceNonPrimitive) {
		if (isList(typeContext)) {
			TeamAppsDtoParser.TypeContext firstTypeArgument = getFirstTypeArgument(typeContext);
			if (isObject(firstTypeArgument)) {
				return "List";
			} else {
				return "List<" + getJavaTypeString(firstTypeArgument, true) + ">";
			}
		} else if (isDictionary(typeContext)) {
			return "Map<String, " + getJavaTypeString(typeContext.typeReference().typeArguments().typeArgument(0).type(), true) + ">";
		} else if (isUiClientObjectReference(typeContext)) {
			TeamAppsDtoParser.ClassDeclarationContext referencedClass = model.findReferencedClass(typeContext);
			TeamAppsDtoParser.InterfaceDeclarationContext referencedInterface = model.findReferencedInterface(typeContext);
			if (referencedClass != null) {
				return "Dto" + model.findReferencableBaseClassName(referencedClass) + "Reference";
			} else if (referencedInterface != null) {
				return "Dto" + model.findReferencableBaseInterfaceName(referencedInterface) + "Reference";
			} else {
				throw new TeamAppsGeneratorException("Cannot find referencable class or interface for type " + typeContext.getText() + ". Are you sure it is a referencable class or interface (has @Referencable properties)?");
			}
		} else if (model.isDtoType(typeContext)) {
			return "Dto" + typeContext.getText();
		} else if (isPrimitiveType(typeContext) && forceNonPrimitive) {
			return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.get(typeContext.getText());
		} else {
			return typeContext.getText();
		}
	}

	private String getJavaTypeWrapperString(TeamAppsDtoParser.TypeContext typeContext) {
		if (isList(typeContext)) {
			TeamAppsDtoParser.TypeContext firstTypeArgument = getFirstTypeArgument(typeContext);
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

	private boolean isUiClientObjectReference(TeamAppsDtoParser.TypeContext typeContext) {
		return typeContext.typeReference() != null && typeContext.typeReference().referenceTypeModifier() != null;
	}

	private boolean isUiClientObjectReferenceOrCollectionOfThese(TeamAppsDtoParser.TypeContext typeContext) {
		while (typeContext != null) {
			if (isUiClientObjectReference(typeContext)) {
				return true;
			}
			typeContext = getFirstTypeArgument(typeContext);
		}
		return false;
	}

	private String getTypeScriptTypeName(TeamAppsDtoParser.TypeContext typeContext) {
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

	private boolean isObject(TeamAppsDtoParser.TypeContext typeContext) {
		return typeContext.typeReference() != null && Set.of("Object", "java.lang.Object").contains(typeContext.typeReference().typeName().getText());
	}

	private boolean isList(TeamAppsDtoParser.TypeContext typeContext) {
		return typeContext.typeReference() != null && Set.of("List", "java.util.List").contains(typeContext.typeReference().typeName().getText());
	}

	private boolean isDictionary(TeamAppsDtoParser.TypeContext typeContext) {
		return typeContext.typeReference() != null
				&& ("Dictionary".equals(typeContext.typeReference().typeName().getText()));
	}

	private TeamAppsDtoParser.TypeContext getFirstTypeArgument(TeamAppsDtoParser.TypeContext typeContext) {
		if (typeContext.typeReference() != null && typeContext.typeReference().typeArguments() != null && !typeContext.typeReference().typeArguments().typeArgument().isEmpty()) {
			return typeContext.typeReference().typeArguments().typeArgument(0).type();
		} else {
			return null;
		}
	}
}
