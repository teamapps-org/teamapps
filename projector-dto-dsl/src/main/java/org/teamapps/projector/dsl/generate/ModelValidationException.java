package org.teamapps.projector.dsl.generate;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.apache.commons.lang3.StringUtils;

public class ModelValidationException extends RuntimeException {
	public ModelValidationException(ParserRuleContext context, String message) {
		super(DtoGeneratorUtil.originNamesByParserRuleContext.getOrDefault(context, "unknown code") + ":" + getLineNumber(context) + " : " + message);
	}

	private static int getLineNumber(ParserRuleContext context) {
		CharStream charStream = context.getStart().getInputStream();
		String textToContext = charStream.getText(Interval.of(0, context.getStart().getStartIndex()));
		return StringUtils.countMatches(textToContext, '\n') + 1;
	}
}
