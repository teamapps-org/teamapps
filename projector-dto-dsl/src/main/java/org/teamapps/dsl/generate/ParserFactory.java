/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.dsl.generate;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.teamapps.dsl.TeamAppsDtoLexer;
import org.teamapps.dsl.TeamAppsDtoParser;

import java.io.IOException;
import java.io.Reader;

public class ParserFactory {

	public static TeamAppsDtoParser createParser(Reader reader) throws IOException {
		return createParser(reader, null);
	}

	public static TeamAppsDtoParser createParser(Reader reader, String logContextName) throws IOException {
		TeamAppsDtoLexer lexer = new TeamAppsDtoLexer(CharStreams.fromReader(reader));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TeamAppsDtoParser parser = new TeamAppsDtoParser(tokens);
		parser.addErrorListener(new ThrowingErrorListener(logContextName));
		parser.setBuildParseTree(true);
		return parser;
	}

	public static class ThrowingErrorListener extends BaseErrorListener {
		private final String logContextName;

		public ThrowingErrorListener(String logContextName) {
			this.logContextName = logContextName;
		}

		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
				throws ParseCancellationException {
			throw new ParseCancellationException((logContextName != null ? logContextName + ": " : "") + "line " + line + ":" + charPositionInLine + " " + msg);
		}
	}
}
