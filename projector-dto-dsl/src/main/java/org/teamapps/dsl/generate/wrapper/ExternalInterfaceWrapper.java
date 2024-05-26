package org.teamapps.dsl.generate.wrapper;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.commons.util.ExceptionUtil;
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.ParserFactory;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.io.StringReader;

public class ExternalInterfaceWrapper extends InterfaceWrapper {

	public ExternalInterfaceWrapper(TeamAppsDtoParser.ImportDeclarationContext context, TeamAppsIntermediateDtoModel model) {
		super(createImplicitInterface(context), model);
	}

	static TeamAppsDtoParser.InterfaceDeclarationContext createImplicitInterface(TeamAppsDtoParser.ImportDeclarationContext importDecl) {
		return ExceptionUtil.runWithSoftenedExceptions(() -> {
			TeamAppsDtoParser.ClassCollectionContext classCollectionContext = ParserFactory.createParser(new StringReader("package \"_external_\":" + importDecl.qualifiedTypeName().packageName().getText() + "; interface " + importDecl.qualifiedTypeName().Identifier().getText() + " {}")).classCollection();
			return classCollectionContext.typeDeclaration().getFirst().interfaceDeclaration();
		});
	}

	public String getJavaClassName() {
		return StringUtils.capitalize(getName());
	}

	@Override
	public boolean isExternal() {
		return true;
	}
}