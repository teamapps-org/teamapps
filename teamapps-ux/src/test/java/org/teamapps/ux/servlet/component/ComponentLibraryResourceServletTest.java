package org.teamapps.ux.servlet.component;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

public class ComponentLibraryResourceServletTest {
	@Test
	public void name() {
		ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
		map.computeIfAbsent(123, o -> null);
		System.out.println(map.containsKey(123));
		System.out.println(map.get(123));
	}
}