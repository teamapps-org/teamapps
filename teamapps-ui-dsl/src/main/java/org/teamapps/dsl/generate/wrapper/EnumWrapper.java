package org.teamapps.dsl.generate.wrapper;

import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.TeamAppsDtoParser.EnumDeclarationContext;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.List;

public class EnumWrapper implements TypeWrapper<EnumDeclarationContext> {

	private final EnumDeclarationContext context;
	private final TeamAppsIntermediateDtoModel model;

	public EnumWrapper(EnumDeclarationContext context, TeamAppsIntermediateDtoModel model) {
		this.context = context;
		this.model = model;
	}

	@Override
	public EnumDeclarationContext getParserRuleContext() {
		return context;
	}

	@Override
	public TeamAppsIntermediateDtoModel getModel() {
		return model;
	}

	@Override
	public String getName() {
		return context.Identifier().getText();
	}

	public boolean isStringEnum() {
		return getParserRuleContext().enumConstant().stream().allMatch(ec -> ec.StringLiteral() != null);
	}

	public List<TeamAppsDtoParser.EnumConstantContext> getEnumConstants() {
		return getParserRuleContext().enumConstant();
	}

	@Override
	public String toString() {
		return "EnumWrapper: " + getName();
	}
}
