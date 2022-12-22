package org.teamapps.dsl.generate.wrapper;

import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.TeamAppsDtoParser.InterfaceDeclarationContext;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.List;

public class InterfaceWrapper implements ClassOrInterfaceWrapper<InterfaceDeclarationContext> {

	private final InterfaceDeclarationContext context;
	private final TeamAppsIntermediateDtoModel model;

	public InterfaceWrapper(InterfaceDeclarationContext context, TeamAppsIntermediateDtoModel model) {
		this.context = context;
		this.model = model;
	}

	@Override
	public InterfaceDeclarationContext getParserRuleContext() {
		return context;
	}

	@Override
	public TeamAppsIntermediateDtoModel getModel() {
		return model;
	}

	@Override
	public String getName() {
		return context.Identifier().getText();
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
	public List<TeamAppsDtoParser.PropertyDeclarationContext> getProperties() {
		return context.propertyDeclaration();
	}

	@Override
	public List<TeamAppsDtoParser.CommandDeclarationContext> getCommands() {
		return context.commandDeclaration();
	}

	@Override
	public List<TeamAppsDtoParser.EventDeclarationContext> getEvents() {
		return context.eventDeclaration();
	}

	@Override
	public List<TeamAppsDtoParser.QueryDeclarationContext> getQueries() {
		return context.queryDeclaration();
	}

	@Override
	public boolean isManagedBaseType() {
		return context.managedModifier() != null;
	}

	@Override
	public boolean isAbstract() {
		return true;
	}

	@Override
	public String toString() {
		return "InterfaceWrapper: " + getName();
	}

}
