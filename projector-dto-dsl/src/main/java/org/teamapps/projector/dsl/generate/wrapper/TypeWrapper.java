package org.teamapps.projector.dsl.generate.wrapper;

import org.antlr.v4.runtime.ParserRuleContext;
import org.teamapps.dsl.TeamAppsDtoParser;

import static org.teamapps.projector.dsl.generate.IntermediateDtoModel.findAncestorOfType;

public interface TypeWrapper<T extends ParserRuleContext> {
	T getParserRuleContext();

	default String getPackageName() {
		return findAncestorOfType(getParserRuleContext(), TeamAppsDtoParser.ClassCollectionContext.class, true)
				.map(ccc -> ccc.packageDeclaration().packageName().getText()).orElse(null);
	}
	
	default String getJsModuleName() {
		return findAncestorOfType(getParserRuleContext(), TeamAppsDtoParser.ClassCollectionContext.class, true)
				.map(ccc -> {
					String stringLiteral = ccc.packageDeclaration().StringLiteral().getText();
					return stringLiteral.substring(1, stringLiteral.length() - 1);
				}).orElse(null);
	}

	String getName();

	default String getQualifiedName() {
		return getPackageName() + "." + getName();
	}


	default boolean isExternal() {
		return false;
	}

}
