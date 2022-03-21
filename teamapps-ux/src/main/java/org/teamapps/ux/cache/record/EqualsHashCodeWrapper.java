package org.teamapps.ux.cache.record;

public class EqualsHashCodeWrapper<T> {

	private final T t;
	private final EqualsAndHashCode<T> equalsAndHashCode;

	public EqualsHashCodeWrapper(T t, EqualsAndHashCode<T> equalsAndHashCode) {
		this.t = t;
		this.equalsAndHashCode = equalsAndHashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EqualsHashCodeWrapper)) {
			return false;
		}
		if (equalsAndHashCode == null) {
			return this.t.equals(((EqualsHashCodeWrapper<?>) o).t);
		} else {
			return equalsAndHashCode.getEquals().test(t, ((EqualsHashCodeWrapper<?>) o).t);
		}
	}

	@Override
	public int hashCode() {
		if (equalsAndHashCode == null) {
			return t.hashCode();
		} else {
			return equalsAndHashCode.getHashCode().applyAsInt(t);
		}
	}
}
