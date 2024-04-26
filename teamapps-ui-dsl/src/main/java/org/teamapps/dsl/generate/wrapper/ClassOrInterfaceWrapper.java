package org.teamapps.dsl.generate.wrapper;

import com.google.common.collect.Lists;
import org.antlr.v4.runtime.ParserRuleContext;
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.TeamAppsDtoParser.CommandDeclarationContext;
import org.teamapps.dsl.TeamAppsDtoParser.EventDeclarationContext;
import org.teamapps.dsl.TeamAppsDtoParser.PropertyDeclarationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.teamapps.common.util.StreamUtil.distinctByKey;

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

	List<PropertyDeclarationContext> getProperties();

	default List<PropertyDeclarationContext> getAllProperties() {
		return Lists.reverse(getAllSuperTypes(true)).stream()
				.flatMap(t -> t.getProperties().stream())
				.filter(distinctByKey(property -> property.Identifier().getText()))
				.toList();
	}

	default List<PropertyDeclarationContext> getRequiredProperties() {
		return getProperties().stream()
				.filter(p -> p.requiredModifier() != null)
				.toList();
	}

	default List<PropertyDeclarationContext> getAllRequiredProperties() {
		return getAllProperties().stream()
				.filter(p -> p.requiredModifier() != null)
				.toList();
	}

	default List<PropertyDeclarationContext> getNonRequiredProperties() {
		return getProperties().stream()
				.filter(p -> p.requiredModifier() == null)
				.toList();
	}

	default List<PropertyDeclarationContext> getAllNonRequiredProperties() {
		return getAllProperties().stream()
				.filter(p -> p.requiredModifier() == null)
				.toList();
	}

	default List<PropertyDeclarationContext> getSimplePropertiesSortedByRelevance() {
			return getAllProperties().stream()
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
							} else if (getModel().findReferencedClass(p.type()) == null) {
								return 10;
							} else {
								return 0;
							}
						};
						return getPriority.apply(p2) - getPriority.apply(p1);
					})
					.toList();
	}

	List<CommandDeclarationContext> getCommands();

	default List<CommandDeclarationContext> getAllCommands() {
		return getAllSuperTypes(true).stream()
				.flatMap(t -> t.getCommands().stream())
				.filter(distinctByKey(property -> property.Identifier().getText()))
				.toList();
	}

	default List<CommandDeclarationContext> getNonStaticCommands() {
		return getCommands().stream()
				.filter(c -> c.staticModifier() == null)
				.toList();
	}

	List<EventDeclarationContext> getEvents();

	default List<EventDeclarationContext> getAllEvents() {
		return getAllSuperTypes(true).stream()
				.flatMap(t -> t.getEvents().stream())
				.filter(distinctByKey(property -> property.Identifier().getText()))
				.toList();
	}

	default List<EventDeclarationContext> getNonStaticEvents() {
		return getEvents().stream()
				.filter(e -> e.staticModifier() == null)
				.toList();
	}

	default List<EventDeclarationContext> getAllStaticEvents() {
		return getAllEvents().stream()
				.filter(e -> e.staticModifier() == null)
				.toList();
	}

	default List<EventDeclarationContext> getAllNonStaticEvents() {
		return getAllEvents().stream()
				.filter(e -> e.staticModifier() == null)
				.toList();
	}

	List<TeamAppsDtoParser.QueryDeclarationContext> getQueries();

	default List<TeamAppsDtoParser.QueryDeclarationContext> getAllQueries() {
		return getAllSuperTypes(true).stream()
				.flatMap(t -> t.getQueries().stream())
				.filter(distinctByKey(property -> property.Identifier().getText()))
				.toList();
	}

	default List<TypeWrapper<?>> getReferencedTypes() {
		return Stream.<Stream<TypeWrapper<?>>>of(
						getSuperTypes().stream().map(t -> (TypeWrapper<?>) t),
						getProperties().stream()
								.map(p -> p.type())
								.filter(t -> getModel().shouldReferenceDtoType(t))
								.map(t -> getModel().findReferencedDtoType(t)),
						getCommands().stream()
								.flatMap(cd -> cd.formalParameter().stream())
								.map(p -> p.type())
								.filter(t -> getModel().shouldReferenceDtoType(t))
								.map(t -> getModel().findReferencedDtoType(t)),
						getEvents().stream()
								.flatMap(cd -> cd.formalParameter().stream())
								.map(p -> p.type())
								.filter(t -> getModel().shouldReferenceDtoType(t))
								.map(t -> getModel().findReferencedDtoType(t)),
						getQueries().stream()
								.flatMap(cd -> cd.formalParameter().stream())
								.map(p -> p.type())
								.filter(t -> getModel().shouldReferenceDtoType(t))
								.map(t -> getModel().findReferencedDtoType(t))
				)
				.flatMap(Function.identity())
				.filter(c -> c != this)
				.distinct()
				.toList();
	}

	boolean isAbstract();
}
