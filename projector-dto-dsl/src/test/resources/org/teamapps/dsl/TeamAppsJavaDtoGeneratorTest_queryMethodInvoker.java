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
import java.lang.reflect.Method;
import org.teamapps.projector.clientobject.AbstractClientObjectQueryMethodInvoker;
import java.util.function.Function;

public class XQueryMethodInvoker extends AbstractClientObjectQueryMethodInvoker {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public XQueryMethodInvoker(Object targetObject) {
		super(targetObject);
	}

	@Override
	protected Object invokeHandlerMethod(Method method, String name, List<JsonWrapper> parameters) throws Exception {
		return switch (name) {
		        case "q1" -> method.invoke(targetObject, ((Function<JsonNode, String>) (node) -> {
		            if (node == null || node.isNull()) {
		                return null;
		            }
		            return node.textValue();

		        }).apply(parameters.get(0).getJsonNode()));
		        case "q2" -> method.invoke(targetObject, ((Function<JsonNode, String>) (node) -> {
		            if (node == null || node.isNull()) {
		                return null;
		            }
		            return node.textValue();

		        }).apply(parameters.get(0).getJsonNode()), ((Function<JsonNode, Integer>) (node) -> {
		            if (node == null || node.isNull()) {
		                return 0;
		            }
		            return node.asInt();

		        }).apply(parameters.get(1).getJsonNode()));
			default -> throw new UnsupportedOperationException("Unknown query name: " + method.getName());
		};
	}
}