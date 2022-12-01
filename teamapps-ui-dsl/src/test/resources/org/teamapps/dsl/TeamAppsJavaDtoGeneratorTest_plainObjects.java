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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.teamapps.dto.DtoJsonWrapper;
import org.teamapps.dto.DtoObject;

/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */


@JsonTypeName("A")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoA implements DtoObject {

    public static final String TYPE_ID = "A";
    public static final List<String> EVENT_NAMES = List.of();
    public static final List<String> QUERY_NAMES = List.of();

	protected Object x;
	protected List y;
	protected Map<String, Object> z;

	public DtoA() {
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
	public DtoA setX(Object x) {
		this.x = x;
		return this;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("y")
	public DtoA setY(List y) {
		this.y = y;
		return this;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("z")
	public DtoA setZ(Map<String, Object> z) {
		this.z = z;
		return this;
	}




}