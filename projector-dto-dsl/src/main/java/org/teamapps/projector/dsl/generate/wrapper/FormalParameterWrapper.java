package org.teamapps.projector.dsl.generate.wrapper;

import org.teamapps.projector.dsl.TeamAppsDtoParser;
import org.teamapps.projector.dsl.generate.IntermediateDtoModel;

public class FormalParameterWrapper {

	private final TeamAppsDtoParser.FormalParameterContext context;
	private final IntermediateDtoModel model;
	private final TypeReferenceWrapper type;

	public FormalParameterWrapper(TeamAppsDtoParser.FormalParameterContext context, IntermediateDtoModel model) {
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
