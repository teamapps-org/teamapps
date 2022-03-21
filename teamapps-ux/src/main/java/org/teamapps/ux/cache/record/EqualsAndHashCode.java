package org.teamapps.ux.cache.record;

import it.unimi.dsi.fastutil.objects.Object2IntFunction;

import java.util.function.BiPredicate;

public class EqualsAndHashCode<T> {
	private final BiPredicate<T, Object> equals;
	private final Object2IntFunction<T> hashCode;

	public static <T> EqualsAndHashCode<T> bypass() {
		return new EqualsAndHashCode<>(Object::equals, Object::hashCode);
	}

	public static <T> EqualsAndHashCode<T> identity() {
		return new EqualsAndHashCode<>((t, o) -> t == o, System::identityHashCode);
	}

	public EqualsAndHashCode(BiPredicate<T, Object> equals, Object2IntFunction<T> hashCode) {
		this.equals = equals;
		this.hashCode = hashCode;
	}

	public BiPredicate<T, Object> getEquals() {
		return equals;
	}

	public Object2IntFunction<T> getHashCode() {
		return hashCode;
	}
}
