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
package org.teamapps.server.uisession.messagebuffer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.teamapps.dto.protocol.server.CMD;
import org.teamapps.server.json.TeamAppsObjectMapperFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ServerMessageBufferTest {

	public static final ObjectMapper OBJECT_MAPPER = TeamAppsObjectMapperFactory.create();

	@Test
	public void size() throws Exception {
		ServerMessageBuffer buffer = new ServerMessageBuffer(3, 1_000_000, OBJECT_MAPPER);
		assertEquals(0, buffer.getBufferedMessagesCount());
		buffer.addMessage(createCmd(100));
		assertEquals(1, buffer.getBufferedMessagesCount());
		buffer.addMessage(createCmd(100));
		assertEquals(2, buffer.getBufferedMessagesCount());
		buffer.addMessage(createCmd(100));
		assertEquals(3, buffer.getBufferedMessagesCount());
		buffer.consumeMessage();
		buffer.addMessage(createCmd(100));
		assertEquals(3, buffer.getBufferedMessagesCount()); // !!
		buffer.consumeMessage();
		buffer.addMessage(createCmd(100));
		assertEquals(3, buffer.getBufferedMessagesCount()); // !!
	}

	@Test
	public void consumeCommand() throws Exception {
		ServerMessageBuffer buffer = new ServerMessageBuffer(2, 1_000_000, OBJECT_MAPPER);

		buffer.addMessage(createCmd(100));
		assertEquals(1, buffer.consumeMessage().sequenceNumber());
		assertNull(buffer.consumeMessage());

		buffer.addMessage(createCmd(100));
		buffer.addMessage(createCmd(100));
		assertEquals(2, buffer.consumeMessage().sequenceNumber());
		assertEquals(3, buffer.consumeMessage().sequenceNumber());
		assertNull(buffer.consumeMessage());
	}

	@Test
	public void throwsExceptionIfCommandsNextConsumableCommandGetsDeletedDueToBufferOverflow() throws Exception {
		ServerMessageBuffer buffer = new ServerMessageBuffer(3, 1_000_000, OBJECT_MAPPER);
		buffer.addMessage(createCmd(100));
		buffer.addMessage(createCmd(100));
		buffer.addMessage(createCmd(100));
		buffer.consumeMessage();
		buffer.addMessage(createCmd(100));
		assertThatThrownBy(() -> buffer.addMessage(createCmd(100)))
				.isInstanceOf(ServerMessageBufferLengthOverflowException.class);
	}

	@Test
	public void rewindToCommand() throws Exception {
		ServerMessageBuffer buffer = new ServerMessageBuffer(5, 1_000_000, OBJECT_MAPPER);
		for (int i = 1; i <= 4; i++) {
			buffer.addMessage(createCmd(100));
			if (i <= 3) {
				buffer.consumeMessage();
			}
		}
		assertEquals(4, buffer.getBufferedMessagesCount());

		buffer.rewindToMessage(2);

		assertEquals(4, buffer.getBufferedMessagesCount());
		assertEquals(3, buffer.consumeMessage().sequenceNumber());
		assertEquals(4, buffer.consumeMessage().sequenceNumber());
		assertNull(buffer.consumeMessage());
	}

	@Test
	public void rewindToCommand2() throws Exception {
		ServerMessageBuffer buffer = new ServerMessageBuffer(6, 1_000_000, OBJECT_MAPPER);
		for (int i = 1; i <= 7; i++) {
			buffer.addMessage(createCmd(100));
			if (i <= 5) {
				buffer.consumeMessage();
			}
		}
		assertEquals(6, buffer.getBufferedMessagesCount());

		buffer.rewindToMessage(3);

		assertEquals(6, buffer.getBufferedMessagesCount());
		assertEquals(4, buffer.consumeMessage().sequenceNumber());
		assertEquals(5, buffer.consumeMessage().sequenceNumber());
		assertEquals(6, buffer.consumeMessage().sequenceNumber());
		assertEquals(7, buffer.consumeMessage().sequenceNumber());
		assertNull(buffer.consumeMessage());
	}

	@Test
	public void rewindToCommandWithLastReceivedIsMinusOne() throws Exception {
		ServerMessageBuffer buffer = new ServerMessageBuffer(6, 1_000_000, OBJECT_MAPPER);
		for (int i = 1; i <= 3; i++) {
			buffer.addMessage(createCmd(100));
		}

		assertEquals(true, buffer.rewindToMessage(-1));
		assertEquals(1, buffer.consumeMessage().sequenceNumber());
	}

	@Test
	public void rewindToCommandWithLastReceivedIsMinusOneFailsIfAlreadyOutOfBuffer() throws Exception {
		ServerMessageBuffer buffer = new ServerMessageBuffer(6, 1_000_000, OBJECT_MAPPER);
		for (int i = 1; i <= 7; i++) {
			buffer.addMessage(createCmd(100));
			if (i <= 2) {
				buffer.consumeMessage();
			}
		}
		assertEquals(6, buffer.getBufferedMessagesCount());

		assertEquals(false, buffer.rewindToMessage(-1));
	}

	@Test
	public void purgeTillCommand() throws Exception {
		ServerMessageBuffer buffer = new ServerMessageBuffer(10, 1_000_000, OBJECT_MAPPER);

		for (int i = 1; i <= 15; i++) {
			buffer.addMessage(createCmd(100));
			if (i <= 11) {
				buffer.consumeMessage();
			}
		}

		assertEquals(10, buffer.getBufferedMessagesCount());

		buffer.purgeTillMessage(8);
		assertEquals(8, buffer.getBufferedMessagesCount());

		buffer.purgeTillMessage(10);
		assertEquals(6, buffer.getBufferedMessagesCount());

		buffer.purgeTillMessage(11);
		assertEquals(5, buffer.getBufferedMessagesCount());

		buffer.purgeTillMessage(12);
		assertEquals(4, buffer.getBufferedMessagesCount(), "must not purge next consumable command");

		buffer.purgeTillMessage(100);
		assertEquals(4, buffer.getBufferedMessagesCount(), "must not purge next consumable command");
	}

	@Test
	public void rewindToCommandWhenTailGreaterThanNextConsumableAndRewindedCommandIsLeftFromNextConsumable()
			throws ServerMessageBufferException {
		ServerMessageBuffer buffer = new ServerMessageBuffer(10, 1_000_000, OBJECT_MAPPER);

		for (int i = 0; i < 15; i++) {
			buffer.addMessage(createCmd(100));
			buffer.consumeMessage();
		}
		buffer.addMessage(createCmd(100));

		buffer.rewindToMessage(13);

		Assertions.assertThat(buffer.consumeMessage().sequenceNumber()).isEqualTo(14);
	}

	@Test
	public void shouldThrowExceptionWhenMaxTotalSizeIsReached() throws ServerMessageBufferException {
		ServerMessageBuffer buffer = new ServerMessageBuffer(100, 1_000, OBJECT_MAPPER);

		for (int i = 0; i < 10; i++) {
			buffer.addMessage(createCmd(100));
		}

		assertThatThrownBy(() -> buffer.addMessage(createCmd(1)))
				.isInstanceOf(ServerMessageBufferSizeOverflowException.class);
	}

	@Test
	public void shouldPurgeACommandBeforeThrowingExceptionWhenMaxTotalSizeIsReached() throws ServerMessageBufferException {
		ServerMessageBuffer buffer = new ServerMessageBuffer(100, 1_000, OBJECT_MAPPER);

		for (int i = 0; i < 10; i++) {
			buffer.addMessage(createCmd(100));
		}

		assertThat(buffer.getBufferedMessagesCount()).isEqualTo(10);

		for (int i = 10; i < 20; i++) {
			buffer.consumeMessage();
			buffer.addMessage(createCmd(100)); // does not throw an exception!
		}

		assertThat(buffer.getBufferedMessagesCount()).isEqualTo(10);

		assertThatThrownBy(() -> buffer.addMessage(createCmd(1)))
				.isInstanceOf(ServerMessageBufferSizeOverflowException.class);
	}

	@Test
	public void shouldPurgeMultipleCommandsBeforeThrowingExceptionWhenMaxTotalSizeIsReached() throws ServerMessageBufferException {
		ServerMessageBuffer buffer = new ServerMessageBuffer(100, 1_000, OBJECT_MAPPER);

		for (int i = 0; i < 10; i++) {
			buffer.addMessage(createCmd(100));
		}

		for (int i = 0; i < 5; i++) {
			buffer.consumeMessage();
		}

		assertThat(buffer.getBufferedMessagesCount()).isEqualTo(10);

		buffer.addMessage(createCmd(500)); // fills up the buffer

		assertThat(buffer.getBufferedMessagesCount()).isEqualTo(6);

		assertThatThrownBy(() -> buffer.addMessage(createCmd(1)))
				.isInstanceOf(ServerMessageBufferSizeOverflowException.class);
	}

	private CMD createCmd(int approximateLengh) {
		// {"_type":"CMD","oid":"clientObjectId","name":"someCommand","params":["xxx<length>"],"r":false,"sn":1}
		return new CMD(null, "clientObjectId", "someCommand", new Object[] {StringUtils.repeat('x', approximateLengh - 91)}, false);
	}
}
