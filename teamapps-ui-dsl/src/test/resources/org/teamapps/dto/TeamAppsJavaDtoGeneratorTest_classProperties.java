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


	protected String a;
	protected String b;
	protected List<Long> c;

	/**
	 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
	 */
	@Deprecated
	public A() {
		// default constructor for Jackson
	}

	public A(String a) {
		this.a = a;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public UiObjectType getUiObjectType() {
		return UiObjectType.A;
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.append("a=" + a).append(", ")
				.append("b=" + b).append(", ")
				.append("c=" + c)
				.toString();
	}

	@com.fasterxml.jackson.annotation.JsonGetter("a")
	public String getA() {
		return a;
	}

	@com.fasterxml.jackson.annotation.JsonGetter("b")
	public String getB() {
		return b;
	}

	@com.fasterxml.jackson.annotation.JsonGetter("c")
	public List<Long> getC() {
		return c;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("b")
	public A setB(String b) {
		this.b = b;
		return this;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("c")
	public A setC(List<Long> c) {
		this.c = c;
		return this;
	}






}