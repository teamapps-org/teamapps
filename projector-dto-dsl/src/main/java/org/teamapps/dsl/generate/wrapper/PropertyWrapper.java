package org.teamapps.dsl.generate.wrapper;

import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

public class PropertyWrapper {

	private final TeamAppsDtoParser.PropertyDeclarationContext context;
	private final TeamAppsIntermediateDtoModel model;
	private final TypeReferenceWrapper type;

	public PropertyWrapper(TeamAppsDtoParser.PropertyDeclarationContext context, TeamAppsIntermediateDtoModel model) {
		this.context = context;
		this.model = model;
		this.type = new TypeReferenceWrapper(context.type(), model);
	}

	public TypeReferenceWrapper getType() {
		return type;
	}

	public String getName() {
		return context.Identifier().getText();
	}

	public ClassOrInterfaceWrapper<?> getDeclaringClass() {
		return model.getDeclaringClassOrInterface(context);
	}

	public boolean isRequired() {
		return context.requiredModifier() != null;
	}


}
