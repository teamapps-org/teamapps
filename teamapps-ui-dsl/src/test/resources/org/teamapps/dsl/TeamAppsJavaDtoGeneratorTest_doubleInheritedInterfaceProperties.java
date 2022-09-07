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
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_type", defaultImpl = D.class)
public class D extends A implements B, C, UiObject {

        static {
            var x = UiObjectJacksonTypeIdMaps.class; // make sure the types are registered
        }

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

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.append("bProperty=" + bProperty).append(", ")
				.append("cProperty=" + cProperty)
				.toString();
	}

	@com.fasterxml.jackson.annotation.JsonGetter("cProperty")
	public List<Integer> getCProperty() {
		return cProperty;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("cProperty")
	public D setCProperty(List<Integer> cProperty) {
		this.cProperty = cProperty;
		return this;
	}





}