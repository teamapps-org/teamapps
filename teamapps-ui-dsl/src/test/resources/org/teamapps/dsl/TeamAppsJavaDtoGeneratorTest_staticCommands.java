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

import org.teamapps.dto.DtoCommand;

@JsonTypeName("A")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class DtoA implements DtoObject {

    public static final String TYPE_ID = "A";
    public static final List<String> EVENT_NAMES = List.of();
    public static final List<String> QUERY_NAMES = List.of();


	public DtoA() {
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.toString();
	}



	public DtoAReference createReference() {
	    return new DtoAReference(getId());
	}



        @com.fasterxml.jackson.annotation.JsonIgnore
        public String getTypeId() {
            return TYPE_ID;
        }
        @com.fasterxml.jackson.annotation.JsonIgnore
        public List<String> getEventNames() {
            return EVENT_NAMES;
        }
        @com.fasterxml.jackson.annotation.JsonIgnore
        public List<String> getQueryNames() {
            return QUERY_NAMES;
        }

	@JsonTypeName("A.a")
	@JsonFormat(shape = JsonFormat.Shape.ARRAY)
	@JsonPropertyOrder({"b"})
	public static class ACommand implements DtoCommand<Void> {

		protected String b;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public ACommand() {
			// default constructor for Jackson
		}

		public ACommand(String b) {
			this.b = b;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("b=" + b)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("b")
		public String getB() {
			return b;
		}

	}

}