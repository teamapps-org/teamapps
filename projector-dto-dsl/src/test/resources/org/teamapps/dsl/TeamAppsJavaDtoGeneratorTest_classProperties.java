package org.teamapps.projector.dto222;

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


@JsonTypeName("A")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class A implements DtoObject {

    public static final String TYPE_ID = "A";  // TODO remove

	protected String aasdf;
	protected String b;
	protected int i;
	protected List<Long> c;

	public A() {
	}

	public A(String aasdf, String b, int i, List<Long> c) {
		this.aasdf = aasdf;
		this.b = b;
		this.i = i;
		this.c = c;
	}


	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.append("aasdf=" + aasdf).append(", ")
				.append("b=" + b).append(", ")
				.append("i=" + i).append(", ")
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

	@com.fasterxml.jackson.annotation.JsonGetter("i")
	public int getI() {
		return i;
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

	@com.fasterxml.jackson.annotation.JsonSetter("i")
	public A setI(int i) {
		this.i = i;
		return this;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("c")
	public A setC(List<Long> c) {
		this.c = c;
		return this;
	}




}