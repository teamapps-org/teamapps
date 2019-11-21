package org.teamapps.ux.resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClassPathResourceTest {
	@Test
	public void testGetName() {
		assertEquals("someName", new ClassPathResource("/my/package/someName").getName());
		assertEquals("someName.dfi.sdfj", new ClassPathResource("/my/package/someName.dfi.sdfj").getName());
		assertEquals("someName", new ClassPathResource("someName").getName());
		assertEquals("someName.dfi.sdfj", new ClassPathResource("someName.dfi.sdfj").getName());
	}
}