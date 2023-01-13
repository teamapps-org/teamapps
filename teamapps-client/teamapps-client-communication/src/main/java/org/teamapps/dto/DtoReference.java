package org.teamapps.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("_ref")
public class DtoReference implements DtoObject {

	String id;

	public DtoReference(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}