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

@JsonTypeName("A")
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface A extends DtoObject {

	public String getA();
	public String getB();
	public A setA(String a);
	public A setB(String b);

    public static class YEventWrapper extends JsonWrapper {

        public static final String TYPE_ID = "y";

        public YEventWrapper(JsonNode jsonNode) {
            super(jsonNode);
        }

        public String getY() {
            var node = jsonNode.get("y");
            if (node == null || node.isNull()) {
                return null;
            }
            return node.textValue();

        }
    }

	public static class QQueryWrapper extends JsonWrapper {

	    public static final String TYPE_ID = "q";

	    public QQueryWrapper(JsonNode jsonNode) {
	        super(jsonNode);
	    }

	    public String getY() {
	        var node = jsonNode.get("y");
	        if (node == null || node.isNull()) {
	            return null;
	        }
	        return node.textValue();

	    }
	}

	@JsonTypeName("A.x")
	@JsonFormat(shape = JsonFormat.Shape.ARRAY)
	@JsonPropertyOrder({"x"})
	public static class XCommand {

	    public static final String CMD_NAME = "x";

		protected String x;

		public XCommand() {
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

		public Object[] getParameters() {
		    return new Object[] {x};
		}

	}

	@JsonTypeName("A.x2")
	@JsonFormat(shape = JsonFormat.Shape.ARRAY)
	@JsonPropertyOrder({"x2"})
	public static class X2Command {

	    public static final String CMD_NAME = "x2";

		protected String x2;

		public X2Command() {
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

		public Object[] getParameters() {
		    return new Object[] {x2};
		}

	}

}