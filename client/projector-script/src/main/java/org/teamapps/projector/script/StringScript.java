/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.projector.script;


import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientobject.ClientObject;
import org.teamapps.projector.clientobject.ClientObjectChannel;
import org.teamapps.projector.clientobject.InvokableClientObject;
import org.teamapps.projector.session.CurrentSessionContext;

import java.util.List;

@ClientObjectLibrary(ScriptLibrary.class)
public class StringScript implements ClientObject, DtoStringScriptEventHandler, InvokableClientObject {

	private final String script;
	private final DtoStringScriptClientObjectChannel clientObjectChannel;

	public StringScript(String script) {
		this.script = script;

		// This IS ok, since SessionContext does not do anything with "this" reference.
		// The only usage of the "this" reference is going to be triggered by this.
		ClientObjectChannel coc = CurrentSessionContext.get().registerClientObject(this);
		this.clientObjectChannel = new DtoStringScriptClientObjectChannel(coc);
	}

	@Override
	public DtoStringScript createDto() {
		return new DtoStringScript(script);
	}

	public void invoke(String name, Object... parameters) {
		clientObjectChannel.invoke(name, List.of(parameters));
	}
}
