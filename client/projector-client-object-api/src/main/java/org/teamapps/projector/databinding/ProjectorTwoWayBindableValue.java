package org.teamapps.projector.databinding;

import org.teamapps.commons.databinding.DataBindings;
import org.teamapps.commons.databinding.TwoWayBindableValue;
import org.teamapps.projector.event.ProjectorEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A two-way bindable value that uses {@link ProjectorEvent} for change notifications.
 * <p>
 * This interface extends {@link TwoWayBindableValue} and provides methods for creating
 * instances with various configurations. It is designed to work with the Projector
 * framework's event system for proper session context propagation.
 *
 * @param <T> the type of the value
 */
public interface ProjectorTwoWayBindableValue<T> extends TwoWayBindableValue<T> {

	/**
	 * Creates a new empty ProjectorTwoWayBindableValue with no initial value.
	 *
	 * @param <T> the type of the value
	 * @return a new ProjectorTwoWayBindableValue instance
	 */
	static <T> ProjectorTwoWayBindableValue<T> create() {
		return new ProjectorTwoWayBindableValueImpl<>();
	}

	/**
	 * Creates a new ProjectorTwoWayBindableValue with the specified initial value.
	 *
	 * @param <T> the type of the value
	 * @param initialValue the initial value to be stored
	 * @return a new ProjectorTwoWayBindableValue instance with the initial value
	 */
	static <T> ProjectorTwoWayBindableValue<T> create(T initialValue) {
		return new ProjectorTwoWayBindableValueImpl<>(initialValue);
	}

	/**
	 * Creates a new ProjectorTwoWayBindableValue that delegates getting and setting
	 * operations to the provided supplier and consumer functions.
	 *
	 * @param <T> the type of the value
	 * @param changeEvent the event that will be fired when the value changes
	 * @param getter the supplier function used to get the current value
	 * @param setter the consumer function used to set a new value
	 * @return a new ProjectorTwoWayBindableValue instance that delegates to the provided functions
	 */
	static <T> ProjectorTwoWayBindableValue<T> create(ProjectorEvent<T> changeEvent, Supplier<T> getter, Consumer<T> setter) {
		return ProjectorDataBindings.createTwoWayBindable(changeEvent, getter, setter);
	}

	/**
	 * Creates a new ProjectorTwoWayBindableValue that delegates setting operations
	 * to the provided consumer function and tracks the last seen value from the change event.
	 *
	 * @param <T> the type of the value
	 * @param changeEvent the event that will be fired when the value changes and used to track the current value
	 * @param setter the consumer function used to set a new value
	 * @return a new ProjectorTwoWayBindableValue instance that delegates setting to the provided function
	 */
	static <T> ProjectorTwoWayBindableValue<T> create(ProjectorEvent<T> changeEvent, Consumer<T> setter) {
		return ProjectorDataBindings.createTwoWayBindable(changeEvent, setter);
	}
}
