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
public class A implements UiObject {


	@JsonSerialize(using = ObjectSerializer.class)
	@JsonDeserialize(using = ObjectDeserializer.class)
	protected Object x;

	@JsonSerialize(using = ObjectSerializer.class)
	@JsonDeserialize(using = ObjectDeserializer.class)
	protected List y;

	@JsonSerialize(using = ObjectSerializer.class)
	@JsonDeserialize(using = ObjectDeserializer.class)
	protected Map<String, Object> z;

	public A() {
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public UiObjectType getUiObjectType() {
		return UiObjectType.A;
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.append("x=" + x).append(", ")
				.append("y=" + y).append(", ")
				.append("z=" + z)
				.toString();
	}

	@com.fasterxml.jackson.annotation.JsonGetter("x")
	public Object getX() {
		return x;
	}

	@com.fasterxml.jackson.annotation.JsonGetter("y")
	public List getY() {
		return y;
	}

	@com.fasterxml.jackson.annotation.JsonGetter("z")
	public Map<String, Object> getZ() {
		return z;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("x")
	public A setX(Object x) {
		this.x = x;
		return this;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("y")
	public A setY(List y) {
		this.y = y;
		return this;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("z")
	public A setZ(Map<String, Object> z) {
		this.z = z;
		return this;
	}






}