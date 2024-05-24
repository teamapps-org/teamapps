package org.teamapps.dsl.generate.wrapper;

import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

public class FormalParameterWrapper {

	private final TeamAppsDtoParser.FormalParameterContext context;
	private final TeamAppsIntermediateDtoModel model;
	private final TypeReferenceWrapper type;

	public FormalParameterWrapper(TeamAppsDtoParser.FormalParameterContext context, TeamAppsIntermediateDtoModel model) {
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


}
