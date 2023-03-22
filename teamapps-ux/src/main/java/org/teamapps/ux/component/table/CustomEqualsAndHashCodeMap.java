package org.teamapps.ux.component.table;

import org.teamapps.ux.cache.record.EqualsAndHashCode;
import org.teamapps.ux.cache.record.EqualsHashCodeWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Note that this class does NOT fulfill the {@link java.util.Map} contract!
 */
public class CustomEqualsAndHashCodeMap<K, V> {

	private final Map<EqualsHashCodeWrapper<K>, V> map = new HashMap<>();
	private final EqualsAndHashCode<K> equalsAndHashCode;

	public CustomEqualsAndHashCodeMap(EqualsAndHashCode<K> equalsAndHashCode) {
		this.equalsAndHashCode = equalsAndHashCode;
	}

	public boolean containsKey(K key) {
		return map.containsKey(keyWrapper(key));
	}

	public V computeIfAbsent(K key, Function<K, V> mappingFunction) {
		Objects.requireNonNull(mappingFunction);
		var keyWrapper = keyWrapper(key);
		V existingValue = map.get(keyWrapper);
		if (existingValue == null) {
			V newValue = mappingFunction.apply(key);
			map.put(keyWrapper, newValue);
			return newValue;
		} else {
			return existingValue;
		}
	}

	public V get(K key) {
		return map.get(keyWrapper(key));
	}

	public V getOrDefault(K key, V defaultValue) {
		return map.getOrDefault(keyWrapper(key), defaultValue);
	}

	public V remove(K key) {
		return map.remove(keyWrapper(key));
	}

	public void clear() {
		map.clear();
	}

	public void put(K key, V value) {
		map.put(keyWrapper(key), value);
	}

	public Set<K> keySet() {
		return map.keySet().stream()
				.map(EqualsHashCodeWrapper::getRecord)
				.collect(Collectors.toUnmodifiableSet());
	}

	private EqualsHashCodeWrapper<K> keyWrapper(K key) {
		return new EqualsHashCodeWrapper<>(key, equalsAndHashCode);
	}
}
