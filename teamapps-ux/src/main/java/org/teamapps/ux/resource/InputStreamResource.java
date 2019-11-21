package org.teamapps.ux.resource;

import java.io.InputStream;
import java.util.function.Supplier;

public class InputStreamResource implements Resource {

	private final Supplier<InputStream> inputStreamSupplier;
	private long length = -1;

	public InputStreamResource(Supplier<InputStream> inputStreamSupplier, long length) {
		this.inputStreamSupplier = inputStreamSupplier;
		this.length = length;
	}

	public InputStreamResource(Supplier<InputStream> inputStreamSupplier) {
		this.inputStreamSupplier = inputStreamSupplier;
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
}
