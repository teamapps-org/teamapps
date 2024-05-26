package org.teamapps.dsl.generate.wrapper;

import com.google.common.collect.Lists;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.commons.lang3.StringUtils;
import org.teamapps.commons.util.ExceptionUtil;
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.ParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.teamapps.commons.util.StreamUtil.distinctByKey;
import static org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel.findAncestorOfType;

public interface ClassOrInterfaceWrapper<T extends ParserRuleContext> extends TypeWrapper<T> {

	List<InterfaceWrapper> getInterfaces();

	default List<InterfaceWrapper> getAllInterfaces(boolean includeSelf) {
		List<InterfaceWrapper> superInterfaces = new ArrayList<>(getInterfaces());
		if (includeSelf && this instanceof InterfaceWrapper self) {
			superInterfaces.add(self);
		}
		superInterfaces.addAll(superInterfaces.stream()
				.flatMap(si -> si.getAllInterfaces(false).stream())
				.toList());
		return superInterfaces.stream()
				.distinct()
				.toList();
	}

	List<ClassOrInterfaceWrapper<?>> getSuperTypes();

	default List<ClassOrInterfaceWrapper<?>> getNonExternalSuperTypes() {
		return getSuperTypes().stream()
				.filter(st -> !st.isExternal())
				.toList();
	}

	default List<ClassOrInterfaceWrapper<?>> getAllSuperTypes(boolean includeSelf) {
		List<ClassOrInterfaceWrapper<?>> superTypes = new ArrayList<>();
		if (includeSelf) {
			superTypes.add(this);
		}
		superTypes.addAll(getSuperTypes());
		superTypes.addAll(getSuperTypes().stream()
				.flatMap(t -> t.getAllSuperTypes(false).stream())
				.toList());
		return superTypes.stream()
				.distinct()
				.toList();
	}

	default List<ClassOrInterfaceWrapper<?>> getSuperTypesWithCommands() {
		return getSuperTypes().stream()
				.filter(t -> !t.getAllCommands().isEmpty())
				.collect(Collectors.toList());
	}

	default List<ClassOrInterfaceWrapper<?>> getSuperTypesWithEvents() {
		return getSuperTypes().stream()
				.filter(t -> !t.getAllEvents().isEmpty())
				.collect(Collectors.toList());
	}

	List<PropertyWrapper> getProperties();

	default List<PropertyWrapper> getAllProperties() {
		return Lists.reverse(getAllSuperTypes(true)).stream()
				.flatMap(t -> t.getProperties().stream())
				.filter(distinctByKey(property -> property.getName()))
				.toList();
	}

	default List<PropertyWrapper> getRequiredProperties() {
		return getProperties().stream()
				.filter(p -> p.isRequired())
				.toList();
	}

	default List<PropertyWrapper> getAllRequiredProperties() {
		return getAllProperties().stream()
				.filter(p -> p.isRequired())
				.toList();
	}

	default List<PropertyWrapper> getNonRequiredProperties() {
		return getProperties().stream()
				.filter(p -> !p.isRequired())
				.toList();
	}

	default List<PropertyWrapper> getAllNonRequiredProperties() {
		return getAllProperties().stream()
				.filter(p -> !p.isRequired())
				.toList();
	}

	default List<PropertyWrapper> getSimplePropertiesSortedByRelevance() {
		return getAllProperties().stream()
				.sorted((p1, p2) -> {
					Function<PropertyWrapper, Integer> getPriority = (p) -> {
						if (p.getName().equals("id")) {
							return 50;
						} else if (p.getName().equals("name")) {
							return 40;
						} else if (p.getName().contains("Id")) {
							return 30;
						} else if (p.getName().contains("Name")) {
							return 20;
						} else if (p.getType().findReferencedClassOrInterface().isEmpty()) {
							return 10;
						} else {
							return 0;
						}
					};
					return getPriority.apply(p2) - getPriority.apply(p1);
				})
				.toList();
	}

	List<CommandWrapper> getCommands();

	default List<CommandWrapper> getAllCommands() {
		return getAllSuperTypes(true).stream()
				.flatMap(t -> t.getCommands().stream())
				.filter(distinctByKey(CommandWrapper::getName))
				.toList();
	}

	default List<CommandWrapper> getNonStaticCommands() {
		return getCommands().stream()
				.filter(c -> !c.isStatic())
				.toList();
	}

	List<EventWrapper> getEvents();

	default List<EventWrapper> getAllEvents() {
		return getAllSuperTypes(true).stream()
				.flatMap(t -> t.getEvents().stream())
				.filter(distinctByKey(property -> property.getName()))
				.toList();
	}

	default List<EventWrapper> getStaticEvents() {
		return getEvents().stream()
				.filter(e -> e.isStatic())
				.toList();
	}

	default List<EventWrapper> getNonStaticEvents() {
		return getEvents().stream()
				.filter(e -> !e.isStatic())
				.toList();
	}

	default List<EventWrapper> getAllStaticEvents() {
		return getAllEvents().stream()
				.filter(e -> e.isStatic())
				.toList();
	}

	default List<EventWrapper> getAllNonStaticEvents() {
		return getAllEvents().stream()
				.filter(e -> !e.isStatic())
				.toList();
	}

	List<QueryWrapper> getQueries();

	default List<QueryWrapper> getAllQueries() {
		return getAllSuperTypes(true).stream()
				.flatMap(t -> t.getQueries().stream())
				.filter(distinctByKey(QueryWrapper::getName))
				.toList();
	}

	default List<QueryWrapper> getStaticQueries() {
		return getQueries().stream()
				.filter(e -> e.isStatic())
				.toList();
	}

	default List<QueryWrapper> getNonStaticQueries() {
		return getQueries().stream()
				.filter(e -> !e.isStatic())
				.toList();
	}

	default List<QueryWrapper> getAllStaticQueries() {
		return getAllQueries().stream()
				.filter(e -> e.isStatic())
				.toList();
	}

	default List<QueryWrapper> getAllNonStaticQueries() {
		return getAllQueries().stream()
				.filter(e -> !e.isStatic())
				.toList();
	}

	default boolean hasNonStaticEventsOrQueries() {
		return !getAllNonStaticEvents().isEmpty() || !getAllNonStaticQueries().isEmpty();
	}

	default boolean hasStaticEventsOrQueries() {
		return !getAllStaticEvents().isEmpty() || !getAllStaticQueries().isEmpty();
	}

	default List<ClassOrInterfaceWrapper<?>> getSuperTypesWithNonStaticQueriesOrEvents() {
		return getNonExternalSuperTypes().stream()
				.filter(s -> !s.getAllNonStaticEvents().isEmpty() || !s.getAllNonStaticQueries().isEmpty())
				.toList();
	}

	default List<ClassOrInterfaceWrapper<?>> getSuperTypesWithStaticQueriesOrEvents() {
		return getNonExternalSuperTypes().stream()
				.filter(s -> !s.getAllStaticEvents().isEmpty() || !s.getAllStaticQueries().isEmpty())
				.toList();
	}


	default List<TypeWrapper<?>> getReferencedTypes(boolean typescript) {
		return Stream.<Stream<TypeWrapper<?>>>of(
						getSuperTypes().stream()
								.map(Function.identity()), /* for the java compiler... */
						getProperties().stream()
								.map(p -> p.getType())
								.filter(t -> t.referencesDtoType())
								.map(t -> t.findReferencedDtoType()),
						getCommands().stream()
								.flatMap(cd -> cd.getParameters().stream())
								.map(p -> p.getType())
								.filter(t -> t.referencesDtoType())
								.map(t -> t.findReferencedDtoType()),
						getEvents().stream()
								.flatMap(cd -> cd.getParameters().stream())
								.map(p -> p.getType())
								.filter(t -> t.referencesDtoType())
								.map(t -> t.findReferencedDtoType()),
						getQueries().stream()
								.flatMap(cd -> cd.getParameters().stream())
								.map(p -> p.getType())
								.filter(t -> t.referencesDtoType())
								.map(t -> t.findReferencedDtoType())
				)
				.flatMap(Function.identity())
				.filter(typeWrapper -> !(typescript && typeWrapper.isExternal()))
				.filter(c -> c != this)
				.distinct()
				.toList();
	}


	default List<TypeWrapper<?>> getAllReferencedTypes(boolean typescript) {
		return getAllSuperTypes(true).stream()
				.flatMap(st -> st.getReferencedTypes(typescript).stream())
				.distinct()
				.toList();

	}

	boolean isAbstract();

	default String getTypeScriptIdentifier() {
		return getName();
	}

	default String getJsonIdentifier() {
		return getName();
	}

	default String getJavaClassName() {
		return StringUtils.capitalize(getName());
	}

	static List<TeamAppsDtoParser.CommandDeclarationContext> createImplicitMutationCommands(List<TeamAppsDtoParser.PropertyDeclarationContext> propertyDeclarations) {
		return propertyDeclarations.stream()
				.filter(property -> property.mutableModifier() != null)
				.map(property -> ExceptionUtil.runWithSoftenedExceptions(
						() -> {
							TeamAppsDtoParser.CommandDeclarationContext command =
									ParserFactory.createParser(new StringReader("command set" + StringUtils.capitalize(property.Identifier().toString())
																				+ "(" + property.type().getText() + " " + property.Identifier().toString() + ");")).commandDeclaration();
							command.setParent(property.getParent());
							return command;
						}
				))
				.collect(Collectors.toList());
	}

	default List<TeamAppsDtoParser.ImportDeclarationContext> getAllImports() {
		return findAncestorOfType(getParserRuleContext(), TeamAppsDtoParser.ClassCollectionContext.class, false).stream()
				.flatMap(ccc -> ccc.importDeclaration().stream())
				.collect(Collectors.toList());
	}

	default Collection<Import> getEffectiveImports(boolean typescript) {
		Imports imports = new Imports(this.getJsModuleName());

		imports.addImport("ClientObject", "projector-client-object-api", "../ClientObject", "org.teamapps.projector.clientobject");

		List<TypeWrapper<?>> referencedTypesToConsider = typescript ? getAllReferencedTypes(true) : this.getReferencedTypes(false);
		referencedTypesToConsider.stream()
				.forEach(t -> {
					imports.addImport(t.getName(), t.getJsModuleName(), t.getPackageName());

					if (!typescript && t instanceof ClassOrInterfaceWrapper<?> referencedClassOrInterface && !referencedClassOrInterface.isExternal()) {
						imports.addImport(t.getName() + "Wrapper", null, t.getPackageName());
					}
				});

		if (typescript) {
			imports.addImport("ServerObjectChannel", "projector-client-object-api", "../ClientObject", null);

			this.getSuperTypes().stream()
					.filter(c -> !c.getAllCommands().isEmpty())
					.forEach(c -> imports.addImport(c.getName() + "CommandHandler", c.getJsModuleName(), "./" + c.getName(), c.getPackageName()));
			this.getSuperTypes().stream()
					.filter(c -> !c.getAllEvents().isEmpty())
					.forEach(c -> imports.addImport(c.getName() + "EventSource", c.getJsModuleName(), "./" + c.getName(), c.getPackageName()));
		}

		return imports.getAll();
	}

	default Collection<Import> getEffectiveTypeScriptImports() {
		return getEffectiveImports(true);
	}

	default Collection<Import> getEffectiveJavaImports() {
		return getEffectiveImports(false);
	}

}
