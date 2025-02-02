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
package org.teamapps.projector.format;

public class Length {

	public static final Length AUTO = new Length(-1) {
		@Override
		public String toCssString() {
			return "auto";
		}
	};

	private final float size;
	private final SizeUnit unit;

	public static Length ofPixels(int pixels) {
		return new Length(pixels, SizeUnit.PIXEL);
	}

	public static Length ofPercent(int percent) {
		return new Length(percent, SizeUnit.PERCENT);
	}

	public Length(float size) {
		this(size, SizeUnit.PIXEL);
	}

	public Length(float size, SizeUnit unit) {
		this.size = size;
		this.unit = unit;
	}

	public String toCssString() {
		return size + unit.getCcsString();
	}

	public float getSize() {
		return size;
	}

	public SizeUnit getUnit() {
		return unit;
	}
}
