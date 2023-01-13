package org.teamapps.dto222;

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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.teamapps.dto.DtoJsonWrapper;
import org.teamapps.dto.DtoObject;

/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */

import org.teamapps.dto.DtoReference;

@JsonTypeName("A")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoA implements DtoObject {

    public static final String TYPE_ID = "A";
    public static final List<String> EVENT_NAMES = List.of();
    public static final List<String> QUERY_NAMES = List.of();

	protected String aasdf;
	protected String b;
	protected List<Long> c;

	/**
	 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
	 */
	@Deprecated
	public DtoA() {
		// default constructor for Jackson
	}

	public DtoA(String aasdf) {
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
	public DtoA setAasdf(String aasdf) {
		this.aasdf = aasdf;
		return this;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("b")
	public DtoA setB(String b) {
		this.b = b;
		return this;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("c")
	public DtoA setC(List<Long> c) {
		this.c = c;
		return this;
	}




}