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
package org.teamapps.projector.dsl.generate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.teamapps.dsl.TeamAppsDtoParser.*;
import org.teamapps.projector.dsl.generate.wrapper.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IntermediateDtoModel {

	private final List<ClassWrapper> classDeclarations = new ArrayList<>();
	private final List<InterfaceWrapper> interfaceDeclarations = new ArrayList<>();
	private final List<EnumWrapper> enumDeclarations = new ArrayList<>();
	private final List<EventWrapper> eventDeclarations = new ArrayList<>();
	private final List<QueryWrapper> queryDeclarations = new ArrayList<>();
	private final List<CommandWrapper> commandDeclarations = new ArrayList<>();

	private final List<ClassWrapper> ownClassDeclarations;
	private final List<InterfaceWrapper> ownInterfaceDeclarations;
	private final List<EnumWrapper> ownEnumDeclarations;
	private final List<EventWrapper> ownEventDeclarations;
	private final List<QueryWrapper> ownQueryDeclarations;
	private final List<CommandWrapper> ownCommandDeclarations;
	private final String debugName;
	private final IntermediateDtoModel[] importedModels;
	private final List<ClassCollectionContext> compilationUnits;

	public IntermediateDtoModel(ClassCollectionContext classCollectionContext) {
		this(Collections.singletonList(classCollectionContext), "test");
	}

	public IntermediateDtoModel(List<ClassCollectionContext> classCollectionContexts, String debugName, IntermediateDtoModel... importedModels) {
		this.debugName = debugName;
		this.importedModels = importedModels;
		this.compilationUnits = classCollectionContexts;
		classCollectionContexts.forEach(classCollectionContext -> {
			List<TypeDeclarationContext> typeDeclarations = classCollectionContext.typeDeclaration();
			classDeclarations.addAll(extractClassDeclarations(typeDeclarations));
			interfaceDeclarations.addAll(extractInterfaceDeclarations(typeDeclarations));
			interfaceDeclarations.addAll(extractExternalInterfaces(classCollectionContext));
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

		for (IntermediateDtoModel delegate : importedModels) {
			classDeclarations.addAll(delegate.classDeclarations);
			interfaceDeclarations.addAll(delegate.interfaceDeclarations);
			enumDeclarations.addAll(delegate.enumDeclarations);
			eventDeclarations.addAll(delegate.eventDeclarations);
			queryDeclarations.addAll(delegate.queryDeclarations);
			commandDeclarations.addAll(delegate.commandDeclarations);
		}
	}

	public List<ClassCollectionContext> getCompilationUnits() {
		return compilationUnits;
	}

	private List<ExternalInterfaceWrapper> extractExternalInterfaces(ClassCollectionContext classCollectionContext) {
		return classCollectionContext.importDeclaration().stream()
				.filter(importDeclarationContext -> importDeclarationContext.externalInterfaceTypeModifier() != null)
				.map(importDeclarationContext -> new ExternalInterfaceWrapper(importDeclarationContext, this))
				.toList();
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

	private List<EventWrapper> extractEventDeclarations() {
		return Stream.concat(
				classDeclarations.stream().flatMap(c -> c.getEvents().stream()),
				interfaceDeclarations.stream().flatMap(i -> i.getEvents().stream())
		).collect(Collectors.toList());
	}

	private List<QueryWrapper> extractQueryDeclarations() {
		return Stream.concat(
				classDeclarations.stream().flatMap(c -> c.getQueries().stream()),
				interfaceDeclarations.stream().flatMap(i -> i.getQueries().stream())
		).collect(Collectors.toList());
	}

	private List<CommandWrapper> extractCommandDeclarations() {
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

	public List<ClassOrInterfaceWrapper<?>> getOwnClassAndInterfaceDeclarations() {
		ArrayList<ClassOrInterfaceWrapper<?>> classOrInterfaces = new ArrayList<>();
		classOrInterfaces.addAll(getOwnClassDeclarations());
		classOrInterfaces.addAll(getOwnInterfaceDeclarations());
		return classOrInterfaces;
	}

	public List<EnumWrapper> getOwnEnumDeclarations() {
		return ownEnumDeclarations;
	}

	public List<EventWrapper> getOwnEventDeclarations() {
		return ownEventDeclarations;
	}

	public List<QueryWrapper> getOwnQueryDeclarations() {
		return ownQueryDeclarations;
	}

	public List<CommandWrapper> getOwnCommandDeclarations() {
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

	public List<EventWrapper> getEventDeclarations() {
		return eventDeclarations;
	}

	public List<QueryWrapper> getQueryDeclarations() {
		return queryDeclarations;
	}

	public List<CommandWrapper> getCommandDeclarations() {
		return commandDeclarations;
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

	public static String getQualifiedTypeName(TypeContext typeContext) {
		if (typeContext.primitiveType() != null) {
			return typeContext.typeReference().getText();
		}

		String typeName = typeContext.typeReference().typeName().getText();
		return getQualifiedTypeName(typeName, typeContext);
	}

	public static String getQualifiedTypeName(String unqualifiedTypeName, ParserRuleContext context) {
		return findImport(context, unqualifiedTypeName)
				.map(i -> i.qualifiedTypeName().getText())
				.or(() -> findAncestorOfType(context, ClassCollectionContext.class, false)
						.map(ccc -> ccc.packageDeclaration().packageName().getText() + "." + unqualifiedTypeName))
				.orElse(unqualifiedTypeName);
	}

	public static Optional<ImportDeclarationContext> findImport(ParserRuleContext parserRuleContext, String simpleTypeName) {
		return findAncestorOfType(parserRuleContext, ClassCollectionContext.class, false).stream()
				.flatMap(ccc -> ccc.importDeclaration().stream())
				.filter(i -> i.qualifiedTypeName().Identifier().getText().equals(simpleTypeName))
				.findFirst();
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
				.orElseThrow(() -> new DtoGeneratorException("Cannot find interface " + qualifiedTypeName));
	}

	public ClassOrInterfaceWrapper<?> getDeclaringClassOrInterface(ParserRuleContext element) {
		return Optional.<ClassOrInterfaceWrapper<?>>empty()
				.or(() -> findAncestorOfType(element, ClassDeclarationContext.class, true)
						.map(c -> new ClassWrapper(c, this)))
				.or(() -> findAncestorOfType(element, InterfaceDeclarationContext.class, true)
						.map(i -> new InterfaceWrapper(i, this)))
				.orElse(null);
	}

	public ModelValidationException createUnresolvedTypeReferenceException(String typeName, ParserRuleContext context) {
		String errorMessage = "Cannot resolve " + typeName + " in " + getDeclaringClassOrInterface(context).getName() + ".";
		Optional<String> importSuggestion = createImportSuggestion(typeName);
		if (importSuggestion.isPresent()) {
			errorMessage += " Did you forget to import it? Suggestion: import " + importSuggestion.get() + ";";
		}
		return new ModelValidationException(errorMessage);
	}

	public Optional<String> createImportSuggestion(String typeName) {
		Optional<String> suggestion = getAllTypeDeclarations().stream()
				.filter(t -> t.getName().equals(typeName))
				.findFirst()
				.or(() -> {
					if (typeName.startsWith("Dto")) {
						return getAllTypeDeclarations().stream()
								.filter(t -> t.getName().equals(typeName.substring(3)))
								.findFirst();
					} else {
						return getAllTypeDeclarations().stream()
								.filter(t -> t.getName().equals("Dto" + typeName))
								.findFirst();
					}
				})
				.map(t -> t.getPackageName() + "." + t.getName());

		return suggestion;
	}
}
