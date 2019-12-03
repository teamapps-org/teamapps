package org.teamapps.ux.resource;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

public class ResourceWrapper implements Resource {

	private final Resource delegate;

	public ResourceWrapper(Resource delegate) {
		this.delegate = delegate;
	}

	@Override
	public InputStream getInputStream() {
		return delegate.getInputStream();
	}

	@Override
	public long getLength() {
		return delegate.getLength();
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public Date getLastModified() {
		return delegate.getLastModified();
	}

	@Override
	public Date getExpires() {
		return delegate.getExpires();
	}

	@Override
	public String getMimeType() {
		return delegate.getMimeType();
	}

	@Override
	public boolean isAttachment() {
		return delegate.isAttachment();
	}

	@Override
	public File getAsFile() {
		return delegate.getAsFile();
	}

	@Override
	public Resource lastModified(Date date) {
		return delegate.lastModified(date);
	}
}
