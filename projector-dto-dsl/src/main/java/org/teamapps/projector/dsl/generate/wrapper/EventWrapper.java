package org.teamapps.projector.dsl.generate.wrapper;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.projector.dsl.TeamAppsDtoParser;
import org.teamapps.projector.dsl.generate.IntermediateDtoModel;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EventWrapper {

	private final TeamAppsDtoParser.EventDeclarationContext context;
	private final IntermediateDtoModel model;
	private final List<FormalParameterWrapper> parameters;

	public EventWrapper(TeamAppsDtoParser.EventDeclarationContext context, IntermediateDtoModel model) {
		this.context = Objects.requireNonNull(context);
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
		return model.getDeclaringClassOrInterface(context);
	}

	public String getTypeScriptInterfaceName() {
		return model.getDeclaringClassOrInterface(context).getName() + "_" + StringUtils.capitalize(context.Identifier().getText()) + "Event";
	}

	public boolean hasNoParameters() {
		return parameters.isEmpty();
	}

	public boolean hasOneParameter() {
		return parameters.size() == 1;
	}

	public boolean hasMultipleParameters() {
		return parameters.size() > 1;
	}

	public String getEffectiveTypeScriptEventObjectTypeName() {
		if (hasMultipleParameters()) {
			return getTypeScriptInterfaceName();
		} else if (hasOneParameter()) {
			return getParameters().getFirst().getType().getTypeScriptTypeName();
		} else {
			return "void";
		}
	}

	public List<FormalParameterWrapper> getProperties() {
		return getParameters();
	}

	public List<FormalParameterWrapper> getAllProperties() {
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
		return model.getDeclaringClassOrInterface(context).getName() + "_" + StringUtils.capitalize(context.Identifier().getText()) + "Event";
	}

	public String getJsonIdentifier() {
		return model.getDeclaringClassOrInterface(context).getName() + "." + context.Identifier().getText();
	}

	public String getJavaClassName() {
		return StringUtils.capitalize(context.Identifier().getText()) + "Event";
	}

}
