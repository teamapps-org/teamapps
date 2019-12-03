package org.teamapps.ux.resource;

import java.io.InputStream;
import java.util.function.Supplier;

public class InputStreamResource implements Resource {

	private final Supplier<InputStream> inputStreamSupplier;
	private final String name;

	private long length = -1;

	public InputStreamResource(Supplier<InputStream> inputStreamSupplier) {
		this(inputStreamSupplier, -1, null);
	}

	public InputStreamResource(Supplier<InputStream> inputStreamSupplier, String name) {
		this(inputStreamSupplier, -1, name);
	}

	public InputStreamResource(Supplier<InputStream> inputStreamSupplier, long length) {
		this(inputStreamSupplier, length, null);
	}

	public InputStreamResource(Supplier<InputStream> inputStreamSupplier, long length, String name) {
		this.inputStreamSupplier = inputStreamSupplier;
		this.length = length;
		this.name = name;
	}

	@Override
	public InputStream getInputStream() {
		return inputStreamSupplier.get();
	}

	@Override
	public long getLength() {
		if (length < 0) {
			length = Resource.super.getLength();
		}
		return this.length;
	}

	@Override
	public String getName() {
		return name;
	}

}
