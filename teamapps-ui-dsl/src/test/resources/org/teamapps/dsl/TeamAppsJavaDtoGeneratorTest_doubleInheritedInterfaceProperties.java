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

import org.teamapps.dto.DtoA;
import org.teamapps.dto.DtoAWrapper;
import org.teamapps.dto.DtoB;
import org.teamapps.dto.DtoBWrapper;
import org.teamapps.dto.DtoC;
import org.teamapps.dto.DtoCWrapper;

@JsonTypeName("D")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoD extends DtoA implements DtoB, DtoC, DtoObject {

    public static final String TYPE_ID = "D";
    public static final List<String> EVENT_NAMES = List.of();
    public static final List<String> QUERY_NAMES = List.of();

	protected List<Integer> cProperty;

	/**
	 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
	 */
	@Deprecated
	public DtoD() {
		// default constructor for Jackson
	}

	public DtoD(List<Integer> cProperty) {
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
	public DtoD setCProperty(List<Integer> cProperty) {
		this.cProperty = cProperty;
		return this;
	}




}