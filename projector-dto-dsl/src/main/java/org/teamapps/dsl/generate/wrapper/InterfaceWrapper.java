package org.teamapps.dsl.generate.wrapper;

import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.TeamAppsDtoParser.InterfaceDeclarationContext;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.ArrayList;
import java.util.List;

import static org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel.findImport;
import static org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel.getQualifiedTypeName;

public class InterfaceWrapper implements ClassOrInterfaceWrapper<InterfaceDeclarationContext> {

	private final InterfaceDeclarationContext context;
	private final TeamAppsIntermediateDtoModel model;

	private List<PropertyWrapper> properties;
	private List<CommandWrapper> commands;
	private List<EventWrapper> events;
	private List<QueryWrapper> queries;

	public InterfaceWrapper(InterfaceDeclarationContext context, TeamAppsIntermediateDtoModel model) {
		this.context = context;
		this.model = model;

		this.properties = context.propertyDeclaration().stream().map(p -> new PropertyWrapper(p, model)).toList();
		this.commands = getAllCommandDeclarationContexts().stream().map(p -> new CommandWrapper(p, model)).toList();
		this.events = context.eventDeclaration().stream().map(p -> new EventWrapper(p, model)).toList();
		this.queries = context.queryDeclaration().stream().map(p -> new QueryWrapper(p, model)).toList();
	}

	private List<TeamAppsDtoParser.CommandDeclarationContext> getAllCommandDeclarationContexts() {
		ArrayList<TeamAppsDtoParser.CommandDeclarationContext> allCommandDeclarations = new ArrayList<>(context.commandDeclaration());
		allCommandDeclarations.addAll(ClassOrInterfaceWrapper.createImplicitMutationCommands(context.propertyDeclaration()));
		return allCommandDeclarations;
	}

	@Override
	public InterfaceDeclarationContext getParserRuleContext() {
		return context;
	}

	@Override
	public String getName() {
		return context.Identifier().getText();
	}

	@Override
	public void checkSuperTypeResolvability() {
		if (context.superInterfaceDecl() != null) {
			for (TeamAppsDtoParser.TypeNameContext typeNameContext : context.superInterfaceDecl().classList().typeName()) {
				String interfaceName = typeNameContext.getText();
				Boolean isExternal = findImport(context, interfaceName).map(i -> i.externalInterfaceTypeModifier() != null).orElse(false);
				if (isExternal) {
					continue;
				}
				String qualifiedName = getQualifiedTypeName(interfaceName, context.superInterfaceDecl());
				model.findClassByQualifiedName(qualifiedName).orElseThrow(() -> model.createUnresolvedTypeReferenceException(interfaceName, context));
			}
		}
	}

	@Override
	public List<InterfaceWrapper> getInterfaces() {
		if (context.superInterfaceDecl() != null) {
			return context.superInterfaceDecl().classList().typeName().stream()
					.map(model::findInterface)
					.toList();
		} else {
			return List.of();
		}
	}

	@Override
	public List<ClassOrInterfaceWrapper<?>> getSuperTypes() {
		return (List) getInterfaces();
	}

	@Override
	public List<PropertyWrapper> getProperties() {
		return properties;
	}

	@Override
	public List<CommandWrapper> getCommands() {
		return commands;
	}

	@Override
	public List<EventWrapper> getEvents() {
		return events;
	}

	@Override
	public List<QueryWrapper> getQueries() {
		return queries;
	}

	@Override
	public boolean isAbstract() {
		return true;
	}

	@Override
	public boolean isExternal() {
		return false;
	}

	@Override
	public String toString() {
		return "InterfaceWrapper: " + getName();
	}

}
