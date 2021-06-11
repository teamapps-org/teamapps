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
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_type", defaultImpl = A.class)
public class A implements UiObject {


	protected Map<String, List<X>> x;

	public A() {
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public UiObjectType getUiObjectType() {
		return UiObjectType.A;
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.append(x != null ? "x={" + x.toString() + "}" : "")
				.toString();
	}

	@com.fasterxml.jackson.annotation.JsonGetter("x")
	public Map<String, List<X>> getX() {
		return x;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("x")
	public A setX(Map<String, List<X>> x) {
		this.x = x;
		return this;
	}




}