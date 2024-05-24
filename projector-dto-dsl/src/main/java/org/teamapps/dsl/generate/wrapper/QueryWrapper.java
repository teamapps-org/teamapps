package org.teamapps.dsl.generate.wrapper;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryWrapper {

	private final TeamAppsDtoParser.QueryDeclarationContext context;
	private final TeamAppsIntermediateDtoModel model;
	private final List<FormalParameterWrapper> parameters;

	public QueryWrapper(TeamAppsDtoParser.QueryDeclarationContext context, TeamAppsIntermediateDtoModel model) {
		this.context = context;
		this.model = model;

		this.parameters = context.formalParameter().stream()
				.map(fp -> new FormalParameterWrapper(fp, model))
				.toList();
	}

	public String getName() {
		return context.Identifier().toString();
	}

	public List<FormalParameterWrapper> getParameters() {
		return parameters;
	}

	public TypeReferenceWrapper getReturnType() {
		return new TypeReferenceWrapper(context.type(), model);
	}







	public ClassOrInterfaceWrapper<?> getDeclaringClass() {
		System.out.println("QueryWrapper.getDeclaringClass");
		return model.getDeclaringClassOrInterface(context);
	}

	public String getTypeScriptInterfaceName() {
		System.out.println("QueryWrapper.getDeclaringClass");
		return "Dto" + model.getDeclaringClassOrInterface(context).getName() + "_" + StringUtils.capitalize(context.Identifier().getText()) + "Query";
	}

	public List<FormalParameterWrapper> getAllProperties() {
		System.out.println("QueryWrapper.getAllProperties");
		return getParameters();
	}
	public List<FormalParameterWrapper> getAllRequiredProperties() {
		System.out.println("QueryWrapper.getAllRequiredProperties");
		return getParameters();
	}
	public List<FormalParameterWrapper> getRequiredPropertiesNotImplementedBySuperClasses() {
		System.out.println("QueryWrapper.getRequiredPropertiesNotImplementedBySuperClasses");
		return getParameters();
	}
	public List<FormalParameterWrapper> simplePropertiesSortedByRelevance() {
		System.out.println("QueryWrapper.simplePropertiesSortedByRelevance");
		return getParameters().stream()
				.sorted((p1, p2) -> {
					Function<FormalParameterWrapper, Integer> getPriority = (p) -> {
						if (p.getName().equals("id")) {
							return 50;
						} else if (p.getName().equals("name")) {
							return 40;
						} else if (p.getName().contains("Id")) {
							return 30;
						} else if (p.getName().contains("Name")) {
							return 20;
						} else if (p.getType().findReferencedClass().isEmpty())  {
							return 10;
						} else {
							return 0;
						}
					};
					return getPriority.apply(p2) - getPriority.apply(p1);
				})
				.collect(Collectors.toList());
	}
	public String getTypeScriptIdentifier() {
		System.out.println("QueryWrapper.getTypeScriptIdentifier");
		return model.getDeclaringClassOrInterface(context).getName() + "_" + StringUtils.capitalize(context.Identifier().getText()) + "Query";
	}

	public String getJsonIdentifier() {
		System.out.println("QueryWrapper.getJsonIdentifier");
		return model.getDeclaringClassOrInterface(context).getName() + "." + context.Identifier().getText();
	}

	public String getJavaClassName() {
		System.out.println("QueryWrapper.getJavaClassName");
		return StringUtils.capitalize(context.Identifier().getText()) + "Query";
	}

}
