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
package org.teamapps.ux.component.grid.bootstrap;

public class BootstrapishSizing {

	public static final int COL_SPAN_FULL_WIDTH = -1;

	private BootstrapishSizing smallerSizing;

	private final BootstrapishBreakpoint breakpoint;
	private Integer offset = null; // null = use value from smaller breakpoint
	private Integer colSpan = null; // null = use value from smaller breakpoint

	public BootstrapishSizing(BootstrapishBreakpoint breakpoint) {
		this.breakpoint = breakpoint;
	}

	public BootstrapishSizing(BootstrapishBreakpoint breakpoint, int colSpan) {
		this.breakpoint = breakpoint;
		this.colSpan = colSpan;
	}

	public BootstrapishSizing(BootstrapishBreakpoint breakpoint, int offset, int colSpan) {
		this.breakpoint = breakpoint;
		this.offset = offset;
		this.colSpan = colSpan;
	}

	public BootstrapishBreakpoint getBreakpoint() {
		return breakpoint;
	}

	public int getColSpan() {
		return colSpan != null ? colSpan : smallerSizing != null ? smallerSizing.getColSpan() : 1;
	}

	public BootstrapishSizing setColSpan(int colSpan) {
		this.colSpan = colSpan;
		return this;
	}

	public int getOffset() {
		return offset != null ? offset : smallerSizing != null ? smallerSizing.getOffset() : 0;
	}

	public BootstrapishSizing setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	/*package-private*/ BootstrapishSizing getSmallerSizing() {
		return smallerSizing;
	}

	/*package-private*/ void setSmallerSizing(BootstrapishSizing smallerSizing) {
		this.smallerSizing = smallerSizing;
	}

	@Override
	public String toString() {
		return "BootstrapishSizing{" +
				"breakpoint=" + breakpoint +
				", offset=" + offset +
				", colSpan=" + colSpan +
				'}';
	}
}
