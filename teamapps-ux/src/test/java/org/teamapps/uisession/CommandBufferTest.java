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
package org.teamapps.uisession;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandBufferTest {

	@Test
	public void size() throws Exception {
		CommandBuffer buffer = new CommandBuffer(3);
		assertEquals(0, buffer.size());
		buffer.addCommand(createCmd(1));
		assertEquals(1, buffer.size());
		buffer.addCommand(createCmd(2));
		assertEquals(2, buffer.size());
		buffer.addCommand(createCmd(3));
		assertEquals(3, buffer.size());
		buffer.consumeCommand();
		buffer.addCommand(createCmd(4));
		assertEquals(3, buffer.size()); // !!
		buffer.consumeCommand();
		buffer.addCommand(createCmd(5));
		assertEquals(3, buffer.size()); // !!
	}

	@Test
	public void consumeCommand() throws Exception {
		CommandBuffer buffer = new CommandBuffer(2);

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
		CommandBuffer buffer = new CommandBuffer(3);
		buffer.addCommand(createCmd(1));
		buffer.addCommand(createCmd(2));
		buffer.addCommand(createCmd(3));
		buffer.consumeCommand();
		buffer.addCommand(createCmd(4));
		try {
			buffer.addCommand(createCmd(5));
		} catch (UnconsumedCommandsOverflowException e) {
			return;
		}
		fail();
	}

	@Test
	public void rewindToCommand() throws Exception {
		CommandBuffer buffer = new CommandBuffer(5);
		for (int i = 1; i <= 4; i++) {
			buffer.addCommand(createCmd(i));
			if (i <= 3) {
				buffer.consumeCommand();
			}
		}
		assertEquals(4, buffer.size());

		buffer.rewindToCommand(2);

		assertEquals(4, buffer.size());
		assertEquals(3, buffer.consumeCommand().getId());
		assertEquals(4, buffer.consumeCommand().getId());
		assertNull(buffer.consumeCommand());
	}

	@Test
	public void rewindToCommand2() throws Exception {
		CommandBuffer buffer = new CommandBuffer(6);
		for (int i = 1; i <= 7; i++) {
			buffer.addCommand(createCmd(i));
			if (i <= 5) {
				buffer.consumeCommand();
			}
		}
		assertEquals(6, buffer.size());

		buffer.rewindToCommand(3);

		assertEquals(6, buffer.size());
		assertEquals(4, buffer.consumeCommand().getId());
		assertEquals(5, buffer.consumeCommand().getId());
		assertEquals(6, buffer.consumeCommand().getId());
		assertEquals(7, buffer.consumeCommand().getId());
		assertNull(buffer.consumeCommand());
	}

	@Test
	public void rewindToCommandWithLastReceivedIsMinusOne() throws Exception {
		CommandBuffer buffer = new CommandBuffer(6);
		for (int i = 1; i <= 3; i++) {
			buffer.addCommand(createCmd(i));
		}

		assertEquals(true, buffer.rewindToCommand(-1));
		assertEquals(1, buffer.consumeCommand().getId());
	}

	@Test
	public void rewindToCommandWithLastReceivedIsMinusOneFailsIfAlreadyOutOfBuffer() throws Exception {
		CommandBuffer buffer = new CommandBuffer(6);
		for (int i = 1; i <= 7; i++) {
			buffer.addCommand(createCmd(i));
			if (i <= 2) {
				buffer.consumeCommand();
			}
		}
		assertEquals(6, buffer.size());

		assertEquals(false, buffer.rewindToCommand(-1));
	}

	@Test
	public void purgeTillCommand() throws Exception {
		CommandBuffer buffer = new CommandBuffer(10);

		for (int i = 1; i <= 15; i++) {
			buffer.addCommand(createCmd(i));
			if (i <= 11) {
				buffer.consumeCommand();
			}
		}

		Assert.assertEquals(10, buffer.size());

		buffer.purgeTillCommand(8);
		Assert.assertEquals(8, buffer.size());

		buffer.purgeTillCommand(10);
		Assert.assertEquals(6, buffer.size());

		buffer.purgeTillCommand(11);
		Assert.assertEquals(5, buffer.size());

		buffer.purgeTillCommand(12);
		Assert.assertEquals("must not purge next consumable command", 4, buffer.size()); 

		buffer.purgeTillCommand(100);
		Assert.assertEquals("must not purge next consumable command", 4, buffer.size());

	}

	private CMD createCmd(int id) {
		return new CMD(id, "asdf");
	}
}
