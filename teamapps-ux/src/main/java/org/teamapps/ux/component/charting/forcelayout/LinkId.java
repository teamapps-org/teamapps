package org.teamapps.ux.component.charting.forcelayout;

import java.util.Objects;

public class LinkId<RECORD> {

	private final RECORD source;
	private final RECORD target;

	public LinkId(RECORD source, RECORD target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LinkId<?> linkId = (LinkId<?>) o;
		return (source.equals(linkId.source) && target.equals(linkId.target)) ||
				(source.equals(linkId.target) && target.equals(linkId.source));
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(source) + Objects.hashCode(target);
	}
}
