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
package org.teamapps.ux.component.flexcontainer;

public class FlexSizingPolicy {

	private final float baseSize;
	private final FlexSizeUnit baseSizeUnit;
	private final int grow;
	private final int shrink;

	public FlexSizingPolicy(int grow, int shrink) {
		this(0, FlexSizeUnit.AUTO, grow, shrink);
	}

	public FlexSizingPolicy(float baseSize, FlexSizeUnit baseSizeUnit, int grow, int shrink) {
		this.baseSize = baseSize;
		this.baseSizeUnit = baseSizeUnit;
		this.grow = grow;
		this.shrink = shrink;
	}

	public float getBaseSize() {
		return baseSize;
	}

	public int getGrow() {
		return grow;
	}

	public int getShrink() {
		return shrink;
	}

	public String toCssValue() {
		if (baseSizeUnit == FlexSizeUnit.AUTO) {
			return grow + " " + shrink + " " + baseSizeUnit.getCcsString();
		} else {
			return grow + " " + shrink + " " + baseSize + baseSizeUnit.getCcsString();
		}
	}
}
