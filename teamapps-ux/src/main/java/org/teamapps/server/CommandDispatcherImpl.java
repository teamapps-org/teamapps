/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.UiCommand;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.uisession.UiCommandExecutor;
import org.teamapps.uisession.UiCommandWithResultCallback;
import org.teamapps.ux.session.CommandDispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandDispatcherImpl implements CommandDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandDispatcherImpl.class);

	private final UiCommandExecutor commandExecutor;
	private final QualifiedUiSessionId qualifiedUiSessionId;
	private final List<UiCommandWithResultCallback> uiCommands = Collections.synchronizedList(new ArrayList<>()); // copy on read, see below

	private boolean closed;

	private SessionRecorder sessionRecorder;

	public CommandDispatcherImpl(UiCommandExecutor commandExecutor, QualifiedUiSessionId qualifiedUiSessionId, SessionRecorder sessionRecorder) {
		this.commandExecutor = commandExecutor;
		this.qualifiedUiSessionId = qualifiedUiSessionId;
		this.sessionRecorder = sessionRecorder;
	}

	@Override
	public <RESULT> void queueCommand(UiCommand<RESULT> command, Consumer<RESULT> resultCallback) {
		if (!this.closed) {
			uiCommands.add(new UiCommandWithResultCallback<>(command, resultCallback));
		} else {
			LOGGER.debug("Not queuing command because already closed.");
		}
	}

	@Override
	public void flushCommands() {
		if (uiCommands.isEmpty()) {
			return;
		}
		if (!this.closed) {
			ArrayList<UiCommandWithResultCallback> commandsCopy;

			// make sure sendCommand() gets executed in the right order!
			synchronized (this) {
				synchronized (uiCommands) {
					commandsCopy = new ArrayList<>(uiCommands);
					uiCommands.clear();
				}

				commandExecutor.sendCommands(qualifiedUiSessionId, commandsCopy);

				if (sessionRecorder != null) {
					sessionRecorder.recordCommands(commandsCopy.stream()
							.map(UiCommandWithResultCallback::getUiCommand)
							.collect(Collectors.toList()));
				}
			}
		} else {
			LOGGER.debug("Not flushing commands because already closed.");
		}
	}

	@Override
	public void close() {
		this.closed = true;
		uiCommands.clear();
	}
}
