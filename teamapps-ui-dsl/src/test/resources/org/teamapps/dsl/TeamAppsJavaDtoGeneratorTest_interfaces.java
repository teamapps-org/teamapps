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

import org.teamapps.dto.protocol.DtoCommand;
import org.teamapps.dto.protocol.DtoEvent;
import org.teamapps.dto.protocol.DtoQuery;

@JsonTypeName("A")
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface DtoA extends DtoObject {

	public String getA();
	public String getB();
	public DtoA setB(String b);

	public static class YEvent implements DtoEvent {

	    public static final String TYPE_ID = "A.y";

		protected String componentId;
		protected String y;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public YEvent() {
			// default constructor for Jackson
		}

		public YEvent(String componentId, String y) {
			this.componentId = componentId;
			this.y = y;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("componentId=" + componentId).append(", ")
					.append("y=" + y)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("componentId")
		public String getComponentId() {
			return componentId;
		}

		@com.fasterxml.jackson.annotation.JsonGetter("y")
		public String getY() {
			return y;
		}

		@com.fasterxml.jackson.annotation.JsonSetter("componentId")
		public YEvent setComponentId(String componentId) {
			this.componentId = componentId;
			return this;
		}

		@com.fasterxml.jackson.annotation.JsonSetter("y")
		public YEvent setY(String y) {
			this.y = y;
			return this;
		}

	}
    public static class YEventWrapper extends DtoJsonWrapper {

        public static final String TYPE_ID = "y";

        public YEventWrapper(JsonNode jsonNode) {
            super(jsonNode);
        }

        public Class<? extends YEvent> getDtoClass() {
            return YEvent.class;
        }

        public String getComponentId() {
            var node = jsonNode.get("componentId");
            if (node == null || node.isNull()) {
                return null;
            }
            return node.textValue();

        }


        public String getY() {
            var node = jsonNode.get("y");
            if (node == null || node.isNull()) {
                return null;
            }
            return node.textValue();

        }

    }

	public static class QQuery implements DtoQuery {

	    public static final String TYPE_ID = "A.q";

		protected String componentId;
		protected String y;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public QQuery() {
			// default constructor for Jackson
		}

		public QQuery(String componentId, String y) {
			this.componentId = componentId;
			this.y = y;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("componentId=" + componentId).append(", ")
					.append("y=" + y)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("componentId")
		public String getComponentId() {
			return componentId;
		}

		@com.fasterxml.jackson.annotation.JsonGetter("y")
		public String getY() {
			return y;
		}

		@com.fasterxml.jackson.annotation.JsonSetter("componentId")
		public QQuery setComponentId(String componentId) {
			this.componentId = componentId;
			return this;
		}

		@com.fasterxml.jackson.annotation.JsonSetter("y")
		public QQuery setY(String y) {
			this.y = y;
			return this;
		}

	}
	public static class QQueryWrapper extends DtoJsonWrapper {

	    public static final String TYPE_ID = "q";

	    public QQueryWrapper(JsonNode jsonNode) {
	        super(jsonNode);
	    }

	    public Class<? extends QQuery> getDtoClass() {
	        return QQuery.class;
	    }

	    public String getComponentId() {
	        var node = jsonNode.get("componentId");
	        if (node == null || node.isNull()) {
	            return null;
	        }
	        return node.textValue();

	    }


	    public String getY() {
	        var node = jsonNode.get("y");
	        if (node == null || node.isNull()) {
	            return null;
	        }
	        return node.textValue();

	    }

	}

	@JsonFormat(shape = JsonFormat.Shape.ARRAY)
	@JsonPropertyOrder({"x"})
	public static class XCommand implements DtoCommand<Void> {

		protected String x;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public XCommand() {
			// default constructor for Jackson
		}

		public XCommand(String x) {
			this.x = x;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("x=" + x)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("x")
		public String getX() {
			return x;
		}

	}

	@JsonFormat(shape = JsonFormat.Shape.ARRAY)
	@JsonPropertyOrder({"x2"})
	public static class X2Command implements DtoCommand<Boolean> {

		protected String x2;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public X2Command() {
			// default constructor for Jackson
		}

		public X2Command(String x2) {
			this.x2 = x2;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("x2=" + x2)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("x2")
		public String getX2() {
			return x2;
		}

	}

}