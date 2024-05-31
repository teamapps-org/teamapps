package org.teamapps.projector.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.dto.DtoObject;
import org.teamapps.projector.session.SessionContext;

import org.teamapps.commons.util.ExceptionUtil;

/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */

import org.teamapps.projector.clientobject.ClientObject;
import org.teamapps.projector.dto.A;
import org.teamapps.projector.dto.AWrapper;
import org.teamapps.projector.dto.B;
import org.teamapps.projector.dto.BWrapper;
import org.teamapps.projector.dto.C;
import org.teamapps.projector.dto.CWrapper;

@JsonTypeName("D")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class D extends A implements B, C, DtoObject {

    public static final String TYPE_ID = "D";  // TODO remove

	protected List<Integer> cProperty;
	protected String bProperty;

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
				.append("cProperty=" + cProperty).append(", ")
				.append("bProperty=" + bProperty)
				.toString();
	}

	@com.fasterxml.jackson.annotation.JsonGetter("cProperty")
	public List<Integer> getCProperty() {
		return cProperty;
	}

	@com.fasterxml.jackson.annotation.JsonGetter("bProperty")
	public String getBProperty() {
		return bProperty;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("cProperty")
	public D setCProperty(List<Integer> cProperty) {
		this.cProperty = cProperty;
		return this;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("bProperty")
	public D setBProperty(String bProperty) {
		this.bProperty = bProperty;
		return this;
	}




}