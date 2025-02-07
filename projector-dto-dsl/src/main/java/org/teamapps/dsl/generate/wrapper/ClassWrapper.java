package org.teamapps.dsl.generate.wrapper;

import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.TeamAppsDtoParser.ClassDeclarationContext;
import org.teamapps.dsl.TeamAppsDtoParser.CommandDeclarationContext;
import org.teamapps.dsl.generate.DtoGeneratorException;
import org.teamapps.dsl.generate.IntermediateDtoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.teamapps.dsl.generate.IntermediateDtoModel.findImport;
import static org.teamapps.dsl.generate.IntermediateDtoModel.getQualifiedTypeName;

public class ClassWrapper implements ClassOrInterfaceWrapper<ClassDeclarationContext> {

	private final ClassDeclarationContext context;
	private final IntermediateDtoModel model;

	private List<PropertyWrapper> properties;
	private List<CommandWrapper> commands;
	private List<EventWrapper> events;
	private List<QueryWrapper> queries;

	public ClassWrapper(ClassDeclarationContext context, IntermediateDtoModel model) {
		this.context = context;
		this.model = model;

		this.properties = context.propertyDeclaration().stream().map(p -> new PropertyWrapper(p, model)).toList();
		this.commands = getAllCommandDeclarationContexts().stream().map(p -> new CommandWrapper(p, model)).toList();
		this.events = context.eventDeclaration().stream().map(p -> new EventWrapper(p, model)).toList();
		this.queries = context.queryDeclaration().stream().map(p -> new QueryWrapper(p, model)).toList();
	}

	private List<CommandDeclarationContext> getAllCommandDeclarationContexts() {
		ArrayList<CommandDeclarationContext> allCommandDeclarations = new ArrayList<>(context.commandDeclaration());
		allCommandDeclarations.addAll(ClassOrInterfaceWrapper.createImplicitMutationCommands(context.propertyDeclaration()));
		return allCommandDeclarations;
	}

	@Override
	public ClassDeclarationContext getParserRuleContext() {
		return context;
	}

	@Override
	public String getName() {
		return context.Identifier().getText();
	}

	@Override
	public void checkSuperTypeResolvability() {
		if (context.superClassDecl() != null) {
			String superClassName = context.superClassDecl().typeName().getText();
			String qualifiedTypeName = getQualifiedTypeName(superClassName, context);
			model.findClassByQualifiedName(qualifiedTypeName).orElseThrow(() -> model.createUnresolvedTypeReferenceException(superClassName, context));
		}

		if (context.implementsDecl() != null) {
			for (TeamAppsDtoParser.TypeNameContext typeNameContext : context.implementsDecl().classList().typeName()) {
				String interfaceName = typeNameContext.getText();
				Boolean isExternal = findImport(context, interfaceName).map(i -> i.externalInterfaceTypeModifier() != null).orElse(false);
				if (isExternal) {
					continue;
				}
				String qualifiedName = getQualifiedTypeName(interfaceName, context);
				model.findInterfaceByQualifiedName(qualifiedName).orElseThrow(() -> model.createUnresolvedTypeReferenceException(interfaceName, context));
			}
		}
	}

	public ClassWrapper getSuperClass() {
		if (context.superClassDecl() != null) {
			String superClassName = context.superClassDecl().typeName().getText();
			String qualifiedTypeName = getQualifiedTypeName(superClassName, context);
			ClassWrapper superClass = model.findClassByQualifiedName(qualifiedTypeName).orElse(null);
			if (superClass == null) {
				throw new DtoGeneratorException("Cannot find super class " + superClassName + " (" + qualifiedTypeName + ") of " + context.Identifier());
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

	public List<PropertyWrapper> getPropertiesNotImplementedBySuperClasses() {
		List<PropertyWrapper> properties = new ArrayList<>(getAllProperties());
		ClassWrapper superClass = getSuperClass();
		if (superClass != null) {
			properties.removeAll(superClass.getAllProperties());
		}
		return List.copyOf(properties);
	}

	public List<PropertyWrapper> getRequiredPropertiesNotImplementedBySuperClasses() {
		return getPropertiesNotImplementedBySuperClasses().stream()
				.filter(p -> p.isRequired())
				.toList();
	}

	@Override
	public boolean isAbstract() {
		return context.abstractModifier() != null;
	}

	@Override
	public boolean isExternal() {
		return false;
	}

	@Override
	public String toString() {
		return "ClassWrapper: " + getName();
	}

}
