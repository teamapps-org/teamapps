package org.teamapps.dsl.generate.wrapper;

import org.antlr.v4.runtime.ParserRuleContext;
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.TeamAppsDtoParser.ImportDeclarationContext;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.List;

import static org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel.findAncestorOfType;

public interface TypeWrapper<T extends ParserRuleContext> {
	T getParserRuleContext();

	TeamAppsIntermediateDtoModel getModel();

	default String getPackageName() {
		return findAncestorOfType(getParserRuleContext(), TeamAppsDtoParser.ClassCollectionContext.class, true)
				.map(ccc -> ccc.packageDeclaration().packageName().getText()).orElse(null);
	}
	
	default String getJsPackageName() {
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

	default List<ImportDeclarationContext> getImports() {
		return getModel().getAllImports(getParserRuleContext());
	}

}
