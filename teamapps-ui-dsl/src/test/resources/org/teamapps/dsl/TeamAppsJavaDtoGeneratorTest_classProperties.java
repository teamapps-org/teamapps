package org.teamapps.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.teamapps.dto.UiObject;
import org.teamapps.dto.TeamAppsJacksonTypeIdResolver;

/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */

import org.teamapps.dto.blah.UiComponent;

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_type", defaultImpl = A.class)
public class A implements UiObject {

        static {
            var x = UiObjectJacksonTypeIdMaps.class; // make sure the types are registered
        }

	protected String aasdf;
	protected String b;
	protected List<Long> c;

	/**
	 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
	 */
	@Deprecated
	public A() {
		// default constructor for Jackson
	}

	public A(String aasdf) {
		this.aasdf = aasdf;
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.append("aasdf=" + aasdf).append(", ")
				.append("b=" + b).append(", ")
				.append("c=" + c)
				.toString();
	}

	@com.fasterxml.jackson.annotation.JsonGetter("aasdf")
	public String getAasdf() {
		return aasdf;
	}

	@com.fasterxml.jackson.annotation.JsonGetter("b")
	public String getB() {
		return b;
	}

	@com.fasterxml.jackson.annotation.JsonGetter("c")
	public List<Long> getC() {
		return c;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("aasdf")
	public A setAasdf(String aasdf) {
		this.aasdf = aasdf;
		return this;
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