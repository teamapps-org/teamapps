package org.teamapps.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_type")
@JsonTypeIdResolver(TeamAppsJacksonTypeIdResolver.class)
public interface UiCommand<RESULT> {

}