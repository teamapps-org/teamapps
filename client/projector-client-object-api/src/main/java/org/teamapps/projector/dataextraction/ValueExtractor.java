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
package org.teamapps.projector.dataextraction;

/**
 * A functional interface for extracting a value from a record object.
 * <p>
 * This interface defines a single method for extracting a value of a specific type from a record object.
 *
 * @param <RECORD> the type of the record object
 * @param <VALUE> the type of the value to extract
 */
public interface ValueExtractor<RECORD, VALUE> {

	/**
	 * Extracts a value from a record object.
	 *
	 * @param object the record object to extract the value from
	 * @return the extracted value
	 */
	VALUE extract(RECORD object);

}
