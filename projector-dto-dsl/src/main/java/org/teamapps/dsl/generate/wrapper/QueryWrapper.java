package org.teamapps.dsl.generate.wrapper;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.IntermediateDtoModel;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryWrapper {

	private final TeamAppsDtoParser.QueryDeclarationContext context;
	private final IntermediateDtoModel model;
	private final List<FormalParameterWrapper> parameters;

	public QueryWrapper(TeamAppsDtoParser.QueryDeclarationContext context, IntermediateDtoModel model) {
		this.context = context;
		this.model = model;

		this.parameters = context.formalParameter().stream()
				.map(fp -> new FormalParameterWrapper(fp, model))
				.toList();
	}

	public String getName() {
		return context.Identifier().toString();
	}

	public boolean isStatic() {
		return context.staticModifier() != null;
	}

	public List<FormalParameterWrapper> getParameters() {
		return parameters;
	}

	public TypeReferenceWrapper getReturnType() {
		return new TypeReferenceWrapper(context.type(), model);
	}


	public ClassOrInterfaceWrapper<?> getDeclaringClass() {
		return model.getDeclaringClassOrInterface(context);
	}

	public List<FormalParameterWrapper> getAllProperties() {
		return getParameters();
	}

	public List<FormalParameterWrapper> getAllRequiredProperties() {
		return getParameters();
	}

	public List<FormalParameterWrapper> getRequiredPropertiesNotImplementedBySuperClasses() {
		return getParameters();
	}

	public List<FormalParameterWrapper> simplePropertiesSortedByRelevance() {
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
						} else if (p.getType().findReferencedClass().isEmpty()) {
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
		return model.getDeclaringClassOrInterface(context).getName() + "_" + StringUtils.capitalize(context.Identifier().getText()) + "Query";
	}

	public String getJsonIdentifier() {
		return model.getDeclaringClassOrInterface(context).getName() + "." + context.Identifier().getText();
	}

	public String getJavaClassName() {
		return StringUtils.capitalize(context.Identifier().getText()) + "Query";
	}

}
