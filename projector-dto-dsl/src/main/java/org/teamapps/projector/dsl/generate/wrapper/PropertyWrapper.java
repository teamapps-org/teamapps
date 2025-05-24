package org.teamapps.projector.dsl.generate.wrapper;

import org.teamapps.projector.dsl.TeamAppsDtoParser;
import org.teamapps.projector.dsl.generate.IntermediateDtoModel;

public class PropertyWrapper {

	private final TeamAppsDtoParser.PropertyDeclarationContext context;
	private final IntermediateDtoModel model;
	private final TypeReferenceWrapper type;

	public PropertyWrapper(TeamAppsDtoParser.PropertyDeclarationContext context, IntermediateDtoModel model) {
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

	public boolean isNonNullable() {
		return getType().isPrimitiveType()
				|| isRequired();
	}


}
