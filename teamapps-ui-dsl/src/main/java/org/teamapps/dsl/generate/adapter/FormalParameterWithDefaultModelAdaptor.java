package org.teamapps.dsl.generate.adapter;

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;
import org.teamapps.dsl.TeamAppsDtoParser;

public class FormalParameterWithDefaultModelAdaptor extends PojoModelAdaptor<TeamAppsDtoParser.FormalParameterWithDefaultContext> {

	@Override
	public Object getProperty(Interpreter interpreter, ST self, TeamAppsDtoParser.FormalParameterWithDefaultContext context, Object property, String propertyName) throws STNoSuchPropertyException {
		if (propertyName.equals("name")) {
			return context.Identifier().getText();
		} else {
			return super.getProperty(interpreter, self, context, property, propertyName);
		}
	}
}
