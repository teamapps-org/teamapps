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

import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.TeamAppsDtoParser.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TeamAppsIntermediateDtoModel {

	public static final Set<String> IMPLICITELY_REFERENCEABLE_CLASSES = Sets.newHashSet("UiEvent", "UiCommand", "UiQuery");

	private final List<ClassDeclarationContext> classDeclarations = new ArrayList<>();
	private final List<InterfaceDeclarationContext> interfaceDeclarations = new ArrayList<>();
	private final List<EnumDeclarationContext> enumDeclarations = new ArrayList<>();
	private final List<EventDeclarationContext> eventDeclarations = new ArrayList<>();
	private final List<QueryDeclarationContext> queryDeclarations = new ArrayList<>();
	private final List<CommandDeclarationContext> commandDeclarations = new ArrayList<>();

	private final List<ClassDeclarationContext> ownClassDeclarations;
	private final List<InterfaceDeclarationContext> ownInterfaceDeclarations;
	private final List<EnumDeclarationContext> ownEnumDeclarations;
	private final List<EventDeclarationContext> ownEventDeclarations;
	private final List<QueryDeclarationContext> ownQueryDeclarations;
	private final List<CommandDeclarationContext> ownCommandDeclarations;

	public TeamAppsIntermediateDtoModel(ClassCollectionContext classCollectionContext) {
		this(Collections.singletonList(classCollectionContext));
	}

	public TeamAppsIntermediateDtoModel(List<ClassCollectionContext> classCollectionContexts, TeamAppsIntermediateDtoModel... delegates) {
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

		for (TeamAppsIntermediateDtoModel delegate : delegates) {
			classDeclarations.addAll(delegate.classDeclarations);
			interfaceDeclarations.addAll(delegate.interfaceDeclarations);
			enumDeclarations.addAll(delegate.enumDeclarations);
			eventDeclarations.addAll(delegate.eventDeclarations);
			queryDeclarations.addAll(delegate.queryDeclarations);
			commandDeclarations.addAll(delegate.commandDeclarations);
		}
	}

	private List<ClassDeclarationContext> extractClassDeclarations(List<TypeDeclarationContext> types) {
		return types.stream()
				.filter(typeContext -> typeContext.classDeclaration() != null)
				.map(typeContext -> typeContext.classDeclaration())
				.collect(Collectors.toList());
	}

	private List<InterfaceDeclarationContext> extractInterfaceDeclarations(List<TypeDeclarationContext> types) {
		return types.stream()
				.filter(typeContext -> typeContext.interfaceDeclaration() != null)
				.map(typeContext -> typeContext.interfaceDeclaration())
				.collect(Collectors.toList());
	}

	private List<EnumDeclarationContext> extractEnumDeclarations(List<TypeDeclarationContext> types) {
		return types.stream()
				.filter(typeContext -> typeContext.enumDeclaration() != null)
				.map(typeContext -> typeContext.enumDeclaration())
				.collect(Collectors.toList());
	}

	private List<EventDeclarationContext> extractEventDeclarations() {
		return Stream.concat(
				classDeclarations.stream().flatMap(c -> c.eventDeclaration().stream()),
				interfaceDeclarations.stream().flatMap(i -> i.eventDeclaration().stream())
		).collect(Collectors.toList());
	}

	private List<QueryDeclarationContext> extractQueryDeclarations() {
		return Stream.concat(
				classDeclarations.stream().flatMap(c -> c.queryDeclaration().stream()),
				interfaceDeclarations.stream().flatMap(i -> i.queryDeclaration().stream())
		).collect(Collectors.toList());
	}

	private List<CommandDeclarationContext> extractCommandDeclarations() {
		return Stream.concat(
				classDeclarations.stream().flatMap(c -> c.commandDeclaration().stream()),
				interfaceDeclarations.stream().flatMap(i -> i.commandDeclaration().stream())
		).collect(Collectors.toList());
	}

	public List<ClassDeclarationContext> getOwnClassDeclarations() {
		return ownClassDeclarations;
	}

	public List<InterfaceDeclarationContext> getOwnInterfaceDeclarations() {
		return ownInterfaceDeclarations;
	}

	public List<EnumDeclarationContext> getOwnEnumDeclarations() {
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

	public List<ClassDeclarationContext> getClassDeclarations() {
		return classDeclarations;
	}

	public List<InterfaceDeclarationContext> getInterfaceDeclarations() {
		return interfaceDeclarations;
	}

	public List<EnumDeclarationContext> getEnumDeclarations() {
		return enumDeclarations;
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

	public ClassDeclarationContext findReferencedClass(TypeContext typeContext) {
		TypeReferenceContext typeReferenceContext = typeContext.typeReference();
		if (typeReferenceContext == null) {
			return null;
		}

		String fqTypename = getQualifiedTypeName(typeContext, typeReferenceContext);

		return classDeclarations.stream()
				.filter(classDeclaration -> {
					String fqClassName = getQualifiedClassName(classDeclaration);
					return fqClassName.equals(fqTypename);
				})
				.findFirst()
				.orElseGet(() -> {
					if (typeReferenceContext.typeArguments() != null && !typeReferenceContext.typeArguments().typeArgument().isEmpty()) {
						return findReferencedClass(typeReferenceContext.typeArguments().typeArgument(0).type());
					} else {
						return null;
					}
				});
	}

	private static String getQualifiedClassName(ClassDeclarationContext classDeclaration) {
		String packageName = getPackageName(classDeclaration);
		return packageName + "." + classDeclaration.Identifier().getText();
	}

	public static String getPackageName(ParserRuleContext parserRuleContext) {
		return findAncestorOfType(parserRuleContext, ClassCollectionContext.class).map(ccc -> ccc.packageDeclaration().packageName().getText()).orElse(null);
	}

	private static String getQualifiedInterfaceName(InterfaceDeclarationContext interfaceDeclaration) {
		String packageName = getPackageName(interfaceDeclaration);
		return packageName + "." + interfaceDeclaration.Identifier().getText();
	}

	private static String getQualifiedTypeName(TypeContext typeContext, TypeReferenceContext typeReferenceContext) {
		if (typeContext.primitiveType() != null) {
			return typeReferenceContext.getText();
		}

		String simpleTypeName = typeContext.typeReference().typeName().getText();

		return findImport(typeContext, simpleTypeName)
				.map(i -> i.packageName().getText() + "." + simpleTypeName)
				.or(() -> findAncestorOfType(typeContext, ClassCollectionContext.class)
						.map(ccc -> ccc.packageDeclaration().packageName().getText() + "." + typeContext.typeReference().typeName().getText()))
				.orElse(typeReferenceContext.getText());
	}

	private static Optional<ImportDeclarationContext> findImport(ParserRuleContext parserRuleContext, String simpleTypeName) {
		return findAncestorOfType(parserRuleContext, ClassCollectionContext.class).stream()
				.flatMap(ccc -> ccc.importDeclaration().stream())
				.filter(i -> i.Identifier().getText().equals(simpleTypeName))
				.findFirst();
	}

	public List<ImportDeclarationContext> getAllImports(ParserRuleContext context) {
		return findAncestorOfType(context, ClassCollectionContext.class).stream()
				.flatMap(ccc -> ccc.importDeclaration().stream())
				.collect(Collectors.toList());
	}

	public InterfaceDeclarationContext findReferencedInterface(TypeContext typeContext) {
		TypeReferenceContext typeReferenceContext = typeContext.typeReference();
		if (typeReferenceContext == null) {
			return null;
		}

		String fqTypename = getQualifiedTypeName(typeContext, typeReferenceContext);

		return interfaceDeclarations.stream()
				.filter(interfaceDecl -> {
					String fqClassName = getQualifiedInterfaceName(interfaceDecl);
					return fqClassName.equals(fqTypename);
				})
				.findFirst()
				.orElseGet(() -> {
					if (typeReferenceContext.typeArguments() != null && !typeReferenceContext.typeArguments().typeArgument().isEmpty()) {
						return findReferencedInterface(typeReferenceContext.typeArguments().typeArgument(0).type());
					} else {
						return null;
					}
				});
	}

	public ClassDeclarationContext findClassByName(String className, boolean throwExceptionIfNotFound) {
		return classDeclarations.stream()
				.filter(otherClassDeclarationContext -> {
					return otherClassDeclarationContext.Identifier().getText().equals(className)
							|| getQualifiedClassName(otherClassDeclarationContext).equals(className);
				})
				.findFirst().orElseGet(() -> {
					if (throwExceptionIfNotFound) {
						throw new IllegalArgumentException("Could not find interface " + className);
					} else {
						return null;
					}
				});
	}

	public EnumDeclarationContext findReferencedEnum(TypeContext typeContext) {
		TypeReferenceContext typeRef = typeContext.typeReference();
		if (typeRef == null) {
			return null;
		}

		return enumDeclarations.stream()
				.filter(e -> e.Identifier().getText().equals(typeRef.getText()))
				.findFirst()
				.orElseGet(() -> {
					if (typeRef.typeArguments() != null && !typeRef.typeArguments().typeArgument().isEmpty()) {
						return findReferencedEnum(typeRef.typeArguments().typeArgument(0).type());
					} else {
						return null;
					}
				});
	}

	public static <T extends RuleContext> Optional<T> findAncestorOfType(RuleContext ruleContext, Class<? extends T> ancestorType) {
		while (ruleContext != null) {
			ruleContext = ruleContext.getParent();
			if (ancestorType.isInstance(ruleContext)) {
				return Optional.of((T) ruleContext);
			}
		}
		return Optional.empty();
	}

	public List<ClassDeclarationContext> findAllSuperClasses(ClassDeclarationContext clazzContext) {
		List<ClassDeclarationContext> superClasses = new ArrayList<>();
		clazzContext = findSuperClass(clazzContext);
		while (clazzContext != null) {
			superClasses.add(clazzContext);
			clazzContext = findSuperClass(clazzContext);
		}
		return superClasses;
	}

	public List<ClassDeclarationContext> findSelfAndAllSuperClasses(ClassDeclarationContext clazzContext) {
		List<ClassDeclarationContext> allSuperClasses = findAllSuperClasses(clazzContext);
		allSuperClasses.add(0, clazzContext);
		return allSuperClasses;
	}

	public List<ClassDeclarationContext> findAllSubClasses(ClassDeclarationContext clazzContext) {
		return classDeclarations.stream()
				.filter(otherClass -> findAllSuperClasses(otherClass).contains(clazzContext))
				.collect(Collectors.toList());
	}

	public List<ClassDeclarationContext> findAllSubClasses(InterfaceDeclarationContext interfaceContext) {
		return classDeclarations.stream()
				.filter(classContext -> findAllImplementedInterfaces(classContext).contains(interfaceContext))
				.collect(Collectors.toList());
	}

	public List<InterfaceDeclarationContext> findAllSubInterfaces(InterfaceDeclarationContext interfaceContext) {
		return interfaceDeclarations.stream()
				.filter(interf -> findSuperInterfaces(interf).contains(interfaceContext))
				.collect(Collectors.toList());
	}

	public List<PropertyDeclarationContext> findAllProperties(ClassDeclarationContext clazzContext) {
		List<ClassDeclarationContext> selfAndAllSuperClasses = findSelfAndAllSuperClasses(clazzContext);
		Collections.reverse(selfAndAllSuperClasses);
		return Stream.concat(
						selfAndAllSuperClasses.stream()
								.flatMap(classContext -> classContext.propertyDeclaration().stream())
								.filter(distinctByKey(property -> property.Identifier().getText())),
						findAllImplementedInterfaces(clazzContext).stream()
								.flatMap(interfaceContext -> interfaceContext.propertyDeclaration().stream())
								.filter(distinctByKey(property -> property.Identifier().getText()))
				)
				.distinct() // interfaces may be implemented multiple times in class hierarchy
				.collect(Collectors.toList());
	}

	public List<PropertyDeclarationContext> findAllProperties(InterfaceDeclarationContext interfaceContext) {
		return findAllSuperInterfacesAndSelf(interfaceContext).stream()
				.flatMap(interf -> interf.propertyDeclaration().stream())
				.filter(distinctByKey(property -> property.Identifier().getText()))
				.collect(Collectors.toList());
	}

	public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	public List<PropertyDeclarationContext> filterRequiredProperties(List<PropertyDeclarationContext> propertyDeclarations, boolean required) {
		return propertyDeclarations.stream()
				.filter(p -> required ^ p.requiredModifier() == null)
				.collect(Collectors.toList());
	}

	public ClassDeclarationContext findSuperClass(ClassDeclarationContext clazzContext) {
		if (clazzContext.superClassDecl() != null) {
			String superClassName = clazzContext.superClassDecl().typeName().getText();
			ClassDeclarationContext superClass = findClassByName(superClassName, true);
			if (superClass == null) {
				throw new IllegalArgumentException("Cannot find super class of " + clazzContext.Identifier().getText() + " with name: " + superClassName);
			}
			return superClass;
		} else {
			return null;
		}
	}

	public List<InterfaceDeclarationContext> findAllSuperInterfaces(InterfaceDeclarationContext interfaceContext) {
		if (interfaceContext.superInterfaceDecl() != null) {
			List<InterfaceDeclarationContext> superInterfaces = findSuperInterfaces(interfaceContext);
			superInterfaces.addAll(superInterfaces.stream().flatMap(si -> findAllSuperInterfaces(si).stream()).collect(Collectors.toList()));
			return superInterfaces.stream().distinct().collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	public List<InterfaceDeclarationContext> findSuperInterfaces(InterfaceDeclarationContext interfaceContext) {
		if (interfaceContext.superInterfaceDecl() != null) {
			return interfaceContext.superInterfaceDecl().classList().typeName().stream()
					.map(typeName -> typeName.getText())
					.map(name -> findInterfaceByName(name, false))
					.collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	public List<InterfaceDeclarationContext> findAllImplementedInterfaces(ClassDeclarationContext classContext) {
		return findSelfAndAllSuperClasses(classContext).stream()
				.flatMap(clazz -> clazz.implementsDecl() != null ? clazz.implementsDecl().classList().typeName().stream()
						.map(typeName -> findInterfaceByName(typeName.getText(), true))
						.flatMap(interfaceContext -> findAllSuperInterfacesAndSelf(interfaceContext).stream()) : Stream.empty())
				.collect(Collectors.toList());
	}

	public List<InterfaceDeclarationContext> getDirectlyImplementedInterfaces(ClassDeclarationContext classContext) {
		if (classContext.implementsDecl() != null) {
			return classContext.implementsDecl().classList().typeName().stream()
					.map(typeName -> findInterfaceByName(typeName.getText(), true))
					.collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	public InterfaceDeclarationContext findInterfaceByName(String name, boolean throwExceptionIfNotFound) {
		return interfaceDeclarations.stream()
				.filter(interfaceDeclaration -> interfaceDeclaration.Identifier().getText().equals(name))
				.findAny().orElseGet(() -> {
					if (throwExceptionIfNotFound) {
						throw new IllegalArgumentException("Could not find interface " + name);
					} else {
						return null;
					}
				});
	}

	public List<ClassDeclarationContext> findAllImplementingClasses(InterfaceDeclarationContext interfaceContext) {
		return classDeclarations.stream()
				.filter(clazz -> findAllImplementedInterfaces(clazz).contains(interfaceContext)).collect(Collectors.toList());
	}

	public List<InterfaceDeclarationContext> findAllSuperInterfacesAndSelf(InterfaceDeclarationContext interfaceContext) {
		List<InterfaceDeclarationContext> superInterfacesAndSelf = findAllSuperInterfaces(interfaceContext);
		superInterfacesAndSelf.add(0, interfaceContext);
		return superInterfacesAndSelf;
	}

	public List<PropertyDeclarationContext> getSimplePropertiesSortedByRelevance(List<PropertyDeclarationContext> properties) {
		return properties.stream()
				.sorted((p1, p2) -> {
					Function<PropertyDeclarationContext, Integer> getPriority = (p) -> {
						if (p.Identifier().getText().equals("id")) {
							return 50;
						} else if (p.Identifier().getText().equals("name")) {
							return 40;
						} else if (p.Identifier().getText().contains("Id")) {
							return 30;
						} else if (p.Identifier().getText().contains("Name")) {
							return 20;
						} else if (findReferencedClass(p.type()) == null) {
							return 10;
						} else {
							return 0;
						}
					};
					return getPriority.apply(p2) - getPriority.apply(p1);
				})
				.collect(Collectors.toList());
	}

	public List<PropertyDeclarationContext> findPropertiesNotImplementedBySuperClasses(ClassDeclarationContext classContext) {
		List<PropertyDeclarationContext> properties = findAllProperties(classContext);
		ClassDeclarationContext superClass = findSuperClass(classContext);
		if (superClass != null) {
			properties.removeAll(findAllProperties(superClass));
		}
		return properties;
	}

	public List<ParserRuleContext> findSuperClassAndDirectlyImplementedInterfaces(ClassDeclarationContext classContext) {
		ClassDeclarationContext superClass = findSuperClass(classContext);
		List<InterfaceDeclarationContext> directlyImplementedInterfaces = getDirectlyImplementedInterfaces(classContext);

		List<ParserRuleContext> result = new ArrayList<>();
		if (superClass != null) {
			result.add(superClass);
		}
		result.addAll(directlyImplementedInterfaces);
		return result;
	}

	public List<CommandDeclarationContext> getAllCommands(ClassDeclarationContext classContext) {
		List<CommandDeclarationContext> commands = Stream.concat(
						findSelfAndAllSuperClasses(classContext).stream()
								.flatMap(clazz -> clazz.commandDeclaration().stream()),
						findAllImplementedInterfaces(classContext).stream()
								.flatMap(interf -> interf.commandDeclaration().stream())

				)
				.filter(distinctByKey(command -> command.Identifier().getText()))
				.collect(Collectors.toList());
		Collections.reverse(commands);
		return commands;
	}

	public List<CommandDeclarationContext> getAllCommands(InterfaceDeclarationContext interfaceContext) {
		return findAllSuperInterfacesAndSelf(interfaceContext).stream()
				.flatMap(interf -> interf.commandDeclaration().stream())
				.filter(distinctByKey(command -> command.Identifier().getText()))
				.collect(Collectors.toList());
	}

	public List<EventDeclarationContext> getAllEvents(ClassDeclarationContext classContext) {
		List<EventDeclarationContext> events = Stream.concat(
						findSelfAndAllSuperClasses(classContext).stream()
								.flatMap(clazz -> clazz.eventDeclaration().stream()),
						findAllImplementedInterfaces(classContext).stream()
								.flatMap(interf -> interf.eventDeclaration().stream())

				)
				.filter(distinctByKey(event -> event.Identifier().getText()))
				.collect(Collectors.toList());
		Collections.reverse(events);
		return events;
	}

	public List<EventDeclarationContext> getAllEvents(InterfaceDeclarationContext interfaceContext) {
		return findAllSuperInterfacesAndSelf(interfaceContext).stream()
				.flatMap(interf -> interf.eventDeclaration().stream())
				.filter(distinctByKey(event -> event.Identifier().getText()))
				.collect(Collectors.toList());
	}

	public List<QueryDeclarationContext> getAllQueries(ClassDeclarationContext classContext) {
		List<QueryDeclarationContext> queries = Stream.concat(
						findSelfAndAllSuperClasses(classContext).stream()
								.flatMap(clazz -> clazz.queryDeclaration().stream()),
						findAllImplementedInterfaces(classContext).stream()
								.flatMap(interf -> interf.queryDeclaration().stream())

				)
				.filter(distinctByKey(query -> query.Identifier().getText()))
				.collect(Collectors.toList());
		Collections.reverse(queries);
		return queries;
	}

	public List<QueryDeclarationContext> getAllQueries(InterfaceDeclarationContext interfaceContext) {
		return findAllSuperInterfacesAndSelf(interfaceContext).stream()
				.flatMap(interf -> interf.queryDeclaration().stream())
				.filter(distinctByKey(query -> query.Identifier().getText()))
				.collect(Collectors.toList());
	}

	public List<ParserRuleContext> superClassAndDirectlyImplementedInterfacesWithCommands(ClassDeclarationContext classContext) {
		return Stream.concat(
				Optional.ofNullable(findSuperClass(classContext))
						.filter(clazz -> !getAllCommands(clazz).isEmpty())
						.map(Stream::of).orElseGet(Stream::empty),
				getDirectlyImplementedInterfaces(classContext).stream()
						.filter(interf -> !getAllCommands(interf).isEmpty())
		).collect(Collectors.toList());
	}

	public List<ParserRuleContext> superClassAndDirectlyImplementedInterfacesWithEvents(ClassDeclarationContext classContext) {
		return Stream.concat(
				Optional.ofNullable(findSuperClass(classContext))
						.filter(clazz -> !getAllEvents(clazz).isEmpty())
						.map(Stream::of).orElseGet(Stream::empty),
				getDirectlyImplementedInterfaces(classContext).stream()
						.filter(interf -> !getAllEvents(interf).isEmpty())
		).collect(Collectors.toList());
	}

	public List<ParserRuleContext> getSuperInterfacesWithCommands(InterfaceDeclarationContext interfaceContext) {
		return findSuperInterfaces(interfaceContext).stream()
				.filter(itf -> !getAllCommands(itf).isEmpty())
				.collect(Collectors.toList());
	}

	public List<ParserRuleContext> getSuperInterfacesWithEvents(InterfaceDeclarationContext interfaceContext) {
		return findSuperInterfaces(interfaceContext).stream()
				.filter(itf -> !getAllEvents(itf).isEmpty())
				.collect(Collectors.toList());
	}

	private ParserRuleContext findClassOrInterfaceByName(String referencedClassName) {
		ClassDeclarationContext clazz = findClassByName(referencedClassName, false);
		if (clazz != null) {
			return clazz;
		} else {
			return findInterfaceByName(referencedClassName, false);
		}
	}

	public static ParserRuleContext getDeclaringClassOrInterface(ParserRuleContext element) {
		if (element instanceof ClassDeclarationContext || element instanceof InterfaceDeclarationContext) {
			return element;
		}
		return TeamAppsIntermediateDtoModel.<ParserRuleContext>findAncestorOfType(element, ClassDeclarationContext.class)
				.or(() -> findAncestorOfType(element, InterfaceDeclarationContext.class))
				.orElse(null);
	}

	public static String getDeclaringClassOrInterfaceName(ParserRuleContext element) {
		ParserRuleContext declaringClassOrInterface = getDeclaringClassOrInterface(element);
		if (declaringClassOrInterface instanceof ClassDeclarationContext) {
			return ((ClassDeclarationContext) declaringClassOrInterface).Identifier().getText();
		} else if (declaringClassOrInterface instanceof InterfaceDeclarationContext) {
			return ((InterfaceDeclarationContext) declaringClassOrInterface).Identifier().getText();
		} else {
			return null;
		}
	}

	public List<ParserRuleContext> getAllClassesAndInterfacesWithEvents() {
		return Stream.concat(
				classDeclarations.stream()
						.filter(classDeclarationContext -> !getAllEvents(classDeclarationContext).isEmpty()),
				interfaceDeclarations.stream()
						.filter(interfaceDeclarationContext -> !getAllEvents(interfaceDeclarationContext).isEmpty())
		).collect(Collectors.toList());
	}

	public List<ParserRuleContext> getAllClassesAndInterfacesWithQueries() {
		return Stream.concat(
				classDeclarations.stream()
						.filter(classDeclarationContext -> !getAllQueries(classDeclarationContext).isEmpty()),
				interfaceDeclarations.stream()
						.filter(interfaceDeclarationContext -> !getAllQueries(interfaceDeclarationContext).isEmpty())
		).collect(Collectors.toList());
	}

	private ParserRuleContext findReferencedClassOrInterface(TypeContext type) {
		ClassDeclarationContext referencedClass = findReferencedClass(type);
		return referencedClass != null ? referencedClass : findReferencedInterface(type);
	}

	public List<ParserRuleContext> findAllReferencedClassesAndInterfaces(ClassDeclarationContext classContext) {
		return Stream.of(
						findSuperClassAndDirectlyImplementedInterfaces(classContext).stream(),
						findSuperClassAndDirectlyImplementedInterfaces(classContext).stream()
								.flatMap(c -> c instanceof ClassDeclarationContext
										? findAllReferencedClassesAndInterfaces(((ClassDeclarationContext) c)).stream()
										: findAllReferencedClassesAndInterfaces(((InterfaceDeclarationContext) c)).stream()),
						classContext.propertyDeclaration().stream()
								.map(p -> findReferencedClassOrInterface(p.type())),
						classContext.commandDeclaration().stream()
								.flatMap(cd -> cd.formalParameterWithDefault().stream())
								.map(fp -> findReferencedClassOrInterface(fp.type())),
						classContext.eventDeclaration().stream()
								.flatMap(cd -> cd.formalParameterWithDefault().stream())
								.map(fp -> findReferencedClassOrInterface(fp.type()))
				)
				.flatMap(Function.identity())
				.filter(Objects::nonNull)
				.filter(c -> c != classContext)
				.distinct()
				.collect(Collectors.toList());
	}

	public List<EnumDeclarationContext> findAllReferencedEnums(ClassDeclarationContext classContext) {
		return Stream.of(
						classContext.propertyDeclaration().stream()
								.map(p -> findReferencedEnum(p.type())),
						classContext.commandDeclaration().stream()
								.flatMap(cd -> cd.formalParameterWithDefault().stream())
								.map(fp -> findReferencedEnum(fp.type())),
						classContext.eventDeclaration().stream()
								.flatMap(cd -> cd.formalParameterWithDefault().stream())
								.map(fp -> findReferencedEnum(fp.type()))
				).flatMap(Function.identity())
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
	}

	public List<ParserRuleContext> findAllReferencedClassesAndInterfaces(InterfaceDeclarationContext interfaceContext) {
		return Stream.of(
						findAllSuperInterfaces(interfaceContext).stream()
								.map(interfDecl -> (ParserRuleContext) interfDecl),
						interfaceContext.propertyDeclaration().stream()
								.map(p -> findReferencedClassOrInterface(p.type())),
						interfaceContext.commandDeclaration().stream()
								.flatMap(cd -> cd.formalParameterWithDefault().stream())
								.map(fp -> findReferencedClassOrInterface(fp.type())),
						interfaceContext.eventDeclaration().stream()
								.flatMap(cd -> cd.formalParameterWithDefault().stream())
								.map(fp -> findReferencedClassOrInterface(fp.type()))
				)
				.flatMap(Function.identity())
				.filter(Objects::nonNull)
				.filter(c -> c != interfaceContext)
				.distinct()
				.collect(Collectors.toList());
	}

	public List<EnumDeclarationContext> findAllReferencedEnums(InterfaceDeclarationContext interfaceContext) {
		return Stream.of(
						interfaceContext.propertyDeclaration().stream()
								.map(p -> findReferencedEnum(p.type())),
						interfaceContext.commandDeclaration().stream()
								.flatMap(cd -> cd.formalParameterWithDefault().stream())
								.map(fp -> findReferencedEnum(fp.type())),
						interfaceContext.eventDeclaration().stream()
								.flatMap(cd -> cd.formalParameterWithDefault().stream())
								.map(fp -> findReferencedEnum(fp.type()))
				).flatMap(Function.identity())
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
	}

	public List<ParserRuleContext> findAllReferencedClassesAndInterfaces(EventDeclarationContext eventContext) {
		return eventContext.formalParameterWithDefault().stream()
				.map(p -> findReferencedClassOrInterface(p.type()))
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
	}

	public List<EnumDeclarationContext> findAllReferencedEnums(EventDeclarationContext eventDeclarationContext) {
		return eventDeclarationContext.formalParameterWithDefault().stream()
				.map(p -> findReferencedEnum(p.type()))
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
	}

	public List<ParserRuleContext> findAllClassesInterfacesAndEnumsReferencedByEvents() {
		return eventDeclarations.stream().flatMap(ed -> Stream.concat(
						findAllReferencedClassesAndInterfaces(ed).stream(),
						findAllReferencedEnums(ed).stream()
				))
				.distinct()
				.collect(Collectors.toList());
	}

	public List<PropertyDeclarationContext> findAllNotYetImplementedProperties(ClassDeclarationContext classContext) {
		return Streams.concat(
						classContext.propertyDeclaration().stream(),
						findAllImplementedInterfaces(classContext).stream()
								.flatMap(interf -> interf.propertyDeclaration().stream())
				).filter(distinctByKey(PropertyDeclarationContext::Identifier))
				.collect(Collectors.toList());
	}

	public static boolean isReferenceType(TypeContext type) {
		return type.typeReference() != null && type.typeReference().referenceTypeModifier() != null;
	}

	public boolean isReferenceableClass(ClassDeclarationContext clazz) {
		return findSelfNearestAncestorClassWithReferenceableAttribute(clazz) != null;
	}

	public boolean isReferenceableBaseClass(ClassDeclarationContext clazz) {
		return findSelfNearestAncestorClassWithReferenceableAttribute(clazz) == clazz;
	}

	public ClassDeclarationContext findSelfNearestAncestorClassWithReferenceableAttribute(ClassDeclarationContext clazz) {
		return findSelfAndAllSuperClasses(clazz).stream()
				.filter(c -> c.propertyDeclaration().stream().anyMatch(p -> p.referenceableAnnotation() != null))
				.findFirst().orElse(null);
	}

	public Object getReferenceableProperties(ClassDeclarationContext classContext) {
		return findAllProperties(classContext).stream()
				.filter(p -> p.referenceableAnnotation() != null)
				.collect(Collectors.toList());
	}

	public boolean isReferenceToJsonAware(TeamAppsDtoParser.TypeContext typeContext) {
		return findReferencedClass(typeContext) != null
				|| findReferencedInterface(typeContext) != null
				|| IMPLICITELY_REFERENCEABLE_CLASSES.contains(typeContext.getText());
	}
}
