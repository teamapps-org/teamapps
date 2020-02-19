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

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public class ZipRatioAnalyzer {

	private static Logger LOGGER = LoggerFactory.getLogger(ZipRatioAnalyzer.class);

	public final String name;

	private AtomicLong uncompressedSizeSum = new AtomicLong(0);
	private AtomicLong statefullyCompressedSizeSum = new AtomicLong(0);
	private AtomicLong statelesslyCompressedSizeSum = new AtomicLong(0);

	public ZipRatioAnalyzer(String name) {
		this.name = name;
	}


	public void addData(byte[] uncompressedData, int statefullyCompressedSize) {
		if (LOGGER.isDebugEnabled()) {
			long uncompressedSum = uncompressedSizeSum.addAndGet(uncompressedData.length);
			long statefulSum = statefullyCompressedSizeSum.addAndGet(statefullyCompressedSize);
			long statelessSum = statelesslyCompressedSizeSum.addAndGet(ZlibUtil.deflate(uncompressedData).length);
			LOGGER.debug(name + ":: Σ uncompressed: " + FileUtils.byteCountToDisplaySize(uncompressedSum)
					+ "; Σ stateless: " + FileUtils.byteCountToDisplaySize(statelessSum) + " (r=" + ratio(uncompressedSum, statelessSum) + ")"
					+ "; Σ stateful: " + FileUtils.byteCountToDisplaySize(statefulSum) + " (r=" + ratio(uncompressedSum, statefulSum) + ")"
					+ "; Ratio stateful vs. stateless: " + ratio(statelessSum, statefulSum));

		}
	}

	private String ratio(long uncompressedSize, long compressedSize) {
		return String.format("%.2f%%", (100 * (uncompressedSize - compressedSize)) / (float) uncompressedSize);
	}

}
