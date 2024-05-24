package org.teamapps.dsl.generate.wrapper;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EventWrapper {

	private final TeamAppsDtoParser.EventDeclarationContext context;
	private final TeamAppsIntermediateDtoModel model;
	private final List<FormalParameterWrapper> parameters;

	public EventWrapper(TeamAppsDtoParser.EventDeclarationContext context, TeamAppsIntermediateDtoModel model) {
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






	public ClassOrInterfaceWrapper<?> getDeclaringClass() {
		System.out.println("EventWrapper.getDeclaringClass");
		return model.getDeclaringClassOrInterface(context);
	}

	public String getTypeScriptInterfaceName() {
		System.out.println("EventWrapper.getDeclaringClass");
		return model.getDeclaringClassOrInterface(context).getName() + "_" + StringUtils.capitalize(context.Identifier().getText()) + "Event";
	}

	public List<FormalParameterWrapper> getAllProperties() {
		System.out.println("EventWrapper.getAllProperties");
		return getParameters();
	}
	public List<FormalParameterWrapper> getAllRequiredProperties() {
		System.out.println("EventWrapper.getAllRequiredProperties");
		return getParameters();
	}
	public List<FormalParameterWrapper> getRequiredPropertiesNotImplementedBySuperClasses() {
		System.out.println("EventWrapper.getRequiredPropertiesNotImplementedBySuperClasses");
		return getParameters();
	}
	public List<FormalParameterWrapper> simplePropertiesSortedByRelevance() {
		System.out.println("EventWrapper.simplePropertiesSortedByRelevance");
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
		System.out.println("EventWrapper.getTypeScriptIdentifier");
		return model.getDeclaringClassOrInterface(context).getName() + "_" + StringUtils.capitalize(context.Identifier().getText()) + "Event";
	}

	public String getJsonIdentifier() {
		System.out.println("EventWrapper.getJsonIdentifier");
		return model.getDeclaringClassOrInterface(context).getName() + "." + context.Identifier().getText();
	}

	public String getJavaClassName() {
		System.out.println("EventWrapper.getJavaClassName");
		return StringUtils.capitalize(context.Identifier().getText()) + "Event";
	}

}
