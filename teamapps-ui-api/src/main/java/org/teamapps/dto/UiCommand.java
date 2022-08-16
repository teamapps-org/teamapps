package org.teamapps.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_type")
@JsonTypeIdResolver(TeamAppsJacksonTypeIdResolver.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface UiCommand<RESULT> {

	String getComponentId();

}