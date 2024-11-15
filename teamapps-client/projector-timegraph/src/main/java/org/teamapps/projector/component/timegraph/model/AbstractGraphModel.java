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
package org.teamapps.projector.component.timegraph.model;

import org.teamapps.projector.component.timegraph.datapoints.GraphData;
import org.teamapps.projector.event.ProjectorEvent;

abstract class AbstractGraphModel<D extends GraphData> implements GraphModel<D> {

	public final ProjectorEvent<Void> onDataChanged = new ProjectorEvent<>();

	@Override
	public ProjectorEvent<Void> onDataChanged() {
		return onDataChanged;
	}

}