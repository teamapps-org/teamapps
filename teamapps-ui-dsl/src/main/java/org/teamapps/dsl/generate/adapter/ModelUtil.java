package org.teamapps.dsl.generate.adapter;

import org.antlr.v4.runtime.ParserRuleContext;
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

public class ModelUtil {

	public static String getDeclaringTypeScriptFileBaseName(ParserRuleContext node) {
		if (node instanceof TeamAppsDtoParser.EnumDeclarationContext) {
			return ((TeamAppsDtoParser.EnumDeclarationContext) node).Identifier().getText();
		} else {
			ParserRuleContext declaringClassOrInterface = TeamAppsIntermediateDtoModel.getDeclaringClassOrInterface(node);
			if (declaringClassOrInterface instanceof TeamAppsDtoParser.InterfaceDeclarationContext i) {
				return getInterfaceTypeScriptIdentifier(i);
			} else if (declaringClassOrInterface instanceof TeamAppsDtoParser.ClassDeclarationContext c) {
				return getClassTypeScriptIdentifier(c);
			}
		}
		throw new RuntimeException("Cannot determin declaring typescript file base name for " + node.getText());
	}

	public static String getInterfaceTypeScriptIdentifier(TeamAppsDtoParser.InterfaceDeclarationContext i) {
		return i.Identifier().getText() + (i.managedModifier() != null ? "Config": "");
	}

	public static String getClassTypeScriptIdentifier(TeamAppsDtoParser.ClassDeclarationContext i) {
		return i.Identifier().getText() + (i.managedModifier() != null ? "Config": "");
	}

}
