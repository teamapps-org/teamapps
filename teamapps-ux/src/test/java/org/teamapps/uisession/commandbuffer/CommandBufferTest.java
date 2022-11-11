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
package org.teamapps.uisession.commandbuffer;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.teamapps.dto.CMD;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CommandBufferTest {

	@Test
	public void size() throws Exception {
		CommandBuffer buffer = new CommandBuffer(3, 1_000_000);
		assertEquals(0, buffer.getBufferedCommandsCount());
		buffer.addCommand(createCmd(1));
		assertEquals(1, buffer.getBufferedCommandsCount());
		buffer.addCommand(createCmd(2));
		assertEquals(2, buffer.getBufferedCommandsCount());
		buffer.addCommand(createCmd(3));
		assertEquals(3, buffer.getBufferedCommandsCount());
		buffer.consumeCommand();
		buffer.addCommand(createCmd(4));
		assertEquals(3, buffer.getBufferedCommandsCount()); // !!
		buffer.consumeCommand();
		buffer.addCommand(createCmd(5));
		assertEquals(3, buffer.getBufferedCommandsCount()); // !!
	}

	@Test
	public void consumeCommand() throws Exception {
		CommandBuffer buffer = new CommandBuffer(2, 1_000_000);

		buffer.addCommand(createCmd(1));
		assertEquals(1, buffer.consumeCommand().getId());
		assertNull(buffer.consumeCommand());

		buffer.addCommand(createCmd(2));
		buffer.addCommand(createCmd(3));
		assertEquals(2, buffer.consumeCommand().getId());
		assertEquals(3, buffer.consumeCommand().getId());
		assertNull(buffer.consumeCommand());
	}

	@Test
	public void throwsExceptionIfCommandsNextConsumableCommandGetsDeletedDueToBufferOverflow() throws Exception {
		CommandBuffer buffer = new CommandBuffer(3, 1_000_000);
		buffer.addCommand(createCmd(1));
		buffer.addCommand(createCmd(2));
		buffer.addCommand(createCmd(3));
		buffer.consumeCommand();
		buffer.addCommand(createCmd(4));
		assertThatThrownBy(() -> buffer.addCommand(createCmd(5)))
				.isInstanceOf(CommandBufferLengthOverflowException.class);
	}

	@Test
	public void rewindToCommand() throws Exception {
		CommandBuffer buffer = new CommandBuffer(5, 1_000_000);
		for (int i = 1; i <= 4; i++) {
			buffer.addCommand(createCmd(i));
			if (i <= 3) {
				buffer.consumeCommand();
			}
		}
		assertEquals(4, buffer.getBufferedCommandsCount());

		buffer.rewindToCommand(2);

		assertEquals(4, buffer.getBufferedCommandsCount());
		assertEquals(3, buffer.consumeCommand().getId());
		assertEquals(4, buffer.consumeCommand().getId());
		assertNull(buffer.consumeCommand());
	}

	@Test
	public void rewindToCommand2() throws Exception {
		CommandBuffer buffer = new CommandBuffer(6, 1_000_000);
		for (int i = 1; i <= 7; i++) {
			buffer.addCommand(createCmd(i));
			if (i <= 5) {
				buffer.consumeCommand();
			}
		}
		assertEquals(6, buffer.getBufferedCommandsCount());

		buffer.rewindToCommand(3);

		assertEquals(6, buffer.getBufferedCommandsCount());
		assertEquals(4, buffer.consumeCommand().getId());
		assertEquals(5, buffer.consumeCommand().getId());
		assertEquals(6, buffer.consumeCommand().getId());
		assertEquals(7, buffer.consumeCommand().getId());
		assertNull(buffer.consumeCommand());
	}

	@Test
	public void rewindToCommandWithLastReceivedIsMinusOne() throws Exception {
		CommandBuffer buffer = new CommandBuffer(6, 1_000_000);
		for (int i = 1; i <= 3; i++) {
			buffer.addCommand(createCmd(i));
		}

		assertEquals(true, buffer.rewindToCommand(-1));
		assertEquals(1, buffer.consumeCommand().getId());
	}

	@Test
	public void rewindToCommandWithLastReceivedIsMinusOneFailsIfAlreadyOutOfBuffer() throws Exception {
		CommandBuffer buffer = new CommandBuffer(6, 1_000_000);
		for (int i = 1; i <= 7; i++) {
			buffer.addCommand(createCmd(i));
			if (i <= 2) {
				buffer.consumeCommand();
			}
		}
		assertEquals(6, buffer.getBufferedCommandsCount());

		assertEquals(false, buffer.rewindToCommand(-1));
	}

	@Test
	public void purgeTillCommand() throws Exception {
		CommandBuffer buffer = new CommandBuffer(10, 1_000_000);

		for (int i = 1; i <= 15; i++) {
			buffer.addCommand(createCmd(i));
			if (i <= 11) {
				buffer.consumeCommand();
			}
		}

		Assert.assertEquals(10, buffer.getBufferedCommandsCount());

		buffer.purgeTillCommand(8);
		Assert.assertEquals(8, buffer.getBufferedCommandsCount());

		buffer.purgeTillCommand(10);
		Assert.assertEquals(6, buffer.getBufferedCommandsCount());

		buffer.purgeTillCommand(11);
		Assert.assertEquals(5, buffer.getBufferedCommandsCount());

		buffer.purgeTillCommand(12);
		Assert.assertEquals("must not purge next consumable command", 4, buffer.getBufferedCommandsCount());

		buffer.purgeTillCommand(100);
		Assert.assertEquals("must not purge next consumable command", 4, buffer.getBufferedCommandsCount());
	}

	@Test
	public void rewindToCommandWhenTailGreaterThanNextConsumableAndRewindedCommandIsLeftFromNextConsumable()
			throws CommandBufferException {
		CommandBuffer buffer = new CommandBuffer(10, 1_000_000);

		for (int i = 0; i < 15; i++) {
			buffer.addCommand(createCmd(i));
			buffer.consumeCommand();
		}
		buffer.addCommand(createCmd(15));

		buffer.rewindToCommand(13);

		Assertions.assertThat(buffer.consumeCommand().getId()).isEqualTo(14);
	}

	@Test
	public void shouldThrowExceptionWhenMaxTotalSizeIsReached() throws CommandBufferException {
		CommandBuffer buffer = new CommandBuffer(100, 1_000);

		for (int i = 0; i < 10; i++) {
			buffer.addCommand(createCmd(i, 100));
		}

		assertThatThrownBy(() -> buffer.addCommand(createCmd(10, 1)))
				.isInstanceOf(CommandBufferSizeOverflowException.class);
	}

	@Test
	public void shouldPurgeACommandBeforeThrowingExceptionWhenMaxTotalSizeIsReached() throws CommandBufferException {
		CommandBuffer buffer = new CommandBuffer(100, 1_000);

		for (int i = 0; i < 10; i++) {
			buffer.addCommand(createCmd(i, 100));
		}

		assertThat(buffer.getBufferedCommandsCount()).isEqualTo(10);

		for (int i = 10; i < 20; i++) {
			buffer.consumeCommand();
			buffer.addCommand(createCmd(10, 100)); // does not throw an exception!
		}

		assertThat(buffer.getBufferedCommandsCount()).isEqualTo(10);

		assertThatThrownBy(() -> buffer.addCommand(createCmd(11, 1)))
				.isInstanceOf(CommandBufferSizeOverflowException.class);
	}

	@Test
	public void shouldPurgeMultipleCommandsBeforeThrowingExceptionWhenMaxTotalSizeIsReached() throws CommandBufferException {
		CommandBuffer buffer = new CommandBuffer(100, 1_000);

		for (int i = 0; i < 10; i++) {
			buffer.addCommand(createCmd(i, 100));
		}

		for (int i = 0; i < 5; i++) {
			buffer.consumeCommand();
		}

		assertThat(buffer.getBufferedCommandsCount()).isEqualTo(10);

		buffer.addCommand(createCmd(10, 500)); // fills up the buffer

		assertThat(buffer.getBufferedCommandsCount()).isEqualTo(6);

		assertThatThrownBy(() -> buffer.addCommand(createCmd(11, 1)))
				.isInstanceOf(CommandBufferSizeOverflowException.class);
	}

	private CMD createCmd(int id) {
		return createCmd(id, 10);
	}

	private CMD createCmd(int id, int length) {
		return new CMD(id, null, "clientObjectId", StringUtils.repeat('x', length), false);
	}
}
