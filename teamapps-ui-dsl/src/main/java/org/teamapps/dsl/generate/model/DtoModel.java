package org.teamapps.dsl.generate.model;

import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DtoModel {

	private final Map<String, Type> types = new HashMap<>();
	private final TeamAppsIntermediateDtoModel intermediateModel;

	public DtoModel(TeamAppsIntermediateDtoModel intermediateModel) {
		this.intermediateModel = intermediateModel;
		List<Class> classes = intermediateModel.getClassDeclarations().stream()
				.map(classDecl -> createClass(classDecl))
				.collect(Collectors.toList());
	}

	private Class createClass(TeamAppsDtoParser.ClassDeclarationContext classDecl) {
//		Class clazz = new Class(TeamAppsIntermediateDtoModel.getPackageName(classDecl), classDecl.Identifier().getText());
//		clazz.setProperties(classDecl.propertyDeclaration().stream().map(pd -> createProperty(clazz, pd)).collect(Collectors.toList()));
//		return clazz;
		return null;
	}

	private Object createProperty(Class clazz, TeamAppsDtoParser.PropertyDeclarationContext pd) {
//		Property property = new Property(pd.requiredModifier(), pd.optionalModifier(), pd.type(), pd.Identifier().getText(), pd.defaultValueAssignment());
//		return property;
		return null;
	}
}
