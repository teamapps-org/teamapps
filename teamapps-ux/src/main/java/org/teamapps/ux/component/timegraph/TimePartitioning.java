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
package org.teamapps.ux.component.timegraph;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public interface TimePartitioning {

	long getApproximateMillisecondsPerPartition();

	ZonedDateTime getPartitionStart(ZonedDateTime zonedDateTime);
	
	default ZonedDateTime getPartitionEnd(ZonedDateTime zonedDateTime) {
		return increment(getPartitionStart(zonedDateTime.minus(1, ChronoUnit.MICROS)));
	}

	ZonedDateTime increment(ZonedDateTime zonedDateTime);

	ZonedDateTime decrement(ZonedDateTime zonedDateTime);

}
