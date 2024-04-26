package org.teamapps.dsl.generate.wrapper;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.common.util.ExceptionUtil;
import org.teamapps.dsl.TeamAppsDtoParser.*;
import org.teamapps.dsl.generate.ParserFactory;
import org.teamapps.dsl.generate.TeamAppsGeneratorException;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel.getQualifiedTypeName;

public class ClassWrapper implements ClassOrInterfaceWrapper<ClassDeclarationContext> {

	private final ClassDeclarationContext context;
	private final TeamAppsIntermediateDtoModel model;

	public ClassWrapper(ClassDeclarationContext context, TeamAppsIntermediateDtoModel model) {
		this.context = context;
		this.model = model;
	}

	@Override
	public ClassDeclarationContext getParserRuleContext() {
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

	public ClassWrapper getSuperClass() {
		if (context.superClassDecl() != null) {
			String superClassName = context.superClassDecl().typeName().getText();
			String qualifiedTypeName = getQualifiedTypeName(superClassName, context.superClassDecl());
			ClassWrapper superClass = model.findClassByQualifiedName(qualifiedTypeName).orElse(null);
			if (superClass == null) {
				throw new TeamAppsGeneratorException("Cannot find super class " + superClassName + " (" + qualifiedTypeName + ") of " + context.Identifier());
			}
			return superClass;
		} else {
			return null;
		}
	}

	@Override
	public List<InterfaceWrapper> getInterfaces() {
		if (context.implementsDecl() != null) {
			return context.implementsDecl().classList().typeName().stream()
					.map(model::findInterface)
					.toList();
		} else {
			return List.of();
		}
	}

	@Override
	public List<ClassOrInterfaceWrapper<?>> getSuperTypes() {
		return Stream.concat(Optional.ofNullable(getSuperClass()).stream(), getInterfaces().stream())
				.toList();
	}

	@Override
	public List<PropertyDeclarationContext> getProperties() {
		return context.propertyDeclaration();
	}

	@Override
	public List<CommandDeclarationContext> getCommands() {
		List<CommandDeclarationContext> explicitDeclarations = context.commandDeclaration();

		List<CommandDeclarationContext> mutablePropertyDeclarations = context.propertyDeclaration().stream()
				.filter(property -> property.mutableModifier() != null)
				.map(property -> ExceptionUtil.softenExceptions(
						() -> {
							CommandDeclarationContext command =
									ParserFactory.createParser(new StringReader("command set" + StringUtils.capitalize(property.Identifier().toString())
																				+ "(" + property.type().getText() + " " + property.Identifier().toString() + ");")).commandDeclaration();
							command.setParent(property.getParent());
							return command;
						}
				))
				.collect(Collectors.toList());

		ArrayList<CommandDeclarationContext> allCommandDeclarations = new ArrayList<>(explicitDeclarations);
		allCommandDeclarations.addAll(mutablePropertyDeclarations);

		return allCommandDeclarations;
	}

	public List<PropertyDeclarationContext> getPropertiesNotImplementedBySuperClasses() {
		List<PropertyDeclarationContext> properties = new ArrayList<>(getAllProperties());
		ClassWrapper superClass = getSuperClass();
		if (superClass != null) {
			properties.removeAll(superClass.getAllProperties());
		}
		return List.copyOf(properties);
	}

	public List<PropertyDeclarationContext> getRequiredPropertiesNotImplementedBySuperClasses() {
		return getPropertiesNotImplementedBySuperClasses().stream()
				.filter(p -> p.requiredModifier() != null)
				.toList();
	}

	@Override
	public List<EventDeclarationContext> getEvents() {
		return context.eventDeclaration();
	}

	@Override
	public List<QueryDeclarationContext> getQueries() {
		return context.queryDeclaration();
	}

	@Override
	public boolean isAbstract() {
		return context.abstractModifier() != null;
	}

	@Override
	public String toString() {
		return "ClassWrapper: " + getName();
	}
}
