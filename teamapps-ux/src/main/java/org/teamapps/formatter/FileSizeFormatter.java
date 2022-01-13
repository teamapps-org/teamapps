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
package org.teamapps.formatter;

public class FileSizeFormatter {

	public static String humanReadableByteCount(long bytes, boolean decimal, int precision) {
		int unit = decimal ? 1000 : 1024;
		if (bytes < unit) {
			return bytes + "B";
		}
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = "" + (decimal ? "kMGTPE" : "KMGTPE").charAt(exp - 1);
		return String.format("%." + precision + "f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
