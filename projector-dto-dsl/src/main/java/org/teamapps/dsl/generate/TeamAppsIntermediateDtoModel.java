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
package org.teamapps.dsl.generate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.TeamAppsDtoParser.*;
import org.teamapps.dsl.generate.adapter.Import;
import org.teamapps.dsl.generate.adapter.Imports;
import org.teamapps.dsl.generate.wrapper.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TeamAppsIntermediateDtoModel {

	private final List<ClassWrapper> classDeclarations = new ArrayList<>();
	private final List<InterfaceWrapper> interfaceDeclarations = new ArrayList<>();
	private final List<EnumWrapper> enumDeclarations = new ArrayList<>();
	private final List<EventDeclarationContext> eventDeclarations = new ArrayList<>();
	private final List<QueryDeclarationContext> queryDeclarations = new ArrayList<>();
	private final List<CommandDeclarationContext> commandDeclarations = new ArrayList<>();

	private final List<ClassWrapper> ownClassDeclarations;
	private final List<InterfaceWrapper> ownInterfaceDeclarations;
	private final List<EnumWrapper> ownEnumDeclarations;
	private final List<EventDeclarationContext> ownEventDeclarations;
	private final List<QueryDeclarationContext> ownQueryDeclarations;
	private final List<CommandDeclarationContext> ownCommandDeclarations;
	private final String debugName;
	private final TeamAppsIntermediateDtoModel[] importedModels;

	public TeamAppsIntermediateDtoModel(ClassCollectionContext classCollectionContext) {
		this(Collections.singletonList(classCollectionContext), "test");
	}

	public TeamAppsIntermediateDtoModel(List<ClassCollectionContext> classCollectionContexts, String debugName, TeamAppsIntermediateDtoModel... importedModels) {
		this.debugName = debugName;
		this.importedModels = importedModels;
		classCollectionContexts.forEach(classCollectionContext -> {
			List<TypeDeclarationContext> typeDeclarations = classCollectionContext.typeDeclaration();
			classDeclarations.addAll(extractClassDeclarations(typeDeclarations));
			interfaceDeclarations.addAll(extractInterfaceDeclarations(typeDeclarations));
			enumDeclarations.addAll(extractEnumDeclarations(typeDeclarations));
		});
		eventDeclarations.addAll(extractEventDeclarations());
		queryDeclarations.addAll(extractQueryDeclarations());
		commandDeclarations.addAll(extractCommandDeclarations());

		ownClassDeclarations = List.copyOf(classDeclarations);
		ownInterfaceDeclarations = List.copyOf(interfaceDeclarations);
		ownEnumDeclarations = List.copyOf(enumDeclarations);
		ownEventDeclarations = List.copyOf(eventDeclarations);
		ownQueryDeclarations = List.copyOf(queryDeclarations);
		ownCommandDeclarations = List.copyOf(commandDeclarations);

		for (TeamAppsIntermediateDtoModel delegate : importedModels) {
			classDeclarations.addAll(delegate.classDeclarations);
			interfaceDeclarations.addAll(delegate.interfaceDeclarations);
			enumDeclarations.addAll(delegate.enumDeclarations);
			eventDeclarations.addAll(delegate.eventDeclarations);
			queryDeclarations.addAll(delegate.queryDeclarations);
			commandDeclarations.addAll(delegate.commandDeclarations);
		}
	}

	private List<ClassWrapper> extractClassDeclarations(List<TypeDeclarationContext> types) {
		return types.stream()
				.filter(typeContext -> typeContext.classDeclaration() != null)
				.map(typeContext -> new ClassWrapper(typeContext.classDeclaration(), this))
				.collect(Collectors.toList());
	}

	private List<InterfaceWrapper> extractInterfaceDeclarations(List<TypeDeclarationContext> types) {
		return types.stream()
				.filter(typeContext -> typeContext.interfaceDeclaration() != null)
				.map(typeContext -> new InterfaceWrapper(typeContext.interfaceDeclaration(), this))
				.collect(Collectors.toList());
	}

	private List<EnumWrapper> extractEnumDeclarations(List<TypeDeclarationContext> types) {
		return types.stream()
				.filter(typeContext -> typeContext.enumDeclaration() != null)
				.map(typeContext -> new EnumWrapper(typeContext.enumDeclaration(), this))
				.collect(Collectors.toList());
	}

	private List<EventDeclarationContext> extractEventDeclarations() {
		return Stream.concat(
				classDeclarations.stream().flatMap(c -> c.getEvents().stream()),
				interfaceDeclarations.stream().flatMap(i -> i.getEvents().stream())
		).collect(Collectors.toList());
	}

	private List<QueryDeclarationContext> extractQueryDeclarations() {
		return Stream.concat(
				classDeclarations.stream().flatMap(c -> c.getQueries().stream()),
				interfaceDeclarations.stream().flatMap(i -> i.getQueries().stream())
		).collect(Collectors.toList());
	}

	private List<CommandDeclarationContext> extractCommandDeclarations() {
		return Stream.concat(
				classDeclarations.stream().flatMap(c -> c.getCommands().stream()),
				interfaceDeclarations.stream().flatMap(i -> i.getCommands().stream())
		).collect(Collectors.toList());
	}

	public List<ClassWrapper> getOwnClassDeclarations() {
		return ownClassDeclarations;
	}

	public List<InterfaceWrapper> getOwnInterfaceDeclarations() {
		return ownInterfaceDeclarations;
	}

	public List<EnumWrapper> getOwnEnumDeclarations() {
		return ownEnumDeclarations;
	}

	public List<EventDeclarationContext> getOwnEventDeclarations() {
		return ownEventDeclarations;
	}

	public List<QueryDeclarationContext> getOwnQueryDeclarations() {
		return ownQueryDeclarations;
	}

	public List<CommandDeclarationContext> getOwnCommandDeclarations() {
		return ownCommandDeclarations;
	}

	public List<ClassWrapper> getClassDeclarations() {
		return classDeclarations;
	}

	public List<InterfaceWrapper> getInterfaceDeclarations() {
		return interfaceDeclarations;
	}

	public List<EnumWrapper> getEnumDeclarations() {
		return enumDeclarations;
	}

	public List<TypeWrapper<?>> getAllTypeDeclarations() {
		ArrayList<TypeWrapper<?>> l = new ArrayList<>();
		l.addAll(getClassDeclarations());
		l.addAll(getInterfaceDeclarations());
		l.addAll(getEnumDeclarations());
		return l;
	}

	public List<EventDeclarationContext> getEventDeclarations() {
		return eventDeclarations;
	}

	public List<QueryDeclarationContext> getQueryDeclarations() {
		return queryDeclarations;
	}

	public List<CommandDeclarationContext> getCommandDeclarations() {
		return commandDeclarations;
	}

	public Optional<ClassWrapper> findReferencedClass(TypeContext typeContext) {
		TypeReferenceContext typeReferenceContext = typeContext.typeReference();
		if (typeReferenceContext == null) {
			return Optional.empty();
		}
		String qualifiedTypeName = getQualifiedTypeName(typeContext);
		return findClassByQualifiedName(qualifiedTypeName)
				.or(() -> {
					if (typeReferenceContext.typeArguments() != null && !typeReferenceContext.typeArguments().typeArgument().isEmpty()) {
						return findReferencedClass(typeReferenceContext.typeArguments().typeArgument(0).type());
					} else {
						return Optional.empty();
					}
				});
	}

	public Optional<TypeWrapper<?>> findTypeByQualifiedName(String qualifiedName) {
		return Optional.<TypeWrapper<?>>empty()
				.or(() -> findClassByQualifiedName(qualifiedName))
				.or(() -> findInterfaceByQualifiedName(qualifiedName))
				.or(() -> findEnumByQualifiedName(qualifiedName));
	}

	public Optional<ClassWrapper> findClassByQualifiedName(String qualifiedName) {
		return classDeclarations.stream()
				.filter(classDeclaration -> classDeclaration.getQualifiedName().equals(qualifiedName))
				.findFirst();
	}

	public Optional<InterfaceWrapper> findInterfaceByQualifiedName(String qualifiedName) {
		return interfaceDeclarations.stream()
				.filter(interfaceDeclaration -> interfaceDeclaration.getQualifiedName().equals(qualifiedName))
				.findFirst();
	}

	public Optional<EnumWrapper> findEnumByQualifiedName(String qualifiedTypeName) {
		return enumDeclarations.stream()
				.filter(interfaceDeclaration -> interfaceDeclaration.getQualifiedName().equals(qualifiedTypeName))
				.findFirst();
	}

	private static String getQualifiedTypeName(TypeContext typeContext) {
		if (typeContext.primitiveType() != null) {
			return typeContext.typeReference().getText();
		}

		String typeName = typeContext.typeReference().typeName().getText();
		return getQualifiedTypeName(typeName, typeContext);
	}

	public static String getQualifiedTypeName(String potentiallyImportedTypeName, ParserRuleContext context) {
		return findImport(context, potentiallyImportedTypeName)
				.map(i -> i.qualifiedTypeName().getText())
				.or(() -> findAncestorOfType(context, ClassCollectionContext.class, false)
						.map(ccc -> ccc.packageDeclaration().packageName().getText() + "." + potentiallyImportedTypeName))
				.orElse(potentiallyImportedTypeName);
	}

	private static Optional<ImportDeclarationContext> findImport(ParserRuleContext parserRuleContext, String simpleTypeName) {
		return findAncestorOfType(parserRuleContext, ClassCollectionContext.class, false).stream()
				.flatMap(ccc -> ccc.importDeclaration().stream())
				.filter(i -> i.qualifiedTypeName().Identifier().getText().equals(simpleTypeName))
				.findFirst();
	}

	public List<ImportDeclarationContext> getAllImports(ParserRuleContext context) {
		return findAncestorOfType(context, ClassCollectionContext.class, false).stream()
				.flatMap(ccc -> ccc.importDeclaration().stream())
				.collect(Collectors.toList());
	}

	public Optional<InterfaceWrapper> findReferencedInterface(TypeContext typeContext) {
		TypeReferenceContext typeReferenceContext = typeContext.typeReference();
		if (typeReferenceContext == null) {
			return Optional.empty();
		}
		return findInterfaceByQualifiedName(getQualifiedTypeName(typeContext))
				.or(() -> {
					if (typeReferenceContext.typeArguments() != null && !typeReferenceContext.typeArguments().typeArgument().isEmpty()) {
						return findReferencedInterface(typeReferenceContext.typeArguments().typeArgument(0).type());
					} else {
						return Optional.empty();
					}
				});
	}

	public Optional<EnumWrapper> findReferencedEnum(TypeContext typeContext) {
		TypeReferenceContext typeRef = typeContext.typeReference();
		if (typeRef == null) {
			return Optional.empty();
		}

		return findEnumByQualifiedName(getQualifiedTypeName(typeContext))
				.or(() -> {
					if (typeRef.typeArguments() != null && !typeRef.typeArguments().typeArgument().isEmpty()) {
						return findReferencedEnum(typeRef.typeArguments().typeArgument(0).type());
					} else {
						return Optional.empty();
					}
				});
	}

	public static <T extends RuleContext> Optional<T> findAncestorOfType(RuleContext ruleContext, Class<? extends T> ancestorType, boolean includeSelf) {
		if (includeSelf && ancestorType.isInstance(ruleContext)) {
			return Optional.of((T) ruleContext);
		}
		while (ruleContext != null) {
			ruleContext = ruleContext.getParent();
			if (ancestorType.isInstance(ruleContext)) {
				return Optional.of((T) ruleContext);
			}
		}
		return Optional.empty();
	}

	public InterfaceWrapper findInterface(TypeNameContext typeNameContext) {
		String qualifiedTypeName = getQualifiedTypeName(typeNameContext.Identifier().getText(), typeNameContext);
		return findInterfaceByQualifiedName(qualifiedTypeName)
				.orElseThrow(() -> new TeamAppsGeneratorException("Cannot find interface " + qualifiedTypeName));
	}

	public ClassOrInterfaceWrapper<?> getDeclaringClassOrInterface(ParserRuleContext element) {
		return Optional.<ClassOrInterfaceWrapper<?>>empty()
				.or(() -> findAncestorOfType(element, ClassDeclarationContext.class, true)
						.map(c -> new ClassWrapper(c, this)))
				.or(() -> findAncestorOfType(element, InterfaceDeclarationContext.class, true)
						.map(i -> new InterfaceWrapper(i, this)))
				.orElse(null);
	}

	public boolean shouldReferenceDtoType(TypeContext type) {
		List<String> knownJavaTypes = List.of(
				"Object", "java.lang.Object",
				"String", "java.lang.String",
				"boolean", "char", "byte", "short", "int", "long", "float", "double",
				"Boolean", "Character", "Byte", "Short", "Integer", "Long", "Float", "Double"
		);
		boolean isKnownJavaType = knownJavaTypes.contains(type.getText());
		return !isKnownJavaType && (!isCollection(type) || shouldReferenceDtoType(getCollectionType(type)));
	}

	public boolean isCollection(TypeContext typeContext) {
		return isList(typeContext) || isDictionary(typeContext);
	}

	public static boolean isList(TypeContext typeContext) {
		return typeContext.typeReference() != null && Set.of("List", "java.util.List").contains(typeContext.typeReference().typeName().getText());
	}

	public static boolean isDictionary(TypeContext typeContext) {
		return typeContext.typeReference() != null
				&& ("Dictionary".equals(typeContext.typeReference().typeName().getText()));
	}

	private TypeContext getCollectionType(TypeContext typeContext) {
		if (typeContext.typeReference() != null && typeContext.typeReference().typeArguments() != null && !typeContext.typeReference().typeArguments().typeArgument().isEmpty()) {
			return typeContext.typeReference().typeArguments().typeArgument(0).type();
		} else {
			throw new IllegalArgumentException(typeContext.typeReference().typeName().getText() + " must have a generic parameter!");
		}
	}

	public static boolean isObject(TypeContext typeContext) {
		return typeContext.typeReference() != null && Set.of("Object", "java.lang.Object").contains(typeContext.typeReference().typeName().getText());
	}

	public TypeWrapper<?> findReferencedDtoType(TypeContext type) {
		if (isCollection(type)) {
			return findReferencedDtoType(getCollectionType(type));
		}
		return Optional.<TypeWrapper<?>>empty()
				.or(() -> findReferencedClass(type))
				.or(() -> findReferencedInterface(type))
				.or(() -> findReferencedEnum(type))
				.orElseThrow(() -> new TeamAppsGeneratorException("Cannot find type with name '" + type.typeReference().typeName().getText() + "'. Did you forget to import it?"));
	}

	public Optional<ClassOrInterfaceWrapper<?>> findReferencedClassOrInterface(TypeContext type) {
		return Optional.<ClassOrInterfaceWrapper<?>>empty()
				.or(() -> findReferencedClass(type))
				.or(() -> findReferencedInterface(type));
	}

	public boolean isDtoClassOrInterface(TeamAppsDtoParser.TypeContext typeContext) {
		return Optional.empty()
				.or(() -> findReferencedClass(typeContext))
				.or(() -> findReferencedInterface(typeContext))
				.isPresent();
	}

	public boolean isDtoType(TeamAppsDtoParser.TypeContext typeContext) {
		return Optional.empty()
				.or(() -> findReferencedClass(typeContext))
				.or(() -> findReferencedInterface(typeContext))
				.or(() -> findReferencedEnum(typeContext))
				.isPresent();
	}

	public Collection<Import> getEffectiveImports(ClassOrInterfaceWrapper<?> classOrInterface, boolean typescript) {
		Imports imports = new Imports();

		boolean isClientApiPackage = classOrInterface.getJsPackageName().equals("projector-client-object-api");

		imports.addImport("ClientObject", isClientApiPackage? "../ClientObject" : "projector-client-object-api", "org.teamapps.projector.clientobject");

		classOrInterface.getReferencedTypes().stream()
				.forEach(t -> {
					imports.addDtoImport(t.getName(), classOrInterface.getJsPackageName().equals(t.getJsPackageName()) ? "./Dto" + t.getName() : t.getJsPackageName(), t.getPackageName());
					if (!typescript && t instanceof ClassOrInterfaceWrapper<?> referencedClassOrInterface) {
						imports.addDtoImport(referencedClassOrInterface.getName() + "Wrapper", null, t.getPackageName());
					}
				});

		if (typescript) {
			classOrInterface.getSuperTypes().stream()
					.filter(c -> !c.getAllCommands().isEmpty())
					.forEach(c -> imports.addDtoImport(c.getName() + "CommandHandler", classOrInterface.getJsPackageName().equals(c.getJsPackageName()) ? "./Dto" + c.getName() : c.getJsPackageName(), c.getPackageName()));
			classOrInterface.getSuperTypes().stream()
					.filter(c -> !c.getAllEvents().isEmpty())
					.forEach(c -> imports.addDtoImport(c.getName() + "EventSource", classOrInterface.getJsPackageName().equals(c.getJsPackageName()) ? "./Dto" + c.getName() : c.getJsPackageName(), c.getPackageName()));
		}

		return imports.getAll();
	}

}
