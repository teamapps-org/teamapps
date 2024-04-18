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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.teamapps.dto.JsonWrapper;
import org.teamapps.dto.DtoObject;

/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */

import org.teamapps.ux.component.DtoClientObject;

@JsonTypeName("A")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoA implements DtoObject {

    public static final String TYPE_ID = "A";
    public static final List<String> EVENT_NAMES = List.of();
    public static final List<String> QUERY_NAMES = List.of();

	protected String a;

	public DtoA() {
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.append("a=" + a)
				.toString();
	}

	@com.fasterxml.jackson.annotation.JsonGetter("a")
	public String getA() {
		return a;
	}

	@com.fasterxml.jackson.annotation.JsonSetter("a")
	public DtoA setA(String a) {
		this.a = a;
		return this;
	}



	@JsonTypeName("A.setA")
	@JsonFormat(shape = JsonFormat.Shape.ARRAY)
	@JsonPropertyOrder({"a"})
	public static class SetACommand {

	    public static final String CMD_NAME = "setA";

		protected String a;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public SetACommand() {
			// default constructor for Jackson
		}

		public SetACommand(String a) {
			this.a = a;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("a=" + a)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("a")
		public String getA() {
			return a;
		}

		public Object[] getParameters() {
		    return new Object[] {a};
		}

	}

}