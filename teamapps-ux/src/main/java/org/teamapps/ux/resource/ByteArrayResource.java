package org.teamapps.ux.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteArrayResource implements Resource {

	private final byte[] data;
	private final String name;

	public ByteArrayResource(byte[] data, String name) {
		this.data = data;
		this.name = name;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(data);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getLength() {
		return data.length;
	}
}
