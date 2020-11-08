package org.teamapps.icon;

import org.teamapps.icons.api.Icon;

//@IconLibrary("example")
public enum TestIcon implements Icon<TestIcon, Void> {

	A, B;

	@Override
	public TestIcon withStyle(Void unused) {
		return this;
	}

	@Override
	public Void getStyle() {
		return null;
	}


}
