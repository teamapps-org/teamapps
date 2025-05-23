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
package org.teamapps.projector.databinding;

import org.teamapps.commons.databinding.TwoWayBindableValue;
import org.teamapps.projector.event.ProjectorEvent;

/**
 * A concrete implementation of the {@link TwoWayBindableValue} interface using {@link ProjectorEvent}
 * for {@link org.teamapps.projector.session.SessionContext} propagation.
 * <p>
 * This class maintains a value and a ProjectorEvent for change notifications,
 * and implements the get() and set() methods to access and modify the value,
 * firing the event when the value changes.
 *
 * @param <T> the type of the value
 */
public class ProjectorTwoWayBindableValueImpl<T> implements ProjectorTwoWayBindableValue<T> {

	/**
	 * The event that is fired when the value changes.
	 */
	public final ProjectorEvent<T> onChange = new ProjectorEvent<>();

	/**
	 * The current value.
	 */
	private T value;

	/**
	 * Creates a new TwoWayBindableValueImpl with no initial value.
	 */
	public ProjectorTwoWayBindableValueImpl() {
	}

	/**
	 * Creates a new TwoWayBindableValueImpl with the specified initial value.
	 *
	 * @param initialValue the initial value
	 */
	public ProjectorTwoWayBindableValueImpl(T initialValue) {
		this.value = initialValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectorEvent<T> onChange() {
		return onChange;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation stores the value and fires the onChanged event
	 * if the value has changed.
	 */
	@Override
	public void set(T value) {
		this.value = value;
		onChange.fireIfChanged(value);
	}
}
