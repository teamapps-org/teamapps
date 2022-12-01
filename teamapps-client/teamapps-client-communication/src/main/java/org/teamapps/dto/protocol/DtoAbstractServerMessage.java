package org.teamapps.dto.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class DtoAbstractServerMessage {

}