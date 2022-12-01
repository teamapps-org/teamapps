package org.teamapps.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
public interface DtoEvent extends DtoObject {

	String getComponentId();

	DtoEvent setComponentId(String componentId);

}