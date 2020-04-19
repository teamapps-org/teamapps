package org.teamapps.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CopyOnWriteLeakyCache<K, V> {

	private Map<K, V> map = Collections.emptyMap(); // invariant: this map is never directly modified, only copied!

	public V computeIfAbsent(K key, Function<K, V> computeFunction) {
		V value = map.get(key);
		if (value != null) {
			return value;
		} else {
			HashMap<K, V> mapCopy = new HashMap<>(map);
			value = computeFunction.apply(key);
			mapCopy.put(key, value);
			map = mapCopy;
			return value;
		}
	}

}
