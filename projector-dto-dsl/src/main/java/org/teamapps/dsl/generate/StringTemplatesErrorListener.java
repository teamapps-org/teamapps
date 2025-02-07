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

import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.misc.STMessage;

public class StringTemplatesErrorListener implements STErrorListener {
	@Override
	public void compileTimeError(STMessage stMessage) {
		throw createException(stMessage);
	}

	@Override
	public void runTimeError(STMessage stMessage) {
		throw createException(stMessage);
	}

	@Override
	public void IOError(STMessage stMessage) {
		throw createException(stMessage);
	}

	@Override
	public void internalError(STMessage stMessage) {
		throw createException(stMessage);
	}

	private RuntimeException createException(STMessage stMessage) {
		return new DtoGeneratorException("ErrorType: " + stMessage.error + ", arg1: " + stMessage.arg + ", arg2: " + stMessage.arg2 + ", arg3: " + stMessage.arg3, stMessage.cause);
	}
}
