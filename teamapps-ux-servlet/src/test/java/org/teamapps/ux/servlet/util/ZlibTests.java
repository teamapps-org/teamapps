/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.ux.servlet.util;

import com.google.common.io.Resources;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ZlibTests {

	@Test
	public void deflate() throws Exception {
		ZlibStatefulDeflater deflator = new ZlibStatefulDeflater();
		ZlibStatefulInflater inflator = new ZlibStatefulInflater();

		Arrays.asList(
				"Initial",
				"Second message",
				"Here comes a larger message!",
				readResourceToString("sample.json"),
				"tiny"
		).forEach(s -> testDeflateInflateChain(deflator, inflator, s));
	}

	@Test
	public void testStreamingIsBetterThanStateless() {
		ZlibStatefulDeflater deflator = new ZlibStatefulDeflater();

		Arrays.asList(
				"Initial",
				"Second message",
				"Here comes a larger message!",
				readResourceToString("sample.json"),
				"tiny"
		).forEach(s -> {
			int firstCompressionSize = deflator.deflate(s.getBytes(StandardCharsets.UTF_8)).length;
			int secondCompressionSize = deflator.deflate(s.getBytes(StandardCharsets.UTF_8)).length;
			int statelessCompressionSize = ZlibUtil.deflateString(s).length;

			System.out.println("First compression: " + firstCompressionSize + "; Second compression: " + secondCompressionSize + "; Stateless compression: " + statelessCompressionSize);

			Assertions.assertThat(secondCompressionSize).isLessThan(firstCompressionSize);
			Assertions.assertThat(secondCompressionSize).isLessThan(statelessCompressionSize);
		});
	}

	@Test
	public void benchmarkImprovementsInRealisticScenario() throws IOException {
		ZlibStatefulDeflater deflator = new ZlibStatefulDeflater();

		InputStream inputStream = getClass().getResourceAsStream("/recordings/ux-component-test-app-recording-2.log");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		int sumStateful = 0;
		int sumStateless = 0;

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			int streamingCompressionSize = deflator.deflate(line.getBytes(StandardCharsets.UTF_8)).length;
			int statelessCompressionSize = ZlibUtil.deflateString(line).length;

			sumStateful += streamingCompressionSize;
			sumStateless += statelessCompressionSize;

			System.out.println("Stateful compression: " + streamingCompressionSize + "; Stateless compression: " + statelessCompressionSize);
		}
		System.out.println("SUMS: Stateful: " + sumStateful + "; Stateless: " + sumStateless);
		System.out.println("Saving absolute: " + (sumStateless - sumStateful) + "; Saving relative: " + ((sumStateless - sumStateful) / (double) sumStateful) * 100 + "%");
	}

	@Test
	@Ignore
	public void testHeapProblems() throws Exception {
		ZlibStatefulDeflater deflator = new ZlibStatefulDeflater();
		while (true) {
			deflator.deflate(RandomStringUtils.random(10000).getBytes(StandardCharsets.UTF_8));
		}
	}

	private void testDeflateInflateChain(ZlibStatefulDeflater deflator, ZlibStatefulInflater inflator, String input) {
		byte[] compressed = deflator.deflate(input.getBytes(StandardCharsets.UTF_8));
		String uncompressed = new String(inflator.inflate(compressed), StandardCharsets.UTF_8);

		int inputLength = input.getBytes(StandardCharsets.UTF_8).length;
		System.out.println("Input length: " + inputLength + "; Compressed length: " + compressed.length);

		Assert.assertEquals(uncompressed, input);
	}

	private static String readResourceToString(String resourceName) {
		URL url = Resources.getResource(resourceName);
		try {
			return Resources.toString(url, Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
