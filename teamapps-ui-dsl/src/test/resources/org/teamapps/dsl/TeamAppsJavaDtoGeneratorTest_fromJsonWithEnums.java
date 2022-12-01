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

import org.teamapps.dto.DtoE;

@JsonTypeName("C")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoC implements DtoObject {

    public static final String TYPE_ID = "C";
    public static final List<String> EVENT_NAMES = List.of();
    public static final List<String> QUERY_NAMES = List.of();

	protected DtoE e = E.A;

	public DtoC() {
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.append("e=" + e)
				.toString();
	}

	@com.fasterxml.jackson.annotation.JsonGetter("e")
	public DtoE getE() {
		return e;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("e")
	public DtoC setE(DtoE e) {
		this.e = e;
		return this;
	}




}