package org.teamapps.ux.session;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.AbstractDualBidiMap;

import java.util.Map;

public class MyBidiMap extends AbstractDualBidiMap {

	public MyBidiMap() {
	}

	@Override
	protected BidiMap createBidiMap(Map normalMap, Map reverseMap, BidiMap inverseMap) {
		return null;
	}
}
