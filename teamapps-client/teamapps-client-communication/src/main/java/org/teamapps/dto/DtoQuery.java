package org.teamapps.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;




@JsonTypeName("Query")
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface DtoQuery extends DtoObject {

	public String getComponentId();

	public DtoQuery setComponentId(String componentId);


}