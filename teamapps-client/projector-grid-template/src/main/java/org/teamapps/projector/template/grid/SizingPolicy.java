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
package org.teamapps.projector.template.grid;

public class SizingPolicy {

	public final static SizingPolicy AUTO = new SizingPolicy(SizeType.AUTO, 0, 0);
	public final static SizingPolicy FRACTION = new SizingPolicy(SizeType.FRACTION, 1, 0);

	private final SizeType type;
	private final float value;

	private final int minAbsoluteSize;

	public static SizingPolicy fixed(float sizeInPixels) {
		return new SizingPolicy(SizeType.FIXED, sizeInPixels, (int) sizeInPixels);
	}

	public SizingPolicy(SizeType type, float value, int minAbsoluteSize) {
		this.type = type;
		this.value = value;
		this.minAbsoluteSize = minAbsoluteSize;
	}

	public SizeType getType() {
		return type;
	}

	public float getValue() {
		return value;
	}

	public int getMinAbsoluteSize() {
		return minAbsoluteSize;
	}

	public DtoSizingPolicy createDtoSizingPolicy() {
		return new DtoSizingPolicy(type)
				.setValue(value)
				.setMinAbsoluteSize(minAbsoluteSize);
	}
}
