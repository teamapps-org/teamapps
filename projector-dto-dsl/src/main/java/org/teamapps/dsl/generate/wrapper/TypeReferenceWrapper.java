package org.teamapps.dsl.generate.wrapper;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.TeamAppsGeneratorException;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.*;

import static org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel.getQualifiedTypeName;

public class TypeReferenceWrapper {

	static final BiMap<String, String> PRIMITIVE_TYPE_TO_WRAPPER_TYPE = HashBiMap.create();
	private static final Map<String, String> PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE = new HashMap<>();
	private static final Map<String, String> PRIMITIVE_TYPE_TO_DEFAULT_VALUE = new HashMap<>();

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
		PRIMITIVE_TYPE_TO_DEFAULT_VALUE.put("long", "0L");
		PRIMITIVE_TYPE_TO_DEFAULT_VALUE.put("float", "0f");
		PRIMITIVE_TYPE_TO_DEFAULT_VALUE.put("double", "0d");
	}

	private final TeamAppsDtoParser.TypeContext context;
	private final TeamAppsIntermediateDtoModel model;
	private final List<TypeReferenceWrapper> typeArguments;

	public TypeReferenceWrapper(TeamAppsDtoParser.TypeContext context, TeamAppsIntermediateDtoModel model) {
		this.context = context;
		this.model = model;

		this.typeArguments = context.typeReference() != null && context.typeReference().typeArguments() != null
				? context.typeReference().typeArguments().typeArgument().stream()
				.map(typeArgumentContext -> new TypeReferenceWrapper(typeArgumentContext.type(), model))
				.toList()
				: List.of();
	}

	public String getName() {
		return context.typeReference() != null ? context.typeReference().getText() : context.primitiveType().getText();
	}

	public String getText() {
		return context.getText();
	}

	public boolean isBoolean() {
		return context.getText().equals("boolean");
	}

	public boolean isList() {
		return context.typeReference() != null
			   && "List".equals(context.typeReference().typeName().getText());
	}

	public boolean isDictionary() {
		return context.typeReference() != null
			   && ("Dictionary".equals(context.typeReference().typeName().getText()));
	}

	public boolean isCollection() {
		return isList() || isDictionary();
	}

	public Optional<TypeReferenceWrapper> getCollectionType() {
		if (context.typeReference() != null && context.typeReference().typeArguments() != null && !context.typeReference().typeArguments().typeArgument().isEmpty()) {
			return Optional.of(new TypeReferenceWrapper(context.typeReference().typeArguments().typeArgument(0).type(), model));
		} else {
			return Optional.empty();
		}
	}

	public Optional<ClassWrapper> findReferencedClass() {
		TeamAppsDtoParser.TypeReferenceContext typeReferenceContext = context.typeReference();
		if (typeReferenceContext == null) {
			return Optional.empty();
		}
		return model.findClassByQualifiedName(getQualifiedTypeName(context))
				.or(() -> {
					if (!typeArguments.isEmpty()) {
						return typeArguments.getFirst().findReferencedClass();
					} else {
						return Optional.empty();
					}
				});
	}

	public Optional<InterfaceWrapper> findReferencedInterface() {
		TeamAppsDtoParser.TypeReferenceContext typeReferenceContext = context.typeReference();
		if (typeReferenceContext == null) {
			return Optional.empty();
		}
		return model.findInterfaceByQualifiedName(getQualifiedTypeName(context))
				.or(() -> {
					if (!typeArguments.isEmpty()) {
						return typeArguments.getFirst().findReferencedInterface();
					} else {
						return Optional.empty();
					}
				});
	}

	public Optional<ClassOrInterfaceWrapper<?>> findReferencedClassOrInterface() {
		return Optional.<ClassOrInterfaceWrapper<?>>empty()
				.or(() -> findReferencedClass())
				.or(() -> findReferencedInterface());
	}

	public Optional<EnumWrapper> findReferencedEnum() {
		TeamAppsDtoParser.TypeReferenceContext typeRef = context.typeReference();
		if (typeRef == null) {
			return Optional.empty();
		}

		return model.findEnumByQualifiedName(getQualifiedTypeName(context))
				.or(() -> {
					if (!typeArguments.isEmpty()) {
						return typeArguments.getFirst().findReferencedEnum();
					} else {
						return Optional.empty();
					}
				});
	}

	public TypeWrapper<?> findReferencedDtoType() {
		if (isCollection()) {
			return getCollectionType().orElseThrow(() -> new IllegalArgumentException(getText() + " must have a generic parameter!")).findReferencedDtoType();
		}
		return Optional.<TypeWrapper<?>>empty()
				.or(this::findReferencedClass)
				.or(this::findReferencedInterface)
				.or(this::findReferencedEnum)
				.orElseThrow(() -> new TeamAppsGeneratorException("Cannot find type with name '" + getText() + "'. Did you forget to import it?"));
	}

	public boolean isDtoClassOrInterface() {
		return Optional.empty()
				.or(this::findReferencedClass)
				.or(this::findReferencedInterface)
				.isPresent();
	}

	public boolean isDtoType() {
		return Optional.empty()
				.or(this::findReferencedClass)
				.or(this::findReferencedInterface)
				.or(this::findReferencedEnum)
				.isPresent();
	}

	public boolean referencesDtoType() {
		List<String> knownJavaTypes = List.of(
				"Object", "java.lang.Object",
				"String", "java.lang.String",
				"boolean", "char", "byte", "short", "int", "long", "float", "double",
				"Boolean", "Character", "Byte", "Short", "Integer", "Long", "Float", "Double"
		);
		boolean isKnownJavaType = knownJavaTypes.contains(getText());
		return !isKnownJavaType && (!isCollection() || getCollectionType().orElseThrow(() -> new IllegalArgumentException(getText() + " must have a generic parameter!")).referencesDtoType());
	}

	public boolean isObject() {
		return context.typeReference() != null && Set.of("Object", "java.lang.Object").contains(context.typeReference().typeName().getText());
	}

	public boolean isObjectOrObjectCollection() {
		if (isObject()) {
			return true;
		} else {
			return getCollectionType().map(TypeReferenceWrapper::isObject).orElse(false);
		}
	}

	public boolean isClientObjectReference() {
		return context.typeReference() != null && context.typeReference().referenceTypeModifier() != null;
	}

	public boolean isUiClientObjectReferenceOrClientObjectCollection() {
		if (isClientObjectReference()) {
			return true;
		}
		return getCollectionType().map(t -> t.isUiClientObjectReferenceOrClientObjectCollection()).orElse(false);
	}

	public String getTypeScriptTypeName() {
		if (isObject()) {
			return "any";
		} else if ("String".equals(getText())) {
			return "string";
		} else if (isList()) {
			return getCollectionType().orElseThrow().getTypeScriptTypeName() + "[]";
		} else if (isDictionary()) {
			return "{[name: string]: " + getCollectionType().orElseThrow().getTypeScriptTypeName() + "}";
		} else if (isClientObjectReference()) {
			return "unknown";
		} else if (isDtoType()) {
			return getText();
		} else if (PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.containsKey(getText())) {
			return PRIMITIVE_TYPE_TO_TYPESCRIPT_TYPE.get(getText());
		} else {
			return getText();
		}
	}

	public String getJavaTypeString() {
		return getJavaTypeString(false);
	}

	public String getJavaNonPrimitiveTypeString() {
		return getJavaTypeString(true);
	}

	public String getJavaTypeString(boolean forceNonPrimitive) {
		if (isList()) {
			TypeReferenceWrapper firstTypeArgument = getCollectionType().orElseThrow(() -> new IllegalArgumentException("Collection type argument not provided!"));
			if (firstTypeArgument.isObject()) {
				return "List<?>";
			} else {
				return "List<" + firstTypeArgument.getJavaTypeString() + ">";
			}
		} else if (isDictionary()) {
			TypeReferenceWrapper firstTypeArgument = getCollectionType().orElseThrow(() -> new IllegalArgumentException("Collection type argument not provided!"));
			if (firstTypeArgument.isObject()) {
				return "Map<String, ?>";
			} else {
				return "Map<String, " + firstTypeArgument.getJavaTypeString() + ">";
			}
		} else if (isClientObjectReference()) {
			return "ClientObject";
		} else if (isDtoType()) {
			return context.getText();
		} else if (isPrimitiveType() && forceNonPrimitive) {
			return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.get(getText());
		} else {
			return context.getText();
		}
	}

	public boolean isPrimitiveType() {
		return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.containsKey(context.getText());
	}

	public boolean isPrimitiveWrapperType() {
		return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.containsValue(context.getText());
	}

	public boolean isPrimitiveOrWrapperType() {
		return isPrimitiveType() || isPrimitiveWrapperType();
	}

	public String getPrimitiveTypeName() {
		return PRIMITIVE_TYPE_TO_WRAPPER_TYPE.inverse().getOrDefault(context.getText(), context.getText());
	}

	public String getJavaJsonWrapperTypeString() {
		if (isList()) {
			return "List<" + getCollectionType().map(TypeReferenceWrapper::getJavaJsonWrapperTypeString).orElse("??") + ">";
		} else if (isDictionary()) {
			return "Map<String, " + getCollectionType().map(TypeReferenceWrapper::getJavaJsonWrapperTypeString).orElse("??") + ">";
		} else if (isClientObjectReference()) {
			return "ClientObject";
		} else if (isDtoClassOrInterface()) {
			return getJavaTypeString() + "Wrapper";
		} else if (isObject()) {
			return "JsonWrapper";
		} else {
			return getJavaTypeString();
		}
	}

	public boolean isString() {
		return "String".equals(context.getText());
	}

	public boolean isEnum() {
		return findReferencedEnum().isPresent();
	}

	public boolean isReferenceList() {
		return isList() && getCollectionType().map(t -> isDtoClassOrInterface()).orElse(false);
	}

	public String getDefaultValue() {
		return PRIMITIVE_TYPE_TO_DEFAULT_VALUE.getOrDefault(context.getText(), "null");
	}

	public List<TypeReferenceWrapper> getTypeArguments() {
		return typeArguments;
	}
}
