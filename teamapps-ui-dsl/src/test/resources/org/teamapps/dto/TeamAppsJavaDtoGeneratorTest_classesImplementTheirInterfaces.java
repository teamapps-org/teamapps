package org.teamapps.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_type")
@JsonTypeIdResolver(TeamAppsJacksonTypeIdResolver.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class D extends A implements B, C, UiObject {


	protected String bProperty;
	protected List<Integer> cProperty;

	/**
	 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
	 */
	@Deprecated
	public D() {
		// default constructor for Jackson
	}

	public D(List<Integer> cProperty) {
		super();
		this.cProperty = cProperty;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public UiObjectType getUiObjectType() {
		return UiObjectType.D;
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.append("bProperty=" + bProperty).append(", ")
				.append("cProperty=" + cProperty)
				.toString();
	}

	@com.fasterxml.jackson.annotation.JsonGetter("bProperty")
	public String getBProperty() {
		return bProperty;
	}

	@com.fasterxml.jackson.annotation.JsonGetter("cProperty")
	public List<Integer> getCProperty() {
		return cProperty;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("bProperty")
	public D setBProperty(String bProperty) {
		this.bProperty = bProperty;
		return this;
	}






}