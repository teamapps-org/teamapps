package org.teamapps.data.extract;

import java.util.Collection;
import java.util.Map;

public interface PropertyProvider<RECORD> {

	Map<String, Object> getValues(RECORD record, Collection<String> propertyNames);

}
