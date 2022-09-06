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
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_type", defaultImpl = A.class)
public class A implements UiObject {

        static {
            var x = UiObjectJacksonTypeIdMaps.class; // make sure the types are registered
        }

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