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
package org.teamapps.ux.resource;

import java.io.InputStream;
import java.util.function.Supplier;

public class InputStreamResource implements Resource {

	private final Supplier<InputStream> inputStreamSupplier;
	private final String name;

	private long length = -1;

	public InputStreamResource(Supplier<InputStream> inputStreamSupplier) {
		this(inputStreamSupplier, -1, null);
	}

	public InputStreamResource(Supplier<InputStream> inputStreamSupplier, String name) {
		this(inputStreamSupplier, -1, name);
	}

	public InputStreamResource(Supplier<InputStream> inputStreamSupplier, long length) {
		this(inputStreamSupplier, length, null);
	}

	public InputStreamResource(Supplier<InputStream> inputStreamSupplier, long length, String name) {
		this.inputStreamSupplier = inputStreamSupplier;
		this.length = length;
		this.name = name;
	}

	@Override
	public InputStream getInputStream() {
		return inputStreamSupplier.get();
	}

	@Override
	public long getLength() {
		if (length < 0) {
			length = Resource.super.getLength();
		}
		return this.length;
	}

	@Override
	public String getName() {
		return name;
	}

}
